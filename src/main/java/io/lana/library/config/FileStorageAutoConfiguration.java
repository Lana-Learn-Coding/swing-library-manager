package io.lana.library.config;

import io.lana.library.core.spi.FileStorage;
import io.lana.library.ui.FileException;
import org.apache.commons.vfs2.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.Optional;
import java.util.UUID;

@Configuration
@ConditionalOnClass(FileStorage.class)
@ConfigurationProperties(prefix = "file-storage")
public class FileStorageAutoConfiguration {

    private String storagePath;

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public static class DefaultFileManager implements FileStorage {
        private final FileSystemManager fileManager;
        private final FileObject baseStorage;

        public DefaultFileManager(String baseStoragePath) {
            try {
                fileManager = VFS.getManager();
                baseStorage = fileManager.resolveFile(baseStoragePath);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String loadFileToStorage(String path) {
            try (FileObject local = fileManager.resolveFile(path);
                 FileObject storage = fileManager.resolveFile(baseStorage, generateRandomFileName(path))) {
                storage.copyFrom(local, Selectors.SELECT_SELF);
                return storage.getName().toString();
            } catch (Exception e) {
                throw new FileException(e);
            }
        }

        @Override
        public void validateIsFile(String path) {
            File file = new File(path);
            if (!file.exists()) {
                throw new FileException("file not found: " + path);
            }

            if (!file.isFile()) {
                throw new FileException("is not a file: " + path);
            }
        }

        @Override
        public InputStream readFileFromStorage(String filename) {
            FileObject fileObject = null;
            try {
                fileObject = fileManager.resolveFile(baseStorage, filename);
                return fileObject.getContent().getInputStream();
            } catch (Exception e) {
                try {
                    fileObject.close();
                } catch (Exception closeFileException) {
                    throw new RuntimeException("Error reading file from storage: " + filename + ". Cannot close file");
                }
                throw new RuntimeException("Cannot read file from storage: " + filename);
            }
        }

        @Override
        public boolean deleteFileFromStorage(String filename) {
            try (FileObject storage = fileManager.resolveFile(baseStorage, filename)) {
                return storage.delete();
            } catch (Exception e) {
                throw new FileException("file not found in storage: " + filename);
            }
        }

        private String generateRandomFileName(String originalPath) {
            String ext = getFileExtension(originalPath);
            String newFileName = UUID.randomUUID().toString();
            return newFileName + "." + ext;
        }

        private String getFileExtension(String path) {
            validateIsFile(path);

            return Optional.ofNullable(path)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(path.lastIndexOf(".") + 1))
                .orElse(null);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public FileStorage fileStorage() {
        return new DefaultFileManager(storagePath);
    }
}

