/*
 * Created by JFormDesigner on Mon Dec 21 15:21:57 ICT 2020
 */

package io.lana.library.ui.view.book;

import javax.swing.*;
import javax.swing.border.*;

import io.lana.library.ui.component.app.AppPanel;
import io.lana.library.ui.component.book.*;
import io.lana.library.ui.component.book.BookMetaTablePane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.GroupLayout;

/**
 * @author unknown
 */
@Component
public class BookManagerPanel extends AppPanel {
    @Autowired
    public BookManagerPanel(BookMetaTablePane bookMetaTablePane, BookMetaFormPanel bookMetaFormPanel) {
        this.bookMetaTablePane = bookMetaTablePane;
        this.bookMetaFormPanel = bookMetaFormPanel;
        initComponents();
    }

    private void createUIComponents() {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        createUIComponents();

        mainTabbedPane = new JTabbedPane();
        formPanel = new JPanel();
        btnSave = new JButton();
        btnClear = new JButton();
        btnDelete = new JButton();
        btnShowBook = new JButton();

        //======== this ========
        setBorder(null);

        //======== mainTabbedPane ========
        {

            //======== formPanel ========
            {
                formPanel.setBorder(new EtchedBorder());

                //---- bookMetaFormPanel ----
                bookMetaFormPanel.setBorder(new TitledBorder("Book Info"));

                //---- btnSave ----
                btnSave.setText("Save");

                //---- btnClear ----
                btnClear.setText("Clear");

                //---- btnDelete ----
                btnDelete.setText("Delete");

                //---- btnShowBook ----
                btnShowBook.setText("View Book");

                GroupLayout formPanelLayout = new GroupLayout(formPanel);
                formPanel.setLayout(formPanelLayout);
                formPanelLayout.setHorizontalGroup(
                    formPanelLayout.createParallelGroup()
                        .addGroup(formPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(bookMetaFormPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnShowBook, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(23, Short.MAX_VALUE))
                );
                formPanelLayout.setVerticalGroup(
                    formPanelLayout.createParallelGroup()
                        .addGroup(formPanelLayout.createSequentialGroup()
                            .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addGroup(formPanelLayout.createSequentialGroup()
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(bookMetaFormPanel, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE))
                                .addGroup(formPanelLayout.createSequentialGroup()
                                    .addGap(19, 19, 19)
                                    .addComponent(btnShowBook)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSave)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnClear)
                                    .addGap(17, 17, 17)
                                    .addComponent(btnDelete)
                                    .addGap(6, 6, 6)))
                            .addContainerGap(8, Short.MAX_VALUE))
                );
            }
            mainTabbedPane.addTab("Book Manager", formPanel);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mainTabbedPane))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 285, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(bookMetaTablePane, GroupLayout.PREFERRED_SIZE, 284, GroupLayout.PREFERRED_SIZE)
                    .addGap(15, 15, 15))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private BookMetaTablePane bookMetaTablePane;
    private JTabbedPane mainTabbedPane;
    private JPanel formPanel;
    private BookMetaFormPanel bookMetaFormPanel;
    private JButton btnSave;
    private JButton btnClear;
    private JButton btnDelete;
    private JButton btnShowBook;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
