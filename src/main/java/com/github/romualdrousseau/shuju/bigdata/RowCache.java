package com.github.romualdrousseau.shuju.bigdata;

import java.util.Map;
import java.util.LinkedHashMap;

public class RowCache extends LinkedHashMap<Integer, Row> {
    public final static int MAX_STORE_ROWS = 10000;

    public RowCache() {
        super(MAX_STORE_ROWS, 0.75F, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Row> eldest) {
        return this.size() > MAX_STORE_ROWS;
    }
}
