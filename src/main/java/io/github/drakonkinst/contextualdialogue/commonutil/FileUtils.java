package io.github.drakonkinst.contextualdialogue.commonutil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {
    private static final ClassLoader classLoader = FileUtils.class.getClassLoader();

    // https://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file#comment53850993_20073154
    public static DirectoryStream<Path> getDirectoryStream(final String folderPath) {
        URI uri = null;
        try {
            final URL url = classLoader.getResource(folderPath);
            if(url == null) {
                MyLogger.severe("Error: File path is invalid");
                return null;
            }
            uri = url.toURI();
        } catch (URISyntaxException e) {
            MyLogger.severe("Error while parsing folder " + folderPath, e);
        }

        if(uri == null) {
            throw new IllegalArgumentException("Cannot locate directory " + folderPath);
        }

        if(uri.getScheme().contains("jar")) {
            // Is JAR file
            try {
                final URL jar = FileUtils.class.getProtectionDomain().getCodeSource().getLocation();

                // Trim out file string
                final Path jarFile = Paths.get(jar.toURI());
                final FileSystem fs = FileSystems.newFileSystem(jarFile, (ClassLoader) null);
                return Files.newDirectoryStream(fs.getPath(folderPath));
            } catch(IOException | URISyntaxException e) {
                MyLogger.severe("Error while parsing folder " + folderPath, e);
            }
        } else {
            // Is IDE file
            final Path path = Paths.get(uri);
            try {
                return Files.newDirectoryStream(path);
            } catch(IOException e) {
                MyLogger.severe("Error while parsing folder " + folderPath, e);
            }
        }

        return null;
    }

    public static InputStream getResourceStream(final String fileName) {
        return FileUtils.class.getClassLoader().getResourceAsStream(fileName);
    }
}
