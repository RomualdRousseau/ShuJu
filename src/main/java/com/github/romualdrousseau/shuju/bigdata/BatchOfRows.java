package com.github.romualdrousseau.shuju.bigdata;

public class BatchOfRows {

    private final long position;
    private final int length;

    private BatchOfRows(long position, int length) {
        this.position = position;
        this.length = length;
    }

    public long position() {
        return position;
    }

    public int length() {
        return length;
    }

    public static BatchOfRows of(long position, int length) {
        return new BatchOfRows(position, length);
    }
}
