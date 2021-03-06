package com.dt.yakhere.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.codename1.ui.Display;
import com.codename1.ui.Image;

public class Utils {
	final static List<String> WORD_PATTERNS = Arrays.asList(
			new String[] {
					"babb",
					"bbabba",
					"babbab",
					"bbaab",
					"babab",
					"baba",
					"abba",
					"abab",
					"bababa",
			});
	final static String COMMON_A = "aeiou";
	final static String COMMON_B = "bcdfghklmnprstvwy";
	
	public static String generateName() {
		final String pattern = WORD_PATTERNS.get(new Random().nextInt(WORD_PATTERNS.size() - 1));
		String name = "";
		for (Character letter : pattern.toCharArray()) {
			name += randomLetterByPattern(letter);
		}
		return name;
	}
	
	private static String randomLetterByPattern(Character letter) {
		switch (letter) {
		case 'a':
			return COMMON_A.charAt(new Random().nextInt(COMMON_A.length() - 1)) + "";
		case 'b':
			return COMMON_B.charAt(new Random().nextInt(COMMON_B.length() - 1)) + "";
		case '0':
			return new Random().nextInt(9) + "";
		default:
			return letter + "";
		}
	}
	
	public static Image loadImage(String path) throws IOException {
	    InputStream in = null;
	    try {
	        in = Display.getInstance().getResourceAsStream(null, path);
	        return Image.createImage(in);
	    } 
	    finally {
	        if (in != null) {
	            in.close();
	        }
	    }
	}
}
