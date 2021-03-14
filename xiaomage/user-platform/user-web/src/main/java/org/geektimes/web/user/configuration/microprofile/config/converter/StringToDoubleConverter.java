package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToDoubleConverter implements Converter<Double> {
    @Override
    public Double convert(String value) throws IllegalArgumentException, NullPointerException {
        return Double.valueOf(value.trim());
    }
}
