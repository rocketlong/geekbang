package org.geektimes.web.user.controller;

import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.repository.DatabaseUserRepository;
import org.geektimes.web.user.repository.UserRepository;
import org.geektimes.web.user.service.UserService;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

@Path("/user")
public class UserController implements PageController {

    @Resource(name = "bean/UserServiceImpl")
    private UserService userService;

    @GET
    @Path("/getUserById")
    public String getUserById(Long id) {
        User user = userService.getById(id);
        System.out.println(user);
        return "success.jsp";
    }

    @GET
    @Path("/getUserAll")
    public String getUserAll() {
        List<User> users = (List<User>) userService.getAll();
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
        userService.save(user);
        return "success.jsp";
    }

    @POST
    @Path("/login")
    public String login(String email, String password) {
        User user = userService.getByEmail(email);
        if (user == null || !password.equals(user.getPassword())) {
            return "error.jsp";
        }
        return "success.jsp";
    }

}
