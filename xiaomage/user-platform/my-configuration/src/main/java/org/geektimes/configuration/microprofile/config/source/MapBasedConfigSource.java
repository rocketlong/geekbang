package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MapBasedConfigSource implements ConfigSource {

    private final String name;

    private final int ordinal;

    private final Map<String, String> source;

    private final ServletContext servletContext;

    public MapBasedConfigSource(String name, int ordinal, ServletContext servletContext) {
        this.name = name;
        this.ordinal = ordinal;
        this.servletContext = servletContext;
        this.source = getProperties();
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> configData = new HashMap<>();
        try {
            prepareConfigData(configData, servletContext);
        } catch (Throwable cause) {
            throw new IllegalStateException("准备配置数据发生错误", cause);
        }
        return Collections.unmodifiableMap(configData);
    }

    protected abstract void prepareConfigData(Map configData, ServletContext servletContext) throws Throwable;

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return source.get(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }

}
