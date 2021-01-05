/*
 * Created by JFormDesigner on Mon Dec 21 14:26:28 ICT 2020
 */

package io.lana.library.ui.view.app;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.PasswordEncoder;
import io.lana.library.core.spi.UserRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component("startupPanel")
public class LoginPanel extends JPanel {
    private UserRepo userRepo;

    private PasswordEncoder passwordEncoder;

    private MainFrame mainFrame;

    private UserContext userContext;

    public LoginPanel() {
        initComponents();
    }

    @Autowired
    public void setup(UserRepo userRepo, PasswordEncoder passwordEncoder,
                      MainFrame mainFrame, UserContext userContext) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mainFrame = mainFrame;
        this.userContext = userContext;
    }

    private void btnLoginActionPerformed(ActionEvent e) {
        String password = String.valueOf(txtPassword.getPassword());
        String username = txtUsername.getText();
        if (StringUtils.isAnyBlank(password, username)) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        disableForm();
        User user = userRepo.findByUsernameEquals(username)
            .orElseThrow(() -> new InputException(mainFrame, "User not exist"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            userContext.setUser(user);
            mainFrame.setContentPane(InitPanel.class);
            enableForm();
            txtPassword.setText("");
            txtUsername.setText("");
            return;
        }
        JOptionPane.showMessageDialog(this, "Wrong username or password");
        enableForm();
    }

    private void disableForm() {
        btnLogin.setEnabled(false);
        txtPassword.setEnabled(false);
        txtUsername.setEnabled(false);
    }

    private void enableForm() {
        btnLogin.setEnabled(true);
        txtPassword.setEnabled(true);
        txtUsername.setEnabled(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblHeader = new JLabel();
        lblUsername = new JLabel();
        lblPassword = new JLabel();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(15, 30, 20, 30));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0, 0.0};

        //---- lblHeader ----
        lblHeader.setText("Login to Library");
        lblHeader.setFont(new Font("Roboto Light", Font.PLAIN, lblHeader.getFont().getSize() + 9));
        add(lblHeader, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 15, 15), 0, 0));

        //---- lblUsername ----
        lblUsername.setText("Username");
        add(lblUsername, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 15, 15), 0, 0));

        //---- lblPassword ----
        lblPassword.setText("Password");
        add(lblPassword, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 15, 15), 0, 0));
        add(txtUsername, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 15, 15), 0, 0));
        add(txtPassword, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 15, 15), 0, 0));

        //---- btnLogin ----
        btnLogin.setText("Login");
        btnLogin.addActionListener(e -> btnLoginActionPerformed(e));
        add(btnLogin, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 15), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblHeader;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
