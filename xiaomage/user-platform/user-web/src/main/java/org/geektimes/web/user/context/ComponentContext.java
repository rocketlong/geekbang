package org.geektimes.web.user.context;

import org.geektimes.web.user.function.ThrowableAction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ComponentContext {

    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    public static final Logger logger = Logger.getLogger(CONTEXT_NAME);

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

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

    }

    public <C> C getComponent(String name) {
        C component = null;
        try {
            component = (C) this.context.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchElementException(name);
        }
        return component;
    }

}
