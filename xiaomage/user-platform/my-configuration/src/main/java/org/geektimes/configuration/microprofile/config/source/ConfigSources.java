package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.configuration.microprofile.config.source.def.DefaultResourceConfigSource;
import org.geektimes.configuration.microprofile.config.source.def.JavaSystemPropertiesConfigSource;
import org.geektimes.configuration.microprofile.config.source.def.OperationSystemEnvironmentVariablesConfigSource;

import java.util.*;
import java.util.stream.Stream;

public class ConfigSources implements Iterable<ConfigSource> {

    private boolean addedDefaultConfigSources;

    private boolean addedDiscoveredConfigSources;

    private final List<ConfigSource> configSources = new LinkedList<>();

    private ClassLoader classLoader;

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDefaultSources() {
        if (addedDefaultConfigSources) {
            return;
        }
        addConfigSources(
                JavaSystemPropertiesConfigSource.class,
                OperationSystemEnvironmentVariablesConfigSource.class,
                DefaultResourceConfigSource.class
        );
        addedDefaultConfigSources = true;
    }

    public void addDiscoveredSources() {
        if (addedDiscoveredConfigSources) {
            return;
        }
        addConfigSources(ServiceLoader.load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }

    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        ConfigSource[] configSources = Stream.of(configSourceClasses)
                .map(this::newInstance)
                .toArray(ConfigSource[]::new);
        addConfigSources(configSources);
    }

    public void addConfigSources(ConfigSource... configSourceClasses) {
        addConfigSources(Arrays.asList(configSourceClasses));
    }

    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        this.configSources.sort(Comparator.comparingInt(ConfigSource::getOrdinal).reversed());
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    @Override
    public Iterator<ConfigSource> iterator() {
        return configSources.iterator();
    }

}
