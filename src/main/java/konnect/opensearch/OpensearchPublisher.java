package konnect.opensearch;

import java.nio.charset.StandardCharsets;
import java.util.List;
import konnect.config.AppConfig;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OpensearchPublisher {

    private final AppConfig appConfig;
    private final OkHttpClient httpClient;
    private final String bulkPostRequestUrl;
    private final String indexName;
    private final String postRequestUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpensearchPublisher.class);
    private static final String BULK_API_CREATE_JSON = "{ \"create\": { \"_index\": " + "\"" + "%s" + "\"" + " } }";
    private static final String HTTP_MEDIA_TYPE = "application/json";

    public OpensearchPublisher(final AppConfig appConfig) {
        this.appConfig = appConfig;
        bulkPostRequestUrl = appConfig.getBulkPostRequestUrl();
        httpClient = buildHttpClient();
        indexName = appConfig.getIndexName();
        postRequestUrl = appConfig.getPostRequestUrl();
    }

    public void publish(final String value) throws IOException {
        executeRequest(buildHttpRequest(value).build());
    }

    public void publishBulk(final List<String> events) throws IOException {
        String requestBody = buildBulkRequestBody(events);
        executeRequest(buildBulkHttpRequest(requestBody).build());
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

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(appConfig.getConnectTimeoutInSeconds(), TimeUnit.SECONDS)
                .writeTimeout(appConfig.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(appConfig.getReadTimeout(), TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(appConfig.getMaxIdleConnections(),
                        appConfig.getKeepAliveDurationInMinutes(), TimeUnit.MINUTES))
                .build();
    }

    private void executeRequest(final Request request) throws IOException {
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (final IOException ex) {
            LOGGER.error("HTTP Request exception occurred:- {}", ex.getMessage());
        }

        if (response != null) {
            if (!response.isSuccessful()) {
                LOGGER.error("HTTP Request exception occurred. ResponseBody={} ResponseCode={}",
                        response.body().string(), response.code());
            }
            response.close();
        }
    }

    private Request.Builder buildHttpRequest(final String requestBody) {

        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(postRequestUrl).newBuilder();
        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8),
                MediaType.parse(HTTP_MEDIA_TYPE));

        return new Request.Builder()
                .url(httpUrlBuilder.build())
                .post(body);
    }

    private Request.Builder buildBulkHttpRequest(final String requestBody) {

        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(bulkPostRequestUrl).newBuilder();
        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8),
                MediaType.parse(HTTP_MEDIA_TYPE));

        return new Request.Builder()
                .url(httpUrlBuilder.build())
                .post(body);
    }
}
