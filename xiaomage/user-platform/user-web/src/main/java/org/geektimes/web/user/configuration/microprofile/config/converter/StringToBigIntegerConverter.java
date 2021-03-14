package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.math.BigInteger;

public class StringToBigIntegerConverter implements Converter<BigInteger> {

    @Override
    public BigInteger convert(String value) throws IllegalArgumentException, NullPointerException {
        return new BigInteger(value.trim());
    }

}
