package com.github.romualdrousseau.shuju.bigdata;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DataFrameWriter implements Closeable {

    private final int batchSize;
    private final Path storePath;
    private final List<BatchOfRows> batches = new ArrayList<>();
    private final List<Row> currentBatch;
    private final FileChannel fileChannel;

    private long currPosition = 0;
    private int rowCount = 0;
    private int columnCount  = 0;
    private boolean isClosed = false;

    public DataFrameWriter(final int batchSize) throws IOException {
        this(batchSize, 0, null);
    }

    public DataFrameWriter(final int batchSize, final int columnCount) throws IOException {
        this(batchSize, columnCount, null);
    }

    public DataFrameWriter(final int batchSize, final Path path) throws IOException {
        this(batchSize, 0, path);
    }

    public DataFrameWriter(final int batchSize, final int columnCount, final Path path) throws IOException {
        this.batchSize = batchSize;
        this.storePath = (path == null) ? Files.createTempFile(null, null) : Files.createTempFile(path, null, null);
        this.storePath.toFile().deleteOnExit();
        this.fileChannel = (FileChannel) Files.newByteChannel(this.storePath, EnumSet.of(StandardOpenOption.CREATE, StandardOpenOption.WRITE));
        this.currentBatch = new ArrayList<Row>(this.batchSize);
        this.columnCount = columnCount;
    }

    @Override
    public void close() throws IOException {
        if (this.isClosed) {
            return;
        }

        if (this.currentBatch.size() > 0) {
            this.flush();
        }

        this.fileChannel.close();
        this.isClosed = true;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public DataFrame getDataFrame() throws IOException {
        this.close();
        return new DataFrame(this.batchSize, this.storePath, this.batches, this.rowCount, this.columnCount);
    }

    public void write(final Row data) throws IOException {
        this.currentBatch.add(data);
        this.columnCount = Math.max(this.columnCount, data.size());
        this.rowCount++;
        if (this.currentBatch.size() >= this.batchSize) {
            this.flush();
        }
    }

    private void flush() throws IOException {
        final var bytes = this.serialize(this.currentBatch);
        this.batches.add(BatchOfRows.of(this.currPosition, bytes.length));
        this.fileChannel.write(ByteBuffer.wrap(bytes));
        this.currPosition += bytes.length;
        this.currentBatch.clear();
    }

    private byte[] serialize(final List<Row> o) throws IOException {
        try (
            final var byteArrayOutputStream = new ByteArrayOutputStream();
            final var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(o);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
