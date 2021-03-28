package org.geektimes.configuration.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * ServletRequestListener 每个进入 servlet 的请求都会进行初始化
 */
@WebListener
public class ServletRequestConfigListener implements ServletRequestListener {

    public static final ThreadLocal<Config> configThreadLocal = new ThreadLocal<>();

    public static Config getConfig() {
        return configThreadLocal.get();
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletRequest request = sre.getServletRequest();
        ServletContext servletContext = request.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        Config config = ConfigProviderResolver.instance().getConfig(classLoader);
        configThreadLocal.set(config);
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        configThreadLocal.remove();
    }

}
