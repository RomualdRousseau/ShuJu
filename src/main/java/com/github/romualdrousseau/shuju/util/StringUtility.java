package com.github.romualdrousseau.shuju.util;

import java.util.regex.Pattern;

public class StringUtility
{
	public static final String WHITE_SPACES = "\\s\\u00A0\\u3000";

    public static final String WRONG_UNICODE = "\\uFFFD";

    public static final char WRONG_UNICODE_CHAR = '\uFFFD';

    public static final String BOM = "\\uFEFF";

    public static final char BOM_CHAR = '\uFEFF';

    public static boolean isEmpty(String s) {
        return s == null || StringUtility.trim(s).equals("");
    }

    public static boolean isFastEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

	public static String trim(String s) {
		return trim(s, StringUtility.WHITE_SPACES);
	}

	public static String trim(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("^[" + whiteSpaces + "]+", "").replaceAll("[" +whiteSpaces + "]+$", "");
	}

	public static String normalizeWhiteSpaces(String s) {
		return normalizeWhiteSpaces(s, StringUtility.WHITE_SPACES);
	}

	public static String normalizeWhiteSpaces(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("[" + whiteSpaces + "]", " ");
	}

	public static String removeWhiteSpaces(String s) {
		return removeWhiteSpaces(s, StringUtility.WHITE_SPACES);
	}

	public static String removeWhiteSpaces(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("[" + whiteSpaces + "]", "");
	}

	public static String singleWhiteSpaces(String s) {
		return singleWhiteSpaces(s, StringUtility.WHITE_SPACES);
	}

	public static String singleWhiteSpaces(String s, String whiteSpaces) {
		if(s == null) {
			return null;
		}
		return s.replaceAll("[" + whiteSpaces + "]+", " ");
	}

	public static boolean checkIfGoodEncoding(String s) {
        if(s == null) {
            return false;
        }
		return !Pattern.compile(StringUtility.WRONG_UNICODE).matcher(s).find();
	}

	public static String cleanToken(String s) {
        if(s == null) {
			return null;
		}
        s = StringUtility.normalizeWhiteSpaces(s);
        s = StringUtility.singleWhiteSpaces(s);
        s = StringUtility.trim(s, StringUtility.WHITE_SPACES);
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = StringUtility.trim(s, StringUtility.WHITE_SPACES + "\"");
        }
		return s;
    }

    public static String removeExtension(String fileName) {
        if(fileName == null) {
            return null;
        }
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    public static String ensureCamelStyle(String s) {
        s = StringUtility.cleanToken(s.replaceAll("_", " "));
        if (StringUtility.isEmpty(s)) {
            return "";
        }

        // Replace space by the next letter upper case
        boolean stillCamelToDo = true;
        while (stillCamelToDo) {
            int i = s.indexOf(" ");
            if (i >= 0) {
                s = s.substring(0, i) + Character.toUpperCase(s.charAt(i + 1)) + s.substring(i + 2);
            } else {
                stillCamelToDo = false;
            }
        }

        // Ensure to start by a lower case
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
