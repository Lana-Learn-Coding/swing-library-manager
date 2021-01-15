package io.lana.library.ui.view.config;

import io.lana.library.LibraryApplication;
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
import java.util.ArrayList;
import java.util.List;
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
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(this, "Config saved. Restart now?")) {
            doRestart();
        }
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

    private void doRestart() {
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                        final File currentJar = new File(LibraryApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

                        /* is it a jar file? */
                        if (!currentJar.getName().endsWith(".jar"))
                            return;

                        /* Build command: java -jar application.jar */
                        final List<String> command = new ArrayList<String>();
                        command.add(javaBin);
                        command.add("-jar");
                        command.add(currentJar.getPath());

                        final ProcessBuilder builder = new ProcessBuilder(command);
                        builder.start();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Cannot restart application. shutting down");
                    }
                    System.exit(0);
                }
            },
            1000);
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
        separator1 = new JSeparator();
        label2 = new JLabel();
        comboBox1 = new JComboBox();
        label3 = new JLabel();
        textField1 = new JTextField();
        label4 = new JLabel();
        textField2 = new JTextField();
        label5 = new JLabel();
        passwordField1 = new JPasswordField();
        label6 = new JLabel();
        textField3 = new JTextField();
        button1 = new JButton();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Config Application");
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
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
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{0, 25, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- label1 ----
                label1.setText("Theme");
                contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(selectTheme, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));
                contentPanel.add(separator1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- label2 ----
                label2.setText("DB Driver");
                contentPanel.add(label2, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(comboBox1, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label3 ----
                label3.setText("Connection Url");
                contentPanel.add(label3, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(textField1, new GridBagConstraints(1, 3, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label4 ----
                label4.setText("Username");
                contentPanel.add(label4, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(textField2, new GridBagConstraints(1, 4, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label5 ----
                label5.setText("Password");
                contentPanel.add(label5, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(passwordField1, new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label6 ----
                label6.setText("File Storage");
                contentPanel.add(label6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 15), 0, 0));
                contentPanel.add(textField3, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 15), 0, 0));

                //---- button1 ----
                button1.setText("text");
                contentPanel.add(button1, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0,
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
    private JSeparator separator1;
    private JLabel label2;
    private JComboBox comboBox1;
    private JLabel label3;
    private JTextField textField1;
    private JLabel label4;
    private JTextField textField2;
    private JLabel label5;
    private JPasswordField passwordField1;
    private JLabel label6;
    private JTextField textField3;
    private JButton button1;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
