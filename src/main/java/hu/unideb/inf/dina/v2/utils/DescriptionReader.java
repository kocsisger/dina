package hu.unideb.inf.dina.v2.utils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class DescriptionReader {

    public static String readDescriptionFromFile(String fileName) {
        InputStream inputStream = DescriptionReader.class.getClassLoader().getResourceAsStream("algorithms/" + fileName);

        if (inputStream != null) {
            String text;
            try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
                text = scanner.useDelimiter("\\A").next();
            }

            return text;
        } else {
            return null;
        }
    }
}
