package org.geektimes.configuration.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.source.servlet.ServletContextConfigSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * ServletContextListener 容器（servlet）启动时，进行初始化
 */
@WebListener
public class ServletContextConfigInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        // 配置 classLoader
        configBuilder.forClassLoader(classLoader);
        // 配置默认源（静态的）
        configBuilder.addDefaultSources();
        // 配置动态源（SPI扩展）
        configBuilder.addDiscoveredSources();
        // 配置扩展源（基于 Servlet 引擎）
        configBuilder.withSources(new ServletContextConfigSource(servletContext));
        // 配置转换器 Converter
        configBuilder.addDiscoveredConverters();
        // 构建 config
        Config config = configBuilder.build();
        // 注册 config 关联的 classLoader
        configProviderResolver.registerConfig(config, classLoader);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
