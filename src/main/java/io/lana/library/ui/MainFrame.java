/*
 * Created by JFormDesigner on Mon Dec 21 01:06:14 ICT 2020
 */

package io.lana.library.ui;

import javax.swing.*;
import javax.swing.GroupLayout;

/**
 * @author unknown
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown

        //======== this ========
        setTitle("Library Manager App");
        setResizable(false);
        var contentPane = getContentPane();

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGap(0, 748, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGap(0, 393, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
