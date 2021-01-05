package io.lana.library;

import com.formdev.flatlaf.FlatLightLaf;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.UIException;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class LibraryApplication implements CommandLineRunner, ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
            FlatLightLaf.install();
            JFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
            Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
                Class<?> errorClass = error.getClass();
                if (UIException.class.isAssignableFrom(errorClass)) {
                    UIException e = (UIException) error;
                    JOptionPane.showMessageDialog(e.getComponent(), e.getMessage());
                    return;
                }
                JOptionPane.showMessageDialog(null, "Unknown Error!");
                error.printStackTrace();
            });
        });
    }
}
