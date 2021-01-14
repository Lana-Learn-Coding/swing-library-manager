/*
 * Created by JFormDesigner on Fri Jan 08 21:09:38 ICT 2021
 */

package io.lana.library.ui.view.reader;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.service.ReaderService;
import io.lana.library.core.service.TicketService;
import io.lana.library.core.spi.datacenter.TicketDataCenter;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.TicketTablePane;
import io.lana.library.ui.component.app.ListPane;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.utils.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class TicketManagerPanel extends JPanel implements CrudPanel<Ticket> {
    private TicketService ticketService;
    private ReaderService readerService;

    private BorrowBookDialog borrowBookDialog;
    private BorrowedBookListDialog borrowedBookListDialog;
    private BorrowHistoryDialog borrowHistoryDialog;

    public TicketManagerPanel() {
        initComponents();
        txtDueDate.setFormats(DateFormatUtils.COMMON_DATE_FORMAT);
        ListSelectionModel selectionModel = ticketTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = ticketTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                btnSave.setEnabled(false);
                btnDelete.setEnabled(false);

                txtNote.setEnabled(false);
                bookList.setEnabled(false);
                txtDueDate.setEnabled(false);
                checkReturn.setEnabled(false);
                btnReturnBook.setEnabled(false);
                btnExtendDue.setEnabled(false);
                return;
            }
            Ticket ticket = ticketTablePane.getRow(pos);
            loadModelToForm(ticket);

            btnSave.setEnabled(true);
            btnDelete.setEnabled(true);
            checkReturn.setEnabled(true);
            btnReturnTicket.setEnabled(!checkReturn.isSelected());

            txtNote.setEnabled(!checkReturn.isSelected());
            bookList.setEnabled(!checkReturn.isSelected());
            txtDueDate.setEnabled(!checkReturn.isSelected());
        });

        bookList.addListSelectionListener(e -> {
            if (ticketTablePane.isAnyRowSelected() &&
                bookList.isAnySelected() &&
                !ticketTablePane.getSelectedRow().isReturned()) {
                btnReturnBook.setEnabled(true);
                btnExtendDue.setEnabled(true);
                return;
            }
            btnReturnBook.setEnabled(false);
            btnExtendDue.setEnabled(false);
        });

        checkReturn.addActionListener(e -> {
            btnReturnTicket.setEnabled(!checkReturn.isSelected());
            txtNote.setEnabled(!checkReturn.isSelected());
            bookList.setEnabled(!checkReturn.isSelected());
            txtDueDate.setEnabled(!checkReturn.isSelected());
        });
    }

    @Autowired
    public void setup(TicketService ticketService,
                      BorrowBookDialog borrowBookDialog,
                      TicketDataCenter ticketDataCenter,
                      ReaderService readerService,
                      BorrowedBookListDialog borrowedBookListDialog,
                      BorrowHistoryDialog borrowHistoryDialog) {
        borrowBookDialog.setLocationRelativeTo(this);
        borrowHistoryDialog.setLocationRelativeTo(this);
        borrowedBookListDialog.setLocationRelativeTo(this);
        this.borrowHistoryDialog = borrowHistoryDialog;
        this.ticketService = ticketService;
        this.borrowBookDialog = borrowBookDialog;
        this.readerService = readerService;
        this.borrowedBookListDialog = borrowedBookListDialog;
        this.ticketTablePane.setRepositoryDataCenter(ticketDataCenter);
    }

    @Override
    public void delete() {
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Are you sure deleting this ticket?")) {
            return;
        }
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Are you really SURE?")) {
            return;
        }
        Ticket ticket = ticketTablePane.getSelectedRow();
        ticketService.deleteTicket(ticket);
        JOptionPane.showMessageDialog(this, "Ticket deleted");
    }

    @Override
    public void save() {
        Ticket ticket = getModelFromForm();
        Ticket updated = ticketTablePane.getSelectedRow();
        if (updated.isReturned() != ticket.isReturned()) {
            if (JOptionPane.OK_OPTION !=
                JOptionPane.showConfirmDialog(this, "Update return status. Are you sure?")) {
                return;
            }
        }
        ticket.setId(updated.getId());
        ticketService.updateTicket(ticket);
        JOptionPane.showMessageDialog(this, "Update success");
    }

    @Override
    public void clearForm() {
        txtReaderId.setText("");
        txtReaderPhone.setText("");
        txtNote.setText("");
        txtBorrowedDate.setText("");
        txtDueDate.setDate(null);
        checkReturn.setSelected(false);
        txtReturnedDate.setText("");
        ticketTablePane.clearSelection();
        bookList.clearListData();
        checkReturn.setText("Returned");
    }

    @Override
    public void loadModelToForm(Ticket model) {
        Reader reader = model.getBorrower();
        txtReaderId.setText(reader.getIdString() + " - " + reader.getName());
        txtReaderPhone.setText(reader.getPhoneNumber());
        txtBorrowedDate.setText(DateFormatUtils.toDateString(model.getBorrowedDate()));
        txtDueDate.setDate(DateFormatUtils.toDate(model.getDueDate()));
        checkReturn.setSelected(model.isReturned());
        txtReturnedDate.setText(model.isReturned() ? DateFormatUtils.toDateString(model.getReturnedDate()) : "");
        bookList.setListData(model.getBooks());
        txtNote.setText(model.getNote());
        checkReturn.setText("Returned - Ticket " + model.getId());
    }

    @Override
    public Ticket getModelFromForm() {
        Ticket ticket = new Ticket();
        ticket.setNote(txtNote.getText());
        if (txtDueDate.getDate() == null) {
            throw new InputException(this, "Invalid Due Date Date");
        }
        ticket.setDueDate(DateFormatUtils.toLocalDate(txtDueDate.getDate()));
        if (!ticket.getDueDate().isAfter(LocalDate.now())) {
            throw new InputException(this, "Invalid Due Date must after today");
        }
        ticket.setReturned(checkReturn.isSelected());
        return ticket;
    }

    private void btnClearActionPerformed(ActionEvent e) {
        clearForm();
    }


    private void btnSaveActionPerformed(ActionEvent e) {
        save();
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        delete();
    }

    private Reader findReader() {
        String query = JOptionPane.showInputDialog(this, "Enter reader id, email, or phone");
        if (StringUtils.isBlank(query)) {
            throw new InputException(this, "Query must not blank");
        }
        Optional<Reader> reader = readerService.findOne(query);
        if (reader.isEmpty()) {
            throw new InputException(this, "No reader found");
        }
        return reader.get();
    }

    private void btnBorrowBookActionPerformed(ActionEvent e) {
        borrowBookDialog.setModel(findReader());
        borrowBookDialog.setVisible(true);
    }

    private void btnReturnTicketActionPerformed(ActionEvent e) {
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Return ticket. Are you sure?")) {
            return;
        }
        Ticket ticket = ticketTablePane.getSelectedRow();
        ticketService.returnTicket(ticket);
        JOptionPane.showMessageDialog(this, "Ticket returned");
    }

    private void btnViewBorrowActionPerformed(ActionEvent e) {
        borrowedBookListDialog.setModel(findReader());
        borrowedBookListDialog.setVisible(true);
    }

    private void btnReturnBookActionPerformed(ActionEvent e) {
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Are you sure return this book?")) {
            return;
        }
        Ticket ticket = ticketService.returnBook(bookList.getSelectedValue());
        JOptionPane.showMessageDialog(this, "Book returned. Tracking ticket - " + ticket.getId());
    }

    private void btnExtendDueActionPerformed(ActionEvent e) {
        Book book = bookList.getSelectedValue();

        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats(DateFormatUtils.COMMON_DATE_FORMAT);
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, datePicker, "Select Due Date", JOptionPane.YES_NO_OPTION)) {
            return;
        }
        if (datePicker.getDate() == null) {
            throw new InputException(this, "Invalid date");
        }
        LocalDate newDate = DateFormatUtils.toLocalDate(datePicker.getDate());
        if (!newDate.isAfter(LocalDate.now())) {
            throw new InputException(this, "Selected Date is not after now");
        }

        Ticket ticket = ticketService.extendBookDueDate(book, newDate);
        JOptionPane.showMessageDialog(this, "Book due date extended! - Tracking ticket: " + ticket.getId());
    }

    private void btnBorrowHistoryActionPerformed(ActionEvent e) {
        borrowHistoryDialog.setModel(findReader());
        borrowHistoryDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ticketTablePane = new TicketTablePane();
        mainTabbedPane = new JTabbedPane();
        tab = new JPanel();
        formPanel = new JPanel();
        label2 = new JLabel();
        txtReaderId = new JTextField();
        label4 = new JLabel();
        txtReaderPhone = new JTextField();
        label1 = new JLabel();
        txtBorrowedDate = new JTextField();
        label5 = new JLabel();
        txtDueDate = new JXDatePicker();
        label6 = new JLabel();
        txtReturnedDate = new JTextField();
        checkReturn = new JCheckBox();
        lblSeries = new JLabel();
        scrollPane1 = new JScrollPane();
        txtNote = new JTextArea();
        label3 = new JLabel();
        bookList = new ListPane<>();
        actionPanel = new JPanel();
        btnClear = new JButton();
        btnSave = new JButton();
        btnDelete = new JButton();
        btnBorrowBook = new JButton();
        btnReturnTicket = new JButton();
        btnViewBorrow = new JButton();
        btnReturnBook = new JButton();
        btnExtendDue = new JButton();
        btnBorrowHistory = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(0, 10, 0, 10));

        //---- ticketTablePane ----
        ticketTablePane.setBorder(null);

        //======== mainTabbedPane ========
        {
            mainTabbedPane.setBorder(null);

            //======== tab ========
            {
                tab.setBorder(new CompoundBorder(
                    new EtchedBorder(),
                    new EmptyBorder(15, 15, 20, 20)));
                tab.setLayout(new GridBagLayout());
                ((GridBagLayout) tab.getLayout()).columnWidths = new int[]{0, 100};
                ((GridBagLayout) tab.getLayout()).columnWeights = new double[]{1.0, 0.0};

                //======== formPanel ========
                {
                    formPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Ticket Info"),
                        new EmptyBorder(15, 15, 20, 15)));
                    formPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 35, 0, 40, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- label2 ----
                    label2.setText("Reader");
                    formPanel.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- txtReaderId ----
                    txtReaderId.setEditable(false);
                    formPanel.add(txtReaderId, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- label4 ----
                    label4.setText("Phone");
                    formPanel.add(label4, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- txtReaderPhone ----
                    txtReaderPhone.setEditable(false);
                    formPanel.add(txtReaderPhone, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label1 ----
                    label1.setText("Borrowed Date");
                    formPanel.add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- txtBorrowedDate ----
                    txtBorrowedDate.setEditable(false);
                    formPanel.add(txtBorrowedDate, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- label5 ----
                    label5.setText("Due Date");
                    formPanel.add(label5, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));
                    formPanel.add(txtDueDate, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label6 ----
                    label6.setText("Returned Date");
                    formPanel.add(label6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- txtReturnedDate ----
                    txtReturnedDate.setEditable(false);
                    formPanel.add(txtReturnedDate, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //---- checkReturn ----
                    checkReturn.setText("Returned");
                    formPanel.add(checkReturn, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- lblSeries ----
                    lblSeries.setText("Note");
                    formPanel.add(lblSeries, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(txtNote);
                    }
                    formPanel.add(scrollPane1, new GridBagConstraints(1, 3, 3, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label3 ----
                    label3.setText("Books");
                    formPanel.add(label3, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));
                    formPanel.add(bookList, new GridBagConstraints(1, 5, 3, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                tab.add(formPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 20), 0, 0));

                //======== actionPanel ========
                {

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnSave ----
                    btnSave.setText("Update");
                    btnSave.setEnabled(false);
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.setEnabled(false);
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    //---- btnBorrowBook ----
                    btnBorrowBook.setText("Borrow Book");
                    btnBorrowBook.addActionListener(e -> btnBorrowBookActionPerformed(e));

                    //---- btnReturnTicket ----
                    btnReturnTicket.setText("Return Ticket");
                    btnReturnTicket.setEnabled(false);
                    btnReturnTicket.addActionListener(e -> btnReturnTicketActionPerformed(e));

                    //---- btnViewBorrow ----
                    btnViewBorrow.setText("View Borrow");
                    btnViewBorrow.addActionListener(e -> btnViewBorrowActionPerformed(e));

                    //---- btnReturnBook ----
                    btnReturnBook.setText("Return Book");
                    btnReturnBook.setEnabled(false);
                    btnReturnBook.addActionListener(e -> btnReturnBookActionPerformed(e));

                    //---- btnExtendDue ----
                    btnExtendDue.setText("Extend Due");
                    btnExtendDue.setEnabled(false);
                    btnExtendDue.addActionListener(e -> btnExtendDueActionPerformed(e));

                    //---- btnBorrowHistory ----
                    btnBorrowHistory.setText("Borrow History");
                    btnBorrowHistory.addActionListener(e -> btnBorrowHistoryActionPerformed(e));

                    GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                    actionPanel.setLayout(actionPanelLayout);
                    actionPanelLayout.setHorizontalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReturnTicket, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBorrowBook, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnViewBorrow, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBorrowHistory, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReturnBook, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExtendDue, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );
                    actionPanelLayout.setVerticalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addGroup(actionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBorrowBook)
                                .addGap(9, 9, 9)
                                .addComponent(btnViewBorrow)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnBorrowHistory)
                                .addGap(23, 23, 23)
                                .addComponent(btnExtendDue)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReturnBook)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                                .addComponent(btnReturnTicket)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSave)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnClear)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnDelete))
                    );
                }
                tab.add(actionPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            mainTabbedPane.addTab("Ticket Manage", tab);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                .addComponent(ticketTablePane, GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 404, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(ticketTablePane, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private TicketTablePane ticketTablePane;
    private JTabbedPane mainTabbedPane;
    private JPanel tab;
    private JPanel formPanel;
    private JLabel label2;
    private JTextField txtReaderId;
    private JLabel label4;
    private JTextField txtReaderPhone;
    private JLabel label1;
    private JTextField txtBorrowedDate;
    private JLabel label5;
    private JXDatePicker txtDueDate;
    private JLabel label6;
    private JTextField txtReturnedDate;
    private JCheckBox checkReturn;
    private JLabel lblSeries;
    private JScrollPane scrollPane1;
    private JTextArea txtNote;
    private JLabel label3;
    private ListPane<Book> bookList;
    private JPanel actionPanel;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnBorrowBook;
    private JButton btnReturnTicket;
    private JButton btnViewBorrow;
    private JButton btnReturnBook;
    private JButton btnExtendDue;
    private JButton btnBorrowHistory;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
