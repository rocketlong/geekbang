package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToBooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean convert(String value) throws IllegalArgumentException, NullPointerException {
        String v = value.trim();
        if (v.isEmpty()) {
            return null;
        }
        v = v.toLowerCase();
        if ("true".equals(v)) {
            return Boolean.TRUE;
        } else if ("false".equals(v)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + value + "'");
        }
    }

}
