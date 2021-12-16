package com.nttdata.consumption.util;

import java.util.Random;

public class Generator {
    private static Random random = new Random(System.currentTimeMillis());

    public static String generateOperationNumber() {
        String bin = "005";

        StringBuilder b = new StringBuilder(bin);
        for (int i = 0; i <= 14; i++) {
            int number = random.nextInt(10);
            b.append(number);
        }
        return b.toString();
    }
}
