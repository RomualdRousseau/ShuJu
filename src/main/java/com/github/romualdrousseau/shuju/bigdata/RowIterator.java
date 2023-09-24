package com.github.romualdrousseau.shuju.bigdata;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

public class RowIterator implements Iterator<String> {
    private final Row row;

	private int curr;

	public RowIterator(final Row row) {
		this.row = row;
		this.curr = 0;
	}

	public boolean hasNext() {
		return this.curr < this.row.size();
	}

	public String next() {
        return this.row.get(this.curr++);
	}

    public Spliterator<String> spliterator() {
        return Spliterators.spliterator(this, this.row.size(), Spliterator.IMMUTABLE);
    }
}
