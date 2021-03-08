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
        return "success.jsp";
    }

    @GET
    @Path("/getUserAll")
    public String getUserAll() {
        List<User> users = (List<User>) userRepository.getAll();
        System.out.println(users);
        return "success.jsp";
    }

    @POST
    @Path("/register")
    public String register(String name, String password, String email, String phoneNumber) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        return "success.jsp";
    }

    @POST
    @Path("/login")
    public String login(String email, String password) {
        User user = userRepository.getByEmail(email);
        if (user == null || !password.equals(user.getPassword())) {
            return "error.jsp";
        }
        return "success.jsp";
    }

}
