/*
 * Created by JFormDesigner on Mon Dec 28 08:23:41 ICT 2020
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.repo.BookRepo;
import io.lana.library.ui.component.book.BookTablePane;
import io.lana.library.ui.view.CrudPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class BookManagerDialog extends JDialog implements CrudPanel<Book> {
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

    @Override
    public void delete() {

    }

    @Override
    public void save() {

    }

    @Override
    public void clearForm() {

    }

    @Override
    public void loadModelToForm(Book model) {

    }

    @Override
    public Book getModelFromForm() {
        return null;
    }

    @Override
    public void renderTable(Collection<Book> data) {
        bookTablePane.setTableData(data);
    }

    @Override
    public void renderTable() {
        if (bookMeta == null) {
            renderTable(new ArrayList<>());
            return;
        }
        renderTable(bookMeta.getBooks());
    }

    public void setModel(BookMeta bookMeta) {
        this.bookMeta = bookMeta;
        renderTable();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainTabbedPane = new JTabbedPane();
        formTab = new JPanel();
        panel1 = new JPanel();
        label1 = new JLabel();
        label4 = new JLabel();
        label5 = new JLabel();
        label6 = new JLabel();
        textField3 = new JTextField();
        label7 = new JLabel();
        textField4 = new JTextField();
        textField1 = new JTextField();
        label3 = new JLabel();
        textField6 = new JTextField();
        comboBox1 = new JComboBox();
        btnNewStorage = new JButton();
        comboBox2 = new JComboBox();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        textArea1 = new JTextArea();
        bookTablePane = new BookTablePane();

        //======== this ========
        setTitle("Book List Detail");
        var contentPane = getContentPane();

        //======== mainTabbedPane ========
        {
            mainTabbedPane.setBorder(null);

            //======== formTab ========
            {
                formTab.setBorder(new EtchedBorder());

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder("Book Form"));

                    //---- label4 ----
                    label4.setText("ID");

                    //---- label5 ----
                    label5.setText("Meta");

                    //---- label6 ----
                    label6.setText("Storage");

                    //---- textField3 ----
                    textField3.setEditable(false);

                    //---- label7 ----
                    label7.setText("Condition");

                    //---- textField1 ----
                    textField1.setEditable(false);

                    //---- label3 ----
                    label3.setText("Position");

                    //---- btnNewStorage ----
                    btnNewStorage.setText("New");

                    //---- label2 ----
                    label2.setText("Note");

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(textArea1);
                    }

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(label1)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(label6)
                                        .addGap(18, 18, 18)
                                        .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnNewStorage))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(label4)
                                                .addGap(12, 12, 12))
                                            .addComponent(label5, GroupLayout.Alignment.TRAILING))
                                        .addGap(30, 30, 30)
                                        .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(textField3, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(label7)
                                                .addGap(18, 18, 18)
                                                .addComponent(textField4, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
                                            .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(label3)
                                            .addComponent(label2))
                                        .addGap(18, 18, 18)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(textField6)
                                            .addComponent(scrollPane1))))
                                .addContainerGap(169, Short.MAX_VALUE))
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label4)
                                    .addComponent(textField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(textField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label7))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(label1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label5)
                                    .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label6)
                                    .addComponent(comboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnNewStorage, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label3)
                                    .addComponent(textField6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(label2)
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(38, Short.MAX_VALUE))
                    );
                }

                GroupLayout formTabLayout = new GroupLayout(formTab);
                formTab.setLayout(formTabLayout);
                formTabLayout.setHorizontalGroup(
                    formTabLayout.createParallelGroup()
                        .addGroup(formTabLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(129, Short.MAX_VALUE))
                );
                formTabLayout.setVerticalGroup(
                    formTabLayout.createParallelGroup()
                        .addGroup(formTabLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addContainerGap())
                );
            }
            mainTabbedPane.addTab("Book List", formTab);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(contentPaneLayout.createParallelGroup()
                        .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                        .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(bookTablePane, GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane mainTabbedPane;
    private JPanel formTab;
    private JPanel panel1;
    private JLabel label1;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JTextField textField3;
    private JLabel label7;
    private JTextField textField4;
    private JTextField textField1;
    private JLabel label3;
    private JTextField textField6;
    private JComboBox comboBox1;
    private JButton btnNewStorage;
    private JComboBox comboBox2;
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JTextArea textArea1;
    private BookTablePane bookTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
