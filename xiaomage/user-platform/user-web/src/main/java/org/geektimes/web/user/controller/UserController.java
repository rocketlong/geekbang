package org.geektimes.web.user.controller;

import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.repository.DatabaseUserRepository;
import org.geektimes.web.user.repository.UserRepository;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/user")
public class UserController implements PageController {

    private UserRepository userRepository;

    public UserController() {
        userRepository = new DatabaseUserRepository();
    }

    @GET
    @Path("/getUserById")
    public String getUserById(Long id) {
        User user = userRepository.getById(id);
        System.out.println(user);
        return "index.jsp";
    }

    @GET
    @Path("/getUserAll")
    public String getUserAll() {
        List<User> users = (List<User>) userRepository.getAll();
        System.out.println(users);
        return "index.jsp";
    }

    @POST
    @Path("/register")
    public String register(User user) {
        userRepository.save(user);
        return "index.jsp";
    }

}
