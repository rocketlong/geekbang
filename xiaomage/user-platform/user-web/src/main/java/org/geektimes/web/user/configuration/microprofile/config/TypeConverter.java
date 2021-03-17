package org.geektimes.web.user.configuration.microprofile.config;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class TypeConverter {

    private final Map<Class<?>, Converter<?>> converterMap = new HashMap<>();

    private TypeConverter() {
        ClassLoader classLoader = getClass().getClassLoader();
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

    private static TypeConverter instance = null;

    static {
        instance = new TypeConverter();
    }

    public static TypeConverter getInstance() {
        return instance;
    }

    public Converter<?> getConverter(Class<?> propertyType) {
        return converterMap.get(propertyType);
    }

}
