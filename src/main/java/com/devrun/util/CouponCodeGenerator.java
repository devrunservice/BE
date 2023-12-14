package com.devrun.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 쿠폰 코드 생성기
 * 생성 규칙 : 앞5글자는 숫자 - 뒤12글자는 숫자, 알파벳 대문자, 알파벳 소문자 조합
 * 총 17자리 코드
 * @author http1220
 *
 */
public class CouponCodeGenerator {

	private static final String NUMBER_CHARS = "0123456789";
	private static final String ALPHABET_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private static final int prefix = 5;
	private static final int postfix = 12;

	private String code = null;

	@Override
	public String toString() {
		return this.code;
	}

	public CouponCodeGenerator() {
		String s = generateRandomNumberString();
		this.code = s;
	}

	private static String generateRandomNumberString() {
		StringBuilder sb = new StringBuilder();
		SecureRandom random = new SecureRandom();
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
		return sb.toString();

	}

}
