package com.github.romualdrousseau.shuju.commons;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PythonSimpleDateFormat extends SimpleDateFormat {

    public PythonSimpleDateFormat() {
        super();
    }

    public PythonSimpleDateFormat(final String pattern) {
        super(PythonSimpleDateFormat.toJava(pattern));
    }

    public PythonSimpleDateFormat(final String pattern, DateFormatSymbols formatSymbols) {
        super(PythonSimpleDateFormat.toJava(pattern), formatSymbols);
    }

    public PythonSimpleDateFormat(final String pattern, Locale locale) {
        super(PythonSimpleDateFormat.toJava(pattern), locale);
    }

    public static String toPython(final String javaPattern) {
        return javaPattern
            .replaceAll("YYYY", "%G")
            .replaceAll("yyyy", "%Y")
            .replaceAll("yy", "%y")
            .replaceAll("MMMMM", "%B")
            .replaceAll("MMM", "%b")
            .replaceAll("MM", "%m")
            .replaceAll("DDD", "%j")
            .replaceAll("dd", "%d")
            .replaceAll("EEEEE", "%A")
            .replaceAll("EEE", "%a")
            .replaceAll("ww", "%W")
            .replaceAll("u", "%u");
    }

    public static String toJava(final String pythonPattern) {
        return pythonPattern
            .replaceAll("%G", "YYYY")
            .replaceAll("%Y", "yyyy")
            .replaceAll("%y", "yy")
            .replaceAll("%B", "MMMMM")
            .replaceAll("%b", "MMM")
            .replaceAll("%m", "MM")
            .replaceAll("%j", "DDD")
            .replaceAll("%d", "dd")
            .replaceAll("%A", "EEEEE")
            .replaceAll("%a", "EEE")
            .replaceAll("%W", "ww")
            .replaceAll("%w", "u")
            .replaceAll("%u", "u")
            .replaceAll("%U", "ww")
            .replaceAll("%V", "ww");
    }
}
