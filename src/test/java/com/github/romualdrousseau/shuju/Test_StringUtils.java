package com.github.romualdrousseau.shuju;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.romualdrousseau.shuju.preprocessing.tokenizer.ShingleTokenizer;
import com.github.romualdrousseau.shuju.strings.StringUtils;

public class Test_StringUtils {

    @Test
    public void testSnakeWithLemmatization() {
        final var tokenizer = new ShingleTokenizer(List.of("al", "total,tot", "dollar", "percent"));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("total quantity $", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("TotalQuantity$", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("totalquantity$", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("tot quantity $", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("TotQuantity$", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("totquantity$", tokenizer));
    }

    @Test
    public void testSnakeWithoutLemmatization() {
        final var tokenizer = new ShingleTokenizer(List.of("al", "total,tot", "dollar", "percent"), 1, false);
        assertEquals("total_quantity_dollar", StringUtils.toSnake("total quantity $", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("TotalQuantity$", tokenizer));
        assertEquals("total_quantity_dollar", StringUtils.toSnake("totalquantity$", tokenizer));
        assertEquals("tot_quantity_dollar", StringUtils.toSnake("tot quantity $", tokenizer));
        assertEquals("tot_quantity_dollar", StringUtils.toSnake("TotQuantity$", tokenizer));
        assertEquals("tot_quantity_dollar", StringUtils.toSnake("totquantity$", tokenizer));
    }

    @Test
    public void testCamelWithLemmatization() {
        final var tokenizer = new ShingleTokenizer(List.of("al", "total,tot", "dollar", "percent"));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("total quantity $", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("TotalQuantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("totalquantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("tot quantity $", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("TotQuantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("totquantity$", tokenizer));
    }

    @Test
    public void testCamelWithoutLemmatization() {
        final var tokenizer = new ShingleTokenizer(List.of("al", "total,tot", "dollar", "percent"), 1, false);
        assertEquals("totQuantityDollar", StringUtils.toCamel("tot quantity $", tokenizer));
        assertEquals("totQuantityDollar", StringUtils.toCamel("TotQuantity$", tokenizer));
        assertEquals("totQuantityDollar", StringUtils.toCamel("totquantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("total quantity $", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("TotalQuantity$", tokenizer));
        assertEquals("totalQuantityDollar", StringUtils.toCamel("totalquantity$", tokenizer));
    }

    @Test
    public void testCleanToken() {
        assertEquals("total quantity $", StringUtils.cleanToken("  total   quantity $  "));
        assertEquals("total quantity $", StringUtils.cleanToken(" \"\"\"  total   quantity $  \"\"\""));
    }
}
