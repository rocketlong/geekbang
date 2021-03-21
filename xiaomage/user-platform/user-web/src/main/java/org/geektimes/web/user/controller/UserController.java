package org.geektimes.web.user.controller;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.user.domain.User;
import org.geektimes.web.user.service.UserService;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;
import java.util.logging.Logger;

@Path("/user")
public class UserController implements PageController {

    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    @Resource(name = "bean/UserServiceImpl")
    private UserService userService;

    @GET
    @Path("/getUserById")
    public String getUserById(Long id) {
        User user = userService.getById(id);
        logger.info(user.toString());
        return "success.jsp";
    }

    @GET
    @Path("/getUserAll")
    public String getUserAll() {
        List<User> users = (List<User>) userService.getAll();
        logger.info(users.toString());
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

    @GET
    @Path("/getConfigPropertyByName")
    public String getConfigPropertyByName(String name) {
        Config config = ConfigProvider.getConfig();
        String value = config.getValue(name, String.class);
        System.out.println(value);
        return "success.jsp";
    }

}
