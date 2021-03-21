package org.geektimes.configuration.microprofile.config.converter.impl;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToStringConverter implements Converter<String> {

    @Override
    public String convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value == null) {
            // 可以返回 null，或者抛出空指针
            throw new NullPointerException("The value must not be null!");
        }
        return value;
    }

}
