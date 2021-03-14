package org.geektimes.web.user.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OSEnvPropertiesConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public OSEnvPropertiesConfigSource() {
        this.properties = new HashMap<>(System.getenv());
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
        return "OS Env Properties";
    }

    @Override
    public int getOrdinal() {
        return 300;
    }

}
