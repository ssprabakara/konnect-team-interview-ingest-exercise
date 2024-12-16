package konnect.opensearch.config;

public interface OpensearchConfig {
    String BULK_POST_REQUEST_URL = "http://localhost:9200/cdc-events/_bulk/";
    String DEFAULT_INDEX_NAME = "cdc-events";
    String POST_REQEUST_URL = "http://localhost:9200/cdc-events/_doc/";

    /**
     *
     */
    default String getOpensearchBulkPostRequestUrl() {
        return BULK_POST_REQUEST_URL;
    }


    /**
     *
     */
    default String getOpensearchIndexName() {
        return DEFAULT_INDEX_NAME;
    }

    /**
     *
     */
    default String getOpensearchPostRequestUrl() {
        return POST_REQEUST_URL;
    }

}
