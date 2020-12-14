package io.lana.library;

import io.lana.library.ui.LoginUI;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        new LoginUI().setVisible(true);
    }

}
