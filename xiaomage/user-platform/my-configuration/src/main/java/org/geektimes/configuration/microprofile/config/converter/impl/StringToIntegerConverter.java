package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringToIntegerConverter extends AbstractConverter<Integer> {

    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value.trim());
    }

}
