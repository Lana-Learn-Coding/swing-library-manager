/*
 * Created by JFormDesigner on Mon Dec 28 08:23:41 ICT 2020
 */

package io.lana.library.ui.view.book;

import java.awt.event.*;

import io.lana.library.core.model.Reader;
import io.lana.library.core.model.book.Book;
import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Storage;
import io.lana.library.core.spi.BookRepo;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.StorageRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.app.*;
import io.lana.library.ui.component.book.BookTablePane;
import io.lana.library.ui.view.CrudPanel;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class BookManagerDialog extends JDialog implements CrudPanel<Book> {
    private final ImagePicker imagePicker = new ImagePicker();

    private FileStorage fileStorage;
    private StorageRepo storageRepo;
    private BookRepo bookRepo;
    private BookMeta bookMetaModel;

    public BookManagerDialog(@Qualifier("mainFrame") Window owner) {
        super(owner);
        initComponents();
        ListSelectionModel selectionModel = bookTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = bookTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                return;
            }
            Book book = bookTablePane.getRow(pos);
            loadModelToForm(book);
        });
    }

    @Autowired
    public void setup(BookRepo bookRepo, StorageRepo storageRepo, FileStorage fileStorage) {
        this.bookRepo = bookRepo;
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
        bookRepo.deleteById(bookMetaModel.getId());
        bookTablePane.removeSelectedRow();
        bookTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        Book book = getModelFromForm();
        if (!bookTablePane.isAnyRowSelected()) {
            book.setMeta(bookMetaModel);
            if (StringUtils.isNotBlank(book.getImage())) {
                String savedImage = fileStorage.loadFileToStorage(book.getImage());
                book.setImage(savedImage);
            }
            bookRepo.save(book);
            JOptionPane.showMessageDialog(this, "Create success!");
            bookTablePane.addRow(0, book);
            bookTablePane.clearSelection();
            return;
        }

        Book updated = bookTablePane.getSelectedRow();
        updated.setStorage(book.getStorage());
        updated.setNote(book.getNote());
        updated.setCondition(book.getCondition());
        updated.setPosition(book.getPosition());
        if (StringUtils.isNotBlank(book.getImage())) {
            String savedImage = fileStorage.loadFileToStorage(book.getImage());
            updated.setImage(savedImage);
        }
        bookRepo.save(book);
        JOptionPane.showMessageDialog(this, "Update success!");
        loadModelToForm(book);
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtPosition.setText("");
        txtNote.setText("");
        txtCondition.setText("");
        selectStorage.setSelectedItem(null);
    }

    @Override
    public void loadModelToForm(Book model) {
        imageViewer.clearImage();
        txtID.setText(model.getIdString());
        txtPosition.setText(model.getPosition());
        txtCondition.setText(model.getCondition().toString());
        selectStorage.setSelectedItem(model.getStorage());
        if (model.getBorrowing() != null) {
            Reader borrower = model.getBorrowing().getBorrower();
            txtBorrow.setText(borrower.getEmail());
        } else {
            txtBorrow.setText("");
        }
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

    @Override
    public void renderTable(Collection<Book> data) {
        bookTablePane.setTableData(data);
    }

    @Override
    public void renderTable() {
        if (bookMetaModel == null) {
            renderTable(new ArrayList<>());
            return;
        }
        renderTable(bookMetaModel.getBooks());
    }

    public void setModel(BookMeta bookMeta) {
        if (bookMeta != null){
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
        selectStorage = new IdCombobox<>();
        btnNewStorage = new JButton();
        lblNote = new JLabel();
        noteScroll = new JScrollPane();
        txtNote = new JTextArea();
        lblBorrow = new JLabel();
        txtBorrow = new JTextField();
        panelImage = new JPanel();
        imageViewer = new ImageViewer();
        btnSelectImage = new JButton();
        panel1 = new JPanel();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnSave = new JButton();
        btnClone = new JButton();
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

                //======== formPanel ========
                {
                    formPanel.setBorder(new TitledBorder("Book Form"));

                    //---- labelId ----
                    labelId.setText("ID");

                    //---- labelMeta ----
                    labelMeta.setText("Meta");

                    //---- labelStorage ----
                    labelStorage.setText("Storage");

                    //---- txtID ----
                    txtID.setEditable(false);

                    //---- lblCondition ----
                    lblCondition.setText("Condition");

                    //---- txtMeta ----
                    txtMeta.setEditable(false);

                    //---- lblPosition ----
                    lblPosition.setText("Position");

                    //---- btnNewStorage ----
                    btnNewStorage.setText("New");
                    btnNewStorage.addActionListener(e -> btnNewStorageActionPerformed(e));

                    //---- lblNote ----
                    lblNote.setText("Note");

                    //======== noteScroll ========
                    {
                        noteScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        noteScroll.setViewportView(txtNote);
                    }

                    //---- lblBorrow ----
                    lblBorrow.setText("Borrow");

                    //---- txtBorrow ----
                    txtBorrow.setEditable(false);

                    //======== panelImage ========
                    {
                        panelImage.setBorder(new TitledBorder("Image"));

                        GroupLayout panelImageLayout = new GroupLayout(panelImage);
                        panelImage.setLayout(panelImageLayout);
                        panelImageLayout.setHorizontalGroup(
                            panelImageLayout.createParallelGroup()
                                .addComponent(imageViewer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        );
                        panelImageLayout.setVerticalGroup(
                            panelImageLayout.createParallelGroup()
                                .addComponent(imageViewer, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                        );
                    }

                    //---- btnSelectImage ----
                    btnSelectImage.setText("Select Image");
                    btnSelectImage.addActionListener(e -> btnSelectImageActionPerformed(e));

                    GroupLayout formPanelLayout = new GroupLayout(formPanel);
                    formPanel.setLayout(formPanelLayout);
                    formPanelLayout.setHorizontalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup()
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addComponent(labelStorage)
                                        .addGap(18, 18, 18)
                                        .addComponent(selectStorage, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnNewStorage, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblPosition)
                                            .addComponent(lblNote))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(noteScroll, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPosition, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addGroup(formPanelLayout.createParallelGroup()
                                                .addGroup(formPanelLayout.createSequentialGroup()
                                                    .addComponent(labelId)
                                                    .addGap(12, 12, 12))
                                                .addComponent(labelMeta, GroupLayout.Alignment.TRAILING))
                                            .addComponent(lblBorrow))
                                        .addGap(20, 20, 20)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtMeta)
                                            .addComponent(txtBorrow)
                                            .addGroup(formPanelLayout.createSequentialGroup()
                                                .addComponent(txtID, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                                                .addGap(39, 39, 39)
                                                .addComponent(lblCondition)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtCondition, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                .addGap(18, 18, 18)
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSelectImage, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE))
                                .addContainerGap(25, Short.MAX_VALUE))
                    );
                    formPanelLayout.setVerticalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(formPanelLayout.createSequentialGroup()
                                .addGroup(formPanelLayout.createParallelGroup()
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(labelId)
                                            .addComponent(txtID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblCondition)
                                            .addComponent(txtCondition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(17, 17, 17)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(labelMeta)
                                            .addComponent(txtMeta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(14, 14, 14)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblBorrow)
                                            .addComponent(txtBorrow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(26, 26, 26)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnNewStorage)
                                            .addComponent(labelStorage)
                                            .addComponent(selectStorage, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblPosition)
                                            .addComponent(txtPosition, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 22, Short.MAX_VALUE))
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(formPanelLayout.createParallelGroup()
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblNote)
                                            .addComponent(noteScroll, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(btnSelectImage)))
                                .addContainerGap(30, Short.MAX_VALUE))
                    );
                }

                //======== panel1 ========
                {

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnSave ----
                    btnSave.setText("Save");
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnClone ----
                    btnClone.setText("Clone");
                    btnClone.addActionListener(e -> btnCloneActionPerformed(e));

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addGap(0, 76, Short.MAX_VALUE)
                                .addComponent(btnClone)
                                .addGap(18, 18, 18)
                                .addComponent(btnSave)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addGap(18, 18, 18)
                                .addComponent(btnDelete))
                    );
                }

                GroupLayout formTabLayout = new GroupLayout(formTab);
                formTab.setLayout(formTabLayout);
                formTabLayout.setHorizontalGroup(
                    formTabLayout.createParallelGroup()
                        .addGroup(formTabLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(20, Short.MAX_VALUE))
                );
                formTabLayout.setVerticalGroup(
                    formTabLayout.createParallelGroup()
                        .addGroup(formTabLayout.createSequentialGroup()
                            .addGap(8, 8, 8)
                            .addGroup(formTabLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(11, 20, Short.MAX_VALUE))
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
                        .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
                        .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(bookTablePane, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addContainerGap())
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
    private IdCombobox<Storage> selectStorage;
    private JButton btnNewStorage;
    private JLabel lblNote;
    private JScrollPane noteScroll;
    private JTextArea txtNote;
    private JLabel lblBorrow;
    private JTextField txtBorrow;
    private JPanel panelImage;
    private ImageViewer imageViewer;
    private JButton btnSelectImage;
    private JPanel panel1;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private BookTablePane bookTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
