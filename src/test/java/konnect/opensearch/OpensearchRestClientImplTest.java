package konnect.opensearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import konnect.config.AppConfig;
import konnect.config.ConfigReader;
import nl.altindag.log.LogCaptor;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.junit.Before;
import org.junit.Test;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class OpensearchRestClientImplTest {

    private OpensearchRestClientImpl opensearchRestClient;
    private OpensearchRestClientImpl opensearchRestClientSpy;
    private RestClient restClient;
    private Map<String, String> idEventMap;

    private static final List<String> CDC_EVENTS = Arrays.asList(
         "{\"before\": null, \"after\": {\"key\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/o/store_event/last_update\", " +
           "\"value\": {\"type\": 3, \"object\": {\"id\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/last_update\", " +
           "\"value\": \"9c341d3d-1a20-4d72-b95c-3dddb08c6602\", \"resource_type\": \"key\"}}}, \"op\": \"u\", " +
           "\"ts_ms\": 1706812604828}",
         "{\"before\": null, \"after\": {\"key\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/o/store_event/last_update\", " +
           "\"value\": {\"type\": 3, \"object\": {\"id\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/last_update\"," +
           " \"value\": \"eaffa699-f3ab-46e1-8eed-d9f1e194ffc4\", \"resource_type\": \"route\"}}}, \"op\": \"u\", " +
           "\"ts_ms\": 1706812644903");

    @Before
    public void mockSetup() {
        AppConfig appConfig = new AppConfig(new ConfigReader());
        opensearchRestClient = new OpensearchRestClientImpl(appConfig);
        restClient = mock(RestClient.class);
        idEventMap = mock(Map.class);
        ReflectionTestUtils.setField(opensearchRestClient, "restClient", restClient);
        ReflectionTestUtils.setField(opensearchRestClient, "idEventMap", idEventMap);
        opensearchRestClientSpy = spy(opensearchRestClient);
    }

    @Test
    public void testBuildBulkRequestBody() {
        when(opensearchRestClientSpy.generateUuid()).thenReturn("1").thenReturn("2");
        String actualStr = opensearchRestClientSpy.buildBulkRequestBody(CDC_EVENTS);
        InputStream is = OpensearchRestClientImplTest.class.getClassLoader().getResourceAsStream("json/opensearch/bulkRequestBody.jsonl");

        assertNotNull(is);

        String expectedStr = new BufferedReader(
                new InputStreamReader(is))
                .lines()
                .parallel()
                .collect(Collectors.joining("\n"));
        assertEquals(expectedStr.trim(), actualStr.trim());
    }

    @Test
    public void testExtractResponse() {
        InputStream is = OpensearchRestClientImplTest.class.getClassLoader().getResourceAsStream("json/opensearch/opensearch_response_entity.json");
        String expectedRespEntity = new BufferedReader(
                new InputStreamReader(is))
                .lines()
                .parallel()
                .collect(Collectors.joining());
        doReturn(expectedRespEntity).when(opensearchRestClientSpy).extractResponse(any());
        Response resp = mock(Response.class);

        String actualRespEntity = opensearchRestClientSpy.extractResponse(resp);

        assertEquals(expectedRespEntity.trim(), actualRespEntity.trim());
    }

    @Test
    public void testExtractResponseException() {
        LogCaptor logCaptor = LogCaptor.forClass(OpensearchRestClientImpl.class);
        Exception exception = null;

        try {
            Response resp = mock(Response.class);
            HttpEntity entity = mock(HttpEntity.class);
            when(resp.getEntity()).thenReturn(entity);
            when(resp.getEntity().getContent()).then(i -> {
                throw new IOException("Test Exception");
            });
            opensearchRestClientSpy.extractResponse(resp);
        } catch (final Exception ex) {
            exception = ex;
        }

        assertNull(exception);
        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Error occurred, while extracting HTTP response"));
    }

    @Test
    public void testBuildPostRequest() throws IOException {
        String requestBody = "{\"before\": null, \"after\": " +
                "{\"key\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/o/store_event/last_update\", " +
                "\"value\": {\"type\": 3, \"object\": {\"id\": \"c/04397908-e846-4019-aeaa-2422a1cb7b6c/last_update\", " +
                "\"value\": \"9c341d3d-1a20-4d72-b95c-3dddb08c6602\", \"resource_type\": \"key\"}}}, \"op\": \"u\", " +
                "\"ts_ms\": 1706812604828}";
        String requestUrl = "http://localhost:9200/cdc-events/_doc/";

        Request request = opensearchRestClientSpy.buildPostRequest(requestUrl, requestBody);
        assertNotNull(request);
        assertEquals(request.getEndpoint().trim(), requestUrl);

        String actualRequestBody = new BufferedReader(
                new InputStreamReader(request.getEntity().getContent()))
                .lines()
                .parallel()
                .collect(Collectors.joining());
        assertEquals(requestBody.trim(), actualRequestBody.trim());
    }

    @Test
    public void testGetFailedDocuments() {
        String response = "{ \"took\": 11, \"errors\": true, \"items\": [ { \"create\": { \"_index\": \"movies\"," +
                "\"_id\": \"tt1392214\", \"status\": 409, \"error\": { \"type\": \"version_conflict_engine_exception\"," +
                "\"reason\": \"[tt1392214]: version conflict, document already exists (current version [1])\", " +
                "\"index\": \"movies\", \"shard\": \"0\", \"index_uuid\": \"yhizhusbSWmP0G7OJnmcLg\" } } } ] }";

        when(idEventMap.get(anyString())).thenReturn("test doc");
        List<String> ids = opensearchRestClientSpy.getFailedDocuments(response);
        assertNotNull(ids);
        assertTrue(ids.size() == 1);
        assertEquals(ids.get(0), "test doc");
    }

    @Test
    public void testExecuteRequest() throws IOException {
        LogCaptor logCaptor = LogCaptor.forClass(OpensearchRestClientImpl.class);

        Response response = mock(Response.class);
        StatusLine statusLine = mock(StatusLine.class);
        doReturn(statusLine).when(response).getStatusLine();
        doReturn(200).when(statusLine).getStatusCode();

        doReturn(response).when(restClient).performRequest(any(Request.class));

        InputStream is = OpensearchRestClientImplTest.class.getClassLoader().getResourceAsStream("json/opensearch/opensearch_response_entity.json");
        String respEntity = new BufferedReader(
                new InputStreamReader(is))
                .lines()
                .parallel()
                .collect(Collectors.joining());
        doReturn(respEntity).when(opensearchRestClientSpy).extractResponse(any());

        opensearchRestClientSpy.executeRequest(new Request("POST", "http://testendpoint.com"));

        assertTrue(logCaptor
                .getInfoLogs()
                .get(logCaptor.getInfoLogs().size() - 1)
                .contains("httpResponse body: {  \"took\": 76,  \"errors\": false"));

        assertTrue(logCaptor
                .getInfoLogs()
                .get(logCaptor.getInfoLogs().size() - 1)
                .contains("httpResponseCode: 200"));

    }

    @Test
    public void testExecuteRequestException() {
        LogCaptor logCaptor = LogCaptor.forClass(OpensearchRestClientImpl.class);

        Response response = mock(Response.class);
        StatusLine statusLine = mock(StatusLine.class);
        doReturn(statusLine).when(response).getStatusLine();
        doReturn(200).when(statusLine).getStatusCode();

        Exception exception = null;
        try {
            when(restClient.performRequest(any(Request.class))).then(i -> {
                throw new IOException("Test Exception");
            });
        } catch (final Exception ex) {
            exception = ex;
        }

        opensearchRestClientSpy.executeRequest(new Request("POST", "http://testendpoint.com"));
        assertNull(exception);
        assertTrue(logCaptor
                .getErrorLogs()
                .get(logCaptor.getErrorLogs().size() - 1)
                .contains("Error occurred while trying to publish the request"));

    }

}
