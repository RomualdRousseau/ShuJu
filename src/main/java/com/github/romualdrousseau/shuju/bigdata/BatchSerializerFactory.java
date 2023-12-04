package com.github.romualdrousseau.shuju.bigdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.romualdrousseau.shuju.bigdata.serializer.BatchSerializerFury;
import com.github.romualdrousseau.shuju.bigdata.serializer.BatchSerializerJava;

public class BatchSerializerFactory {
    public enum SerializerType {
        DEFAULT,
        JAVA,
        FURY
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSerializerFactory.class);

    private static BatchSerializerFactory singleton = new BatchSerializerFactory();

    public static BatchSerializer newInstance() {
        return singleton.createSerializerInstance();
    }

    public static BatchSerializer newInstance(final SerializerType type) {
        return singleton.createSerializerInstance(type);
    }

    private SerializerType type = SerializerType.FURY;

    private BatchSerializerFactory() {
        try {
            final var prop = new Properties();
            prop.load(this.findPropertiesFile());
            final var typeVal = prop.getProperty("serializer");
            if (typeVal != null) {
                this.type = Enum.valueOf(SerializerType.class, typeVal);
            }
            LOGGER.info("Factory set to {}", this.type);
        } catch(final IOException x) {
            LOGGER.error("Error during initialization", x);
        }
    }

    private BatchSerializer createSerializerInstance() {
        return this.createSerializerInstance(this.type);
    }

    private BatchSerializer createSerializerInstance(final SerializerType type) {
        switch (type){
            case JAVA:
                return new BatchSerializerJava();
            case FURY:
                return new BatchSerializerFury();
            default:
                return new BatchSerializerFury();
        }
    }

    private InputStream findPropertiesFile() throws IOException {
        final var userDir = System.getProperty("user.dir");
        return this.getPathIfExists(Path.of(userDir, "batch-serializer.properties"))
                .or(() -> this.getPathIfExists(Path.of(userDir, "classes", "batch-serializer.properties")))
                .flatMap(this::pathToStream)
                .or(() -> this.resolveResourceAsStream("batch-serializer.properties"))
                .orElseGet(() -> new ByteArrayInputStream(new byte[0]));
    }

    private Optional<InputStream> pathToStream(final Path x) {
        try {
            return Optional.of(Files.newInputStream(x));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    private Optional<InputStream> resolveResourceAsStream(final String resourceName) {
        final InputStream resource = this.getClass().getClassLoader().getResourceAsStream(resourceName);
        if (resource == null) {
            LOGGER.debug("module: {} not found", resourceName);
            return Optional.empty();
        }
        LOGGER.debug("module: {} found at {}", resourceName, resource);
        return Optional.of(resource);
    }

    private Optional<Path> getPathIfExists(final Path path) {
        if (!path.toFile().exists()) {
            LOGGER.debug("module: {} not found at {}", path.getFileName(), path);
            return Optional.empty();
        }
        LOGGER.debug("module: {} found at {}", path.getFileName(), path);
        return Optional.of(path);
    }
}
