package com.github.romualdrousseau.shuju.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PythonManager.class);

    public PythonManager(final String moduleName) throws IOException, URISyntaxException {
        final Properties prop = new Properties();
        prop.load(this.findPropertiesFile());

        this.modulePath = PythonManager.resolveResourcePath(prop.getProperty(moduleName + ".module-path")).get();
        this.mainEntry = prop.getProperty(moduleName + ".module-main", "main.py");
        this.hasVirtualEnv = prop.getProperty(moduleName + ".virtual-env", "false").equals("true");
        this.virtualEnvPath = prop.getProperty(moduleName + ".virtual-env-path", ".venv");
        this.hasDependencies = prop.getProperty(moduleName + ".dependencies", "false").equals("true");
    }

    public PythonManager enableVirtualEnv() throws IOException, InterruptedException {
        if (this.modulePath.resolve(this.virtualEnvPath).toFile().exists()) {
            return this;
        }

        LOGGER.info("venv: Create a new virtual environment");

        final ProcessBuilder processBuilder = new ProcessBuilder("python", "-m", "venv", this.virtualEnvPath);
        processBuilder.directory(this.modulePath.toFile());
        processBuilder.inheritIO();
        processBuilder.redirectErrorStream(true);
        processBuilder.start().waitFor();
        return this;
    }

    public PythonManager installDependencies() throws IOException, InterruptedException {
        if (this.isRequirementsInstalled()) {
            return this;
        }

        LOGGER.info("pip: Install and update all dependencies");

        final ProcessBuilder processBuilder = new ProcessBuilder(this.getPipScript(), "install", "-r",
                "requirements.txt");
        processBuilder.directory(this.modulePath.toFile());
        processBuilder.inheritIO();
        processBuilder.redirectErrorStream(true);
        processBuilder.start().waitFor();

        return this;
    }

    public PythonManager setEnviroment(final Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    public Process run(final String... args) throws IOException, InterruptedException {
        if (this.hasVirtualEnv) {
            this.enableVirtualEnv();
        }

        if (this.hasDependencies) {
            this.installDependencies();
        }

        LOGGER.info("python: Call {} with args: {}", this.mainEntry, args);

        final List<String> command = Stream.of(List.of(this.getPythonScript(), this.mainEntry), List.of(args))
                .flatMap(Collection::stream).toList();
        final ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(this.modulePath.toFile());
        processBuilder.redirectErrorStream(true);

        if (this.environment != null || this.environment.size() > 0) {
            final Map<String, String> env = processBuilder.environment();
            this.environment.forEach((k, v) -> env.put(k, v));
        }

        return processBuilder.start();
    }

    private InputStream findPropertiesFile() {
        return this.getClass().getClassLoader().getResourceAsStream("python4j.properties");
    }

    private boolean isRequirementsInstalled() throws IOException {
        final File requireFile = this.modulePath.resolve("requirements.txt").toFile();
        if (!requireFile.exists()) {
            return false;
        }

        final File lockFile = this.modulePath.resolve("requirements.lock").toFile();
        if (lockFile.exists()) {
            if (requireFile.lastModified() < lockFile.lastModified()) {
                return true;
            }
            lockFile.delete();
        }

        lockFile.createNewFile();
        return false;
    }

    private String getPythonScript() {
        if (this.hasVirtualEnv) {
            return Path.of(this.virtualEnvPath, "/bin/python").toString();
        } else {
            return "python";
        }
    }

    private String getPipScript() {
        if (this.hasVirtualEnv) {
            return Path.of(this.virtualEnvPath, "/bin/pip").toString();
        } else {
            return "pip";
        }
    }

    private static Optional<Path> resolveResourcePath(String resourceName) throws URISyntaxException {
        final URL resource = PythonManager.class.getResource(resourceName);
        if (resource != null) {
            LOGGER.debug(resource.toURI().toString());
            return Optional.of(Path.of(resource.toURI()));
        }
        return Optional.empty();
    }

    private final Path modulePath;
    private final String mainEntry;
    private final boolean hasVirtualEnv;
    private final String virtualEnvPath;
    private final boolean hasDependencies;
    private Map<String, String> environment = null;
}
