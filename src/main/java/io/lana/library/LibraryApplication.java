package io.lana.library;

import com.formdev.flatlaf.FlatLightLaf;
import io.lana.library.ui.view.LoginUI;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        FlatLightLaf.install();
        new LoginUI().setVisible(true);
    }

}
