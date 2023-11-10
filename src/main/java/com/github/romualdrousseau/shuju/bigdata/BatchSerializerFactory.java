package com.github.romualdrousseau.shuju.bigdata;

import com.github.romualdrousseau.shuju.bigdata.serializer.BatchSerializerFury;

public class BatchSerializerFactory {
    private static BatchSerializer DEFAULT_SERIALIZER = new BatchSerializerFury();

    private static BatchSerializer serializer = DEFAULT_SERIALIZER;

    public static BatchSerializer get() {
        return BatchSerializerFactory.serializer;
    }

    public static void set(final BatchSerializer defaultSerializer) {
        BatchSerializerFactory.serializer = defaultSerializer;
    }
}
