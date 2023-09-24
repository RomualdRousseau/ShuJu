package com.github.romualdrousseau.shuju.bigdata;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.Iterator;

import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileReader;

public class DataFrame implements Closeable, Iterable<Row> {

    private final int batchSize;
    private final int rowCount;
    private final int columnCount;
    private final Path storePath;
    private final SeekableByteChannel input;
    private final ArrowFileReader reader;

    private int currBlock = -1;
    private VectorSchemaRoot root;
    private boolean isClosed = false;
    private final RowCache rowCache = new RowCache();

    public DataFrame(final RootAllocator allocator, final int batchSize, final int rowCount, final int columnCount, final Path storePath) throws IOException {
        this.batchSize = batchSize;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.storePath = storePath;
        this.input = Files.newByteChannel(storePath, EnumSet.of(StandardOpenOption.READ));
        this.reader = new ArrowFileReader(this.input, allocator);
    }

    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }
        this.reader.close();
        this.input.close();
        Files.deleteIfExists(this.storePath);
        this.isClosed = true;
    }

    public DataView view(final int rowStart, final int columnStart, final int rowCount, final int columnCount) {
        this.checkRowRange(rowStart, rowStart + rowCount - 1);
        this.checkColumnRange(columnStart, columnStart + columnCount - 1);
        return new DataView(this, rowStart, columnStart, rowCount, columnCount);
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Row getRow(final int row) {
        this.checkRowIndex(row);
        return rowCache.computeIfAbsent(row, y -> {
            try {
                final int block_n = row / this.batchSize;
                final int batch_n = row % this.batchSize;

                if (this.currBlock != block_n) {
                    final var block = DataFrame.this.reader.getRecordBlocks().get(block_n);
                    DataFrame.this.reader.loadRecordBatch(block);
                    this.root = DataFrame.this.reader.getVectorSchemaRoot();
                    this.currBlock = block_n;
                }

                return Row.of(this.root.getFieldVectors().stream()
                    .map(x -> (VarCharVector) x)
                    .map(x -> !x.isNull(batch_n) ? new String(x.get(batch_n)) : null)
                    .toArray(String[]::new));
            }
            catch(final IOException e) {
                throw new UncheckedIOException(e);
            }
        });
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
