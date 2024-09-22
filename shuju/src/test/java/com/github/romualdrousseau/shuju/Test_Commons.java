package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;

import com.github.romualdrousseau.shuju.commons.PythonSimpleDateFormat;

public class Test_Commons {

    @Test
    public void testPythonSimpleDateformat() throws ParseException {
        final PythonSimpleDateFormat formatter = new PythonSimpleDateFormat("%a,%d/%m/%y");
        assertEquals("Sun,24/09/23", formatter.format(Date.from(LocalDate.of(2023, 9, 24).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        assertEquals("Sun,05/12/99", formatter.format(formatter.parse("Sun,05/12/99")));
    }

    @Test
    public void testPythonSimpleDateformatWithTime() throws ParseException {
        final PythonSimpleDateFormat formatter = new PythonSimpleDateFormat("%Y.%m.%d. %H:%M");
        assertEquals("2023.03.16. 11:36", formatter.format(formatter.parse("2023.03.16. 11:36")));
    }
}
