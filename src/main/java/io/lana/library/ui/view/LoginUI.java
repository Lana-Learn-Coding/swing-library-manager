/*
 * Created by JFormDesigner on Mon Dec 21 01:08:27 ICT 2020
 */

package io.lana.library.ui.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author unknown
 */
public class LoginUI extends JPanel {
    private JFrame main;

    public LoginUI(JFrame main) {
        initComponents();
    }

    private void login(ActionEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        textField1 = new JTextField();
        textField2 = new JTextField();
        lblUsername = new JLabel();
        lblPassword = new JLabel();
        btnLogin = new JButton();
        label2 = new JLabel();

        //======== this ========
        setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.
        border.EmptyBorder(0,0,0,0), "JFor\u006dDesi\u0067ner \u0045valu\u0061tion",javax.swing.border.TitledBorder.CENTER
        ,javax.swing.border.TitledBorder.BOTTOM,new java.awt.Font("Dia\u006cog",java.awt.Font
        .BOLD,12),java.awt.Color.red), getBorder())); addPropertyChangeListener(
        new java.beans.PropertyChangeListener(){@Override public void propertyChange(java.beans.PropertyChangeEvent e){if("bord\u0065r"
        .equals(e.getPropertyName()))throw new RuntimeException();}});

        //---- lblUsername ----
        lblUsername.setText("Username");

        //---- lblPassword ----
        lblPassword.setText("Password");

        //---- btnLogin ----
        btnLogin.setText("Login");
        btnLogin.addActionListener(e -> login(e));

        //---- label2 ----
        label2.setText("Login to Library");
        label2.setFont(new Font("Roboto Light", label2.getFont().getStyle(), label2.getFont().getSize() + 10));
        label2.setHorizontalAlignment(SwingConstants.CENTER);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(153, 153, 153)
                    .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(167, Short.MAX_VALUE))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(25, 25, 25)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(lblUsername, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                        .addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(textField1, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addComponent(textField2, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                        .addComponent(label2, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE))
                    .addGap(22, 22, 22))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(label2, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblUsername, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addComponent(textField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(18, 18, 18)
                    .addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(16, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JTextField textField1;
    private JTextField textField2;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JButton btnLogin;
    private JLabel label2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
