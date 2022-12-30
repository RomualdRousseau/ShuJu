package com.github.romualdrousseau.shuju.util;

import java.util.regex.Pattern;

public class StringUtility {
    public static final String WHITE_SPACES = "\\s\\u00A0\\u3000";

    public static final String WRONG_UNICODE = "\\uFFFD";

    public static final char WRONG_UNICODE_CHAR = '\uFFFD';

    public static final String BOM = "\\uFEFF";

    public static final char BOM_CHAR = '\uFEFF';

    public static boolean isEmpty(final String s) {
        return s == null || StringUtility.trim(s).equals("");
    }

    public static boolean isFastEmpty(final String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String trim(final String s) {
        return trim(s, StringUtility.WHITE_SPACES);
    }

    public static String trim(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("^[" + whiteSpaces + "]+", "").replaceAll("[" + whiteSpaces + "]+$", "");
    }

    public static String normalizeWhiteSpaces(final String s) {
        return normalizeWhiteSpaces(s, StringUtility.WHITE_SPACES);
    }

    public static String normalizeWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]", " ");
    }

    public static String removeWhiteSpaces(final String s) {
        return removeWhiteSpaces(s, StringUtility.WHITE_SPACES);
    }

    public static String removeWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]", "");
    }

    public static String singleWhiteSpaces(final String s) {
        return singleWhiteSpaces(s, StringUtility.WHITE_SPACES);
    }

    public static String singleWhiteSpaces(final String s, final String whiteSpaces) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("[" + whiteSpaces + "]+", " ");
    }

    public static boolean checkIfGoodEncoding(final String s) {
        if (s == null) {
            return false;
        }
        return !Pattern.compile(StringUtility.WRONG_UNICODE).matcher(s).find();
    }

    public static String cleanToken(final String s) {
        if (s == null) {
            return null;
        }
        String ss = StringUtility.normalizeWhiteSpaces(s);
        ss = StringUtility.singleWhiteSpaces(ss);
        ss = StringUtility.trim(ss, StringUtility.WHITE_SPACES);
        if (ss.startsWith("\"") && ss.endsWith("\"")) {
            ss = StringUtility.trim(ss, StringUtility.WHITE_SPACES + "\"");
        }
        return ss;
    }

    public static String ensureCamelStyle(final String s) {
        // Consider _ seprated words instead of space 
        String ss = StringUtility.cleanToken(s.replaceAll("_", " "));
        if (StringUtility.isEmpty(ss)) {
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

        // Ensure to start by a lower case
        return Character.toLowerCase(ss.charAt(0)) + ss.substring(1);
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

    public static String removeExtension(final String fileName) {
        if (fileName == null) {
            return null;
        }
        return fileName.replaceFirst("[.][^.]+$", "");
    }
}
