package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringToBooleanConverter extends AbstractConverter<Boolean> {

    @Override
    protected Boolean doConvert(String value) {
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
