package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringToLongConverter extends AbstractConverter<Long> {

    @Override
    protected Long doConvert(String value) {
        return Long.valueOf(value.trim());
    }

}
