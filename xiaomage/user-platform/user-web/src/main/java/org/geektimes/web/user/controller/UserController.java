package org.geektimes.web.user.controller;

import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.repository.DatabaseUserRepository;
import org.geektimes.web.user.repository.UserRepository;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/user")
public class UserController implements PageController {

    private UserRepository userRepository;

    public UserController() {
        userRepository = new DatabaseUserRepository();
    }

    @GET
    @Path("/getUserById")
    public String getUserById() {
        return "index.jsp";
    }

    @POST
    @Path("/register")
    public String register() {
        User user = new User();
        user.setName("测试");
        user.setPassword("123456");
        user.setEmail("123456@qq.com");
        user.setPhoneNumber("12345678912");
        userRepository.save(user);
        return "index.jsp";
    }

}
