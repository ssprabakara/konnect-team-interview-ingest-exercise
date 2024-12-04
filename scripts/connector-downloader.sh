#!/usr/bin/env bash
shopt -s extglob

FILEPULSE_CONNECTOR_VERSION=2.14.1
OPENSEARCH_CONNECTOR_VERSION=3.1.1
ROOT_DIR="$(cd "$(dirname "$0")" || exit; cd .. ; pwd)"
echo "${ROOT_DIR}"
SRC_PATH=${ROOT_DIR}/plugins
rm -rf "${SRC_PATH}" && mkdir "${SRC_PATH}"

## Avien opensearch sink connector
echo "Downloading Opensearch Sink Connector..."
DOWNLOAD_URL=https://github.com/Aiven-Open/opensearch-connector-for-apache-kafka/releases/download/v"${OPENSEARCH_CONNECTOR_VERSION}"/opensearch-connector-for-apache-kafka-"${OPENSEARCH_CONNECTOR_VERSION}".zip

curl -L -o "${SRC_PATH}"/tmp.zip ${DOWNLOAD_URL} \
  && unzip -qq "${SRC_PATH}"/tmp.zip -d "${SRC_PATH}" \
  && rm -rf "${SRC_PATH}"/!(opensearch-connector-for-apache-kafka-"${OPENSEARCH_CONNECTOR_VERSION}"):? \
  && mv "${SRC_PATH}"/opensearch-connector-for-apache-kafka-"${OPENSEARCH_CONNECTOR_VERSION}" "${SRC_PATH}"/opensearch-connector \
  && cd "${SRC_PATH}"/opensearch-connector \
  && mkdir -p "${SRC_PATH}"/opensearch-connector/lib \
  && mv "${SRC_PATH}"/opensearch-connector/*.jar "${SRC_PATH}"/opensearch-connector/lib \
  && mv "${SRC_PATH}"/licenses "${SRC_PATH}"/opensearch-connector/ \
  && mv "${SRC_PATH}"/config "${SRC_PATH}"/opensearch-connector/ \
  && mkdir -p "${SRC_PATH}"/opensearch-connector/doc \
  && mv "${SRC_PATH}"/README.md "${SRC_PATH}"/opensearch-connector/doc \
  && mv "${SRC_PATH}"/LICENSE "${SRC_PATH}"/opensearch-connector/doc \
  && mv "${SRC_PATH}"/version.txt "${SRC_PATH}"/opensearch-connector \
  && zip ../opensearch-connector.zip ./* \
  && rm ../tmp.zip

## Streamthoughts Filepulse source connector
echo -e "\n\n Downloading Filepulse Source Connector..."
DOWNLOAD_URL=https://github.com/streamthoughts/kafka-connect-file-pulse/releases/download/v"${FILEPULSE_CONNECTOR_VERSION}"/streamthoughts-kafka-connect-file-pulse-"${FILEPULSE_CONNECTOR_VERSION}".zip

curl -L -o "${SRC_PATH}"/tmp.zip ${DOWNLOAD_URL} \
  && unzip -qq "${SRC_PATH}"/tmp.zip -d "${SRC_PATH}" \
  && rm -rf "${SRC_PATH}"/!(streamthoughts-kafka-connect-file-pulse-"${FILEPULSE_CONNECTOR_VERSION}"):? \
  && mv "${SRC_PATH}"/streamthoughts-kafka-connect-file-pulse-"${FILEPULSE_CONNECTOR_VERSION}" "${SRC_PATH}"/filepulse-connector \
  && cd "${SRC_PATH}"/filepulse-connector \
  && zip ../filepulse-connector.zip ./* \
  && rm ../tmp.zip