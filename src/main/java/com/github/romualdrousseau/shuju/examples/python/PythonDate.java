package com.github.romualdrousseau.shuju.examples.python;

import java.text.ParseException;
import java.util.Date;

import com.github.romualdrousseau.shuju.commons.PythonSimpleDateFormat;

public class PythonDate {

    public static void main(final String[] args) throws ParseException {
        final PythonSimpleDateFormat formatter = new PythonSimpleDateFormat("%a,%d/%m/%y");
        System.out.println(formatter.format(new Date()));
        System.out.println(formatter.format(formatter.parse("Sun,05/12/99")));
    }
}
