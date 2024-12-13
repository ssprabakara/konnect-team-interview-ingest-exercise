package konnect.opensearch;

import java.util.List;
import konnect.config.AppConfig;
import konnect.httpclient.HttpClientImpl;
import konnect.httpclient.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpensearchPublisher {

    private final AppConfig appConfig;
    private final HttpClientImpl httpClient;
    private final String bulkPostRequestUrl;
    private final String indexName;
    private final String postRequestUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpensearchPublisher.class);
    private static final String BULK_API_CREATE_JSON = "{ \"create\": { \"_index\": " + "\"" + "%s" + "\"" + " } }";

    public OpensearchPublisher(final AppConfig appConfig, final HttpClientImpl httpClient) {
        this.appConfig = appConfig;
        bulkPostRequestUrl = appConfig.getBulkPostRequestUrl();
        this.httpClient = httpClient;
        indexName = appConfig.getIndexName();
        postRequestUrl = appConfig.getPostRequestUrl();
    }

    public void publish(final String value) {
        executePostRequest(value, postRequestUrl);
    }

    public void publishBulk(final List<String> events) {
        // process the error response and how to handle the failed records?
        executePostRequest(buildBulkRequestBody(events), bulkPostRequestUrl);
    }

    private String buildBulkRequestBody(final List<String> events) {
        StringBuilder sb = new StringBuilder();

        for (final String event: events) {
            sb.append(String.format(BULK_API_CREATE_JSON, indexName));
            sb.append("\n");
            sb.append(event);
            sb.append("\n");
        }

        return sb.toString();
    }

    private HttpResponse executePostRequest(final String requestBody, final String requestUrl) {
        return httpClient.executePostRequest(requestBody, requestUrl);
    }
}
