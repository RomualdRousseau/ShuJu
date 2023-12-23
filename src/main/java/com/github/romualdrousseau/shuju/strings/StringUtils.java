package com.github.romualdrousseau.shuju.strings;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.preprocessing.Text;

public class StringUtils {
    public static final String WHITE_SPACES = "\\s\\u00A0\\u3000";
    public static final String WRONG_UNICODE = "\\uFFFD";
    public static final char WRONG_UNICODE_CHAR = '\uFFFD';
    public static final String BOM = "\\uFEFF";
    public static final char BOM_CHAR = '\uFEFF';

    public static final Map<String, String> symbols = Map.of(
        "%+", "percent",
        "\\$+", "dollar"
    );

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
        if (s.length() <= 1) {
            return s.toLowerCase();
        } else {
            return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
        }
    }

    public static String uncapitalize(final String s) {
        if (s == null) {
            return null;
        }
        if (s.length() <= 1) {
            return s.toLowerCase();
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
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

    public static String toSnake(final String w, final Text.ITokenizer tokenizer) {
        return String.join("_", tokenizer.apply(StringUtils.encodeSymbols(w).replaceAll("\\W+", " "))).toLowerCase();
    }

    public static String toCamel(final String w, final Text.ITokenizer tokenizer) {
        return uncapitalize(
                String.join("", tokenizer.apply(StringUtils.encodeSymbols(w).replaceAll("\\W+", " ")).stream()
                        .map(StringUtils::capitalize).toArray(String[]::new)));
    }

    public static String encodeSymbols(final String s) {
        var tmp = s;
        for(var e: symbols.entrySet()) {
            tmp = tmp.replaceAll(e.getKey(), e.getValue());
        }
        return tmp;
    }

    public static Set<String> getSymbols() {
        return symbols.keySet();
    }

    public static Optional<String> merge(final String sep, final List<String> values) {
        return values.stream().reduce((a, x) -> !a.contains(x) ? String.join(sep, a, x) : a);
    }
}
