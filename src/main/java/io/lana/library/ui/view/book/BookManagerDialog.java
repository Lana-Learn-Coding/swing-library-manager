/*
 * Created by JFormDesigner on Mon Dec 28 08:23:41 ICT 2020
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Storage;
import io.lana.library.core.service.BookService;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.repo.StorageRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.BookTablePane;
import io.lana.library.ui.component.app.ComboBox;
import io.lana.library.ui.component.app.ImagePicker;
import io.lana.library.ui.component.app.ImageViewer;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class BookManagerDialog extends JDialog implements CrudPanel<Book> {
    private final ImagePicker imagePicker = new ImagePicker();

    private FileStorage fileStorage;
    private BookService bookService;
    private StorageRepo storageRepo;
    private BookMeta bookMetaModel;

    public BookManagerDialog() {
        initComponents();
        ListSelectionModel selectionModel = bookTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = bookTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                btnDelete.setEnabled(false);
                btnClone.setEnabled(false);
                return;
            }
            Book book = bookTablePane.getRow(pos);
            loadModelToForm(book);
            btnDelete.setEnabled(true);
            btnClone.setEnabled(true);
        });
    }

    @Autowired
    public void setup(BookService bookService, StorageRepo storageRepo, FileStorage fileStorage) {
        this.bookService = bookService;
        this.storageRepo = storageRepo;
        this.storageRepo.findAll().forEach(selectStorage::addItem);
        this.fileStorage = fileStorage;
        selectStorage.setSelectedItem(null);
    }

    @Override
    public void delete() {
        if (!bookTablePane.isAnyRowSelected()) {
            JOptionPane.showMessageDialog(this, "Please select some thing");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure delete this?");
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        Book book = bookTablePane.getSelectedRow();
        if (book.isBorrowed() &&
            JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this, "Book is borrowed, Are you SURE?")) {
            return;
        }
        bookService.deleteBook(book);
        bookTablePane.removeSelectedRow();
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        Book book = getModelFromForm();
        if (!bookTablePane.isAnyRowSelected()) {
            bookService.createBook(book);
            bookMetaModel.getBooks().add(book);
            bookTablePane.addRow(0, book);
            bookTablePane.clearSearch();
            bookTablePane.setSelectedRow(0);
            JOptionPane.showMessageDialog(this, "Create success!");
            return;
        }

        book.setId(bookTablePane.getSelectedRow().getId());
        bookService.updateBook(book);
        JOptionPane.showMessageDialog(this, "Update success!");
        bookTablePane.refreshSelectedRow();
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtPosition.setText("");
        txtNote.setText("");
        txtCondition.setText("");
        selectStorage.setSelectedItem(null);
        bookTablePane.clearSearch();
    }

    @Override
    public void loadModelToForm(Book model) {
        imageViewer.clearImage();
        txtID.setText(model.getIdString());
        txtPosition.setText(model.getPosition());
        txtCondition.setText(model.getCondition().toString());
        selectStorage.setSelectedItem(model.getStorage());
        txtBorrow.setText(model.getBorrowerName());
        txtNote.setText(model.getNote());
        if (StringUtils.isNotBlank(model.getImage())) {
            WorkerUtils.runAsync(() -> {
                try (InputStream image = fileStorage.readFileFromStorage(model.getImage())) {
                    imageViewer.loadImage(image);
                } catch (IOException e) {
                    // ignore
                }
            });
        }
    }

    @Override
    public Book getModelFromForm() {
        Book book = new Book();
        book.setNote(txtNote.getText());
        book.setPosition(txtPosition.getText());
        book.setStorage(selectStorage.getSelectedItem());
        book.setImage(imageViewer.getImagePath());
        if (book.getStorage() == null) {
            throw new InputException(this, "Please select storage of the book");
        }
        try {
            book.setCondition(Integer.parseInt(txtCondition.getText()));
            if (book.getCondition() > 10 || book.getCondition() < 1) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            throw new InputException(this, "Condition must a number between 1 and 10");
        }
        return book;
    }

    public void renderTable(Collection<Book> data) {
        bookTablePane.setTableData(data);
    }

    public void renderTable() {
        if (bookMetaModel == null) {
            renderTable(new ArrayList<>());
            return;
        }
        renderTable(bookMetaModel.getBooks());
    }

    public void setModel(BookMeta bookMeta) {
        if (bookMeta != null) {
            bookMetaModel = bookMeta;
            renderTable();
            bookTablePane.clearSelection();
            txtMeta.setText(bookMetaModel.getId() + " - " + bookMetaModel.getTitle());
        }
    }

    private void btnNewStorageActionPerformed(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Enter storage name");
        if (name == null) {
            return;

        }
        if (StringUtils.isBlank(name)) {
            JOptionPane.showMessageDialog(this, "Name must not blank");
            return;
        }
        Storage storage = new Storage();
        storage.setName(name);
        storageRepo.save(storage);
        selectStorage.addItem(storage);
        JOptionPane.showMessageDialog(this, "Storage created successfully");
    }

    private void btnCloneActionPerformed(ActionEvent e) {
        Book book = getModelFromForm();
        book.setImage(null);
        bookTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Cloned book. edit then press save to save new book");
        loadModelToForm(book);
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        save();
    }

    private void btnClearActionPerformed(ActionEvent e) {
        clearForm();
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        delete();
    }

    private void btnSelectImageActionPerformed(ActionEvent e) {
        imagePicker.showSelectImageDialog().ifPresent(selectedFile -> {
            String path = selectedFile.getAbsolutePath();
            imageViewer.loadImage(path);
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainTabbedPane = new JTabbedPane();
        formTab = new JPanel();
        formPanel = new JPanel();
        labelId = new JLabel();
        labelMeta = new JLabel();
        labelStorage = new JLabel();
        txtID = new JTextField();
        lblCondition = new JLabel();
        txtCondition = new JTextField();
        txtMeta = new JTextField();
        lblPosition = new JLabel();
        txtPosition = new JTextField();
        selectStorage = new ComboBox<>();
        lblNote = new JLabel();
        noteScroll = new JScrollPane();
        txtNote = new JTextArea();
        lblBorrow = new JLabel();
        txtBorrow = new JTextField();
        panelImage = new JPanel();
        imageViewer = new ImageViewer();
        btnNewStorage = new JButton();
        btnSelectImage = new JButton();
        actionPanel = new JPanel();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnSave = new JButton();
        btnClone = new JButton();
        bookTablePane = new BookTablePane();

        //======== this ========
        setTitle("Book List Detail");
        setModal(true);
        var contentPane = getContentPane();

        //======== mainTabbedPane ========
        {
            mainTabbedPane.setBorder(new EmptyBorder(5, 15, 0, 15));

            //======== formTab ========
            {
                formTab.setBorder(new CompoundBorder(
                    new EtchedBorder(),
                    new EmptyBorder(15, 15, 20, 20)));
                formTab.setLayout(new GridBagLayout());
                ((GridBagLayout) formTab.getLayout()).columnWidths = new int[]{0, 100, 0};
                ((GridBagLayout) formTab.getLayout()).rowHeights = new int[]{0, 0};
                ((GridBagLayout) formTab.getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0E-4};
                ((GridBagLayout) formTab.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

                //======== formPanel ========
                {
                    formPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Book Form"),
                        new EmptyBorder(15, 15, 20, 15)));
                    formPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 25, 0, 0, 0, 25, 130, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- labelId ----
                    labelId.setText("ID");
                    formPanel.add(labelId, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- labelMeta ----
                    labelMeta.setText("Meta");
                    formPanel.add(labelMeta, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- labelStorage ----
                    labelStorage.setText("Storage");
                    formPanel.add(labelStorage, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtID ----
                    txtID.setEditable(false);
                    formPanel.add(txtID, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblCondition ----
                    lblCondition.setText("Condition");
                    formPanel.add(lblCondition, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtCondition, new GridBagConstraints(4, 0, 2, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtMeta ----
                    txtMeta.setEditable(false);
                    formPanel.add(txtMeta, new GridBagConstraints(1, 1, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblPosition ----
                    lblPosition.setText("Position");
                    formPanel.add(lblPosition, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtPosition, new GridBagConstraints(1, 4, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- selectStorage ----
                    selectStorage.setSelectedIndex(-1);
                    formPanel.add(selectStorage, new GridBagConstraints(1, 3, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblNote ----
                    lblNote.setText("Note");
                    formPanel.add(lblNote, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 15), 0, 0));

                    //======== noteScroll ========
                    {
                        noteScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        noteScroll.setViewportView(txtNote);
                    }
                    formPanel.add(noteScroll, new GridBagConstraints(1, 5, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 15), 0, 0));

                    //---- lblBorrow ----
                    lblBorrow.setText("Borrow");
                    formPanel.add(lblBorrow, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtBorrow ----
                    txtBorrow.setEditable(false);
                    formPanel.add(txtBorrow, new GridBagConstraints(1, 2, 5, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //======== panelImage ========
                    {
                        panelImage.setBorder(new TitledBorder("Image"));
                        panelImage.setLayout(new GridLayout());
                        panelImage.add(imageViewer);
                    }
                    formPanel.add(panelImage, new GridBagConstraints(7, 0, 1, 5, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));

                    //---- btnNewStorage ----
                    btnNewStorage.setText("New");
                    btnNewStorage.addActionListener(e -> btnNewStorageActionPerformed(e));
                    formPanel.add(btnNewStorage, new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- btnSelectImage ----
                    btnSelectImage.setText("Select Image");
                    btnSelectImage.addActionListener(e -> btnSelectImageActionPerformed(e));
                    formPanel.add(btnSelectImage, new GridBagConstraints(7, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                formTab.add(formPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 20), 0, 0));

                //======== actionPanel ========
                {

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.setEnabled(false);
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnSave ----
                    btnSave.setText("Save");
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnClone ----
                    btnClone.setText("Clone");
                    btnClone.setEnabled(false);
                    btnClone.addActionListener(e -> btnCloneActionPerformed(e));

                    GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                    actionPanel.setLayout(actionPanelLayout);
                    actionPanelLayout.setHorizontalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    );
                    actionPanelLayout.setVerticalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, actionPanelLayout.createSequentialGroup()
                                .addGap(0, 153, Short.MAX_VALUE)
                                .addComponent(btnClone)
                                .addGap(18, 18, 18)
                                .addComponent(btnSave)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addGap(18, 18, 18)
                                .addComponent(btnDelete))
                    );
                }
                formTab.add(actionPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            mainTabbedPane.addTab("Book List", formTab);
        }

        //---- bookTablePane ----
        bookTablePane.setBorder(new EmptyBorder(0, 15, 15, 15));

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addComponent(mainTabbedPane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
                .addComponent(bookTablePane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 778, Short.MAX_VALUE)
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(bookTablePane, GroupLayout.PREFERRED_SIZE, 283, GroupLayout.PREFERRED_SIZE))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane mainTabbedPane;
    private JPanel formTab;
    private JPanel formPanel;
    private JLabel labelId;
    private JLabel labelMeta;
    private JLabel labelStorage;
    private JTextField txtID;
    private JLabel lblCondition;
    private JTextField txtCondition;
    private JTextField txtMeta;
    private JLabel lblPosition;
    private JTextField txtPosition;
    private ComboBox<Storage> selectStorage;
    private JLabel lblNote;
    private JScrollPane noteScroll;
    private JTextArea txtNote;
    private JLabel lblBorrow;
    private JTextField txtBorrow;
    private JPanel panelImage;
    private ImageViewer imageViewer;
    private JButton btnNewStorage;
    private JButton btnSelectImage;
    private JPanel actionPanel;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private BookTablePane bookTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
