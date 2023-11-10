package com.github.romualdrousseau.shuju.bigdata.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.romualdrousseau.shuju.bigdata.BatchSerializer;
import com.github.romualdrousseau.shuju.bigdata.Row;

public class BatchSerializerJava implements BatchSerializer {

    @Override
    public byte[] serialize(Row[] batch) throws IOException {
        try (
            final var byteArrayOutputStream = new ByteArrayOutputStream();
            final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(batch);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public Row[] deserialize(byte[] bytes) throws IOException {
        try (ObjectInputStream o = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (Row[]) o.readObject();
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
