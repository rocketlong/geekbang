package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

import java.math.BigDecimal;

public class StringToBigDecimalConverter extends AbstractConverter<BigDecimal> {

    @Override
    protected BigDecimal doConvert(String value) {
        return new BigDecimal(value.trim());
    }

}
