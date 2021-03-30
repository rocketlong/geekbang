package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class RestClientDemo {

    public static void main(String[] args) {
//        System.out.println(get());
        System.out.println(post());
    }

    public static String get() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")  // WebTarget
                .request()                                        // Invocation.Builder
                .get();                                           // Response
        return response.readEntity(String.class);
    }

    public static Map post() {
        Client client = ClientBuilder.newClient();
        Map<String, Object> body = new HashMap<>();
        body.put("a", 1);
        body.put("b", 2);
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")  // WebTarget
                .request()                                        // Invocation.Builder
                .post(Entity.json(body));                         // Response
        return response.readEntity(Map.class);
    }

}
