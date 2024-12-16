package konnect.opensearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import konnect.config.AppConfig;
import konnect.util.Utils;
import org.apache.http.HttpHost;
import org.opensearch.client.Node;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.opensearch.client.ResponseListener;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.common.UUIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OpensearchRestClientImpl implements OpensearchRestClient {

    private final AppConfig appConfig;
    private final ExecutorService executorService;
    private final RestClient restClient;
    private final ScheduledExecutorService scheduledExecutorService;
    private final Set<String> failedDocIds; // documentIds
    private final String bulkPostRequestUrl;
    private final String indexName;
    private final String postRequestUrl;
    private final int maxRetry;

    private Map<String, String> idEventMap; // documentId, cdcEvent

    private static final String BULK_API_CREATE_JSON = "{ \"create\": { \"_index\": " + "\"" + "%s" + "\"" +
            ", \"_id\": " + "\"" + "%s" + "\"" + " } }";
    private static final String HTTP_POST_METHOD = "POST";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpensearchRestClientImpl.class);

    public OpensearchRestClientImpl(final AppConfig appConfig) {
        this.appConfig = appConfig;
        bulkPostRequestUrl = appConfig.getOpensearchBulkPostRequestUrl();
        this.executorService = Executors.newFixedThreadPool(10);
        this.failedDocIds = ConcurrentHashMap.newKeySet();
        this.idEventMap = new ConcurrentHashMap<>();
        indexName = appConfig.getOpensearchIndexName();
        this.maxRetry = appConfig.getOpenSearchRestClientMaxRetry();
        postRequestUrl = appConfig.getOpensearchPostRequestUrl();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("id-doc-mapping-cleaner")
                        .build());
        this.restClient = buildRestClient();
        scheduleIdDocMapCleanup();
    }

    private RestClient buildRestClient() {

        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(appConfig.getOpensearchRestClientHost(),
                             appConfig.getOpensearchRestClientPort(),
                             appConfig.getOpensearchRestClientHttpScheme()))
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(appConfig.getOpensearchRestClientConnectTimeoutInSeconds()));

        restClientBuilder.setFailureListener(new RestClient.FailureListener() {

            @Override
            public void onFailure(final Node node) {
                LOGGER.error("Error occurred, while building the Opensearch Rest client on node: {}", node.getHost());
            }
        });

        return restClientBuilder.build();
    }

    public void publish(final String requestBody) {
        Request request = buildPostRequest(postRequestUrl, requestBody);
        executeRequest(request);
    }

    public void publishAsync(final String requestBody) {
        Request request = buildPostRequest(postRequestUrl, requestBody);
        executeRequestAsync(request, 0);
    }

    public void publishBulk(final List<String> events) {
        Request request = buildPostRequest(bulkPostRequestUrl, buildBulkRequestBody(events));
        executeRequest(request);
    }

    public void publishBulkAsync(final List<String> events) {
        Request request = buildPostRequest(bulkPostRequestUrl, buildBulkRequestBody(events));
        executeRequestAsync(request, 0);
    }

    @VisibleForTesting
    String buildBulkRequestBody(final List<String> events) {
        StringBuilder sb = new StringBuilder();
        idEventMap = new HashMap<>();

        for (final String event: events) {
            String id = generateUuid();

            sb.append(String.format(BULK_API_CREATE_JSON, indexName, id));
            sb.append("\n");
            sb.append(event);
            sb.append("\n");

            String request = String.format(BULK_API_CREATE_JSON, indexName, id) + "\n" + event + "\n";
            idEventMap.put(id, request);
        }

        return sb.toString();
    }

    @VisibleForTesting
    String generateUuid() {
        return UUIDs.base64UUID();
    }

    @VisibleForTesting
    Request buildPostRequest(final String requestUrl,
                             final String requestBody) {

        Request request = new Request(HTTP_POST_METHOD, requestUrl);
        request.setJsonEntity(requestBody);
        return request;
    }

    @VisibleForTesting
    void executeRequest(final Request request) {
        try {
            Response response = restClient.performRequest(request);
            if (response != null) {
                LOGGER.info("httpResponse body: {}, httpResponseCode: {}",
                        extractResponse(response),
                        response.getStatusLine().getStatusCode());
            }
        } catch (final IOException ex) {
            LOGGER.error("Error occurred while trying to publish the request", ex);
        }
    }

    private void executeRequestAsync(final Request request, final int attempts) {
        if (attempts <= maxRetry) {
            executorService.submit(() -> {
                restClient.performRequestAsync(request,
                        new ResponseListener() {
                            @Override
                            public void onSuccess(final Response response) {
                                String resp = extractResponse(response);
                                if (resp != null) {
                                    LOGGER.info("httpResponse body: {}, httpResponseCode: {}",
                                            resp,
                                            response.getStatusLine().getStatusCode());

                                    List<String> failedDocs = getFailedDocuments(resp);

                                    if (!failedDocs.isEmpty()) {
                                        String request = String.join("", failedDocs);
                                        executeRequestAsync(buildPostRequest(bulkPostRequestUrl, request),
                                                attempts + 1);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(final Exception ex) {
                                LOGGER.error("Publish Request failed:- ", ex);

                                if (ex instanceof ResponseException responseException) {
                                    LOGGER.error("HTTP Status Code: {}, Error Response: {}",
                                            responseException.getResponse().getStatusLine().getStatusCode(),
                                            extractResponse(responseException.getResponse()));
                                }
                            }
                        });
            });
        } else {
            LOGGER.error("Publish request failed after Max retry of {} attempts", maxRetry);
        }
    }

    @VisibleForTesting
    List<String> getFailedDocuments(final String response) {
        List<String> failedDocuments = new ArrayList<>();

        /*
            {
                "took": 11,
                "errors": true,
                "items": [
                    {
                        "create": {
                            "_index": "movies",
                            "_id": "tt1392214",
                            "status": 409,
                            "error": {
                                "type": "version_conflict_engine_exception",
                                "reason": "[tt1392214]: version conflict, document already exists (current version [1])",
                                "index": "movies",
                                "shard": "0",
                                "index_uuid": "yhizhusbSWmP0G7OJnmcLg"
                            }
                        }
                    }
                ]
            }
         */

        try {
            // Read the response body (bulk insert response)
            JsonNode jsonNode = Utils.OBJECT_MAPPER.readTree(response);

            if (jsonNode.get("errors").isBoolean() && jsonNode.get("errors").asBoolean()) {
                JsonNode itemsNode = jsonNode.get("items");

                if (itemsNode.isArray()) {
                    for (Iterator<JsonNode> it = itemsNode.elements(); it.hasNext(); ) {
                        JsonNode item = it.next();
                        JsonNode createNode = item.get("create");
                        String id = createNode.get("_id").asText();
                        if (idEventMap.get(id) != null) {
                            failedDocIds.add(id);
                            failedDocuments.add(idEventMap.get(id));
                        }

                    }
                }
            }

        } catch (final IOException ex) {
            LOGGER.error("Failed to parse bulk insert response", ex);
        }

        return failedDocuments;
    }

    @VisibleForTesting
    String extractResponse(final Response response) {
        try {
                return new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()))
                        .lines()
                        .parallel()
                        .collect(Collectors.joining("\n"));
        } catch (final IOException ex) {
            LOGGER.error("Error occurred, while extracting HTTP response", ex);
        }

        return null;
    }

    @VisibleForTesting
    void scheduleIdDocMapCleanup() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            for (final String id : idEventMap.keySet()) {
                if (!failedDocIds.contains(id)) {
                    idEventMap.remove(id);
                }
            }
        }, 10, 10, TimeUnit.MINUTES);
    }

}
