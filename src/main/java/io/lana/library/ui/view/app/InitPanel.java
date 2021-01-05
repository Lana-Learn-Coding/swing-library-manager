/*
 * Created by JFormDesigner on Tue Jan 05 12:35:10 ICT 2021
 */

package io.lana.library.ui.view.app;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.UserRepo;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.MainFrameContainer;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.view.book.BookMetaManagerPanel;
import io.lana.library.ui.view.reader.ReaderManagerPanel;
import io.lana.library.ui.view.user.UserManagerPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Component
public class InitPanel extends JPanel implements MainFrameContainer {
    private MainFrame mainFrame;

    private UserContext userContext;

    private ApplicationContext applicationContext;

    public InitPanel() {
        initComponents();
    }

    @Autowired
    public void setup(UserContext userContext, MainFrame mainFrame, ApplicationContext applicationContext) {
        this.mainFrame = mainFrame;
        this.applicationContext = applicationContext;
        this.userContext = userContext;
    }

    @Override
    public <T extends Container & MainFrameContainer> void onPaneMounted(T previousPane) {
        header.setText("Welcome, " + userContext.getUser().getName());
        subheader.setText(userContext.getUser().getUsername());

        progress.setValue(5);
        loadingText.setText("Initializing...");
        CrudPanel<BookMeta> bookMetaManagePanel = applicationContext.getBean(BookMetaManagerPanel.class);
        CrudPanel<Reader> readerMetaManagePanel = applicationContext.getBean(ReaderManagerPanel.class);
        CrudPanel<User> userCrudPanel = applicationContext.getBean(UserManagerPanel.class);
        progress.setValue(20);

        loadingText.setText("Loading User...");
        List<User> users = applicationContext.getBean(UserRepo.class).findAllByOrderByUpdatedAtDesc();
        boolean userSynced = syncUser(users);
        if (!userSynced) {
            JOptionPane.showMessageDialog(this, "User sync failed, please login again");
            userContext.logout();
            return;
        }
        userCrudPanel.renderTable(users);
        progress.setValue(40);

        loadingText.setText("Loading Book...");
        readerMetaManagePanel.renderTable();
        progress.setValue(70);

        loadingText.setText("Loading Reader...");
        bookMetaManagePanel.renderTable();
        progress.setValue(95);

        loadingText.setText("Getting Ready...");
        applicationContext.getBean(MainPanel.class);
        progress.setValue(100);

        delay(1000);
        mainFrame.switchContentPane(MainPanel.class);
    }

    @Override
    public <T extends Container & MainFrameContainer> void onPaneUnMounted(T nextPane) {
        progress.setValue(0);
        loadingText.setText("");
        this.header.setText("");
        this.subheader.setText("");
    }

    private void delay(int millis) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private boolean syncUser(List<User> users) {
        User loggedInUser = userContext.getUser();
        for (User user : users) {
            if (user.getId().equals(loggedInUser.getId())) {
                userContext.setUser(user);
                return true;
            }
        }
        return false;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainPanel = new JPanel();
        header = new JLabel();
        subheader = new JLabel();
        loadingText = new JLabel();
        progress = new JProgressBar();

        //======== this ========
        setBorder(new EmptyBorder(10, 10, 10, 10));

        //======== mainPanel ========
        {
            mainPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) mainPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
            ((GridBagLayout) mainPanel.getLayout()).rowHeights = new int[]{0, 0, 35, 0, 0};
            ((GridBagLayout) mainPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
            ((GridBagLayout) mainPanel.getLayout()).rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0E-4};

            //---- header ----
            header.setHorizontalAlignment(SwingConstants.CENTER);
            header.setFont(new Font("Tahoma", Font.PLAIN, 15));
            mainPanel.add(header, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- subheader ----
            subheader.setHorizontalAlignment(SwingConstants.CENTER);
            subheader.setFont(new Font("Tahoma", Font.PLAIN, 11));
            subheader.setVerticalAlignment(SwingConstants.TOP);
            subheader.setForeground(Color.gray);
            mainPanel.add(subheader, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));

            //---- loadingText ----
            loadingText.setFont(loadingText.getFont().deriveFont(12f));
            loadingText.setHorizontalAlignment(SwingConstants.CENTER);
            loadingText.setVerticalAlignment(SwingConstants.BOTTOM);
            loadingText.setForeground(Color.darkGray);
            mainPanel.add(loadingText, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 5), 0, 0));
            mainPanel.add(progress, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel mainPanel;
    private JLabel header;
    private JLabel subheader;
    private JLabel loadingText;
    private JProgressBar progress;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
