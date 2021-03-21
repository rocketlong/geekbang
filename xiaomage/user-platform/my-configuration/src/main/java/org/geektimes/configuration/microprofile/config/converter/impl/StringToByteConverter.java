package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringToByteConverter extends AbstractConverter<Byte> {

    @Override
    protected Byte doConvert(String value) {
        return Byte.valueOf(value.trim());
    }

}
