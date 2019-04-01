package engineering.pvl.bank.utils;

import com.google.gson.Gson;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestAssertions {

    private RestAssertions() {
    }

    static public void assertResponse(Gson gson, int expectedCode, Object expectedBody, HttpResponse<String> actualResponse) {
        String expectedJsonBody = expectedBody != null ? gson.toJson(expectedBody) : "";

        assertEquals(expectedCode, actualResponse.statusCode());
        assertEquals(expectedJsonBody, actualResponse.body());
        assertEquals("application/json", actualResponse.headers().firstValue("content-type").orElse(""));
    }
}
