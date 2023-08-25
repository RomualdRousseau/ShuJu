package com.github.romualdrousseau.shuju.commons;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PythonSimpleDateFormat extends SimpleDateFormat {

    public PythonSimpleDateFormat() {
        super();
    }

    public PythonSimpleDateFormat(final String pattern) {
        super(convert(pattern));
    }

    public PythonSimpleDateFormat(final String pattern, DateFormatSymbols formatSymbols) {
        super(convert(pattern), formatSymbols);
    }

    public PythonSimpleDateFormat(final String pattern, Locale locale) {
        super(convert(pattern), locale);
    }

    private static String convert(final String pattern) {
        return pattern
            .replaceAll("%y", "yy")
            .replaceAll("%Y", "yyyy")
            .replaceAll("%G", "YYYY")
            .replaceAll("%m", "MM")
            .replaceAll("%b", "MMM")
            .replaceAll("%B", "MMMMM")
            .replaceAll("%d", "dd")
            .replaceAll("%j", "DDD")
            .replaceAll("%a", "EEE")
            .replaceAll("%A", "EEEEE")
            .replaceAll("%w", "u")
            .replaceAll("%W", "ww")
            .replaceAll("%u", "u")
            .replaceAll("%U", "ww")
            .replaceAll("%V", "ww");
    }
}
