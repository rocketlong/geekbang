package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class Converters implements Iterable<Converter> {

    public static final int DEFAULT_PRIORITY = 100;

    private final Map<Class<?>, List<PrioritizedConverter>> typedConverters = new HashMap<>();

    private ClassLoader classLoader;

    private boolean addedDiscoveredConverters = false;

    public Converters(ClassLoader loader) {
        this.classLoader = loader == null ? getClass().getClassLoader() : loader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }
        addConverters(ServiceLoader.load(Converter.class, classLoader));
        addedDiscoveredConverters = true;
    }

    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    public void addConverters(Iterable<Converter> converters) {
        converters.forEach((convert) -> addConverter(convert, DEFAULT_PRIORITY));
    }

    public void addConverter(Converter converter, int priority) {
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        List<PrioritizedConverter> converterList = typedConverters.computeIfAbsent(convertedType, t -> new LinkedList<>());
        converterList.add(new PrioritizedConverter(converter, priority));
        converterList.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
    }

    protected Class<?> resolveConvertedType(Converter<?> converter) {
        assertConverter(converter);
        Class<?> converterType = null;
        Class<?> converterClass = converter.getClass();
        while (converterClass != null) {
            converterType = resolveConvertedType(converterClass);
            if (converterType != null) {
                break;
            }
            Type superType = converterClass.getGenericSuperclass(); // 获取带有泛型的父类
            if (superType instanceof ParameterizedType) {
                converterType = resolveConvertedType(superType);
                if (converterType != null) {
                    break;
                }
            }
            converterClass = converterClass.getSuperclass(); // 获取父类
        }
        return converterType;
    }

    private Class<?> resolveConvertedType(Class<?> converterClass) {
        Class<?> converterType = null;
        for (Type type : converterClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                converterType = resolveConvertedType(type);
                if (converterType != null) {
                    break;
                }
            }
        }
        return converterType;
    }

    private Class<?> resolveConvertedType(Type type) {
        Class<?> converterType = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) pType.getRawType();
            if (Converter.class.isAssignableFrom(rawType)) {
                Type[] typeArguments = pType.getActualTypeArguments();
                converterType = (Class<?>) typeArguments[0];
            }
        }
        return converterType;
    }

    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not be an interface!");
        }
        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not be abstract!");
        }
    }

    public List<Converter> getConverters(Class<?> convertedType) {
        List<PrioritizedConverter> converterList = typedConverters.get(convertedType);
        if (converterList == null || converterList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Converter> converters = new LinkedList<>();
        for (PrioritizedConverter prioritizedConverter : converterList) {
            converters.add(prioritizedConverter.getConverter());
        }
        return converters;
    }

    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new LinkedList<>();
        for (List<PrioritizedConverter> converters : typedConverters.values()) {
            allConverters.addAll(converters);
        }
        return allConverters.iterator();
    }

}
