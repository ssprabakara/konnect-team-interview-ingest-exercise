package konnect.config;

public interface HttpClientConfig {

    /**
     *
     * @return
     */
    default int getConnectTimeoutInSeconds() {
        return 10;
    }

    /**
     *
     * @return
     */
    default int getKeepAliveDurationInMinutes() {
        return 30;
    }

    /**
     *
     * @return
     */
    default int getMaxIdleConnections() {
        return 50;
    }

    /**
     *
     * @return
     */
    default int getReadTimeout() {
        return 10;
    }

    /**
     *
     * @return
     */
    default int getWriteTimeout() {
        return 10;
    }

}
