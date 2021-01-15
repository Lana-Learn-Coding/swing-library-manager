package io.lana.library;

import io.lana.library.core.service.ServiceException;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.UIException;
import io.lana.library.ui.view.app.InitPanel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.swing.*;
import java.awt.*;

import static java.awt.Window.getWindows;

@SpringBootApplication
public class LibraryApplication implements CommandLineRunner, ApplicationContextAware {
    private ApplicationContext context;

    @Value("${application.theme.classname}")
    private String theme;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(LibraryApplication.class)
            .headless(false)
            .run(args);
    }

    @Override
    public void run(String... args) {
        EventQueue.invokeLater(() -> {
            installTheme(theme);
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setVisible(true);
            Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
                Class<?> errorClass = error.getClass();
                if (UIException.class.isAssignableFrom(errorClass)) {
                    UIException e = (UIException) error;
                    JOptionPane.showMessageDialog(e.getComponent(), e.getMessage());
                    return;
                }

                if (ServiceException.class.isAssignableFrom(errorClass)) {
                    ServiceException e = (ServiceException) error;
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    return;
                }

                error.printStackTrace();
                int confirm = JOptionPane.showConfirmDialog(null,
                    "Fatal error occurred! Application will restart", "Fatal Error", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.OK_OPTION) {
                    Window[] windows = getWindows();
                    for (Window window : windows) {
                        window.setVisible(false);
                    }
                    mainFrame.switchContentPane(InitPanel.class);
                    mainFrame.setVisible(true);
                    return;
                }

                System.exit(0);
            });
        });
    }

    public void installTheme(String classname) {
        try {
            UIManager.setLookAndFeel(theme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
