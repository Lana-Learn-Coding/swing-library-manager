package io.lana.library.ui.view.config;

import io.lana.library.ui.FileException;
import io.lana.library.utils.DateFormatUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DefaultPropertiesPersister;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Properties;

@Component
public class ApplicationConfigDialog extends JDialog {
    private static final String RESOURCES_FILE = "application.properties";
    private final Properties properties = new Properties();

    public ApplicationConfigDialog() {
        initComponents();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(RESOURCES_FILE);
        if (inputStream == null) {
            throw new FileException("Config file not found: " + RESOURCES_FILE);
        }
        try {
            properties.load(inputStream);
        } catch (Exception e) {
            throw new FileException("Cannot load config: " + RESOURCES_FILE);
        }
    }

    private void okButtonActionPerformed(ActionEvent e) {
        loadFormToProperties();
        try (OutputStream out = new FileOutputStream(new File(getClass().getClassLoader().getResource(RESOURCES_FILE).toURI()))) {
            DefaultPropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
            propertiesPersister.store(properties, out, "Updated at: " + DateFormatUtils.toDateString(LocalDate.now()));
        } catch (Exception error) {
            throw new FileException("Cannot save config file");
        }
        JOptionPane.showMessageDialog(this, "Config saved. Restart application to take effect");
        System.exit(0);
    }

    private void loadFormToProperties() {
        UIManager.LookAndFeelInfo lookAndFeelInfo = selectTheme.getSelectedItem();
        if (lookAndFeelInfo != null) {
            properties.setProperty("application.theme.classname", lookAndFeelInfo.getClassName());
        } else {
            properties.setProperty("application.theme.classname", UIManager.getLookAndFeel().getClass().getName());
            selectTheme.selectCurrentTheme();
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        selectTheme = new ThemeSwitcherComboBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Config Application");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setBorder(new CompoundBorder(
                    new TitledBorder("Config"),
                    new EmptyBorder(10, 10, 15, 10)));
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                //---- label1 ----
                label1.setText("Theme");
                contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 15), 0, 0));
                contentPanel.add(selectTheme, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> okButtonActionPerformed(e));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private ThemeSwitcherComboBox selectTheme;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
