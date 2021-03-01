package org.geektimes.web.user.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class HelloWorldController implements PageController {

    @GET
    @Path("/world")
    public String helloWorld() {
        return "index.jsp";
    }

}
