package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultConfigProviderResolver extends ConfigProviderResolver {

    private final Map<ClassLoader, Config> configsRepository = new ConcurrentHashMap<>();

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader loader) {
        return configsRepository.computeIfAbsent(resolveClassLoader(loader), this::newConfig);
    }

    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }

    protected Config newConfig(ClassLoader classLoader) {
        return new DefaultConfigBuilder(classLoader).build();
    }

    @Override
    public ConfigBuilder getBuilder() {
        return new DefaultConfigBuilder(resolveClassLoader(null));
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configsRepository.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        List<ClassLoader> removeList = new LinkedList<>();
        for (Map.Entry<ClassLoader, Config> entry : configsRepository.entrySet()) {
            if (Objects.equals(config, entry.getValue())) {
                removeList.add(entry.getKey());
            }
        }
        removeList.forEach(configsRepository::remove);
    }

}
