package io.lana.library.ui.view.config;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class FlatAllDefaultThemes {
    public static final UIManager.LookAndFeelInfo[] INFOS = {
        new UIManager.LookAndFeelInfo(FlatDarkLaf.NAME, FlatDarkLaf.class.getName()),
        new UIManager.LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName()),
        new UIManager.LookAndFeelInfo(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName()),
    };
}
