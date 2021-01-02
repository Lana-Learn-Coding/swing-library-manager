package io.lana.library.ui.component.app;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.Optional;

public class ImagePicker extends JFileChooser {
    public ImagePicker() {
        setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "png");
        addChoosableFileFilter(filter);
    }

    public Optional<File> showSelectImageDialog() {
        if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            return Optional.ofNullable(getSelectedFile());
        }
        return Optional.empty();
    }
}
