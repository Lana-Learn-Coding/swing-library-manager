/*
 * Created by JFormDesigner on Mon Dec 21 15:21:57 ICT 2020
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Category;
import io.lana.library.core.model.book.Series;
import io.lana.library.core.repo.BookMetaRepo;
import io.lana.library.core.repo.CategoryRepo;
import io.lana.library.core.repo.SeriesRepo;
import io.lana.library.ui.UIException;
import io.lana.library.ui.component.app.AppPanel;
import io.lana.library.ui.component.app.IdCombobox;
import io.lana.library.ui.component.book.BookMetaTablePane;
import io.lana.library.ui.view.CrudPanel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.util.Collection;

@Component
public class BookMetaManagerPanel extends AppPanel implements CrudPanel<BookMeta> {

    private BookManagerDialog bookManagerDialog;
    private SeriesRepo seriesRepo;
    private CategoryRepo categoryRepo;
    private BookMetaRepo bookMetaRepo;

    @Autowired
    public void setup(SeriesRepo seriesRepo, CategoryRepo categoryRepo, BookMetaRepo bookMetaRepo,
                      BookManagerDialog bookManagerDialog) {
        this.bookManagerDialog = bookManagerDialog;
        this.seriesRepo = seriesRepo;
        this.bookMetaRepo = bookMetaRepo;
        this.categoryRepo = categoryRepo;
        this.categoryRepo.findAll().forEach(selectCategory::addItem);
        this.seriesRepo.findAll().forEach(selectSeries::addItem);
        selectSeries.setSelectedItem(null);
        selectCategory.setSelectedItem(null);
        renderTable();
    }

    @Autowired
    public BookMetaManagerPanel() {
        initComponents();
        ListSelectionModel selectionModel = bookMetaTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = bookMetaTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                return;
            }
            BookMeta bookMeta = bookMetaTablePane.getRow(pos);
            loadModelToForm(bookMeta);
        });
    }

    @Override
    public void delete() {
        BookMeta bookMeta = bookMetaTablePane.getSelectedRow();
        if (bookMeta == null) {
            JOptionPane.showMessageDialog(this, "Please select some thing");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure delete this?");
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }
        bookMetaRepo.deleteById(bookMeta.getId());
        bookMetaTablePane.removeSelectedRow();
        bookMetaTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        BookMeta bookMeta = getModelFromForm();
        if (bookMeta.getId() == null) {
            bookMetaRepo.save(bookMeta);
            JOptionPane.showMessageDialog(this, "Create Success");
            bookMetaTablePane.addRow(0, bookMeta);
            bookMetaTablePane.clearSelection();
            return;
        }

        JOptionPane.showMessageDialog(this, "Update Success");
        BookMeta updated = bookMetaTablePane.getSelectedRow();
        updated.setTitle(bookMeta.getTitle());
        updated.setYear(bookMeta.getYear());
        updated.setPublisher(bookMeta.getPublisher());
        updated.setAuthor(bookMeta.getAuthor());
        updated.setCategory(bookMeta.getCategory());
        updated.setSeries(bookMeta.getSeries());
        bookMetaRepo.save(updated);
        loadModelToForm(updated);
    }

    @Override
    public void clearForm() {
        txtAuthor.setText("");
        txtPublisher.setText("");
        txtTitle.setText("");
        txtID.setText("");
        txtYear.setText("");
        selectCategory.setSelectedItem(null);
        selectSeries.setSelectedItem(null);
        bookMetaTablePane.clearSelection();
    }

    @Override
    public void loadModelToForm(BookMeta model) {
        txtAuthor.setText(model.getAuthorName());
        txtPublisher.setText(model.getPublisherName());
        txtTitle.setText(model.getTitle());
        txtID.setText(model.getId().toString());
        txtYear.setText(model.getYear().toString());
        selectSeries.setSelectedItem(model.getSeries());
        selectCategory.setSelectedItem(model.getCategory());
    }

    @Override
    public BookMeta getModelFromForm() {
        BookMeta bookMeta = new BookMeta();
        bookMeta.setAuthor(txtAuthor.getText());
        bookMeta.setPublisher(txtPublisher.getText());
        bookMeta.setTitle(txtTitle.getText());
        bookMeta.setCategory(selectCategory.getSelectedItem());
        bookMeta.setSeries(selectSeries.getSelectedItem());
        if (StringUtils.isAnyBlank(bookMeta.getAuthor(), bookMeta.getTitle(), bookMeta.getPublisher())) {
            throw new UIException(this, "Please enter all information");
        }
        try {
            bookMeta.setYear(Integer.parseInt(txtYear.getText()));
        } catch (Exception e) {
            throw new UIException(this, "Invalid year");
        }
        return bookMeta;
    }

    @Override
    public void renderTable(Collection<BookMeta> books) {
        bookMetaTablePane.setTableData(books);
    }

    @Override
    public void renderTable() {
        renderTable(bookMetaRepo.findAllByOrderByUpdatedAtDesc());
    }

    private void btnNewSeriesActionPerformed(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Enter series name");
        if (name == null) {
            return;

        }
        if (StringUtils.isBlank(name)) {
            JOptionPane.showMessageDialog(this, "Name must not blank");
            return;
        }
        Series series = new Series();
        series.setName(name);
        seriesRepo.save(series);
        selectSeries.addItem(series);
        JOptionPane.showMessageDialog(this, "Series created successfully");
    }

    private void btnNewCategoryActionPerformed(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Enter category name");
        if (name == null) {
            return;

        }
        if (StringUtils.isBlank(name)) {
            JOptionPane.showMessageDialog(this, "Name must not blank");
            return;
        }
        Category category = new Category();
        category.setName(name);
        categoryRepo.save(category);
        selectCategory.addItem(category);
        JOptionPane.showMessageDialog(this, "Category created successfully");
    }

    private void btnDeleteActionPerformed(ActionEvent e) {
        delete();
    }

    private void btnSaveActionPerformed(ActionEvent e) {
        save();
    }

    private void btnClearActionPerformed(ActionEvent e) {
        clearForm();
    }

    private void btnViewBooksActionPerformed(ActionEvent e) {
        BookMeta bookMeta = bookMetaTablePane.getSelectedRow();
        if (bookMeta == null) {
            JOptionPane.showMessageDialog(this, "Please select some thing");
        }
        bookManagerDialog.setModel(bookMeta);
        bookManagerDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        bookMetaTablePane = new BookMetaTablePane();
        mainTabbedPane = new JTabbedPane();
        bookMetaManagerTab = new JPanel();
        formPanel = new JPanel();
        lblSeries = new JLabel();
        lblID = new JLabel();
        lblTitle = new JLabel();
        lblCategory = new JLabel();
        txtTitle = new JTextField();
        txtID = new JTextField();
        selectSeries = new IdCombobox<>();
        txtAuthor = new JTextField();
        lblPublisher = new JLabel();
        txtPublisher = new JTextField();
        lblAuthor = new JLabel();
        txtYear = new JTextField();
        selectCategory = new IdCombobox<>();
        lblYear = new JLabel();
        btnNewSeries = new JButton();
        btnNewCategory = new JButton();
        btnImage = new JButton();
        panelImage = new JPanel();
        panel1 = new JPanel();
        btnClear = new JButton();
        btnViewBooks = new JButton();
        button4 = new JButton();
        btnSave = new JButton();
        btnDelete = new JButton();

        //======== this ========
        setBorder(null);

        //======== mainTabbedPane ========
        {
            mainTabbedPane.setBorder(null);

            //======== bookMetaManagerTab ========
            {
                bookMetaManagerTab.setBorder(new EtchedBorder());

                //======== formPanel ========
                {
                    formPanel.setBorder(new TitledBorder("Book Info"));

                    //---- lblSeries ----
                    lblSeries.setText("Series");

                    //---- lblID ----
                    lblID.setText("ID");

                    //---- lblTitle ----
                    lblTitle.setText("Title");

                    //---- lblCategory ----
                    lblCategory.setText("Category");

                    //---- txtID ----
                    txtID.setEditable(false);

                    //---- lblPublisher ----
                    lblPublisher.setText("Publisher");

                    //---- lblAuthor ----
                    lblAuthor.setText("Author");

                    //---- lblYear ----
                    lblYear.setText("Year");

                    //---- btnNewSeries ----
                    btnNewSeries.setText("New");
                    btnNewSeries.addActionListener(e -> btnNewSeriesActionPerformed(e));

                    //---- btnNewCategory ----
                    btnNewCategory.setText("New");
                    btnNewCategory.addActionListener(e -> btnNewCategoryActionPerformed(e));

                    //---- btnImage ----
                    btnImage.setText("Select Image");

                    //======== panelImage ========
                    {
                        panelImage.setBorder(new TitledBorder("Image"));

                        GroupLayout panelImageLayout = new GroupLayout(panelImage);
                        panelImage.setLayout(panelImageLayout);
                        panelImageLayout.setHorizontalGroup(
                            panelImageLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                        panelImageLayout.setVerticalGroup(
                            panelImageLayout.createParallelGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                        );
                    }

                    GroupLayout formPanelLayout = new GroupLayout(formPanel);
                    formPanel.setLayout(formPanelLayout);
                    formPanelLayout.setHorizontalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                    .addGroup(GroupLayout.Alignment.LEADING, formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblSeries)
                                            .addGroup(formPanelLayout.createSequentialGroup()
                                                .addGroup(formPanelLayout.createParallelGroup()
                                                    .addComponent(lblTitle)
                                                    .addComponent(lblID))
                                                .addGap(48, 48, 48)
                                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(txtTitle, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 390, GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(formPanelLayout.createSequentialGroup()
                                                        .addComponent(txtID, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                                        .addGap(59, 59, 59)
                                                        .addComponent(lblYear)
                                                        .addGap(29, 29, 29)
                                                        .addComponent(txtYear))
                                                    .addGroup(GroupLayout.Alignment.TRAILING, formPanelLayout.createSequentialGroup()
                                                        .addGroup(formPanelLayout.createParallelGroup()
                                                            .addComponent(selectSeries, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(selectCategory, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE))
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                            .addComponent(btnNewCategory, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(btnNewSeries, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
                                                        .addGap(2, 2, 2)))))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 158, Short.MAX_VALUE))
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblPublisher)
                                            .addComponent(lblAuthor)
                                            .addComponent(lblCategory))
                                        .addGap(28, 28, 28)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtPublisher, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                            .addComponent(txtAuthor))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnImage, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                            .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(20, 20, 20))
                    );
                    formPanelLayout.setVerticalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblYear)
                                            .addComponent(txtID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblID)
                                            .addComponent(txtYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblTitle))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(selectSeries, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblSeries)
                                            .addComponent(btnNewSeries))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblCategory)
                                            .addComponent(selectCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnNewCategory))
                                        .addGap(19, 19, 19)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblPublisher)
                                            .addComponent(txtPublisher, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblAuthor, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAuthor, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnImage))
                                .addContainerGap(26, Short.MAX_VALUE))
                    );
                }

                //======== panel1 ========
                {

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnViewBooks ----
                    btnViewBooks.setText("View Books");
                    btnViewBooks.addActionListener(e -> {
                        btnViewBooksActionPerformed(e);
                        btnViewBooksActionPerformed(e);
                    });

                    //---- button4 ----
                    button4.setText("text");

                    //---- btnSave ----
                    btnSave.setText("Save");
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                            .addComponent(button4, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnViewBooks, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap(20, Short.MAX_VALUE)
                                .addComponent(button4)
                                .addGap(67, 67, 67)
                                .addComponent(btnViewBooks)
                                .addGap(18, 18, 18)
                                .addComponent(btnSave)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear)
                                .addGap(18, 18, 18)
                                .addComponent(btnDelete))
                    );
                }

                GroupLayout bookMetaManagerTabLayout = new GroupLayout(bookMetaManagerTab);
                bookMetaManagerTab.setLayout(bookMetaManagerTabLayout);
                bookMetaManagerTabLayout.setHorizontalGroup(
                    bookMetaManagerTabLayout.createParallelGroup()
                        .addGroup(bookMetaManagerTabLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(21, Short.MAX_VALUE))
                );
                bookMetaManagerTabLayout.setVerticalGroup(
                    bookMetaManagerTabLayout.createParallelGroup()
                        .addGroup(bookMetaManagerTabLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(bookMetaManagerTabLayout.createParallelGroup()
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap(32, Short.MAX_VALUE))
                );
            }
            mainTabbedPane.addTab("Book Manager", bookMetaManagerTab);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 805, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 5, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 335, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(bookMetaTablePane, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(17, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private BookMetaTablePane bookMetaTablePane;
    private JTabbedPane mainTabbedPane;
    private JPanel bookMetaManagerTab;
    private JPanel formPanel;
    private JLabel lblSeries;
    private JLabel lblID;
    private JLabel lblTitle;
    private JLabel lblCategory;
    private JTextField txtTitle;
    private JTextField txtID;
    private IdCombobox<Series> selectSeries;
    private JTextField txtAuthor;
    private JLabel lblPublisher;
    private JTextField txtPublisher;
    private JLabel lblAuthor;
    private JTextField txtYear;
    private IdCombobox<Category> selectCategory;
    private JLabel lblYear;
    private JButton btnNewSeries;
    private JButton btnNewCategory;
    private JButton btnImage;
    private JPanel panelImage;
    private JPanel panel1;
    private JButton btnClear;
    private JButton btnViewBooks;
    private JButton button4;
    private JButton btnSave;
    private JButton btnDelete;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
