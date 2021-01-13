/*
 * Created by JFormDesigner on Fri Jan 08 21:09:38 ICT 2021
 */

package io.lana.library.ui.view.ticket;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.service.TicketService;
import io.lana.library.core.spi.datacenter.TicketDataCenter;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.TicketTablePane;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.ui.view.reader.BorrowBookDialog;
import io.lana.library.utils.DateFormatUtils;
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

@Component
public class TicketManagerPanel extends JPanel implements CrudPanel<Ticket> {
    private TicketService ticketService;

    private BorrowBookDialog borrowBookDialog;

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
                      TicketDataCenter ticketDataCenter) {
        this.ticketService = ticketService;
        this.borrowBookDialog = borrowBookDialog;
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

    private void btnBorrowBookActionPerformed(ActionEvent e) {
//        String query = JOptionPane.showInputDialog(this, "Enter reader id, email, or phone");
//        if (StringUtils.isBlank(query)) {
//            throw new InputException(this, "Query must not blank");
//        }
//        Optional<Reader> readerFound = readerDataCenter.stream().filter(reader ->
//            query.equals(reader.getIdString()) || query.equals(reader.getEmail()) || query.equals(reader.getPhoneNumber())
//        ).findFirst();
//
//        if (readerFound.isEmpty()) {
//            throw new InputException(this, "No reader found");
//        }
//
//        borrowBookDialog.setModel(readerFound.get());
//        borrowBookDialog.setVisible(true);
    }

    private void btnReturnTicketActionPerformed(ActionEvent e) {
        if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Return ticket. Are you sure?")) {
            return;
        }
        Ticket ticket = ticketTablePane.getSelectedRow();
        ticketService.returnTicket(ticket);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        ticketTablePane = new TicketTablePane();
        mainTabbedPane = new JTabbedPane();
        tab = new JPanel();
        formPanel = new JPanel();
        label2 = new JLabel();
        panel1 = new JPanel();
        txtReaderId = new JTextField();
        label4 = new JLabel();
        txtReaderPhone = new JTextField();
        label1 = new JLabel();
        panel2 = new JPanel();
        txtBorrowedDate = new JTextField();
        label5 = new JLabel();
        txtDueDate = new JXDatePicker();
        label6 = new JLabel();
        panel3 = new JPanel();
        txtReturnedDate = new JTextField();
        checkReturn = new JCheckBox();
        lblSeries = new JLabel();
        scrollPane1 = new JScrollPane();
        txtNote = new JTextArea();
        label3 = new JLabel();
        scrollPane2 = new JScrollPane();
        bookList = new JList();
        actionPanel = new JPanel();
        btnClear = new JButton();
        btnSave = new JButton();
        btnDelete = new JButton();
        btnBorrowBook = new JButton();
        btnReturnTicket = new JButton();

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
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 35, 0, 40, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- label2 ----
                    label2.setText("Reader");
                    formPanel.add(label2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel1.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
                        ((GridBagLayout) panel1.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel1.getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel1.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                        //---- txtReaderId ----
                        txtReaderId.setEditable(false);
                        panel1.add(txtReaderId, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));

                        //---- label4 ----
                        label4.setText("Phone");
                        panel1.add(label4, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));

                        //---- txtReaderPhone ----
                        txtReaderPhone.setEditable(false);
                        panel1.add(txtReaderPhone, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formPanel.add(panel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label1 ----
                    label1.setText("Borrowed Date");
                    formPanel.add(label1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
                        ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                        //---- txtBorrowedDate ----
                        txtBorrowedDate.setEditable(false);
                        panel2.add(txtBorrowedDate, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));

                        //---- label5 ----
                        label5.setText("Due Date");
                        panel2.add(label5, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));
                        panel2.add(txtDueDate, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formPanel.add(panel2, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label6 ----
                    label6.setText("Returned Date");
                    formPanel.add(label6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //======== panel3 ========
                    {
                        panel3.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel3.getLayout()).columnWidths = new int[]{0, 0, 0};
                        ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel3.getLayout()).columnWeights = new double[]{1.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel3.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                        //---- txtReturnedDate ----
                        txtReturnedDate.setEditable(false);
                        panel3.add(txtReturnedDate, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 20), 0, 0));

                        //---- checkReturn ----
                        checkReturn.setText("Returned");
                        panel3.add(checkReturn, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formPanel.add(panel3, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
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
                    formPanel.add(scrollPane1, new GridBagConstraints(1, 3, 1, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label3 ----
                    label3.setText("Books");
                    formPanel.add(label3, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 10), 0, 0));

                    //======== scrollPane2 ========
                    {
                        scrollPane2.setViewportView(bookList);
                    }
                    formPanel.add(scrollPane2, new GridBagConstraints(1, 5, 1, 2, 0.0, 0.0,
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
                    btnReturnTicket.addActionListener(e -> btnReturnTicketActionPerformed(e));

                    GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                    actionPanel.setLayout(actionPanelLayout);
                    actionPanelLayout.setHorizontalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnBorrowBook, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnReturnTicket, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    );
                    actionPanelLayout.setVerticalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addGroup(actionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBorrowBook)
                                .addGap(18, 18, 18)
                                .addComponent(btnReturnTicket)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                                .addComponent(btnSave)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addGap(18, 18, 18)
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
                .addComponent(ticketTablePane, GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(ticketTablePane, GroupLayout.PREFERRED_SIZE, 287, GroupLayout.PREFERRED_SIZE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private TicketTablePane ticketTablePane;
    private JTabbedPane mainTabbedPane;
    private JPanel tab;
    private JPanel formPanel;
    private JLabel label2;
    private JPanel panel1;
    private JTextField txtReaderId;
    private JLabel label4;
    private JTextField txtReaderPhone;
    private JLabel label1;
    private JPanel panel2;
    private JTextField txtBorrowedDate;
    private JLabel label5;
    private JXDatePicker txtDueDate;
    private JLabel label6;
    private JPanel panel3;
    private JTextField txtReturnedDate;
    private JCheckBox checkReturn;
    private JLabel lblSeries;
    private JScrollPane scrollPane1;
    private JTextArea txtNote;
    private JLabel label3;
    private JScrollPane scrollPane2;
    private JList bookList;
    private JPanel actionPanel;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnBorrowBook;
    private JButton btnReturnTicket;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
