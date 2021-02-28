package org.geektimes.web.mvc;

import org.geektimes.web.mvc.controller.Controller;

import java.lang.reflect.Method;
import java.util.Set;

public class HandlerMethodInfo {

    private final String requestPath;

    private final Method handlerMethod;

    private final Set<String> supportedHttpMethods;

    private final Controller handlerController;

    public HandlerMethodInfo(String requestPath, Method handlerMethod, Set<String> supportedHttpMethods, Controller handlerController) {
        this.requestPath = requestPath;
        this.handlerMethod = handlerMethod;
        this.supportedHttpMethods = supportedHttpMethods;
        this.handlerController = handlerController;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public Set<String> getSupportedHttpMethods() {
        return supportedHttpMethods;
    }

    public Controller getHandlerController() {
        return handlerController;
    }

}
