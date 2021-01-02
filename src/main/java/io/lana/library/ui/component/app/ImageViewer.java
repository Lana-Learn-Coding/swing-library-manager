package io.lana.library.ui.component.app;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageViewer extends JLabel {

    private String path;

    public String getImagePath() {
        return path;
    }

    public void loadImage(String path) {
        if (path == null || path.trim().equals("")) {
            clearImage();
            return;
        }
        try (InputStream stream = new FileInputStream(path)) {
            Image img = ImageIO.read(stream);
            Image newImg = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(newImg));
            this.path = path;
        } catch (Exception e) {
            clearImage();
        }
    }

    public void loadImage(InputStream stream) {
        try {
            BufferedImage bufferedImage = ImageIO.read(stream);
            Image newImg = bufferedImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(newImg));
            this.path = null;
        } catch (Exception e) {
            clearImage();
        }
    }

    public void clearImage() {
        this.setIcon(null);
        path = null;
    }
}
