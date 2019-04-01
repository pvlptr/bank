package engineering.pvl.bank;

import com.google.gson.Gson;
import engineering.pvl.bank.utils.RestApiTestServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;

import static engineering.pvl.bank.utils.RestAssertions.assertResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;

// only basic testing
// details are tested in test for concrete REST api servers
class RestApiServerIT {

    private RestApiTestServer testServer;
    private Gson gson;

    @BeforeEach
    void setUp() {
        ServiceRegistry serviceRegistry = new ServiceRegistry();

        testServer = new RestApiTestServer(serviceRegistry, "");
        testServer.start();

        this.gson = serviceRegistry.getGson();
    }

    @AfterEach
    void tearDown() {
        testServer.stop();
    }

    @Test
    void testMainUrls() {
        HttpResponse<String> response = testServer.get("transactions");
        assertEquals(200, response.statusCode());

        response = testServer.get("accounts");
        assertEquals(200, response.statusCode());
    }

    @Test
    void testMainUrls_notExisting() {
        HttpResponse<String> response = testServer.get("not-existing");
        assertResponse(gson, 404, new RestApiServer.ErrorResponse("Resource not found"), response);

        response = testServer.get("accounts-2");
        assertResponse(gson, 404, new RestApiServer.ErrorResponse("Resource not found"), response);

        response = testServer.post("some object");
        assertResponse(gson, 404, new RestApiServer.ErrorResponse("Resource not found"), response);
    }
}