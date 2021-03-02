package org.geektimes.web.mvc;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.geektimes.web.mvc.controller.Controller;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.mvc.controller.RestController;
import sun.misc.IOUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class FrontControllerServlet extends HttpServlet {

    /**
     * 请求路径 和 {@link HandlerMethodInfo} 隐射关系
     */
    private final Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    /**
     * 初始化 Servlet
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        initHandlerMethods();
    }

    /**
     * 获取所有 Controller 元信息
     */
    private void initHandlerMethods() {
        // Java SPI
        ServiceLoader<Controller> controllers = ServiceLoader.load(Controller.class);
        for (Controller controller : controllers) {
            Class<?> controllersClass = controller.getClass();
            Path pathClass = controllersClass.getAnnotation(Path.class);
            String requestPath = pathClass.value();
            if (!requestPath.startsWith("/")) {
                requestPath = "/" + requestPath;
            }
            Method[] publicMethods = controllersClass.getDeclaredMethods();
            for (Method method : publicMethods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path pathMethod = method.getAnnotation(Path.class);
                if (pathMethod != null) {
                    String pathMethodValue = pathMethod.value();
                    if (!pathMethodValue.startsWith("/")) {
                        pathMethodValue = "/" + pathMethodValue;
                    }
                    String path = requestPath + pathMethodValue;
                    handleMethodInfoMapping.put(path, new HandlerMethodInfo(path, method, supportedHttpMethods, controller));
                }
            }
        }
    }

    /**
     * 获取处理方法中标注的HTTP方法集合
     *
     * @param method
     * @return
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotation : method.getAnnotations()) {
            HttpMethod httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }
        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(Arrays.asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }
        return supportedHttpMethods;
    }

    /**
     * service
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletContextPath = req.getContextPath();
        String requestURI = req.getRequestURI();
        String requestMappingPath = StringUtils.substringAfter(requestURI,
                StringUtils.replace(servletContextPath, "//", "/"));
        HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);
        if (handlerMethodInfo != null) {
            try {
                String httpMethod = req.getMethod();
                if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                    resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    return;
                }
                Controller controller = handlerMethodInfo.getHandlerController();
                if (controller instanceof PageController) {
                    String viewPath = handlePageController(req, handlerMethodInfo);
                    if (!viewPath.startsWith("/")) {
                        viewPath = "/" + viewPath;
                    }
                    ServletContext servletContext = req.getServletContext();
                    RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                    requestDispatcher.forward(req, resp);
                } else if (controller instanceof RestController) {
                    // TODO
                }
            } catch (Throwable e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                } else {
                    throw new ServletException(e.getCause());
                }
            }
        }
    }

    private String handlePageController(HttpServletRequest req, HandlerMethodInfo handlerMethodInfo) throws Exception {
        Controller controller = handlerMethodInfo.getHandlerController();
        Method method = handlerMethodInfo.getHandlerMethod();
        String httpMethod = req.getMethod();
        String pathValue = "";
        if (httpMethod.equals(HttpMethod.GET)) {
            int count = method.getParameterCount();
            if (count > 0) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Parameter[] parameters = method.getParameters();
                Object[] objects = new Object[count];
                Map<String, String[]> parameterMap = req.getParameterMap();
                for (int i = 0; i < method.getParameterCount(); i++) {
                    String parameter = req.getParameter(parameters[i].getName());
                    if (StringUtils.isBlank(parameter)) {
                        objects[i] = null;
                    } else {
                        Class<?> classType = Class.forName(paramTypes[i].getName());
                        objects[i] = getVal(parameter,classType);
                    }
                }
                pathValue = (String) method.invoke(controller, objects);
            } else {
                pathValue = (String) method.invoke(controller);
            }
        } else if (httpMethod.equals(HttpMethod.POST)) {
            String body = req.getReader().lines().collect(Collectors.joining());
            JSONObject jsonObject = JSONObject.parseObject(body);
            int count = method.getParameterCount();
            if (count > 0) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Parameter[] parameters = method.getParameters();
                Object[] objects = new Object[count];
                for (int i = 0; i < method.getParameterCount(); i++) {
                    String parameter = jsonObject.getString(parameters[i].getName());
                    if (StringUtils.isBlank(parameter)) {
                        objects[i] = null;
                    } else {
                        Class<?> classType = Class.forName(paramTypes[i].getName());
                        objects[i] = JSONObject.parseObject(parameter,classType);
                    }
                }
                pathValue = (String) method.invoke(controller, objects);
            } else {
                pathValue = (String) method.invoke(controller);
            }
        }
        return pathValue;
    }

    public <T> T getVal(String val, Class<T> type) {
        T value = null;
        try {
            Constructor<T> constructor = type.getConstructor(String.class);
            constructor.setAccessible(true);
            value = constructor.newInstance(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
