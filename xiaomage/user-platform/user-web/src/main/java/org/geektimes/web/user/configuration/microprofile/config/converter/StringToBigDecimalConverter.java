package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.math.BigDecimal;

public class StringToBigDecimalConverter implements Converter<BigDecimal> {

    @Override
    public BigDecimal convert(String value) throws IllegalArgumentException, NullPointerException {
        return new BigDecimal(value.trim());
    }

}
