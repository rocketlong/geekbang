package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.microprofile.config.converter.Converters;
import org.geektimes.configuration.microprofile.config.source.ConfigSources;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public class DefaultConfig implements Config {

    private final ConfigSources configSources;

    private final Converters converters;

    public DefaultConfig(ConfigSources configSources, Converters converters) {
        this.configSources = configSources;
        this.converters = converters;
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        Converter<T> converter = doGetConverter(propertyType);
        return converter.convert(propertyValue);
    }

    protected String getPropertyValue(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        return propertyValue;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        String propertyValue = null;
        ConfigSource configSource = null;
        for (ConfigSource source : configSources) {
            propertyValue = source.getValue(propertyName);
            if (propertyValue != null) {
                configSource = source;
                break;
            }
        }
        if (propertyValue == null) {
            return null;
        }
        return new DefaultConfigValue(propertyName, propertyValue, null, configSource.getName(), configSource.getOrdinal());
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return StreamSupport.stream(configSources.spliterator(), false)
                .map(ConfigSource::getPropertyNames)
                .collect(LinkedHashSet::new, Set::addAll, Set::addAll);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSources;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter<T> converter = doGetConverter(forType);
        return converter == null ? Optional.empty() : Optional.of(converter);
    }

    protected <T> Converter<T> doGetConverter(Class<T> forType) {
        List<Converter> converters = this.converters.getConverters(forType);
        return converters.isEmpty() ? null : converters.get(0);
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }
}
