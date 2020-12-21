/*
 * Created by JFormDesigner on Mon Dec 21 14:55:20 ICT 2020
 */

package io.lana.library.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * @author unknown
 */
@Component
public class MainFrame extends JFrame {
    @Autowired
    @Qualifier("startupPanel")
    public void setStartupPanel(JPanel startupPanel) {
        setContentPane(startupPanel);
        pack();
    }

    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Library Manager App");
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        pack();
        setLocationRelativeTo(getOwner());
    }
}
