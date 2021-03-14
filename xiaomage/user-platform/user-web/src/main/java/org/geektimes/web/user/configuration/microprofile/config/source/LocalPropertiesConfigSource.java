package org.geektimes.web.user.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LocalPropertiesConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public LocalPropertiesConfigSource() {
        this.properties = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileUrl = classLoader.getResource("META-INF/microprofile-config.properties");
        Properties localProperties = new Properties();
        try {
            if (fileUrl != null) {
                localProperties.load(fileUrl.openStream());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (String propertyName : localProperties.stringPropertyNames()) {
            properties.put(propertyName, localProperties.getProperty(propertyName));
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "Local Properties";
    }

    @Override
    public int getOrdinal() {
        return 100;
    }

}
