package com.github.romualdrousseau.shuju.bigdata;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class DataFrame implements Closeable, Iterable<Row> {

    private final int batchSize;
    private final Path storePath;
    private final List<BatchOfRows> batches;
    private final int rowCount;
    private final int columnCount;
    private final FileChannel fileChannel;
    private final MappedByteBuffer mappedBuffer;

    private List<Row> currentBatch = null;
    private int currentBatchIdx = -1;
    private boolean isClosed = false;

    public DataFrame(final int batchSize, final Path storePath, final List<BatchOfRows> batches, final int rowCount,
            final int columnCount) throws IOException {
        this.batchSize = batchSize;
        this.storePath = storePath;
        this.batches = batches;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.fileChannel = (FileChannel) Files.newByteChannel(this.storePath,
                EnumSet.of(StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE));
        if (this.fileChannel.size() <= Integer.MAX_VALUE) {
            this.mappedBuffer = this.fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, this.fileChannel.size());
        } else {
            this.mappedBuffer = null;
        }
    }

    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.fileChannel.close();
        this.isClosed = true;
    }

    public DataView view(final int rowStart, final int columnStart, final int rowCount, final int columnCount) {
        this.checkRowRange(rowStart, rowStart + rowCount - 1);
        this.checkColumnRange(columnStart, columnStart + columnCount - 1);
        return new DataView(this, rowStart, columnStart, rowCount, columnCount);
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public int getColumnCount(final int row) {
        this.checkRowIndex(row);
        final var r = this.getRow(row);
        return r.size();
    }

    public Row getRow(final int row) {
        this.checkRowIndex(row);
        final int idx = row / batchSize;
        if (this.currentBatchIdx != idx) {
            this.currentBatch = this.loadOneBatch(batches.get(idx));
            this.currentBatchIdx = idx;
        }
        return this.currentBatch.get(row % batchSize);
    }

    public String getCell(final int row, final int column) {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        return this.getRow(row).get(column);
    }

    @Override
    public Iterator<Row> iterator() {
        return new DataFrameIterator(this);
    }

    private List<Row> loadOneBatch(final BatchOfRows batch) {
        try {
            if (this.isMappedBuffer()) {
                final var bytes = new byte[batch.length()];
                this.mappedBuffer.position((int) batch.position());
                this.mappedBuffer.get(bytes);
                return this.deserializeOneBatch(bytes);
            } else {
                final var bytes = ByteBuffer.allocate(batch.length());
                this.fileChannel.position(batch.position());
                this.fileChannel.read(bytes);
                return this.deserializeOneBatch(bytes.array());
            }
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isMappedBuffer() {
        return this.mappedBuffer != null;
    }

    @SuppressWarnings("unchecked")
    private List<Row> deserializeOneBatch(final byte[] bytes) throws IOException {
        try (ObjectInputStream o = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (List<Row>) o.readObject();
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkRowIndex(final int index) {
        if (index < 0 || index >= this.rowCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index, this.rowCount));
    }

    private void checkColumnIndex(final int index) {
        if (index < 0 || index >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(index, this.columnCount));
    }

    private void checkRowRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || fromIndex >= this.rowCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(fromIndex, this.rowCount));
        if (toIndex < 0 || toIndex >= this.rowCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(toIndex, this.rowCount));
    }

    private void checkColumnRange(final int fromIndex, final int toIndex) {
        if (fromIndex < 0 || fromIndex >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(fromIndex, this.columnCount));
        if (toIndex < 0 || toIndex >= this.columnCount)
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(toIndex, this.columnCount));
    }

    private String outOfBoundsMsg(final int index, final int count) {
        return "Index: " + index + ", Size: " + count;
    }
}
