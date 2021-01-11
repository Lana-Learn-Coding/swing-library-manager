/*
 * Created by JFormDesigner on Tue Jan 05 22:07:52 ICT 2021
 */

package io.lana.library.ui.view.reader;

import io.lana.library.core.spi.datacenter.BookDataCenter;
import io.lana.library.core.spi.datacenter.ReaderDataCenter;
import io.lana.library.core.spi.datacenter.TicketDataCenter;
import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.Ticket;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.BorrowBookTablePane;
import io.lana.library.ui.component.app.ImageViewer;
import io.lana.library.ui.component.app.TextField;
import io.lana.library.utils.DateFormatUtils;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BorrowBookDialog extends JDialog {
    private Reader readerModel;

    private BookDataCenter bookDataCenter;
    private TicketDataCenter ticketDataCenter;
    private ReaderDataCenter readerDataCenter;
    private FileStorage fileStorage;

    public BorrowBookDialog() {
        initComponents();
        ListSelectionModel selectionModel = bookTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = bookTablePane.getSelectedRowIndex();
            if (pos < 0) {
                txtBookIds.setText("");
                return;
            }
            txtBookIds.setText(bookTablePane.getSelectedRow().getIdString());
        });
        txtDueDate.setFormats(DateFormatUtils.COMMON_DATE_FORMAT);
    }

    @Autowired
    public void setup(BookDataCenter bookDataCenter, FileStorage fileStorage,
                      TicketDataCenter ticketDataCenter,
                      ReaderDataCenter readerDataCenter) {
        this.bookDataCenter = bookDataCenter;
        this.fileStorage = fileStorage;
        this.ticketDataCenter = ticketDataCenter;
        this.readerDataCenter = readerDataCenter;
    }

    public void setModel(Reader readerModel) {
        clearForm();
        this.readerModel = readerModel;
        txtID.setText(readerModel.getIdString());
        txtEmail.setText(readerModel.getEmail());
        txtName.setText(readerModel.getName());
        txtLimit.setText(readerModel.getLimit().toString());
        txtPhone.setText(readerModel.getPhoneNumber());
        txtBorrowDate.setText(DateFormatUtils.toDateString(LocalDate.now()));
        if (StringUtils.isNotBlank(readerModel.getAvatar())) {
            WorkerUtils.runAsync(() -> {
                try (InputStream image = fileStorage.readFileFromStorage(readerModel.getAvatar())) {
                    imageViewer.loadImage(image);
                } catch (IOException e) {
                    // ignore
                }
            });
        }
    }

    private void clearForm() {
        txtDueDate.setDate(null);
        txtBookIds.setText("");
        txtNote.setText("");
        bookTablePane.clearSearch();
        bookTablePane.setTableData(new ArrayList<>());
    }

    private void okButtonActionPerformed(ActionEvent e) {
        int addedBookCount = bookTablePane.rowCount();
        if (addedBookCount <= 0) {
            throw new InputException(this, "Please add some book to ticket");
        }
        Integer totalBorrowedBook = addedBookCount + readerModel.getBorrowedBookCount();
        if (readerModel.getLimit() < totalBorrowedBook) {
            throw new InputException(this, "Borrowing book over limit " + totalBorrowedBook.toString()
                + "/" + readerModel.getLimit().toString());
        }

        Ticket bookBorrowing = new Ticket();
        if (txtDueDate.getDate() == null) {
            throw new InputException(this, "Invalid Due Date Date");
        }
        bookBorrowing.setDueDate(DateFormatUtils.toLocalDate(txtDueDate.getDate()));
        if (!bookBorrowing.getDueDate().isAfter(LocalDate.now())) {
            throw new InputException(this, "Invalid Due Date must after today");
        }

        bookBorrowing.setBorrowedDate(LocalDate.now());
        bookBorrowing.setNote(txtNote.getText());
        bookBorrowing.setBooks(new HashSet<>(bookTablePane.asList()));
        bookBorrowing.getBooks().forEach(book -> book.setBorrowing(bookBorrowing));
        bookBorrowing.setBorrower(readerModel);

        ticketDataCenter.save(bookBorrowing);
        bookDataCenter.updateAll(bookBorrowing.getBooks());
        readerModel.getBorrowedBooks().add(bookBorrowing);
        readerDataCenter.refresh(readerModel);
        JOptionPane.showMessageDialog(this, "Book borrowing ticket created successfully");
        setVisible(false);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void btnAddActionPerformed(ActionEvent e) {
        List<Integer> ids = getIdsFromFilter();
        bookTablePane.forEach(book -> {
            if (ids.contains(book.getId())) {
                if (checkStrictFilter.isSelected()) {
                    throw new InputException(this, "Book already added: " + book.getId());
                }
                ids.remove(book.getId());
            }
        });

        List<Book> books = bookDataCenter.findAllNotBorrowedAndIdIn(ids);
        List<Integer> bookIds = books.stream().map(Book::getId).collect(Collectors.toList());
        if (books.size() != ids.size() && checkStrictFilter.isSelected()) {
            ids.removeAll(bookIds);
            throw new InputException(this, "Books not found: " + StringUtils.join(ids, ", "));
        }

        books.forEach(bookTablePane::addRow);
        if (checkResotreFilter.isSelected()) {
            txtBookIds.setText(StringUtils.join(bookIds, ", "));
        }
        JOptionPane.showMessageDialog(this, "Added " + books.size() + " to borrow list");
    }

    private void btnDelActionPerformed(ActionEvent e) {
        List<Integer> ids = getIdsFromFilter();
        List<Integer> indexToRemoves = new ArrayList<>();
        List<Integer> tableBookIds = bookTablePane.stream()
            .map(Book::getId)
            .collect(Collectors.toList());

        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            if (tableBookIds.contains(id)) {
                indexToRemoves.add(tableBookIds.indexOf(id));
                continue;
            }
            if (checkStrictFilter.isSelected()) {
                throw new InputException(this, "Book not found: " + id);
            }
            ids.remove(id);
        }
        indexToRemoves.forEach(bookTablePane::removeRow);
        if (checkResotreFilter.isSelected()) {
            txtBookIds.setText(StringUtils.join(ids, ", "));
        }
        JOptionPane.showMessageDialog(this, "Removed " + indexToRemoves.size() + " from borrow list");
    }

    private List<Integer> getIdsFromFilter() {
        if (StringUtils.isBlank(txtBookIds.getText())) {
            throw new InputException(this, "Please enter ids (separate by ',')");
        }
        String[] idStrings = StringUtils.split(txtBookIds.getText(), ",");

        List<Integer> ids = new ArrayList<>(idStrings.length);
        for (String idString : idStrings) {
            String id = idString.trim();
            try {
                ids.add(Integer.parseInt(id));
            } catch (Exception e) {
                if (checkStrictFilter.isSelected()) {
                    throw new InputException(this, "Invalid id format: " + id.trim());
                }
            }
        }

        return ids;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        bookTablePane = new BorrowBookTablePane();
        formPanel = new JPanel();
        label1 = new JLabel();
        txtID = new JTextField();
        label3 = new JLabel();
        txtLimit = new JTextField();
        imagePanel = new JPanel();
        imageViewer = new ImageViewer();
        label2 = new JLabel();
        txtName = new JTextField();
        label4 = new JLabel();
        txtPhone = new JTextField();
        label5 = new JLabel();
        txtEmail = new JTextField();
        separator1 = new JSeparator();
        txtBorrowed = new JLabel();
        txtBorrowDate = new JTextField();
        label8 = new JLabel();
        txtDueDate = new JXDatePicker();
        label6 = new JLabel();
        txtBookIds = new TextField();
        panel2 = new JPanel();
        btnAdd = new JButton();
        btnDel = new JButton();
        label9 = new JLabel();
        scrollPane1 = new JScrollPane();
        txtNote = new JTextArea();
        checkStrictFilter = new JCheckBox();
        checkResotreFilter = new JCheckBox();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== formPanel ========
                {
                    formPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 25, 0, 25, 100, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 25, 0, 0, 0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- label1 ----
                    label1.setText("ID");
                    formPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtID ----
                    txtID.setEditable(false);
                    formPanel.add(txtID, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- label3 ----
                    label3.setText("Limit");
                    formPanel.add(label3, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtLimit ----
                    txtLimit.setEditable(false);
                    formPanel.add(txtLimit, new GridBagConstraints(3, 0, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //======== imagePanel ========
                    {
                        imagePanel.setBorder(new TitledBorder("Avatar"));
                        imagePanel.setLayout(new GridLayout());
                        imagePanel.add(imageViewer);
                    }
                    formPanel.add(imagePanel, new GridBagConstraints(6, 0, 1, 4, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label2 ----
                    label2.setText("Name");
                    formPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtName ----
                    txtName.setEditable(false);
                    formPanel.add(txtName, new GridBagConstraints(1, 1, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- label4 ----
                    label4.setText("Phone");
                    formPanel.add(label4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtPhone ----
                    txtPhone.setEditable(false);
                    formPanel.add(txtPhone, new GridBagConstraints(1, 2, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- label5 ----
                    label5.setText("Email");
                    formPanel.add(label5, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtEmail ----
                    txtEmail.setEditable(false);
                    formPanel.add(txtEmail, new GridBagConstraints(1, 3, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(separator1, new GridBagConstraints(0, 4, 7, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- txtBorrowed ----
                    txtBorrowed.setText("Borrowed");
                    formPanel.add(txtBorrowed, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtBorrowDate ----
                    txtBorrowDate.setEditable(false);
                    formPanel.add(txtBorrowDate, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- label8 ----
                    label8.setText("Due Date");
                    formPanel.add(label8, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtDueDate, new GridBagConstraints(3, 5, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label6 ----
                    label6.setText("Book IDs");
                    formPanel.add(label6, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtBookIds, new GridBagConstraints(1, 6, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //======== panel2 ========
                    {
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout) panel2.getLayout()).columnWidths = new int[]{65, 60, 0};
                        ((GridBagLayout) panel2.getLayout()).rowHeights = new int[]{0, 0};
                        ((GridBagLayout) panel2.getLayout()).columnWeights = new double[]{1.0, 1.0, 1.0E-4};
                        ((GridBagLayout) panel2.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                        //---- btnAdd ----
                        btnAdd.setText("Add");
                        btnAdd.addActionListener(e -> btnAddActionPerformed(e));
                        panel2.add(btnAdd, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- btnDel ----
                        btnDel.setText("Del");
                        btnDel.addActionListener(e -> btnDelActionPerformed(e));
                        panel2.add(btnDel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    formPanel.add(panel2, new GridBagConstraints(5, 6, 2, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- label9 ----
                    label9.setText("Note");
                    formPanel.add(label9, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //======== scrollPane1 ========
                    {
                        scrollPane1.setViewportView(txtNote);
                    }
                    formPanel.add(scrollPane1, new GridBagConstraints(1, 7, 4, 2, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 15), 0, 0));

                    //---- checkStrictFilter ----
                    checkStrictFilter.setText("Strict Ids Filter");
                    checkStrictFilter.setSelected(true);
                    formPanel.add(checkStrictFilter, new GridBagConstraints(6, 7, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- checkResotreFilter ----
                    checkResotreFilter.setText("Restore filter");
                    formPanel.add(checkResotreFilter, new GridBagConstraints(6, 8, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                        .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE)
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(34, 34, 34)
                            .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> okButtonActionPerformed(e));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private BorrowBookTablePane bookTablePane;
    private JPanel formPanel;
    private JLabel label1;
    private JTextField txtID;
    private JLabel label3;
    private JTextField txtLimit;
    private JPanel imagePanel;
    private ImageViewer imageViewer;
    private JLabel label2;
    private JTextField txtName;
    private JLabel label4;
    private JTextField txtPhone;
    private JLabel label5;
    private JTextField txtEmail;
    private JSeparator separator1;
    private JLabel txtBorrowed;
    private JTextField txtBorrowDate;
    private JLabel label8;
    private JXDatePicker txtDueDate;
    private JLabel label6;
    private TextField txtBookIds;
    private JPanel panel2;
    private JButton btnAdd;
    private JButton btnDel;
    private JLabel label9;
    private JScrollPane scrollPane1;
    private JTextArea txtNote;
    private JCheckBox checkStrictFilter;
    private JCheckBox checkResotreFilter;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
