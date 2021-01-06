package io.lana.library.ui.component.app;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

public class TextField extends JTextField {
    @Override
    public String getText() {
        String text = super.getText();
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return text.trim();
    }
}
