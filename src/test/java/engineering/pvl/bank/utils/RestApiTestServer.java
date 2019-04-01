package engineering.pvl.bank.utils;

import engineering.pvl.bank.RestApiServer;
import engineering.pvl.bank.ServiceRegistry;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RestApiTestServer {

    private RestApiServer server;
    private final ServiceRegistry serviceRegistry;
    private final String baseUri;
    private final HttpClient client;

    public RestApiTestServer(ServiceRegistry serviceRegistry, String baseUri) {
        this.serviceRegistry = serviceRegistry;
        this.baseUri = baseUri;
        client = HttpClient.newBuilder().build();
    }

    public void start() {
        server = new RestApiServer(serviceRegistry);
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public HttpResponse<String> get() {
        return get("");
    }

    public HttpResponse<String> get(String uriEnd) {
        HttpRequest request = newBaseBuilder(uriEnd).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public HttpResponse<String> post(Object postBodyObject) {
        return post("", postBodyObject);
    }

    public HttpResponse<String> post(String uriEnd, Object postBodyObject) {
        HttpRequest request = newBaseBuilder(uriEnd)
                .POST(HttpRequest.BodyPublishers.ofString(serviceRegistry.getGson().toJson(postBodyObject))).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest.Builder newBaseBuilder(String uriEnd) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" +
                        RestApiServer.SERVER_PORT + "/" +
                        RestApiServer.API_PREFIX + "/" +
                        RestApiServer.API_VERSION + "/"
                        + baseUri + uriEnd))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json");
    }

}
