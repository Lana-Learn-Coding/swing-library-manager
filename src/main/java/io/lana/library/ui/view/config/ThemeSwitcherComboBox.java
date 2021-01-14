package io.lana.library.ui.view.config;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import io.lana.library.ui.component.app.ComboBox;
import io.lana.library.utils.WorkerUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.util.Arrays;

public class ThemeSwitcherComboBox extends ComboBox<UIManager.LookAndFeelInfo> {
    public ThemeSwitcherComboBox() {
        setRenderer(new BasicComboBoxRenderer() {
            public Component getListCellRendererComponent(JList list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                value = (value != null)
                    ? ((UIManager.LookAndFeelInfo) value).getName()
                    : UIManager.getLookAndFeel().getName();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        Arrays.asList(FlatAllIJThemes.INFOS).forEach(this::addItem);
        Arrays.asList(FlatAllDefaultThemes.INFOS).forEach(this::addItem);

        addActionListener(l -> WorkerUtils.runAsync(() -> {
            try {
                UIManager.LookAndFeelInfo lookAndFeelInfo = getSelectedItem();
                if (lookAndFeelInfo != null) {
                    FlatAnimatedLafChange.showSnapshot();
                    UIManager.setLookAndFeel(lookAndFeelInfo.getClassName());
                    FlatLaf.updateUI();
                    FlatAnimatedLafChange.hideSnapshotWithAnimation();
                }
            } catch (Exception e) {
                selectCurrentTheme();
            }
        }));
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName()))
                selectCurrentTheme();
        });
    }

    public void selectCurrentTheme() {
        setSelectedItem(getLookAndFeelInfo(UIManager.getLookAndFeel().getName()));
    }

    public UIManager.LookAndFeelInfo getLookAndFeelInfo(String className) {
        ComboBoxModel<UIManager.LookAndFeelInfo> model = getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            UIManager.LookAndFeelInfo lookAndFeelInfo = model.getElementAt(i);
            if (className.equals(lookAndFeelInfo.getClassName()))
                return lookAndFeelInfo;
        }
        return null;
    }
}
