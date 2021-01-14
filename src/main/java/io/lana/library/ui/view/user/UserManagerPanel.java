/*
 * Created by JFormDesigner on Sun Jan 03 20:54:04 ICT 2021
 */

package io.lana.library.ui.view.user;

import io.lana.library.core.model.user.Permission;
import io.lana.library.core.model.user.User;
import io.lana.library.core.spi.PasswordEncoder;
import io.lana.library.core.spi.datacenter.UserDataCenter;
import io.lana.library.ui.InputException;
import io.lana.library.ui.UIException;
import io.lana.library.ui.UserContext;
import io.lana.library.ui.component.UserTablePane;
import io.lana.library.ui.component.app.TextField;
import io.lana.library.ui.view.app.CrudPanel;
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
import java.util.HashSet;
import java.util.Set;

@Component
public class UserManagerPanel extends JPanel implements CrudPanel<User> {
    private User loggedInUser;
    private PasswordEncoder passwordEncoder;
    private UserDataCenter userDataCenter;

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
    public void setup(UserDataCenter userDataCenter, UserContext userContext, PasswordEncoder passwordEncoder) {
        this.loggedInUser = userContext.getUser();
        this.passwordEncoder = passwordEncoder;
        this.userDataCenter = userDataCenter;
        userTablePane.setRepositoryDataCenter(userDataCenter);
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
        userDataCenter.delete(user);
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        User user = getModelFromForm();
        if (!userTablePane.isAnyRowSelected()) {
            if (StringUtils.isBlank(user.getPassword())) {
                throw new InputException(this, "Please enter password");

            }
            if (StringUtils.isNotBlank(user.getEmail()) && existsByEmail(user.getEmail())) {
                throw new InputException(this, "Email already exited");
            }
            if (StringUtils.isNotBlank(user.getPhoneNumber()) && existsByPhoneNumber(user.getPhoneNumber())) {
                throw new InputException(this, "Phone number already exited");
            }
            if (existsByUsername(user.getUsername())) {
                throw new InputException(this, "Username already existed");
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDataCenter.save(user);
            JOptionPane.showMessageDialog(this, "Create success!");
            return;
        }

        User updated = userTablePane.getSelectedRow();
        if (StringUtils.isNotBlank(user.getEmail())
            && user.getEmail().equals(updated.getEmail())
            && existsByEmail(user.getEmail())) {
            throw new InputException(this, "Email already exited");
        }
        if (!user.getUsername().equals(updated.getUsername())
            && existsByUsername(user.getUsername())) {
            throw new InputException(this, "Username already exited");
        }
        if (StringUtils.isNotBlank(user.getPhoneNumber()) &&
            !user.getPhoneNumber().equals(updated.getPhoneNumber())
            && existsByPhoneNumber(user.getPhoneNumber())) {
            throw new InputException(this, "Phone number already exited");
        }
        if (StringUtils.isNotBlank(user.getPassword())) {
            updated.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        updated.setName(user.getName());
        updated.setEmail(user.getEmail());
        updated.setPhoneNumber(user.getPhoneNumber());
        updated.setUsername(user.getUsername());
        updated.setPermissions(user.getPermissions());
        userDataCenter.update(updated);
        JOptionPane.showMessageDialog(this, "Update success!");
    }

    private boolean existsByUsername(String username) {
        return userTablePane.stream().anyMatch(user -> username.equals(user.getUsername()));
    }

    private boolean existsByPhoneNumber(String phoneNumber) {
        return userTablePane.stream().anyMatch(user -> phoneNumber.equals(user.getPhoneNumber()));
    }

    private boolean existsByEmail(String email) {
        return userTablePane.stream().anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public void clearForm() {
        txtID.setText("");
        txtEmail.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtUserName.setText("");
        txtPhone.setText("");
        checkUserManage.setSelected(false);
        checkBookManage.setSelected(false);
        checkReaderManage.setSelected(false);
        checkBorrowManage.setSelected(false);
        userTablePane.clearSelection();
    }

    @Override
    public void loadModelToForm(User model) {
        txtID.setText(model.getIdString());
        txtUserName.setText(model.getUsername());
        txtPassword.setText("");
        txtName.setText(model.getName());
        txtEmail.setText(model.getEmail());
        txtPhone.setText(model.getPhoneNumber());
        checkUserManage.setSelected(model.hasPermission(Permission.USER_MANAGE));
        checkBookManage.setSelected(model.hasPermission(Permission.BOOK_MANAGE));
        checkReaderManage.setSelected(model.hasPermission(Permission.READER_MANAGE));
        checkBorrowManage.setSelected(model.hasPermission(Permission.BORROWING_MANAGE));
    }

    @Override
    public User getModelFromForm() {
        User user = new User();
        user.setEmail(txtEmail.getText());
        user.setPhoneNumber(txtPhone.getText());
        user.setName(txtName.getText());
        user.setUsername(txtUserName.getText());
        user.setPassword(String.valueOf(txtPassword.getPassword()));
        Set<Permission> permissions = new HashSet<>();
        if (checkUserManage.isSelected()) {
            permissions.add(Permission.USER_MANAGE);
        }
        if (checkBookManage.isSelected()) {
            permissions.add(Permission.BOOK_MANAGE);
        }
        if (checkReaderManage.isSelected()) {
            permissions.add(Permission.READER_MANAGE);
        }
        if (checkBorrowManage.isSelected()) {
            permissions.add(Permission.BORROWING_MANAGE);
        }
        if (permissions.isEmpty()) {
            throw new InputException(this, "Please select a permission");
        }
        user.setPermissions(permissions);

        if (StringUtils.isBlank(user.getUsername()) ||
            StringUtils.isBlank(user.getName())) {
            throw new InputException(this, "Please enter required information: name, username");
        }

        if (StringUtils.isNotBlank(user.getEmail()) && !user.getEmail().matches(".+@.+")) {
            throw new InputException(this, "Invalid email format");
        }

        if (StringUtils.isNotBlank(user.getPhoneNumber()) && !user.getPhoneNumber().matches("[0-9]{10,15}")) {
            throw new InputException(this, "Invalid phone format: contain only number, and 10 - 15 length");
        }

        return user;
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
        txtUserName = new TextField();
        lblPassword = new JLabel();
        txtPassword = new JPasswordField();
        label3 = new JLabel();
        permission = new JPanel();
        checkBookManage = new JCheckBox();
        checkReaderManage = new JCheckBox();
        checkUserManage = new JCheckBox();
        checkBorrowManage = new JCheckBox();
        separator1 = new JSeparator();
        lblPhone = new JLabel();
        txtPhone = new TextField();
        lblEmail = new JLabel();
        txtEmail = new TextField();
        lblName = new JLabel();
        txtName = new TextField();
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
                        ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 25, 0, 0, 0};
                        ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

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
                        formPanel.add(txtUserName, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblPassword ----
                        lblPassword.setText("Password");
                        formPanel.add(lblPassword, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtPassword, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- label3 ----
                        label3.setText("Permission");
                        formPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //======== permission ========
                        {
                            permission.setLayout(new GridBagLayout());
                            ((GridBagLayout) permission.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0};
                            ((GridBagLayout) permission.getLayout()).rowHeights = new int[]{0, 0};
                            ((GridBagLayout) permission.getLayout()).columnWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0E-4};
                            ((GridBagLayout) permission.getLayout()).rowWeights = new double[]{0.0, 1.0E-4};

                            //---- checkBookManage ----
                            checkBookManage.setText("Book Manage");
                            permission.add(checkBookManage, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- checkReaderManage ----
                            checkReaderManage.setText("Reader Manage");
                            permission.add(checkReaderManage, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- checkUserManage ----
                            checkUserManage.setText("User Manage");
                            permission.add(checkUserManage, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- checkBorrowManage ----
                            checkBorrowManage.setText("Borrowing Manage");
                            permission.add(checkBorrowManage, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        formPanel.add(permission, new GridBagConstraints(1, 2, 4, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));
                        formPanel.add(separator1, new GridBagConstraints(0, 3, 5, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblPhone ----
                        lblPhone.setText("Phone");
                        formPanel.add(lblPhone, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtPhone, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));

                        //---- lblEmail ----
                        lblEmail.setText("Email");
                        formPanel.add(lblEmail, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 15), 0, 0));
                        formPanel.add(txtEmail, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 20, 0), 0, 0));

                        //---- lblName ----
                        lblName.setText("Name");
                        formPanel.add(lblName, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 15), 0, 0));
                        formPanel.add(txtName, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
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
                                    .addContainerGap(136, Short.MAX_VALUE)
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
                        .addComponent(userTablePane, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                        .addComponent(actionFormPanel, GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                );
                panelUserManageLayout.setVerticalGroup(
                    panelUserManageLayout.createParallelGroup()
                        .addGroup(panelUserManageLayout.createSequentialGroup()
                            .addComponent(actionFormPanel, GroupLayout.PREFERRED_SIZE, 322, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(userTablePane, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(tab, GroupLayout.DEFAULT_SIZE, 735, Short.MAX_VALUE)
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
    private TextField txtUserName;
    private JLabel lblPassword;
    private JPasswordField txtPassword;
    private JLabel label3;
    private JPanel permission;
    private JCheckBox checkBookManage;
    private JCheckBox checkReaderManage;
    private JCheckBox checkUserManage;
    private JCheckBox checkBorrowManage;
    private JSeparator separator1;
    private JLabel lblPhone;
    private TextField txtPhone;
    private JLabel lblEmail;
    private TextField txtEmail;
    private JLabel lblName;
    private TextField txtName;
    private JPanel actionPanel;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSave;
    private JButton btnClone;
    private UserTablePane userTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
