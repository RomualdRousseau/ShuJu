package com.github.romualdrousseau.shuju.bigdata;

import com.github.romualdrousseau.shuju.bigdata.serializer.BatchSerializerFury;
import com.github.romualdrousseau.shuju.bigdata.serializer.BatchSerializerJava;

public class BatchSerializerFactory {

    public enum SerializerType {
        DEFAULT,
        JAVA,
        FURY
    }
    public static BatchSerializer newInstance() {
        return BatchSerializerFactory.newInstance(SerializerType.DEFAULT);
    }

    public static BatchSerializer newInstance(final SerializerType type) {
        switch (type){
            case JAVA:
                return new BatchSerializerJava();
            case FURY:
                return new BatchSerializerFury();
            default:
                return new BatchSerializerFury();
        }
    }
}
