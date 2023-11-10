package com.github.romualdrousseau.shuju.bigdata.serializer;

import java.io.IOException;

import org.xerial.snappy.Snappy;

import com.github.romualdrousseau.shuju.bigdata.BatchSerializer;
import com.github.romualdrousseau.shuju.bigdata.Row;

import io.fury.Fury;
import io.fury.config.Language;

public class BatchSerializerFury implements BatchSerializer {

    private final Fury fury;

    public BatchSerializerFury() {
        this.fury = Fury.builder()
                .withLanguage(Language.JAVA)
                .build();
        this.fury.register(String.class);
        this.fury.register(String[].class);
        this.fury.register(Row.class);
        this.fury.register(Row[].class);
    }

    @Override
    public byte[] serialize(Row[] batch) throws IOException {
        return Snappy.compress(this.fury.serializeJavaObject(batch));
    }

    @Override
    public Row[] deserialize(byte[] bytes) throws IOException {
        return this.fury.deserializeJavaObject(Snappy.uncompress(bytes), Row[].class);
    }
}
