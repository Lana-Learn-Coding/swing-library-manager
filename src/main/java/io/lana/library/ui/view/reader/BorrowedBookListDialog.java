/*
 * Created by JFormDesigner on Tue Jan 05 01:25:36 ICT 2021
 */

package io.lana.library.ui.view.reader;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.service.BookService;
import io.lana.library.core.service.TicketService;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.ReaderBorrowedBookTablePane;
import io.lana.library.utils.DateFormatUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

@Component
public class BorrowedBookListDialog extends JDialog {

    private TicketService ticketService;
    private BookService bookService;

    public BorrowedBookListDialog() {
        initComponents();
        ListSelectionModel selectionModel = borrowTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = borrowTablePane.getSelectedRowIndex();
            if (pos < 0) {
                btnExtendTicketDue.setEnabled(false);
                btnExtendDue.setEnabled(false);
                btnReturnBook.setEnabled(false);
                btnReturnTicket.setEnabled(false);
                btnRemoveBook.setEnabled(false);
                return;
            }
            btnExtendTicketDue.setEnabled(true);
            btnExtendDue.setEnabled(true);
            btnReturnBook.setEnabled(true);
            btnReturnTicket.setEnabled(true);
            btnRemoveBook.setEnabled(checkSafe.isSelected());
        });
        checkSafe.addActionListener(e -> btnRemoveBook.setEnabled(checkSafe.isSelected() && borrowTablePane.isAnyRowSelected()));
    }

    @Autowired
    public void setup(TicketService ticketService, BookService bookService) {
        this.bookService = bookService;
        this.ticketService = ticketService;
    }

    public void setModel(Reader readerModel) {
        borrowTablePane.setTableData(readerModel.getBorrowedBooks());
    }

    private void btnReturnBookActionPerformed(ActionEvent e) {
        if (userNotConfirmed()) {
            return;
        }
        Book book = borrowTablePane.getSelectedRow();
        ticketService.returnBook(book);
        borrowTablePane.removeSelectedRow();
        JOptionPane.showMessageDialog(this, "Book returned!");
    }

    private boolean userNotConfirmed() {
        if (checkSafe.isSelected()) {
            return JOptionPane.showConfirmDialog(this, "Are you sure?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.OK_OPTION;
        }
        return false;
    }

    private void btnReturnTicketActionPerformed(ActionEvent e) {
        if (userNotConfirmed()) {
            return;
        }
        Ticket ticket = borrowTablePane.getSelectedRow().getBorrowingTicket();
        ticket.getBooks().forEach(book -> borrowTablePane.removeRow(book));
        ticketService.returnTicket(ticket);
        JOptionPane.showMessageDialog(this, "All book in ticket returned!");
    }

    private void btnExtendDueActionPerformed(ActionEvent e) {
        if (userNotConfirmed()) {
            return;
        }

        Book book = borrowTablePane.getSelectedRow();
        LocalDate dueDate = inputDateAfter(LocalDate.now());
        if (dueDate == null) {
            return;
        }

        ticketService.extendBookDueDate(book, dueDate);
        borrowTablePane.refreshSelectedRow();
        JOptionPane.showMessageDialog(this, "Book due date extended!");
    }

    private void btnExtendTicketDueActionPerformed(ActionEvent e) {
        if (userNotConfirmed()) {
            return;
        }

        Book book = borrowTablePane.getSelectedRow();
        Ticket ticket = book.getBorrowingTicket();

        LocalDate dueDate = inputDateAfter(LocalDate.now());
        if (dueDate == null) {
            return;
        }
        ticketService.extendTicketDueDate(ticket, dueDate);
        ticket.getBooks().forEach(borrowTablePane::refreshRow);
        JOptionPane.showMessageDialog(this, "Ticket due date extended!");
    }

    private LocalDate inputDateAfter(LocalDate date) {
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats(DateFormatUtils.COMMON_DATE_FORMAT);
        int confirm = JOptionPane.showConfirmDialog(this, datePicker, "Select Due Date", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.OK_OPTION) {
            return null;
        }

        if (datePicker.getDate() == null) {
            throw new InputException(this, "Invalid date");
        }
        LocalDate newDate = DateFormatUtils.toLocalDate(datePicker.getDate());
        if (!newDate.isAfter(date)) {
            throw new InputException(this, "Selected Date must after " + DateFormatUtils.toDateString(date));
        }
        return newDate;
    }

    private void btnRemoveBookActionPerformed(ActionEvent e) {
        if (userNotConfirmed()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "You are about to remove a borrowed book",
            "WARNING", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        Book book = borrowTablePane.getSelectedRow();
        bookService.deleteBook(book);
        borrowTablePane.removeSelectedRow();
        JOptionPane.showMessageDialog(this, "Book removed");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        borrowTablePane = new ReaderBorrowedBookTablePane();
        actionPanel = new JPanel();
        btnReturnBook = new JButton();
        btnReturnTicket = new JButton();
        btnRemoveBook = new JButton();
        btnExtendDue = new JButton();
        btnExtendTicketDue = new JButton();
        checkSafe = new JCheckBox();

        //======== this ========
        setTitle("User Borrowed Book");
        setModal(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(5, 20, 10, 20));

            //======== actionPanel ========
            {
                actionPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) actionPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 25, 0, 0, 0};
                ((GridBagLayout) actionPanel.getLayout()).rowHeights = new int[]{0, 0, 0};
                ((GridBagLayout) actionPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout) actionPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                //---- btnReturnBook ----
                btnReturnBook.setText("Return Book");
                btnReturnBook.setEnabled(false);
                btnReturnBook.addActionListener(e -> btnReturnBookActionPerformed(e));
                actionPanel.add(btnReturnBook, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- btnReturnTicket ----
                btnReturnTicket.setText("Return All Book of Ticket");
                btnReturnTicket.setEnabled(false);
                btnReturnTicket.addActionListener(e -> btnReturnTicketActionPerformed(e));
                actionPanel.add(btnReturnTicket, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- btnRemoveBook ----
                btnRemoveBook.setText("Remove Book");
                btnRemoveBook.setEnabled(false);
                btnRemoveBook.addActionListener(e -> btnRemoveBookActionPerformed(e));
                actionPanel.add(btnRemoveBook, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- btnExtendDue ----
                btnExtendDue.setText("Extend Due Date");
                btnExtendDue.setEnabled(false);
                btnExtendDue.addActionListener(e -> btnExtendDueActionPerformed(e));
                actionPanel.add(btnExtendDue, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 15), 0, 0));

                //---- btnExtendTicketDue ----
                btnExtendTicketDue.setText("Extend Ticket Due Date");
                btnExtendTicketDue.setEnabled(false);
                btnExtendTicketDue.addActionListener(e -> btnExtendTicketDueActionPerformed(e));
                actionPanel.add(btnExtendTicketDue, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 15, 0), 0, 0));

                //---- checkSafe ----
                checkSafe.setText("Safe Action");
                checkSafe.setSelected(true);
                actionPanel.add(checkSafe, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }

            GroupLayout dialogPaneLayout = new GroupLayout(dialogPane);
            dialogPane.setLayout(dialogPaneLayout);
            dialogPaneLayout.setHorizontalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addComponent(actionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(borrowTablePane, GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE)
            );
            dialogPaneLayout.setVerticalGroup(
                dialogPaneLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.TRAILING, dialogPaneLayout.createSequentialGroup()
                        .addComponent(actionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(borrowTablePane, GroupLayout.PREFERRED_SIZE, 304, GroupLayout.PREFERRED_SIZE))
            );
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private ReaderBorrowedBookTablePane borrowTablePane;
    private JPanel actionPanel;
    private JButton btnReturnBook;
    private JButton btnReturnTicket;
    private JButton btnRemoveBook;
    private JButton btnExtendDue;
    private JButton btnExtendTicketDue;
    private JCheckBox checkSafe;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
