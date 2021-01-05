package io.lana.library.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainFrame extends JFrame {
    @Autowired
    @Qualifier("startupPanel")
    public void setContentPane(Container container) {
        super.setContentPane(container);
        setSize(container.getSize());
        pack();
        setLocationRelativeTo(null);
    }

    public MainFrame() {
        setTitle("Library Manager App");
        setResizable(true);
    }
}
