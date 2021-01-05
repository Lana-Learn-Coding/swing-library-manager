/*
 * Created by JFormDesigner on Mon Jan 04 01:17:53 ICT 2021
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Category;
import io.lana.library.ui.UIException;
import io.lana.library.ui.component.BookMetaTablePane;
import io.lana.library.ui.component.app.ComboBox;
import io.lana.library.ui.view.app.FormPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class BookMetaFilterDialog extends JDialog implements FormPanel<BookMeta> {
    private final BookMetaTablePane rootTablePane;
    private final ComboBox<Category> rootComboBox;
    private BookMeta example = new BookMeta();

    public BookMetaFilterDialog(BookMetaTablePane tablePane,
                                ComboBox<Category> comboBox) {
        initComponents();
        this.rootTablePane = tablePane;
        this.rootComboBox = comboBox;
        example.setAuthor("");
        example.setPublisher("");

        for (RowFilter.ComparisonType comparisonType : RowFilter.ComparisonType.values()) {
            selectYearComparsion.addItem(comparisonType);
        }
        selectYearComparsion.setSelectedItem(RowFilter.ComparisonType.EQUAL);
    }

    private void loadCategories() {
        selectCategory.removeAllItems();
        for (Category category : rootComboBox) {
            selectCategory.addItem(category);
        }
    }

    private void btnClearCategoryActionPerformed(ActionEvent e) {
        selectCategory.setSelectedItem(null);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            setSize(500, 300);
            loadCategories();
            loadModelToForm(example);
        }
        super.setVisible(b);
    }

    @Override
    public void clearForm() {
        txtAuthor.setText("");
        txtPublisher.setText("");
        txtTitle.setText("");
        selectCategory.setSelectedItem(null);
        txtYear.setText("");
    }

    @Override
    public void loadModelToForm(BookMeta model) {
        txtAuthor.setText(model.getAuthor());
        txtPublisher.setText(model.getPublisher());
        txtTitle.setText(model.getTitle());
        selectCategory.setSelectedItem(model.getCategory());
        if (model.getYear() != null) {
            txtYear.setText(model.getYear().toString());
        }
    }

    @Override
    public BookMeta getModelFromForm() {
        BookMeta bookMeta = new BookMeta();
        bookMeta.setAuthor(txtAuthor.getText());
        bookMeta.setPublisher(txtPublisher.getText());
        bookMeta.setTitle(txtTitle.getText());
        bookMeta.setCategory(selectCategory.getSelectedItem());
        if (StringUtils.isNotBlank(txtYear.getText())) {
            try {
                bookMeta.setYear(Integer.parseInt(txtYear.getText()));
            } catch (Exception e) {
                throw new UIException(this, "Invalid year format");
            }
        }
        return bookMeta;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        example = getModelFromForm();
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (StringUtils.isNotBlank(txtAuthor.getText())) {
            filters.add(RowFilter.regexFilter(txtAuthor.getText(), 3));
        }
        if (StringUtils.isNotBlank(txtPublisher.getText())) {
            filters.add(RowFilter.regexFilter(txtPublisher.getText(), 4));
        }
        if (StringUtils.isNotBlank(txtTitle.getText())) {
            filters.add(RowFilter.regexFilter(txtTitle.getText(), 1));
        }
        if (StringUtils.isNotBlank(txtYear.getText())) {
            try {
                RowFilter.ComparisonType comparisonType = (RowFilter.ComparisonType) selectYearComparsion.getSelectedItem();
                filters.add(RowFilter.numberFilter(comparisonType, Integer.parseInt(txtYear.getText()), 6));
            } catch (Exception error) {
                throw new UIException(this, "Invalid year format");
            }
        }
        rootTablePane.setFilter(RowFilter.andFilter(filters));
        setVisible(false);
    }

    private void clearButtonActionPerformed(ActionEvent e) {
        clearForm();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        txtTitle = new JTextField();
        label2 = new JLabel();
        txtAuthor = new JTextField();
        label3 = new JLabel();
        txtPublisher = new JTextField();
        label4 = new JLabel();
        selectCategory = new ComboBox<>();
        btnClearCategory = new JButton();
        label5 = new JLabel();
        txtYear = new JTextField();
        selectYearComparsion = new JComboBox<>();
        buttonBar = new JPanel();
        clearButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Filter Book");
        setModal(true);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                //---- label1 ----
                label1.setText("Title");
                contentPanel.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(txtTitle, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label2 ----
                label2.setText("Author");
                contentPanel.add(label2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(txtAuthor, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label3 ----
                label3.setText("Publisher");
                contentPanel.add(label3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));
                contentPanel.add(txtPublisher, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label4 ----
                label4.setText("Category");
                contentPanel.add(label4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- selectCategory ----
                selectCategory.setSelectedIndex(-1);
                contentPanel.add(selectCategory, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 15), 0, 0));

                //---- btnClearCategory ----
                btnClearCategory.setText("Clear");
                btnClearCategory.addActionListener(e -> btnClearCategoryActionPerformed(e));
                contentPanel.add(btnClearCategory, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 20, 0), 0, 0));

                //---- label5 ----
                label5.setText("Year");
                contentPanel.add(label5, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 15), 0, 0));
                contentPanel.add(txtYear, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 15), 0, 0));

                //---- selectYearComparsion ----
                selectYearComparsion.setSelectedIndex(-1);
                contentPanel.add(selectYearComparsion, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                //---- clearButton ----
                clearButton.setText("Clear");
                clearButton.addActionListener(e -> clearButtonActionPerformed(e));
                buttonBar.add(clearButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> okButtonActionPerformed(e));
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(e -> cancelButtonActionPerformed(e));
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
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
    private JLabel label1;
    private JTextField txtTitle;
    private JLabel label2;
    private JTextField txtAuthor;
    private JLabel label3;
    private JTextField txtPublisher;
    private JLabel label4;
    private ComboBox<Category> selectCategory;
    private JButton btnClearCategory;
    private JLabel label5;
    private JTextField txtYear;
    private JComboBox<RowFilter.ComparisonType> selectYearComparsion;
    private JPanel buttonBar;
    private JButton clearButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
