package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToByteConverter implements Converter<Byte> {

    @Override
    public Byte convert(String value) throws IllegalArgumentException, NullPointerException {
        return Byte.valueOf(value.trim());
    }

}
