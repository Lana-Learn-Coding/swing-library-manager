/*
 * Created by JFormDesigner on Tue Jan 05 12:35:10 ICT 2021
 */

package io.lana.library.ui.view.app;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.ui.MainFrame;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.view.book.BookMetaManagerPanel;
import io.lana.library.ui.view.reader.ReaderManagerPanel;
import io.lana.library.utils.WorkerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class InitPanel extends JPanel {
    private UserContext userContext;

    private MainFrame mainFrame;

    private ApplicationContext applicationContext;

    public InitPanel() {
        initComponents();
    }

    @Autowired
    public void setup(UserContext userContext, MainFrame mainFrame, ApplicationContext applicationContext) {
        this.userContext = userContext;
        this.mainFrame = mainFrame;
        this.applicationContext = applicationContext;
        WorkerUtils.runAsync(this::initApp);
    }

    private void initApp() {
        CrudPanel<BookMeta> bookMetaManagePanel = applicationContext.getBean(BookMetaManagerPanel.class);
        CrudPanel<Reader> readerMetaManagePanel = applicationContext.getBean(ReaderManagerPanel.class);
        readerMetaManagePanel.renderTable();
        bookMetaManagePanel.renderTable();
        mainFrame.setContentPane(MainPanel.class);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGap(0, 385, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGap(0, 190, Short.MAX_VALUE)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
