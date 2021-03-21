package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class PrioritizedConverter<T> implements Converter<T> {

    private final Converter<T> converter;

    private final int priority;

    public PrioritizedConverter(Converter<T> converter, int priority) {
        this.converter = converter;
        this.priority = priority;
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return converter.convert(value);
    }

    public Converter<T> getConverter() {
        return converter;
    }

    public int getPriority() {
        return priority;
    }

}
