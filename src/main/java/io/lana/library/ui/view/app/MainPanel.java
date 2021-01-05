package io.lana.library.ui.view.app;

import io.lana.library.ui.MainFrameContainer;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.view.book.BookMetaManagerPanel;
import io.lana.library.ui.view.reader.ReaderManagerPanel;
import io.lana.library.ui.view.user.UserManagerPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

@Component
public class MainPanel extends JPanel implements MainFrameContainer {
    private final UserContext userContext;

    private enum Views {
        BOOK_MANAGE,
        READER_MANAGE,
        USER_MANAGE,
    }

    @Autowired
    public MainPanel(BookMetaManagerPanel bookMetaManagerPanel, ReaderManagerPanel readerManagerPanel,
                     UserManagerPanel userManagerPanel, UserContext userContext) {
        initComponents();
        this.userContext = userContext;
        mainPanel.add(Views.BOOK_MANAGE.name(), bookMetaManagerPanel);
        mainPanel.add(Views.READER_MANAGE.name(), readerManagerPanel);
        mainPanel.add(Views.USER_MANAGE.name(), userManagerPanel);
    }

    private void btnBookManageActionPerformed(ActionEvent e) {
        showView(Views.BOOK_MANAGE);
    }

    private void btnReaderManageActionPerformed(ActionEvent e) {
        showView(Views.READER_MANAGE);
    }

    private void showView(Views view) {
        CardLayout layout = (CardLayout) mainPanel.getLayout();
        layout.show(mainPanel, view.name());
    }

    private void btnExitActionPerformed(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this, "Log out ?");
        if (confirm == JOptionPane.OK_OPTION) {
            userContext.logout();
        }
    }

    private void btnUserManageActionPerformed(ActionEvent e) {
        showView(Views.USER_MANAGE);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        menuPanel = new JPanel();
        btnBookManage = new JButton();
        btnReaderManage = new JButton();
        btnUserManage = new JButton();
        btnAccount = new JButton();
        btnExit = new JButton();
        mainPanel = new JPanel();

        //======== this ========
        setBorder(new EmptyBorder(5, 0, 20, 0));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[]{0, 0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[]{0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

        //======== menuPanel ========
        {
            menuPanel.setBorder(new EmptyBorder(10, 15, 0, 15));
            menuPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) menuPanel.getLayout()).columnWidths = new int[]{90, 0};
            ((GridBagLayout) menuPanel.getLayout()).rowHeights = new int[]{45, 45, 45, 115, 0, 0, 0};
            ((GridBagLayout) menuPanel.getLayout()).columnWeights = new double[]{0.0, 1.0E-4};
            ((GridBagLayout) menuPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

            //---- btnBookManage ----
            btnBookManage.setText("Book Manage");
            btnBookManage.setFocusPainted(false);
            btnBookManage.setFocusable(false);
            btnBookManage.addActionListener(e -> btnBookManageActionPerformed(e));
            menuPanel.add(btnBookManage, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- btnReaderManage ----
            btnReaderManage.setText("Reader Manage");
            btnReaderManage.setFocusPainted(false);
            btnReaderManage.setFocusable(false);
            btnReaderManage.addActionListener(e -> btnReaderManageActionPerformed(e));
            menuPanel.add(btnReaderManage, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- btnUserManage ----
            btnUserManage.setText("User Manage");
            btnUserManage.setFocusPainted(false);
            btnUserManage.setFocusable(false);
            btnUserManage.addActionListener(e -> btnUserManageActionPerformed(e));
            menuPanel.add(btnUserManage, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- btnAccount ----
            btnAccount.setText("Account");
            btnAccount.setFocusPainted(false);
            btnAccount.setFocusable(false);
            menuPanel.add(btnAccount, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 15, 0), 0, 0));

            //---- btnExit ----
            btnExit.setText("Exit");
            btnExit.setFocusPainted(false);
            btnExit.setFocusable(false);
            btnExit.addActionListener(e -> btnExitActionPerformed(e));
            menuPanel.add(btnExit, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        add(menuPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 5), 0, 0));

        //======== mainPanel ========
        {
            mainPanel.setBorder(null);
            mainPanel.setLayout(new CardLayout());
        }
        add(mainPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel menuPanel;
    private JButton btnBookManage;
    private JButton btnReaderManage;
    private JButton btnUserManage;
    private JButton btnAccount;
    private JButton btnExit;
    private JPanel mainPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
