/*
 * Created by JFormDesigner on Sun Jan 03 20:54:04 ICT 2021
 */

package io.lana.library.ui.view.user;

import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.UserRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.UIException;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.component.UserTablePane;
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
import java.util.Collection;

@Component
public class UserManagerPanel extends JPanel implements CrudPanel<User> {
    private UserRepo userRepo;
    private User loggedInUser;

    public UserManagerPanel() {
        initComponents();

        ListSelectionModel selectionModel = userTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = userTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                btnDelete.setEnabled(false);
                btnClone.setEnabled(false);
                return;
            }
            User user = userTablePane.getRow(pos);
            loadModelToForm(user);
            btnDelete.setEnabled(true);
            btnClone.setEnabled(true);
        });
    }

    @Autowired
    public void setup(UserRepo userRepo, UserContext userContext) {
        this.userRepo = userRepo;
        this.loggedInUser = userContext.getUser();
    }

    @Override
    public void delete() {
        User user = userTablePane.getSelectedRow();
        if (user.equals(loggedInUser)) {
            throw new UIException(this, "Cannot delete current logged in user");
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure delete this?");
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        userTablePane.removeSelectedRow();
        WorkerUtils.runAsync(() -> userRepo.deleteById(user.getId()));
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        User user = getModelFromForm();
        if (!userTablePane.isAnyRowSelected()) {
            if (StringUtils.isNotBlank(user.getEmail()) && existsByEmail(user.getEmail())) {
                throw new InputException(this, "Email already exited");
            }
            if (existsByPhoneNumber(user.getPhoneNumber())) {
                throw new InputException(this, "Phone number already exited");
            }
            WorkerUtils.runAsync(() -> userRepo.save(user));
            JOptionPane.showMessageDialog(this, "Create success!");
            userTablePane.clearSearch();
            userTablePane.addRow(0, user);
            userTablePane.setSelectedRow(0);
            return;
        }

        User updated = userTablePane.getSelectedRow();
        if (!user.getEmail().equals(updated.getEmail())
            && StringUtils.isNotBlank(user.getEmail())
            && existsByEmail(user.getEmail())) {
            throw new InputException(this, "Email already exited");
        }
        if (!user.getPhoneNumber().equals(updated.getPhoneNumber())
            && existsByPhoneNumber(user.getPhoneNumber())) {
            throw new InputException(this, "Phone number already exited");
        }
        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
        updated.setPhoneNumber(user.getPhoneNumber());
        userRepo.save(updated);
        JOptionPane.showMessageDialog(this, "Update success!");
        userTablePane.refreshSelectedRow();
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return userTablePane.getInternalData().stream().anyMatch(user -> phoneNumber.equals(user.getPhoneNumber()));
    }

    private boolean existsByEmail(String email) {
        return userTablePane.getInternalData().stream().anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtEmail.setText("");
        txtName.setText("");
        txtPhone.setText("");
    }

    @Override
    public void loadModelToForm(User model) {
        txtID.setText(model.getIdString());
        txtName.setText(model.getName());
        txtEmail.setText(model.getEmail());
        txtPhone.setText(model.getPhoneNumber());
    }

    @Override
    public User getModelFromForm() {
        User user = new User();
        user.setEmail(txtEmail.getText());
        user.setPhoneNumber(txtPhone.getText().trim());
        user.setName(txtName.getText().trim());


        if (StringUtils.isBlank(user.getPhoneNumber()) ||
            StringUtils.isBlank(user.getName())) {
            throw new InputException(this, "Please enter required information: phone number, name");
        }

        if (StringUtils.isNotBlank(user.getEmail()) && !user.getEmail().matches(".+@.+")) {
            throw new InputException(this, "Invalid email format");
        }

        if (!user.getPhoneNumber().matches("[0-9]{10,15}")) {
            throw new InputException(this, "Invalid phone format: contain only number, and 10 - 15 length");
        }

        return user;
    }

    @Override
    public void renderTable(Collection<User> data) {
        userTablePane.setTableData(data);
    }

    @Override
    public void renderTable() {
        renderTable(userRepo.findAllByOrderByUpdatedAtDesc());
    }

    private void btnCloneActionPerformed(ActionEvent e) {
        User user = getModelFromForm();
        user.setPassword("");
        userTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Cloned user. edit then press save to save");
        loadModelToForm(user);
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
        panelUserManage = new JPanel();
        actionFormPanel = new JPanel();
        formPanel = new JPanel();
        lblID = new JLabel();
        txtID = new JTextField();
        label2 = new JLabel();
        textField1 = new JTextField();
        lblPassword = new JLabel();
        passwordField1 = new JPasswordField();
        label1 = new JLabel();
        txtConfirmPassword = new JTextField();
        lblAddress = new JLabel();
        scrollPane1 = new JScrollPane();
        list1 = new JList();
        separator1 = new JSeparator();
        lblPhone = new JLabel();
        txtPhone = new JTextField();
        lblEmail = new JLabel();
        txtEmail = new JTextField();
        lblName = new JLabel();
        txtName = new JTextField();
        actionPanel = new JPanel();
        btnDelete = new JButton();
        btnClear = new JButton();
        btnSave = new JButton();
        btnClone = new JButton();
        userTablePane = new UserTablePane();

        //======== this ========
        setBorder(new EmptyBorder(0, 10, 0, 10));

        //======== tab ========
        {

            //======== panelUserManage ========
            {
                panelUserManage.setBorder(null);

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
                            new TitledBorder("User Info"),
                            new EmptyBorder(15, 15, 20, 15)));
                        formPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 215, 0, 0, 200, 0};
                        ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 50, 25, 0, 0, 0};
                        ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
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

                        //---- label2 ----
                        label2.setText("Username");
                        formPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(textField1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblPassword ----
                        lblPassword.setText("Password");
                        formPanel.add(lblPassword, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(passwordField1, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- label1 ----
                        label1.setText("Confirm");
                        formPanel.add(label1, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtConfirmPassword, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblAddress ----
                        lblAddress.setText("Permissions");
                        formPanel.add(lblAddress, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setViewportView(list1);
                        }
                        formPanel.add(scrollPane1, new GridBagConstraints(1, 3, 1, 2, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(separator1, new GridBagConstraints(0, 5, 5, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblPhone ----
                        lblPhone.setText("Phone");
                        formPanel.add(lblPhone, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtPhone, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblEmail ----
                        lblEmail.setText("Email");
                        formPanel.add(lblEmail, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtEmail, new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblName ----
                        lblName.setText("Name");
                        formPanel.add(lblName, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));
                        formPanel.add(txtName, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
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

                        GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                        actionPanel.setLayout(actionPanelLayout);
                        actionPanelLayout.setHorizontalGroup(
                            actionPanelLayout.createParallelGroup()
                                .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        );
                        actionPanelLayout.setVerticalGroup(
                            actionPanelLayout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, actionPanelLayout.createSequentialGroup()
                                    .addContainerGap(220, Short.MAX_VALUE)
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

                GroupLayout panelUserManageLayout = new GroupLayout(panelUserManage);
                panelUserManage.setLayout(panelUserManageLayout);
                panelUserManageLayout.setHorizontalGroup(
                    panelUserManageLayout.createParallelGroup()
                        .addComponent(actionFormPanel, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                        .addComponent(userTablePane, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                );
                panelUserManageLayout.setVerticalGroup(
                    panelUserManageLayout.createParallelGroup()
                        .addGroup(panelUserManageLayout.createSequentialGroup()
                            .addComponent(actionFormPanel, GroupLayout.PREFERRED_SIZE, 398, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(userTablePane, GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                );
            }
            tab.addTab("User Manage", panelUserManage);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(tab)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(tab, GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane tab;
    private JPanel panelUserManage;
    private JPanel actionFormPanel;
    private JPanel formPanel;
    private JLabel lblID;
    private JTextField txtID;
    private JLabel label2;
    private JTextField textField1;
    private JLabel lblPassword;
    private JPasswordField passwordField1;
    private JLabel label1;
    private JTextField txtConfirmPassword;
    private JLabel lblAddress;
    private JScrollPane scrollPane1;
    private JList list1;
    private JSeparator separator1;
    private JLabel lblPhone;
    private JTextField txtPhone;
    private JLabel lblEmail;
    private JTextField txtEmail;
    private JLabel lblName;
    private JTextField txtName;
    private JPanel actionPanel;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private UserTablePane userTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
