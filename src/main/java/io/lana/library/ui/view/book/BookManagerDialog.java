/*
 * Created by JFormDesigner on Mon Dec 28 08:23:41 ICT 2020
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.repo.BookRepo;
import io.lana.library.ui.component.book.BookTablePane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class BookManagerDialog extends JDialog {
    private BookRepo bookRepo;
    private BookMeta bookMeta;

    public BookManagerDialog(@Qualifier("mainFrame") Window owner) {
        super(owner);
        initComponents();
    }

    @Autowired
    public void setup(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public void setModel(BookMeta bookMeta) {
        this.bookMeta = bookMeta;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainTabbedPane = new JTabbedPane();
        formTab = new JPanel();
        bookTablePane = new BookTablePane();

        //======== this ========
        var contentPane = getContentPane();

        //======== mainTabbedPane ========
        {

            //======== formTab ========
            {

                GroupLayout formTabLayout = new GroupLayout(formTab);
                formTab.setLayout(formTabLayout);
                formTabLayout.setHorizontalGroup(
                    formTabLayout.createParallelGroup()
                        .addGap(0, 738, Short.MAX_VALUE)
                );
                formTabLayout.setVerticalGroup(
                    formTabLayout.createParallelGroup()
                        .addGap(0, 207, Short.MAX_VALUE)
                );
            }
            mainTabbedPane.addTab("text", formTab);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(mainTabbedPane)
                        .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane mainTabbedPane;
    private JPanel formTab;
    private BookTablePane bookTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
