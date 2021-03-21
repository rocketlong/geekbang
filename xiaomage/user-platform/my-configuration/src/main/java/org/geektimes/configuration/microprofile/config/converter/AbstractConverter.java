package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public abstract class AbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value == null) {
            // 可以返回 null，或者抛出空指针
            throw new NullPointerException("The value must not be null!");
        }
        return doConvert(value);
    }

    protected abstract T doConvert(String value);

}
