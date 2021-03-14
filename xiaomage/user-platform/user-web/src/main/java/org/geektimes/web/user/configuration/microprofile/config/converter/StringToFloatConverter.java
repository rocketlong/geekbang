package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToFloatConverter implements Converter<Float> {

    @Override
    public Float convert(String value) throws IllegalArgumentException, NullPointerException {
        return Float.valueOf(value.trim());
    }

}
