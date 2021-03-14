package org.geektimes.web.user.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public class StringToCharacterConverter implements Converter<Character> {

    @Override
    public Character convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value.isEmpty()) {
            return null;
        } else if (value.length() > 1) {
            throw new IllegalArgumentException("Can only convert a [String] with length of 1 to a [Character]; string value '" + value + "'  has length of " + value.length());
        } else {
            return value.charAt(0);
        }
    }

}
