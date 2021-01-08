/*
 * Created by JFormDesigner on Sun Jan 03 20:54:04 ICT 2021
 */

package io.lana.library.ui.view.reader;

import io.lana.library.core.datacenter.ReaderDataCenter;
import io.lana.library.core.model.Reader;
import io.lana.library.core.model.user.Permission;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.ui.InputException;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.component.ReaderTablePane;
import io.lana.library.ui.component.app.ImagePicker;
import io.lana.library.ui.component.app.ImageViewer;
import io.lana.library.ui.component.app.TextField;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.utils.DateFormatUtils;
import io.lana.library.utils.WorkerUtils;
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
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;

@Component
public class ReaderManagerPanel extends JPanel implements CrudPanel<Reader> {
    private final ImagePicker imagePicker = new ImagePicker();
    private final ButtonGroup genderButtonGroup = new ButtonGroup();

    private UserContext userContext;
    private BorrowBookDialog borrowBookDialog;
    private BorrowedBookListDialog borrowedBookListDialog;
    private ReaderDataCenter readerDataCenter;
    private FileStorage fileStorage;

    public ReaderManagerPanel() {
        initComponents();
        genderButtonGroup.add(radioFemale);
        genderButtonGroup.add(radioMale);

        ListSelectionModel selectionModel = readerTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = readerTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                btnDelete.setEnabled(false);
                btnClone.setEnabled(false);
                btnBorrow.setEnabled(false);
                btnViewBorrowed.setEnabled(false);
                readerTablePane.setEnabled(false);
                return;
            }
            Reader reader = readerTablePane.getRow(pos);
            loadModelToForm(reader);
            btnDelete.setEnabled(true);
            btnClone.setEnabled(true);
            btnBorrow.setEnabled(true);
            btnViewBorrowed.setEnabled(true);
        });
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            boolean canHandleBorrow = userContext.getUser().hasPermission(Permission.BORROWING_MANAGE);
            btnBorrow.setVisible(canHandleBorrow);
            btnViewBorrowed.setVisible(canHandleBorrow);
        }
        txtDate.setFormats(DateFormatUtils.COMMON_DATE_FORMAT);
        super.setVisible(aFlag);
    }

    @Autowired
    public void setup(ReaderDataCenter readerDataCenter, FileStorage fileStorage,
                      BorrowedBookListDialog borrowedBookListDialog,
                      BorrowBookDialog borrowBookDialog,
                      UserContext userContext) {
        this.userContext = userContext;
        this.readerDataCenter = readerDataCenter;
        this.fileStorage = fileStorage;
        this.borrowedBookListDialog = borrowedBookListDialog;
        this.borrowBookDialog = borrowBookDialog;
        this.readerTablePane.setRepositoryDataCenter(readerDataCenter);
    }

    @Override
    public void delete() {
        Reader reader = readerTablePane.getSelectedRow();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure delete this?");
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        if (reader.getBorrowedBookCount() > 0) {
            JOptionPane.showMessageDialog(this,
                "Reader still borrow some book. " +
                "Please remove them before delete this reader");
            return;
        }
        readerDataCenter.delete(reader);
        WorkerUtils.runAsync(() -> fileStorage.deleteFileFromStorage(reader.getAvatar()));
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        Reader reader = getModelFromForm();
        if (!readerTablePane.isAnyRowSelected()) {
            if (StringUtils.isNotBlank(reader.getEmail()) && existsByEmail(reader.getEmail())) {
                throw new InputException(this, "Email already exited");
            }
            if (existsByPhoneNumber(reader.getPhoneNumber())) {
                throw new InputException(this, "Phone number already exited");
            }
            if (StringUtils.isNotBlank(reader.getAvatar())) {
                String savedImage = fileStorage.loadFileToStorage(reader.getAvatar());
                reader.setAvatar(savedImage);
            }
            readerDataCenter.save(reader);
            JOptionPane.showMessageDialog(this, "Create success!");
            return;
        }

        Reader updated = readerTablePane.getSelectedRow();
        if (StringUtils.isNotBlank(reader.getEmail()) &&
            !reader.getEmail().equals(updated.getEmail())
            && existsByEmail(reader.getEmail())) {
            throw new InputException(this, "Email already exited");
        }
        if (!reader.getPhoneNumber().equals(updated.getPhoneNumber())
            && existsByPhoneNumber(reader.getPhoneNumber())) {
            throw new InputException(this, "Phone number already exited");
        }
        updated.setBirth(reader.getBirth());
        updated.setName(reader.getName());
        updated.setEmail(reader.getEmail());
        updated.setPhoneNumber(reader.getPhoneNumber());
        updated.setGender(reader.getGender());
        updated.setLimit(reader.getLimit());
        updated.setAddress(reader.getAddress());
        if (StringUtils.isNotBlank(reader.getAvatar())) {
            String savedImage = fileStorage.loadFileToStorage(reader.getAvatar());
            updated.setAvatar(savedImage);
        }
        readerDataCenter.update(updated);
        JOptionPane.showMessageDialog(this, "Update success!");
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return readerTablePane.stream().anyMatch(reader -> phoneNumber.equals(reader.getPhoneNumber()));
    }

    private boolean existsByEmail(String email) {
        return readerTablePane.stream().anyMatch(reader -> email.equals(reader.getEmail()));
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtDate.setDate(null);
        txtEmail.setText("");
        txtLimit.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        imageViewer.clearImage();
        readerTablePane.clearSelection();
    }

    @Override
    public void loadModelToForm(Reader model) {
        imageViewer.clearImage();
        txtID.setText(model.getIdString());
        txtName.setText(model.getName());
        txtEmail.setText(model.getEmail());
        txtPhone.setText(model.getPhoneNumber());
        txtLimit.setText(model.getLimit().toString());
        txtDate.setDate(DateFormatUtils.toDate(model.getBirth()));
        txtAddress.setText(model.getAddress());
        genderButtonGroup.setSelected(radioMale.getModel(), model.getGender());
        genderButtonGroup.setSelected(radioFemale.getModel(), !model.getGender());

        if (StringUtils.isNotBlank(model.getAvatar())) {
            WorkerUtils.runAsync(() -> {
                try (InputStream image = fileStorage.readFileFromStorage(model.getAvatar())) {
                    imageViewer.loadImage(image);
                } catch (IOException e) {
                    // ignore
                }
            });
        }
    }

    @Override
    public Reader getModelFromForm() {
        Reader reader = new Reader();
        reader.setAddress(txtAddress.getText());
        reader.setEmail(txtEmail.getText());
        reader.setAvatar(imageViewer.getImagePath());
        reader.setGender(radioMale.isSelected());
        reader.setPhoneNumber(txtPhone.getText());
        reader.setName(txtName.getText());
        try {
            reader.setLimit(Integer.parseInt(txtLimit.getText()));
            if (reader.getLimit() < -1) {
                throw new InputException(this, "Limit must >= 0 (or -1 if no limit) ");
            }
        } catch (Exception e) {
            throw new InputException(this, "Limit is required and must a number");
        }

        Date date = txtDate.getDate();
        if (date == null) {
            throw new InputException(this, "Birthdate is required and must follow format yyyy-MM-dd");
        }
        reader.setBirth(DateFormatUtils.toLocalDate(date));
        if (reader.getBirth().isAfter(LocalDate.now())) {
            throw new InputException(this, "Are you from the future ?");
        }

        if (StringUtils.isBlank(reader.getPhoneNumber()) ||
            StringUtils.isBlank(reader.getName())) {
            throw new InputException(this, "Please enter required information: phone number, name");
        }

        if (StringUtils.isNotBlank(reader.getEmail()) && !reader.getEmail().matches(".+@.+")) {
            throw new InputException(this, "Invalid email format");
        }

        if (!reader.getPhoneNumber().matches("[0-9]{10,15}")) {
            throw new InputException(this, "Invalid phone format: contain only number, and 10 - 15 length");
        }

        return reader;
    }

    private void btnSelectAvatarActionPerformed(ActionEvent e) {
        imagePicker.showSelectImageDialog().ifPresent(selectedFile -> {
            String path = selectedFile.getAbsolutePath();
            imageViewer.loadImage(path);
        });
    }

    private void btnCloneActionPerformed(ActionEvent e) {
        Reader reader = getModelFromForm();
        reader.setAvatar(null);
        readerTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Cloned reader. edit then press save to save");
        loadModelToForm(reader);
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        delete();
    }

    private void btnClearActionPerformed(ActionEvent e) {
        clearForm();
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        save();
    }

    private void btnViewBorrowedActionPerformed(ActionEvent e) {
        borrowedBookListDialog.setModel(readerTablePane.getSelectedRow());
        borrowedBookListDialog.setVisible(true);
    }

    private void btnBorrowActionPerformed(ActionEvent e) {
        borrowBookDialog.setModel(readerTablePane.getSelectedRow());
        borrowBookDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tab = new JTabbedPane();
        panelReaderManage = new JPanel();
        readerTablePane = new ReaderTablePane();
        actionFormPanel = new JPanel();
        formPanel = new JPanel();
        lblID = new JLabel();
        txtID = new JTextField();
        lblLimit = new JLabel();
        txtLimit = new TextField();
        panelImage = new JPanel();
        imageViewer = new ImageViewer();
        lblName = new JLabel();
        txtName = new TextField();
        lblEmail = new JLabel();
        txtEmail = new TextField();
        lblPhone = new JLabel();
        txtPhone = new TextField();
        lblGender = new JLabel();
        radioMale = new JRadioButton();
        radioFemale = new JRadioButton();
        lblBirth = new JLabel();
        txtDate = new JXDatePicker();
        btnSelectAvatar = new JButton();
        lblAddress = new JLabel();
        scrollPane1 = new JScrollPane();
        txtAddress = new JTextArea();
        actionPanel = new JPanel();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnSave = new JButton();
        btnClone = new JButton();
        btnViewBorrowed = new JButton();
        btnBorrow = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(0, 10, 0, 10));

        //======== tab ========
        {

            //======== panelReaderManage ========
            {
                panelReaderManage.setBorder(null);

                //---- readerTablePane ----
                readerTablePane.setBorder(null);

                //======== actionFormPanel ========
                {
                    actionFormPanel.setBorder(new CompoundBorder(
                        new EtchedBorder(),
                        new EmptyBorder(15, 15, 20, 15)));
                    actionFormPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) actionFormPanel.getLayout()).columnWidths = new int[]{0, 90, 0};
                    ((GridBagLayout) actionFormPanel.getLayout()).rowHeights = new int[]{0, 0};
                    ((GridBagLayout) actionFormPanel.getLayout()).columnWeights = new double[]{1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) actionFormPanel.getLayout()).rowWeights = new double[]{1.0, 1.0E-4};

                    //======== formPanel ========
                    {
                        formPanel.setBorder(new CompoundBorder(
                            new TitledBorder("Reader Info"),
                            new EmptyBorder(15, 15, 20, 15)));
                        formPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 25, 150, 0};
                        ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                        ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                        //---- lblID ----
                        lblID.setText("ID");
                        formPanel.add(lblID, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- txtID ----
                        txtID.setEditable(false);
                        formPanel.add(txtID, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblLimit ----
                        lblLimit.setText("Limit");
                        formPanel.add(lblLimit, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtLimit, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //======== panelImage ========
                        {
                            panelImage.setBorder(new TitledBorder("Avatar"));
                            panelImage.setLayout(new GridLayout());
                            panelImage.add(imageViewer);
                        }
                        formPanel.add(panelImage, new GridBagConstraints(6, 0, 1, 5, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblName ----
                        lblName.setText("Name");
                        formPanel.add(lblName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtName, new GridBagConstraints(1, 1, 4, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblEmail ----
                        lblEmail.setText("Email");
                        formPanel.add(lblEmail, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtEmail, new GridBagConstraints(1, 2, 4, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblPhone ----
                        lblPhone.setText("Phone");
                        formPanel.add(lblPhone, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtPhone, new GridBagConstraints(1, 3, 4, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblGender ----
                        lblGender.setText("Gender");
                        formPanel.add(lblGender, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- radioMale ----
                        radioMale.setText("Male");
                        formPanel.add(radioMale, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- radioFemale ----
                        radioFemale.setText("Female");
                        formPanel.add(radioFemale, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblBirth ----
                        lblBirth.setText("Birthdate");
                        formPanel.add(lblBirth, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtDate, new GridBagConstraints(1, 5, 4, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- btnSelectAvatar ----
                        btnSelectAvatar.setText("Select Avatar");
                        btnSelectAvatar.addActionListener(e -> btnSelectAvatarActionPerformed(e));
                        formPanel.add(btnSelectAvatar, new GridBagConstraints(6, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblAddress ----
                        lblAddress.setText("Address");
                        formPanel.add(lblAddress, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setViewportView(txtAddress);
                        }
                        formPanel.add(scrollPane1, new GridBagConstraints(1, 6, 4, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));
                    }
                    actionFormPanel.add(formPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 20), 0, 0));

                    //======== actionPanel ========
                    {
                        actionPanel.setBorder(null);

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

                        //---- btnViewBorrowed ----
                        btnViewBorrowed.setText("View Borrowed");
                        btnViewBorrowed.setEnabled(false);
                        btnViewBorrowed.addActionListener(e -> btnViewBorrowedActionPerformed(e));

                        //---- btnBorrow ----
                        btnBorrow.setText("Borrow Book");
                        btnBorrow.setEnabled(false);
                        btnBorrow.addActionListener(e -> btnBorrowActionPerformed(e));

                        GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                        actionPanel.setLayout(actionPanelLayout);
                        actionPanelLayout.setHorizontalGroup(
                            actionPanelLayout.createParallelGroup()
                                .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnBorrow, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnViewBorrowed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        );
                        actionPanelLayout.setVerticalGroup(
                            actionPanelLayout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, actionPanelLayout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(btnViewBorrowed)
                                    .addGap(16, 16, 16)
                                    .addComponent(btnBorrow)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                                    .addComponent(btnClone)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnSave)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnClear)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnDelete))
                        );
                    }
                    actionFormPanel.add(actionPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }

                GroupLayout panelReaderManageLayout = new GroupLayout(panelReaderManage);
                panelReaderManage.setLayout(panelReaderManageLayout);
                panelReaderManageLayout.setHorizontalGroup(
                    panelReaderManageLayout.createParallelGroup()
                        .addComponent(readerTablePane, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                        .addComponent(actionFormPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                );
                panelReaderManageLayout.setVerticalGroup(
                    panelReaderManageLayout.createParallelGroup()
                        .addGroup(panelReaderManageLayout.createSequentialGroup()
                            .addComponent(actionFormPanel, GroupLayout.PREFERRED_SIZE, 373, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(readerTablePane, GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                );
            }
            tab.addTab("Reader Manage", panelReaderManage);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(tab)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addComponent(tab)
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tab;
    private JPanel panelReaderManage;
    private ReaderTablePane readerTablePane;
    private JPanel actionFormPanel;
    private JPanel formPanel;
    private JLabel lblID;
    private JTextField txtID;
    private JLabel lblLimit;
    private TextField txtLimit;
    private JPanel panelImage;
    private ImageViewer imageViewer;
    private JLabel lblName;
    private TextField txtName;
    private JLabel lblEmail;
    private TextField txtEmail;
    private JLabel lblPhone;
    private TextField txtPhone;
    private JLabel lblGender;
    private JRadioButton radioMale;
    private JRadioButton radioFemale;
    private JLabel lblBirth;
    private JXDatePicker txtDate;
    private JButton btnSelectAvatar;
    private JLabel lblAddress;
    private JScrollPane scrollPane1;
    private JTextArea txtAddress;
    private JPanel actionPanel;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private JButton btnViewBorrowed;
    private JButton btnBorrow;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
