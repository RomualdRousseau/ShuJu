package com.github.romualdrousseau.shuju.strings;

import java.util.regex.Pattern;

public class StringUtils {
    public static final String WHITE_SPACES = "\\s\\u00A0\\u3000";

    public static final String WRONG_UNICODE = "\\uFFFD";

    public static final char WRONG_UNICODE_CHAR = '\uFFFD';

    public static final String BOM = "\\uFEFF";

    public static final char BOM_CHAR = '\uFEFF';

    public static boolean isBlank(final String s) {
        return s == null || StringUtils.trim(s).equals("");
    }

    public static boolean isFastBlank(final String s) {
        return s == null || s.isBlank();
    }

    public static String trim(final String s) {
        return trim(s, StringUtils.WHITE_SPACES);
    }

    public static String trim(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("^[" + whiteSpaces + "]+", "").replaceAll("[" + whiteSpaces + "]+$", "");
    }

    public static String normalizeWhiteSpaces(final String s) {
        return normalizeWhiteSpaces(s, StringUtils.WHITE_SPACES);
    }

    public static String normalizeWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]", " ");
    }

    public static String removeWhiteSpaces(final String s) {
        return removeWhiteSpaces(s, StringUtils.WHITE_SPACES);
    }

    public static String removeWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]", "");
    }

    public static String singleWhiteSpaces(final String s) {
        return singleWhiteSpaces(s, StringUtils.WHITE_SPACES);
    }

    public static String singleWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]+", " ");
    }

    public static String capitalize(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() == 1) {
            return s.toLowerCase();
        }
        else {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }
    }

    public static boolean checkIfGoodEncoding(final String s) {
        if (s == null) {
            return false;
        }
        return !Pattern.compile(StringUtils.WRONG_UNICODE).matcher(s).find();
    }

    public static String cleanToken(final String s) {
        if (s == null) {
            return null;
        }
        String ss = StringUtils.normalizeWhiteSpaces(s);
        ss = StringUtils.singleWhiteSpaces(ss);
        ss = StringUtils.trim(ss, StringUtils.WHITE_SPACES);
        if (ss.startsWith("\"") && ss.endsWith("\"")) {
            ss = StringUtils.trim(ss, StringUtils.WHITE_SPACES + "\"");
        }
        return ss;
    }

    public static String ensureCamelStyle(final String s) {
        // Consider _ separated words instead of space
        String ss = s.replaceAll("_", " ");
        if (StringUtils.isBlank(ss)) {
            return "";
        }

        // Replace space by the next letter upper case
        boolean stillCamelToDo = true;
        while (stillCamelToDo) {
            final int i = ss.indexOf(" ");
            if (i >= 0) {
                ss = ss.substring(0, i) + Character.toUpperCase(ss.charAt(i + 1)) + ss.substring(i + 2);
            } else {
                stillCamelToDo = false;
            }
        }

        return ss;
    }
}
