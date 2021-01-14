package io.lana.library.ui.view.reader;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.ui.component.ReaderBorrowHistoryTablePane;
import io.lana.library.ui.component.app.ListPane;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BorrowHistoryDialog extends JDialog {
    private Reader reader;

    public BorrowHistoryDialog() {
        initComponents();
        ListSelectionModel selectionModel = historyTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = historyTablePane.getSelectedRowIndex();
            if (pos < 0) {
                txtBook.setText("");
                txtAuthor.setText("");
                ticketList.clearListData();
                return;
            }
            BookMeta bookMeta = historyTablePane.getRow(pos);
            txtBook.setText(bookMeta.getId() + " - " + bookMeta.getTitle());
            txtAuthor.setText(bookMeta.getAuthor());

            Set<Ticket> readerTickets = bookMeta.getBooks().stream()
                .flatMap(book -> book.getTickets().stream())
                .filter(ticket -> ticket.getBorrower().equals(reader))
                .collect(Collectors.toSet());
            ticketList.setListData(readerTickets);
        });
    }

    public void setModel(Reader reader) {
        this.reader = reader;
        txtReader.setText(reader.getIdString() + " - " + reader.getName());
        txtPhone.setText(reader.getPhoneNumber());
        historyTablePane.setTableData(reader);
    }

    private void clearForm() {
        txtReader.setText("");
        txtPhone.setText("");
        txtBook.setText("");
        txtAuthor.setText("");
        ticketList.clearListData();
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            clearForm();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainPanel = new JPanel();
        historyTablePane = new ReaderBorrowHistoryTablePane();
        form = new JPanel();
        label1 = new JLabel();
        txtReader = new JTextField();
        label4 = new JLabel();
        txtPhone = new JTextField();
        label2 = new JLabel();
        txtBook = new JTextField();
        label5 = new JLabel();
        txtAuthor = new JTextField();
        label3 = new JLabel();
        ticketList = new ListPane<>();

        //======== this ========
        setTitle("REader Borrow History");
        var contentPane = getContentPane();

        //======== mainPanel ========
        {
            mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

            //---- historyTablePane ----
            historyTablePane.setBorder(null);

            //======== form ========
            {
                form.setBorder(new CompoundBorder(
                    new TitledBorder("Information"),
                    new EmptyBorder(15, 15, 20, 15)));
                form.setLayout(new GridBagLayout());
                ((GridBagLayout) form.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
                ((GridBagLayout) form.getLayout()).rowHeights = new int[]{0, 0, 0, 40, 0};
                ((GridBagLayout) form.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) form.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- label1 ----
                label1.setText("Reader");
                form.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- txtReader ----
                txtReader.setEditable(false);
                form.add(txtReader, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- label4 ----
                label4.setText("Phone");
                form.add(label4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- txtPhone ----
                txtPhone.setEditable(false);
                form.add(txtPhone, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label2 ----
                label2.setText("Book Info");
                form.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- txtBook ----
                txtBook.setEditable(false);
                form.add(txtBook, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- label5 ----
                label5.setText("Author");
                form.add(label5, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- txtAuthor ----
                txtAuthor.setEditable(false);
                form.add(txtAuthor, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label3 ----
                label3.setText("Tickets");
                form.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                form.add(ticketList, new GridBagConstraints(1, 2, 3, 2, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }

            GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
            mainPanel.setLayout(mainPanelLayout);
            mainPanelLayout.setHorizontalGroup(
                mainPanelLayout.createParallelGroup()
                    .addComponent(form, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                    .addComponent(historyTablePane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
            );
            mainPanelLayout.setVerticalGroup(
                mainPanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(form, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(historyTablePane, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE))
            );
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel mainPanel;
    private ReaderBorrowHistoryTablePane historyTablePane;
    private JPanel form;
    private JLabel label1;
    private JTextField txtReader;
    private JLabel label4;
    private JTextField txtPhone;
    private JLabel label2;
    private JTextField txtBook;
    private JLabel label5;
    private JTextField txtAuthor;
    private JLabel label3;
    private ListPane<Ticket> ticketList;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
