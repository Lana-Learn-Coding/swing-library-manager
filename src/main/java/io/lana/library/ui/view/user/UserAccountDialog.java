/*
 * Created by JFormDesigner on Thu Jan 14 23:55:56 ICT 2021
 */

package io.lana.library.ui.view.user;

import io.lana.library.core.model.user.User;
import io.lana.library.core.service.UserService;
import io.lana.library.core.spi.PasswordEncoder;
import io.lana.library.ui.InputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component
public class UserAccountDialog extends JDialog {
    private User user;
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    public UserAccountDialog() {
        initComponents();
        setSize(600, 425);
        checkChangePassword.addActionListener(e -> {
            txtNewPassword.setText("");
            txtPassword.setText("");
            txtConfirm.setText("");
            txtNewPassword.setEnabled(checkChangePassword.isSelected());
            txtPassword.setEnabled(checkChangePassword.isSelected());
            txtConfirm.setEnabled(checkChangePassword.isSelected());
        });
    }

    @Autowired
    public void setup(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void setModel(User user) {
        this.user = user;
        txtUser.setText(user.getIdString() + " - " + user.getUsername());
        txtEmail.setText(user.getEmail());
        txtName.setText(user.getName());
        txtPhone.setText(user.getPhoneNumber());
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            clearForm();
        }
    }

    private void clearForm() {
        txtUser.setText("");
        txtEmail.setText("");
        txtName.setText("");
        txtPhone.setText("");
        checkChangePassword.setSelected(false);
    }

    private User getModelFromForm() {
        User user = new User();
        user.setName(txtName.getText());
        user.setEmail(txtEmail.getText());
        user.setPhoneNumber(txtPhone.getText());
        if (checkChangePassword.isSelected()) {
            String oldPassword = String.valueOf(txtPassword.getPassword());
            String newPassword = String.valueOf(txtNewPassword.getPassword());
            String confirmPassword = String.valueOf(txtConfirm.getPassword());
            if (newPassword.length() < 8) {
                throw new InputException(this, "New password must at least 8 character");
            }
            if (!StringUtils.equals(newPassword, confirmPassword)) {
                throw new InputException(this, "New password and confirmation is not matching");
            }
            if (!passwordEncoder.matches(oldPassword, this.user.getPassword())) {
                throw new InputException(this, "Wrong password!");
            }
            if (StringUtils.equals(oldPassword, newPassword)) {
                throw new InputException(this, "New password must different than old ones");
            }
            user.setPassword(newPassword);
        }
        if (StringUtils.isBlank(user.getName())) {
            throw new InputException(this, "Please enter required information: name");
        }

        if (StringUtils.isNotBlank(user.getEmail()) && !user.getEmail().matches(".+@.+")) {
            throw new InputException(this, "Invalid email format");
        }

        if (StringUtils.isNotBlank(user.getPhoneNumber()) && !user.getPhoneNumber().matches("[0-9]{10,15}")) {
            throw new InputException(this, "Invalid phone format: contain only number, and 10 - 15 length");
        }
        return user;
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void okButtonActionPerformed(ActionEvent e) {
        User user = getModelFromForm();
        user.setId(this.user.getId());
        user.setUsername(this.user.getUsername());
        user.setPermissions(this.user.getPermissions());
        userService.updateUser(user);
        JOptionPane.showMessageDialog(this, "Your information has updated");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        contentPanel = new JPanel();
        label1 = new JLabel();
        txtUser = new JTextField();
        label2 = new JLabel();
        txtName = new JTextField();
        label3 = new JLabel();
        txtEmail = new JTextField();
        label4 = new JLabel();
        txtPhone = new JTextField();
        separator1 = new JSeparator();
        label5 = new JLabel();
        txtPassword = new JPasswordField();
        label6 = new JLabel();
        txtNewPassword = new JPasswordField();
        label7 = new JLabel();
        txtConfirm = new JPasswordField();
        checkChangePassword = new JCheckBox();

        //======== this ========
        setTitle("Update Account Information");
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

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

            //======== contentPanel ========
            {
                contentPanel.setBorder(new CompoundBorder(
                    new TitledBorder("Account"),
                    new EmptyBorder(8, 10, 10, 10)));
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 20, 0, 0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- label1 ----
                label1.setText("User");
                contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- txtUser ----
                txtUser.setEditable(false);
                contentPanel.add(txtUser, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label2 ----
                label2.setText("Name");
                contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));
                contentPanel.add(txtName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label3 ----
                label3.setText("Email");
                contentPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));
                contentPanel.add(txtEmail, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label4 ----
                label4.setText("Phone Number");
                contentPanel.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));
                contentPanel.add(txtPhone, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));
                contentPanel.add(separator1, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label5 ----
                label5.setText("Password");
                contentPanel.add(label5, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- txtPassword ----
                txtPassword.setEnabled(false);
                contentPanel.add(txtPassword, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label6 ----
                label6.setText("New Password");
                contentPanel.add(label6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- txtNewPassword ----
                txtNewPassword.setEnabled(false);
                contentPanel.add(txtNewPassword, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- label7 ----
                label7.setText("Confirmation");
                contentPanel.add(label7, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- txtConfirm ----
                txtConfirm.setEnabled(false);
                contentPanel.add(txtConfirm, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- checkChangePassword ----
                checkChangePassword.setText("Change Password");
                contentPanel.add(checkChangePassword, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField txtUser;
    private JLabel label2;
    private JTextField txtName;
    private JLabel label3;
    private JTextField txtEmail;
    private JLabel label4;
    private JTextField txtPhone;
    private JSeparator separator1;
    private JLabel label5;
    private JPasswordField txtPassword;
    private JLabel label6;
    private JPasswordField txtNewPassword;
    private JLabel label7;
    private JPasswordField txtConfirm;
    private JCheckBox checkChangePassword;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
