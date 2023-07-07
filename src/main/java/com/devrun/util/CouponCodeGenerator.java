package com.devrun.util;

import org.junit.Test;

import java.util.Random;
public class CouponCodeGenerator {

    private static final String NUMBER_CHARS = "0123456789";
    private static final String ALPHABET_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int prefix = 5;
    private static final int postfix = 12;

    private String code = null;

    @Override
    public String toString(){
        return this.code;
    }


    public CouponCodeGenerator() {
        String s = generateRandomNumberString();
        this.code = s;
    }

    private static String generateRandomNumberString() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        // Generate 5-digit number
        for (int i = 0; i < prefix; i++) {
            char randomChar = NUMBER_CHARS.charAt(random.nextInt(NUMBER_CHARS.length()));
            sb.append(randomChar);
        }
        sb.append("-");

        // Generate 12 random characters
        for (int i = 0; i < postfix; i++) {
            char randomChar = ALPHABET_CHARS.charAt(random.nextInt(ALPHABET_CHARS.length()));
            sb.append(randomChar);
        }
        System.out.println("생성한 쿠폰 등록 코드"+sb.toString());
        return sb.toString();

    }

}
