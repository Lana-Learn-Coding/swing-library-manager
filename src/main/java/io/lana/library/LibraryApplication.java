package io.lana.library;

import com.formdev.flatlaf.FlatLightLaf;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.view.LoginUI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import javax.swing.*;

@SpringBootApplication
public class LibraryApplication implements CommandLineRunner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(LibraryApplication.class)
            .headless(false)
            .run(args);
    }

    @Override
    public void run(String... args) {
        FlatLightLaf.install();
        JFrame mainFrame = new MainFrame();
        JPanel loginPanel = new LoginUI(mainFrame);
        mainFrame.setContentPane(loginPanel);
        mainFrame.setVisible(true);
        mainFrame.pack();
    }
}
