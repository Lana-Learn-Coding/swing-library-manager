package io.lana.library.ui;

import java.awt.*;

public class FileException extends UIException {
    public FileException(String message) {
        this(null, message);
    }

    public FileException(Throwable e) {
        super(null, e.getMessage(), e);
    }

    public FileException(Component component, String message) {
        super(component, message);
    }
}
