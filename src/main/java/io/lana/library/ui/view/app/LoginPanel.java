/*
 * Created by JFormDesigner on Mon Dec 21 14:26:28 ICT 2020
 */

package io.lana.library.ui.view.app;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.PasswordEncoder;
import io.lana.library.core.spi.UserRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.MainFrameContainer;
import io.lana.library.ui.UserContext;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component("startupPane")
public class LoginPanel extends JPanel implements MainFrameContainer {
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
        doLogin();
    }

    private void doLogin() {
        String password = String.valueOf(txtPassword.getPassword());
        String username = txtUsername.getText();
        if (StringUtils.isAnyBlank(password, username)) {
            JOptionPane.showMessageDialog(this, "Please enter username and password");
            return;
        }

        disableForm();
        WorkerUtils.runAsync(() -> {
            User user = userRepo.findByUsernameEquals(username)
                .orElseThrow(() -> new InputException(mainFrame, "User not exist"));

            if (passwordEncoder.matches(password, user.getPassword())) {
                userContext.setUser(user);
                mainFrame.switchContentPane(InitPanel.class);
                enableForm();
                return;
            }
            JOptionPane.showMessageDialog(this, "Wrong username or password");
            enableForm();
        });
    }

    @Override
    public <T extends Container & MainFrameContainer> void onPaneUnMounted(T nextPane) {
        txtPassword.setText("");
        txtUsername.setText("");
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

    private void txtPasswordActionPerformed(ActionEvent e) {
        doLogin();
    }

    private void txtUsernameActionPerformed(ActionEvent e) {
        txtPassword.requestFocusInWindow();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainPanel = new JPanel();
        lblHeader = new JLabel();
        lblUsername = new JLabel();
        txtUsername = new JTextField();
        lblPassword = new JLabel();
        txtPassword = new JPasswordField();
        btnLogin = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(20, 30, 20, 40));

        //======== mainPanel ========
        {
            mainPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) mainPanel.getLayout()).columnWidths = new int[]{0, 0, 80, 0};
            ((GridBagLayout) mainPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0};
            ((GridBagLayout) mainPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) mainPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};

            //---- lblHeader ----
            lblHeader.setText("Login to Library");
            lblHeader.setFont(new Font("Tahoma", lblHeader.getFont().getStyle(), 18));
            lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
            lblHeader.setForeground(Color.darkGray);
            mainPanel.add(lblHeader, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- lblUsername ----
            lblUsername.setText("Username");
            mainPanel.add(lblUsername, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 15), 0, 0));

            //---- txtUsername ----
            txtUsername.addActionListener(e -> txtUsernameActionPerformed(e));
            mainPanel.add(txtUsername, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- lblPassword ----
            lblPassword.setText("Password");
            mainPanel.add(lblPassword, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 15), 0, 0));

            //---- txtPassword ----
            txtPassword.addActionListener(e -> txtPasswordActionPerformed(e));
            mainPanel.add(txtPassword, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- btnLogin ----
            btnLogin.setText("Login");
            btnLogin.addActionListener(e -> btnLoginActionPerformed(e));
            mainPanel.add(btnLogin, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(mainPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(mainPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel mainPanel;
    private JLabel lblHeader;
    private JLabel lblUsername;
    private JTextField txtUsername;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
