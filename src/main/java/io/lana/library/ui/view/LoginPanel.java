/*
 * Created by JFormDesigner on Mon Dec 21 14:26:28 ICT 2020
 */

package io.lana.library.ui.view;

import io.lana.library.ui.component.app.AppPanel;
import io.lana.library.ui.view.book.BookManagerPanel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

@Component("startupPanel")
public class LoginPanel extends AppPanel {
    public LoginPanel() {
        initComponents();
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        String password = txtPassword.getText();
        String username = txtUsername.getText();
        if (StringUtils.isAllBlank(password, username)) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }
        final String ADMIN = "admin";
        if (!password.equals(ADMIN) || !username.equals(ADMIN)) {
            JOptionPane.showMessageDialog(this, "Wrong username or password");
            return;
        }
        gotoPanel(BookManagerPanel.class);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblUsername = new JLabel();
        lblPassword = new JLabel();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        lblHeader = new JLabel();
        btnLogin = new JButton();

        //======== this ========

        //---- lblUsername ----
        lblUsername.setText("Username");

        //---- lblPassword ----
        lblPassword.setText("Password");

        //---- lblHeader ----
        lblHeader.setText("Login to Library");
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setFont(new Font("Roboto Light", Font.PLAIN, lblHeader.getFont().getSize() + 9));

        //---- btnLogin ----
        btnLogin.setText("Login");
        btnLogin.addActionListener(e -> btnLoginActionPerformed(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(26, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(lblUsername)
                                .addComponent(lblPassword))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup()
                                .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
                            .addGap(26, 26, 26))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(lblHeader, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                            .addGap(69, 69, 69))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                            .addGap(128, 128, 128))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(lblHeader, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblUsername))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblPassword))
                    .addGap(27, 27, 27)
                    .addComponent(btnLogin)
                    .addContainerGap(33, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblHeader;
    private JButton btnLogin;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
