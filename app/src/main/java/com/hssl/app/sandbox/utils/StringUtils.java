package com.hssl.app.sandbox.utils;

import java.io.IOException;
import java.io.InputStream;

public class StringUtils {

    public static String readLine(InputStream is) throws IOException {
        StringBuilder line = new StringBuilder();
        char nextByte;
        while ( (nextByte = (char) is.read()) != '\n' ) {
            line.append(nextByte);
        }
        return line.toString();
    }
}
