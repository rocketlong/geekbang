package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.HashMap;

public class RestClientDemo {

    public static void main(String[] args) {
        System.out.println(get());
    }

    public static String get() {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")  // WebTarget
                .request()                                        // Invocation.Builder
                .get();                                           // Response
        return response.readEntity(String.class);
    }

}
