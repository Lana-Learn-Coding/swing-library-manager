package io.lana.library.ui;

import lombok.Getter;

import java.awt.*;

@Getter
public class UIException extends RuntimeException {
    private final Component component;

    public UIException(Component component, String message) {
        super(message);
        this.component = component;
    }

    public UIException(Component component, String message, Throwable cause) {
        super(message, cause);
        this.component = component;
    }
}
