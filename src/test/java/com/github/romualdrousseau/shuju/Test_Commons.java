package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import com.github.romualdrousseau.shuju.commons.PythonSimpleDateFormat;

public class Test_Commons {

    @Test
    public void testPythonSimpleDateformat() throws ParseException {
        final PythonSimpleDateFormat formatter = new PythonSimpleDateFormat("%a,%d/%m/%y");
        assertEquals("Sun,24/09/23", formatter.format(new Date()));
        assertEquals("Sun,05/12/99", formatter.format(formatter.parse("Sun,05/12/99")));
    }
}
