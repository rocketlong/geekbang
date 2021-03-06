package org.geektimes.web.user.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.NoSuchElementException;

/**
 * 组件上下文（Web 应用全局使用）
 */
public class ComponentContext {

    public static final String CONTEXT_NAME = ComponentContext.class.getName();

    // 注意：
    // 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会）
    // static 字段是 JVM 缓存吗？（是 ClassLoader 缓存，属于 AppClassLoader，属于同一级别）
    private static ServletContext servletContext;

    private Context context;

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(ComponentContext.CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) {
        try {
            this.context = (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        servletContext.setAttribute(ComponentContext.CONTEXT_NAME, this);
        ComponentContext.servletContext = servletContext;
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
     * 通过名称进行依赖查找
     *
     * @param name
     * @param <C>
     * @return
     */
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
