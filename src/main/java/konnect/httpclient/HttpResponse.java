package konnect.httpclient;

public class HttpResponse {

    private final String responseBody;
    private final int responseCode;


    public HttpResponse(final String responseBody, final int responseCode) {
        this.responseBody = responseBody;
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
