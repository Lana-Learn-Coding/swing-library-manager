/*
 * Created by JFormDesigner on Mon Dec 21 14:26:28 ICT 2020
 */

package io.lana.library.ui.view.app;

import io.lana.library.ui.component.app.AppPanel;
import io.lana.library.ui.view.book.BookMetaManagerPanel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component("startupPanel")
public class LoginPanel extends AppPanel {
    public LoginPanel() {
        initComponents();
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        if (checkUserIsValid()) {
            gotoPanel(BookMetaManagerPanel.class);
        }
    }

    private boolean checkUserIsValid() {
        String password = txtPassword.getText();
        String username = txtUsername.getText();
        if (StringUtils.isAllBlank(password, username)) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return false;
        }
        final String ADMIN = "admin";
        if (!password.equals(ADMIN) || !username.equals(ADMIN)) {
            JOptionPane.showMessageDialog(this, "Wrong username or password");
            return false;
        }
        return true;
    }

    private void btnHackActionPerformed(ActionEvent e) {
        gotoPanel(BookMetaManagerPanel.class);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblUsername = new JLabel();
        lblPassword = new JLabel();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        lblHeader = new JLabel();
        btnLogin = new JButton();
        btnHack = new JButton();

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

        //---- btnHack ----
        btnHack.setText("Hack");
        btnHack.addActionListener(e -> btnHackActionPerformed(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(lblPassword)
                        .addComponent(lblUsername))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblHeader, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnHack, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(lblHeader, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUsername)
                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPassword)
                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(btnHack)
                        .addComponent(btnLogin))
                    .addContainerGap(27, Short.MAX_VALUE))
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
    private JButton btnHack;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
