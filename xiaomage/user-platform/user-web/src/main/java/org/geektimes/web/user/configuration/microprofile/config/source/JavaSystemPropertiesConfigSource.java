package org.geektimes.web.user.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JavaSystemPropertiesConfigSource implements ConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行时不去调整
     * -Dapplication.name=user-web
     */
    private final Map<String, String> properties;

    public JavaSystemPropertiesConfigSource() {
        this.properties = new HashMap<>();
        for (String propertyName : System.getProperties().stringPropertyNames()) {
            properties.put(propertyName, System.getProperties().getProperty(propertyName));
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
        return "Java System Properties";
    }

    @Override
    public int getOrdinal() {
        return 400;
    }
}
