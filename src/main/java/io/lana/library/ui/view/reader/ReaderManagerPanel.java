/*
 * Created by JFormDesigner on Sun Jan 03 20:54:04 ICT 2021
 */

package io.lana.library.ui.view.reader;

import io.lana.library.core.model.Reader;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.ReaderRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.ReaderTablePane;
import io.lana.library.ui.component.app.ImagePicker;
import io.lana.library.ui.component.app.ImageViewer;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.utils.DateFormatUtils;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;

@Component
public class ReaderManagerPanel extends JPanel implements CrudPanel<Reader> {
    private final ImagePicker imagePicker = new ImagePicker();
    private final ButtonGroup genderButtonGroup = new ButtonGroup();

    private ReaderRepo readerRepo;
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

    @Autowired
    public void setup(ReaderRepo readerRepo, FileStorage fileStorage) {
        this.readerRepo = readerRepo;
        this.fileStorage = fileStorage;
        WorkerUtils.runAsync(this::renderTable);
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
        readerRepo.deleteById(reader.getId());
        fileStorage.deleteFileFromStorage(reader.getAvatar());
        readerTablePane.removeSelectedRow();
        readerTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        Reader reader = getModelFromForm();
        if (!readerTablePane.isAnyRowSelected()) {
            if (StringUtils.isNotBlank(reader.getEmail()) && readerRepo.existsByEmail(reader.getEmail())) {
                throw new InputException(this, "Email already exited");
            }
            if (readerRepo.existsByPhoneNumber(reader.getPhoneNumber())) {
                throw new InputException(this, "PhoneNumber already exited");
            }
            if (StringUtils.isNotBlank(reader.getAvatar())) {
                String savedImage = fileStorage.loadFileToStorage(reader.getAvatar());
                reader.setAvatar(savedImage);
            }
            readerRepo.save(reader);
            JOptionPane.showMessageDialog(this, "Create success!");
            readerTablePane.addRow(0, reader);
            readerTablePane.clearSearch();
            readerTablePane.setSelectedRow(0);
            return;
        }

        Reader updated = readerTablePane.getSelectedRow();
        if (!reader.getEmail().equals(updated.getEmail())
            && StringUtils.isNotBlank(reader.getEmail())
            && readerRepo.existsByEmail(reader.getEmail())) {
            throw new InputException(this, "Email already exited");
        }
        if (!reader.getPhoneNumber().equals(updated.getPhoneNumber())
            && readerRepo.existsByPhoneNumber(reader.getPhoneNumber())) {
            throw new InputException(this, "PhoneNumber already exited");
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
        readerRepo.save(updated);
        JOptionPane.showMessageDialog(this, "Update success!");
        readerTablePane.refreshSelectedRow();
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtDate.setText("");
        txtEmail.setText("");
        txtLimit.setText("");
        txtName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        imageViewer.clearImage();
    }

    @Override
    public void loadModelToForm(Reader model) {
        imageViewer.clearImage();
        txtID.setText(model.getIdString());
        txtName.setText(model.getName());
        txtEmail.setText(model.getEmail());
        txtPhone.setText(model.getPhoneNumber());
        txtLimit.setText(model.getLimit().toString());
        txtDate.setText(DateFormatUtils.toDateStringWithDefaultUnknown(model.getBirth()));
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
        reader.setPhoneNumber(txtPhone.getText().trim());
        reader.setName(txtName.getText().trim());
        try {
            reader.setLimit(Integer.parseInt(txtLimit.getText()));
            if (reader.getLimit() < -1) {
                throw new RuntimeException("Limit must >= 0 (or -1 if no limit) ");
            }
        } catch (Exception e) {
            throw new InputException(this, "Limit is required and must a number");
        }

        try {
            reader.setBirth(LocalDate.parse(txtDate.getText(), DateFormatUtils.COMMON_DATE_FORMAT));
        } catch (Exception e) {
            throw new InputException(this, "Birthdate is required and must follow format yyyy-MM-dd");
        }

        if (StringUtils.isBlank(reader.getPhoneNumber()) ||
            StringUtils.isBlank(reader.getName())) {
            throw new InputException(this, "Please enter required information: phone number, name");
        }

        return reader;
    }

    @Override
    public void renderTable(Collection<Reader> data) {
        readerTablePane.setTableData(data);
    }

    @Override
    public void renderTable() {
        renderTable(readerRepo.findAll());
    }

    private void btnSelectAvatarActionPerformed(ActionEvent e) {
        imagePicker.showSelectImageDialog().ifPresent(selectedFile -> {
            String path = selectedFile.getAbsolutePath();
            imageViewer.loadImage(path);
        });
    }

    private void btnCloneActionPerformed(ActionEvent e) {
        Reader reader = getModelFromForm();
        reader.setAddress(null);
        readerTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Cloned reader. edit then press save to save new book");
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        tab = new JTabbedPane();
        panelReaderManage = new JPanel();
        formPanel = new JPanel();
        lblID = new JLabel();
        txtID = new JTextField();
        lblLimit = new JLabel();
        txtLimit = new JTextField();
        panelImage = new JPanel();
        imageViewer = new ImageViewer();
        lblName = new JLabel();
        txtName = new JTextField();
        lblEmail = new JLabel();
        txtEmail = new JTextField();
        lblPhone = new JLabel();
        txtPhone = new JTextField();
        lblGender = new JLabel();
        radioMale = new JRadioButton();
        radioFemale = new JRadioButton();
        lblBirth = new JLabel();
        txtDate = new JTextField();
        lblAddress = new JLabel();
        scrollPane1 = new JScrollPane();
        txtAddress = new JTextArea();
        btnSelectAvatar = new JButton();
        readerTablePane = new ReaderTablePane();
        panel1 = new JPanel();
        panel2 = new JPanel();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnSave = new JButton();
        btnClone = new JButton();
        btnViewBorrowed = new JButton();
        btnBorrow = new JButton();

        //======== this ========

        //======== tab ========
        {

            //======== panelReaderManage ========
            {
                panelReaderManage.setBorder(new EtchedBorder());

                //======== formPanel ========
                {
                    formPanel.setBorder(null);
                    formPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 25, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0E-4};
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
                    formPanel.add(panelImage, new GridBagConstraints(6, 0, 1, 6, 0.0, 0.0,
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
                    formPanel.add(radioFemale, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
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

                    //---- btnSelectAvatar ----
                    btnSelectAvatar.setText("Select Avatar");
                    btnSelectAvatar.addActionListener(e -> btnSelectAvatarActionPerformed(e));
                    formPanel.add(btnSelectAvatar, new GridBagConstraints(6, 6, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));
                }

                //---- readerTablePane ----
                readerTablePane.setBorder(null);

                //======== panel1 ========
                {
                    panel1.setBorder(new MatteBorder(0, 1, 0, 0, Color.lightGray));

                    //======== panel2 ========
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

                        //---- btnViewBorrowed ----
                        btnViewBorrowed.setText("View Borrowed");
                        btnViewBorrowed.setEnabled(false);

                        //---- btnBorrow ----
                        btnBorrow.setText("Borrow Book");
                        btnBorrow.setEnabled(false);

                        GroupLayout panel2Layout = new GroupLayout(panel2);
                        panel2.setLayout(panel2Layout);
                        panel2Layout.setHorizontalGroup(
                            panel2Layout.createParallelGroup()
                                .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnViewBorrowed, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                .addComponent(btnBorrow, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        );
                        panel2Layout.setVerticalGroup(
                            panel2Layout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, panel2Layout.createSequentialGroup()
                                    .addComponent(btnViewBorrowed)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnBorrow)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                                    .addComponent(btnClone)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnSave)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnClear)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnDelete))
                        );
                    }

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addContainerGap(18, Short.MAX_VALUE)
                                .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    );
                }

