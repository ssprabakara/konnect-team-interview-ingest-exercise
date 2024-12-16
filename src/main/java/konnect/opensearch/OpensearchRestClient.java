package konnect.opensearch;

import java.util.List;

public interface OpensearchRestClient {

    void publish(final String requestPayload);
    void publishAsync(final String requestPayload);
    void publishBulk(final List<String> events);
    void publishBulkAsync(final List<String> events);

}
