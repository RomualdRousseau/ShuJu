package com.github.romualdrousseau.shuju.bigdata;

public class BatchMetaData {

    private final long position;
    private final int length;

    private BatchMetaData(final long position, final int length) {
        this.position = position;
        this.length = length;
    }

    public long position() {
        return position;
    }

    public int length() {
        return length;
    }

    public static BatchMetaData of(final long position, final int length) {
        return new BatchMetaData(position, length);
    }
}
