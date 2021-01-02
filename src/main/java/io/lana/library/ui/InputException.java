package io.lana.library.ui;

import java.awt.*;

public class InputException extends UIException {
    public InputException(Component component, String message) {
        super(component, "Invalid: " + message);
    }

    public InputException(Component component, String message, Throwable cause) {
        super(component, "Invalid: " + message, cause);
    }
}
