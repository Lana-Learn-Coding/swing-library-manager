package io.lana.library.core.spi;

import java.io.InputStream;

public interface FileStorage {
    String loadFileToStorage(String path);

    void validateIsFile(String path);

    InputStream readFileFromStorage(String filename);

    boolean deleteFileFromStorage(String filename);
}
