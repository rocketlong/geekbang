package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToIntegerConverter implements Converter<Integer> {

    @Override
    public Integer convert(String value) throws IllegalArgumentException, NullPointerException {
        return Integer.valueOf(value.trim());
    }

}
