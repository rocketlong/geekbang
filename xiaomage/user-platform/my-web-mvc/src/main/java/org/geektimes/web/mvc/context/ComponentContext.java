package org.geektimes.web.mvc.context;

import org.geektimes.web.mvc.function.ThrowableAction;
import org.geektimes.web.mvc.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ComponentContext {

    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    public static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private static final Map<String, Object> componentsMap = new LinkedHashMap<>();

    // 注意：
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存，属于 AppClassLoader，属于同一级别）
    private static ServletContext servletContext;

    private Context context;

    private ClassLoader classLoader;

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }

    public void init(ServletContext servletContext) {
        ComponentContext.servletContext = servletContext;
        servletContext.setAttribute(CONTEXT_NAME, this);
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
        initEnvContext(); // 初始化 context
        instantiateComponents(); // 实例化组件 相当于 new
        initializeComponents(); // 初始化组件 相当于 setter
    }

    public void destroy() {
        if (this.context != null) {
            try {
                this.context.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 初始化 context
     *
     * @throws RuntimeException
     */
    private void initEnvContext() throws RuntimeException {
        if (this.context != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.context = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }

    /**
     * 实例化组件
     */
    protected void instantiateComponents() {
        // 获取所有组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找实例化对象（Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> componentsMap.put(name, lookupComponent(name)));
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     */
    protected void initializeComponents() {
        componentsMap.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(component, componentClass);
            // 初始阶段 - {@link PostConstruct}
            processPostConstruct(component, componentClass);
            // 销毁阶段 - {@link PreDestroy}
            processPreDestroy(component, componentClass);
        });
    }

    private void injectComponents(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()) &&
                        field.isAnnotationPresent(Resource.class))
                .forEach(field -> {
                    Resource resource = field.getAnnotation(Resource.class);
                    String resourceName = resource.name();
                    Object injectObject = lookupComponent(resourceName);
                    field.setAccessible(true);
                    try {
                        field.set(component, injectObject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void processPostConstruct(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()) &&
                        method.getParameterCount() == 0 &&
                        method.isAnnotationPresent(PostConstruct.class)
                ).forEach(method -> {
            try {
                method.invoke(component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void processPreDestroy(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()) &&
                        method.getParameterCount() == 0 &&
                        method.isAnnotationPresent(PreDestroy.class)
                ).forEach(method -> Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                method.invoke(component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })));
    }

    /**
     * 内部查找
     *
     * @param name
     * @param <C>
     * @return
     */
    protected <C> C lookupComponent(String name) {
        return (C) executeInContext(this.context, context -> context.lookup(name), false);
    }

    /**
     * 外部查找
     *
     * @param name
     * @param <C>
     * @return
     */
    public static  <C> C getComponent(String name) {
        return (C) componentsMap.get(name);
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    protected List<String> listComponentNames(String name) {
        return executeInContext(this.context, context -> {
            NamingEnumeration<NameClassPair> enumeration = executeInContext(context, ctx -> ctx.list(name), true);
            if (enumeration == null) { // 当前 JNDI 名称下没有子节点
                return Collections.emptyList();
            }
            List<String> fullNames = new LinkedList<>();
            while (enumeration.hasMoreElements()) {
                NameClassPair element = enumeration.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) { // 目录 - Context
                    fullNames.addAll(listComponentNames(element.getName()));
                } else { // 节点
                    fullNames.add(name.startsWith("/") ? element.getName() : name + "/" + element.getName());
                }
            }
            return fullNames;
        }, false);
    }

    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function, boolean ignoredException) {
        R result = null;
        try {
            result = function.apply(context);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

}
