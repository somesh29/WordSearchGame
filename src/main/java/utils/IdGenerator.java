package utils;

import org.apache.commons.lang3.RandomStringUtils;

public class IdGenerator {

    public static String generate(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
