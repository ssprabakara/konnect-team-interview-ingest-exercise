package konnect.opensearch;


import java.nio.charset.StandardCharsets;
import java.util.List;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(OpensearchPublisher.class);
    private static final String REQUEST_URL = "http://localhost:9200/cdc-events/_doc/";
    private static final String BULK_API_REQUEST_URL = "http://localhost:9200/cdc-events/_bulk/";
    private static final String INDEX_NAME = "cdc-events";
    private static final String BULK_API_CREATE_JSON = "{ \"create\": { \"_index\": " + "\"" + INDEX_NAME + "\"" + " } }";
    private final OkHttpClient httpClient;

    public OpensearchPublisher() {
        httpClient = buildHttpClient();
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
            sb.append(BULK_API_CREATE_JSON);
            sb.append("\n");
            sb.append(event);
            sb.append("\n");
        }

        return sb.toString();
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .connectionPool(new ConnectionPool(50, 30, TimeUnit.MINUTES))
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

        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(REQUEST_URL).newBuilder();
        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8),
                MediaType.parse("application/json"));

        return new Request.Builder()
                .url(httpUrlBuilder.build())
                .post(body);
    }

    private Request.Builder buildBulkHttpRequest(final String requestBody) {

        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(BULK_API_REQUEST_URL).newBuilder();
        RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8),
                MediaType.parse("application/json"));

        return new Request.Builder()
                .url(httpUrlBuilder.build())
                .post(body);
    }
}
