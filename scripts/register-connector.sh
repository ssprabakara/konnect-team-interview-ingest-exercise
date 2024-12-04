#!/bin/bash

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/connectors/ \
-d "@properties/opensearch_sink_connector.json"

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8083/connectors/ \
-d "@properties/filepulse_source_connector.json"