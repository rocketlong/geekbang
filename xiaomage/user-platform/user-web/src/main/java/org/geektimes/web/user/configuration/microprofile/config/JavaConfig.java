package org.geektimes.web.user.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JavaConfig implements Config {

    private final List<ConfigSource> configSources = new LinkedList<>();

    private final Map<Class<?>, Converter> converterMap = new HashMap<>();

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader.load(ConfigSource.class, classLoader).forEach(configSources::add);
        configSources.sort(Comparator.comparingInt(ConfigSource::getOrdinal).reversed());
        ServiceLoader.load(Converter.class, classLoader).forEach(converter -> {
            Type[] genericInterfaces = converter.getClass().getGenericInterfaces();
            for (Type interfaceType : genericInterfaces) {
                if (ParameterizedType.class.isAssignableFrom(interfaceType.getClass())) {
                    ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    try {
                        converterMap.put(Class.forName(actualTypeArguments[0].getTypeName()), converter);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        if (String.class.isAssignableFrom(propertyType)) {
            return (T) propertyValue;
        } else {
            Converter<T> converter = converterMap.get(propertyType);
            if (converter != null) {
                return converter.convert(propertyValue);
            } else {
                throw new RuntimeException("没有对应的 Converter : " + propertyType);
            }
        }
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
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        Set<String> propertyNames = new HashSet<>();
        configSources.forEach(configSource -> propertyNames.addAll(configSource.getPropertyNames()));
        return propertyNames;
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableCollection(configSources);
    }

    @Override
    public <T> Optional getConverter(Class<T> forType) {
        return Optional.ofNullable(converterMap.get(forType));
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        T value = null;
        try {
            Constructor<T> constructor = type.getConstructor();
            value = constructor.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

}
