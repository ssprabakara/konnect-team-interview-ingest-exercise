package konnect.httpclient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
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

public class HttpClientImpl implements HttpClient {
    private final AppConfig appConfig;
    private final OkHttpClient okHttpClient;

    private static final String HTTP_MEDIA_TYPE = "application/json";
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientImpl.class);

    public HttpClientImpl(final AppConfig appConfig) {
        this.appConfig = appConfig;
        this.okHttpClient = buildHttpClient();
    }

    public HttpResponse executePostRequest(final String requestBody, final String requestUrl) {
        Response response = null;
        HttpResponse httpResponse = null;
        try {
            Request request = buildHttpPostRequest(requestBody, requestUrl).build();
            response = okHttpClient.newCall(request).execute();
            httpResponse = new HttpResponse(response.body() == null ? "" : response.body().string(), response.code());
        } catch (final IOException ex) {
            LOGGER.error("HTTP Request exception occurred:- {}", ex.getMessage());
        }

        if (response != null) {
            if (!response.isSuccessful()) {
                LOGGER.error("HTTP Request exception occurred. ResponseBody={} ResponseCode={}",
                        response.body(), response.code());
            }
            response.close();
        }

        return httpResponse;
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

    private Request.Builder buildHttpPostRequest(final String requestBody, final String requestUrl) {
        if (HttpUrl.parse(requestUrl) != null) {

            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(requestUrl).newBuilder();
            RequestBody body = RequestBody.create(requestBody.getBytes(StandardCharsets.UTF_8),
                    MediaType.parse(HTTP_MEDIA_TYPE));

            return new Request.Builder()
                    .url(httpUrlBuilder.build())
                    .post(body);
        }

        return new Request.Builder();
    }
}
