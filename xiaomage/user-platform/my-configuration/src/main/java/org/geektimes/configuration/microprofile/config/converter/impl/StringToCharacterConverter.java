package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringToCharacterConverter extends AbstractConverter<Character> {

    @Override
    protected Character doConvert(String value) {
        if (value.isEmpty()) {
            return null;
        } else if (value.length() > 1) {
            throw new IllegalArgumentException("Can only convert a [String] with length of 1 to a [Character]; string value '" + value + "'  has length of " + value.length());
        } else {
            return value.charAt(0);
        }
    }

}
