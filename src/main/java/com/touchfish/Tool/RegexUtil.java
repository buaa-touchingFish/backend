package com.touchfish.Tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
    private static final String EMAIL_REGEX = "^(\\w+([-_.][A-Za-z0-9]+)*){3,18}@\\w+([-_.][A-Za-z0-9]+)*\\.\\w+([-_.][A-Za-z0-9]+)*$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
