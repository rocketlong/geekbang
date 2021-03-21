package org.geektimes.configuration.microprofile.config.source.def;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultResourceConfigSource extends MapBasedConfigSource {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static final String configFileLocation = "META-INF/microprofile-config.properties";

    public DefaultResourceConfigSource() {
        super("Default Config File", 100, null);
    }

    @Override
    protected void prepareConfigData(Map configData, ServletContext servletContext) throws Throwable {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(configFileLocation);
        if (resource == null) {
            logger.info("The default config file can't be found in the classpath : " + configFileLocation);
            return;
        }
        try (InputStream inputStream = resource.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            configData.putAll(properties);
        }
    }
}
