package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToLongConverter implements Converter<Long> {

    @Override
    public Long convert(String value) throws IllegalArgumentException, NullPointerException {
        return Long.valueOf(value.trim());
    }

}
