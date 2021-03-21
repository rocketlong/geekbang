package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

import java.math.BigInteger;

public class StringToBigIntegerConverter extends AbstractConverter<BigInteger> {

    @Override
    protected BigInteger doConvert(String value) {
        return new BigInteger(value.trim());
    }

}
