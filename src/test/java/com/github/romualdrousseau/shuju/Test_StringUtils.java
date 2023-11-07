package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.romualdrousseau.shuju.preprocessing.tokenizer.ShingleTokenizer;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class Test_StringUtils {

    @Test
    public void testSnake() {
        final var tokenizer = new ShingleTokenizer(List.of("total", "dollar", "percent"));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("total quantity $", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("TotalQuantity$", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("totalquantity$", tokenizer));
    }

    @Test
    public void testCamel() {
        final var tokenizer = new ShingleTokenizer(List.of("total", "dollar", "percent"));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("total quantity $", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("TotalQuantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("totalquantity$", tokenizer));
    }
}
