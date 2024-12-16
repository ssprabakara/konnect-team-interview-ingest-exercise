package konnect.opensearch.config;

public interface OpensearchRestClientConfig {

    /**
     *
     * @return
     */
    default int getOpensearchRestClientConnectTimeoutInSeconds() {
        return 10;
    }

    default String getOpensearchRestClientHost() {
        return "localhost";
    }

    default int getOpensearchRestClientPort() {
        return 9200;
    }

    default String getOpensearchRestClientHttpScheme() {
        return "http";
    }

    default int getOpenSearchRestClientMaxRetry() {
        return 3;
    }
}
