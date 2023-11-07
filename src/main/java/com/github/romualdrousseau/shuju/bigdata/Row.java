package com.github.romualdrousseau.shuju.bigdata;

import java.io.Serializable;
import java.util.Iterator;

public class Row implements Iterable<String>, Serializable {

    private final int columnStart;
    private final int columnCount;
    private final String[] data;

    public Row(final int columnCount) {
        this.columnStart = 0;
        this.columnCount = columnCount;
        this.data = new String[columnCount];
    }

    private Row(final String[] data) {
        this.columnStart = 0;
        this.columnCount = data.length;
        this.data = data;
    }

    private Row(final int columnStart, final int columnCount, final String[] data) {
        this.columnStart = columnStart;
        this.columnCount = columnCount;
        this.data = data;
    }

    public static Row of(String... data) {
        return new Row(data);
    }

    public Row view(final int columnStart, final int columnCount) {
        this.checkRange(columnStart, columnStart + columnCount - 1);
        return new Row(columnStart, columnCount, this.data);
    }

    public int size() {
        return this.columnCount;
    }

    public String get(final int index) {
        this.checkIndex(index);
        return this.data[this.columnStart + index];
    }

    public Row set(final int index, final String element) {
        this.checkIndex(index);
        this.data[this.columnStart + index] = element;
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        return new RowIterator(this);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index));
    }

    private void checkRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || fromIndex >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(fromIndex));
        if (toIndex < 0 || toIndex >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(toIndex));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + this.columnCount;
    }
}
