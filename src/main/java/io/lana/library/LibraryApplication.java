package io.lana.library;

import com.formdev.flatlaf.FlatLightLaf;
import io.lana.library.ui.view.LoginUI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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
        new LoginUI().setVisible(true);
    }
}