                GroupLayout panelReaderManageLayout = new GroupLayout(panelReaderManage);
                panelReaderManage.setLayout(panelReaderManageLayout);
                panelReaderManageLayout.setHorizontalGroup(
                    panelReaderManageLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, panelReaderManageLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(panelReaderManageLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(readerTablePane, GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                                .addGroup(panelReaderManageLayout.createSequentialGroup()
                                    .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                                    .addGap(18, 18, 18)
                                    .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                            .addGap(10, 10, 10))
                );
                panelReaderManageLayout.setVerticalGroup(
                    panelReaderManageLayout.createParallelGroup()
                        .addGroup(panelReaderManageLayout.createSequentialGroup()
                            .addGap(11, 11, 11)
                            .addGroup(panelReaderManageLayout.createParallelGroup()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(formPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(31, 31, 31)
                            .addComponent(readerTablePane, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
            }
            tab.addTab("Reader Manage", panelReaderManage);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tab, GroupLayout.DEFAULT_SIZE, 780, Short.MAX_VALUE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(tab, GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tab;
    private JPanel panelReaderManage;
    private JPanel formPanel;
    private JLabel lblID;
    private JTextField txtID;
    private JLabel lblLimit;
    private JTextField txtLimit;
    private JPanel panelImage;
    private ImageViewer imageViewer;
    private JLabel lblName;
    private JTextField txtName;
    private JLabel lblEmail;
    private JTextField txtEmail;
    private JLabel lblPhone;
    private JTextField txtPhone;
    private JLabel lblGender;
    private JRadioButton radioMale;
    private JRadioButton radioFemale;
    private JLabel lblBirth;
    private JTextField txtDate;
    private JLabel lblAddress;
    private JScrollPane scrollPane1;
    private JTextArea txtAddress;
    private JButton btnSelectAvatar;
    private ReaderTablePane readerTablePane;
    private JPanel panel1;
    private JPanel panel2;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private JButton btnViewBorrowed;
    private JButton btnBorrow;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
