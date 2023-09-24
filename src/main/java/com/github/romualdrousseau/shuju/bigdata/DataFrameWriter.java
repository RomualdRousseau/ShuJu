package com.github.romualdrousseau.shuju.bigdata;

import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.vector.FieldVector;
import org.apache.arrow.vector.VarCharVector;
import org.apache.arrow.vector.VectorSchemaRoot;
import org.apache.arrow.vector.ipc.ArrowFileWriter;
import org.apache.arrow.vector.types.pojo.ArrowType;
import org.apache.arrow.vector.types.pojo.Field;
import org.apache.arrow.vector.types.pojo.FieldType;
import org.apache.arrow.vector.types.pojo.Schema;

public class DataFrameWriter implements Closeable {
    private final RootAllocator allocator;
    private final int batchSize;
    private final int columnCount;
    private final VectorSchemaRoot root;
    private final Path storePath;
    private final FileOutputStream output;
    private final ArrowFileWriter writer;

    private int rowCount = 0;
    private boolean isWriteEnd = false;

    public DataFrameWriter(final int batchSize, final int columnCount) throws IOException {
        this(batchSize, columnCount, null);
    }

    public DataFrameWriter(final int batchSize, final int columnCount, final Path path) throws IOException {
        this.allocator = new RootAllocator(Long.MAX_VALUE);
        this.batchSize = batchSize;
        this.columnCount = columnCount;

        final var fields = new ArrayList<Field>();
        for (int i = 0; i < columnCount; i++) {
            fields.add(new Field(String.valueOf(i), FieldType.nullable(new ArrowType.Utf8()), null));
        }
        this.root = VectorSchemaRoot.create(new Schema(fields), this.allocator);

        this.storePath = (path == null) ? Files.createTempFile(null, null) : Files.createTempFile(path, null, null);
        this.storePath.toFile().deleteOnExit();
        this.output = new FileOutputStream(this.storePath.toFile());
        this.writer = new ArrowFileWriter(root, null, Channels.newChannel(output));

        this.writer.start();
    }

    @Override
    public void close() throws IOException {
        this.writeEnd();
        this.allocator.close();
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getColumnCount() {
        return this.columnCount;
    }

    public DataFrame getDataFrame() throws IOException {
        this.writeEnd();
        return new DataFrame(this.allocator, this.batchSize, this.rowCount, this.columnCount, this.storePath);
    }

    public void write(final Row data) throws IOException {
        assert data.size() == this.root.getFieldVectors().size();

        for(int i = 0; i < data.size(); i++) {
            final var v = (VarCharVector) this.root.getVector(i);
            final var d = data.get(i);
            if (d != null) {
                v.setSafe(this.rowCount % this.batchSize, d.getBytes());
            }
        }

        if ((++this.rowCount % this.batchSize) == 0) {
            this.root.setRowCount(this.batchSize);
            this.writer.writeBatch();
            this.root.getFieldVectors().forEach(FieldVector::reset);
        }
    }

    private void writeEnd() throws IOException {
        if (this.isWriteEnd) {
            return;
        }
        if ((this.rowCount % this.batchSize) > 0) {
            root.setRowCount(this.rowCount % this.batchSize);
            writer.writeBatch();
        }
        this.writer.end();
        this.writer.close();
        this.output.close();
        this.root.close();
        this.isWriteEnd = true;
    }
}
