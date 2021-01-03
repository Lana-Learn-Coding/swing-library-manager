/*
 * Created by JFormDesigner on Mon Dec 21 15:21:57 ICT 2020
 */

package io.lana.library.ui.view;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Category;
import io.lana.library.core.model.book.Series;
import io.lana.library.core.spi.BookMetaRepo;
import io.lana.library.core.spi.CategoryRepo;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.SeriesRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.BookMetaTablePane;
import io.lana.library.ui.component.app.AppPanel;
import io.lana.library.ui.component.app.ComboBox;
import io.lana.library.ui.component.app.ImagePicker;
import io.lana.library.ui.component.app.ImageViewer;
import io.lana.library.ui.view.app.CrudPanel;
import io.lana.library.utils.WorkerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Component
public class BookMetaManagerPanel extends AppPanel implements CrudPanel<BookMeta> {
    private final ImagePicker imagePicker = new ImagePicker();

    private BookManagerDialog bookManagerDialog;
    private SeriesRepo seriesRepo;
    private CategoryRepo categoryRepo;
    private BookMetaRepo bookMetaRepo;
    private FileStorage fileStorage;

    @Autowired
    public void setup(SeriesRepo seriesRepo, CategoryRepo categoryRepo, BookMetaRepo bookMetaRepo,
                      BookManagerDialog bookManagerDialog, FileStorage fileStorage) {
        this.bookManagerDialog = bookManagerDialog;
        this.seriesRepo = seriesRepo;
        this.bookMetaRepo = bookMetaRepo;
        this.categoryRepo = categoryRepo;
        this.fileStorage = fileStorage;
        WorkerUtils.runAsync(() -> {
            this.categoryRepo.findAll().forEach(selectCategory::addItem);
            this.seriesRepo.findAll().forEach(selectSeries::addItem);
            selectSeries.setSelectedItemModel(null);
            selectCategory.setSelectedItemModel(null);
            renderTable();
        });
    }

    @Autowired
    public BookMetaManagerPanel() {
        initComponents();
        ListSelectionModel selectionModel = bookMetaTablePane.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int pos = bookMetaTablePane.getSelectedRowIndex();
            if (pos < 0) {
                clearForm();
                btnDelete.setEnabled(false);
                btnClone.setEnabled(false);
                btnViewBooks.setEnabled(false);
                return;
            }
            BookMeta bookMeta = bookMetaTablePane.getRow(pos);
            loadModelToForm(bookMeta);
            btnDelete.setEnabled(true);
            btnClone.setEnabled(true);
            btnViewBooks.setEnabled(true);
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
        if (!bookMeta.getBooks().isEmpty()) {
            int confirmDeleteBooks = JOptionPane.showConfirmDialog(this,
                "There are " + bookMeta.getBooks().size() + " books, Are you sure?");
            if (confirmDeleteBooks != JOptionPane.OK_OPTION) {
                return;
            }
        }
        bookMetaRepo.deleteById(bookMeta.getId());
        fileStorage.deleteFileFromStorage(bookMeta.getImage());
        bookMetaTablePane.removeSelectedRow();
        bookMetaTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Delete success!");
    }

    @Override
    public void save() {
        if (!bookMetaTablePane.isAnyRowSelected()) {
            BookMeta created = createFromForm();
            JOptionPane.showMessageDialog(this, "Create Success");
            bookMetaTablePane.addRow(0, created);
            bookMetaTablePane.clearSearch();
            bookMetaTablePane.setSelectedRow(0);
            return;
        }
        BookMeta updated = updateFromForm();
        JOptionPane.showMessageDialog(this, "Update Success");
        loadModelToForm(updated);
    }

    private BookMeta createFromForm() {
        BookMeta bookMeta = getModelFromForm();
        if (StringUtils.isNotBlank(bookMeta.getImage())) {
            String savedImage = fileStorage.loadFileToStorage(bookMeta.getImage());
            bookMeta.setImage(savedImage);
        }
        bookMetaRepo.save(bookMeta);
        return bookMeta;
    }

    private BookMeta updateFromForm() {
        BookMeta bookMeta = getModelFromForm();
        BookMeta updated = bookMetaTablePane.getSelectedRow();
        updated.setTitle(bookMeta.getTitle());
        updated.setYear(bookMeta.getYear());
        updated.setPublisher(bookMeta.getPublisher());
        updated.setAuthor(bookMeta.getAuthor());
        updated.setCategory(bookMeta.getCategory());
        updated.setSeries(bookMeta.getSeries());
        if (StringUtils.isNotBlank(bookMeta.getImage())) {
            fileStorage.deleteFileFromStorage(updated.getImage());
            String savedImage = fileStorage.loadFileToStorage(bookMeta.getImage());
            updated.setImage(savedImage);
        }
        bookMetaRepo.save(updated);
        return updated;
    }

    @Override
    public void clearForm() {
        txtAuthor.setText("");
        txtPublisher.setText("");
        txtTitle.setText("");
        txtID.setText("");
        txtYear.setText("");
        selectCategory.setSelectedItemModel(null);
        selectSeries.setSelectedItemModel(null);
        bookMetaTablePane.clearSelection();
        imageViewer.clearImage();
    }

    @Override
    public void loadModelToForm(BookMeta model) {
        imageViewer.clearImage();
        txtID.setText(model.getIdString());
        txtAuthor.setText(model.getAuthorName());
        txtPublisher.setText(model.getPublisherName());
        txtTitle.setText(model.getTitle());
        txtYear.setText(model.getYear().toString());
        selectSeries.setSelectedItemModel(model.getSeries());
        selectCategory.setSelectedItemModel(model.getCategory());
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
    public BookMeta getModelFromForm() {
        BookMeta bookMeta = new BookMeta();
        bookMeta.setAuthor(txtAuthor.getText());
        bookMeta.setPublisher(txtPublisher.getText());
        bookMeta.setTitle(txtTitle.getText());
        bookMeta.setCategory(selectCategory.getSelectedItemModel());
        bookMeta.setSeries(selectSeries.getSelectedItemModel());
        bookMeta.setImage(imageViewer.getImagePath());
        if (StringUtils.isAnyBlank(bookMeta.getAuthor(), bookMeta.getTitle(), bookMeta.getPublisher())) {
            throw new InputException(this, "Please enter all information: author, title, publisher");
        }
        try {
            bookMeta.setYear(Integer.parseInt(txtYear.getText()));
        } catch (Exception e) {
            throw new InputException(this, "Invalid year");
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
        if (!bookMetaTablePane.isAnyRowSelected()) {
            JOptionPane.showMessageDialog(this, "Please select some thing");
            return;
        }
        BookMeta bookMeta = bookMetaTablePane.getSelectedRow();
        bookManagerDialog.setModel(bookMeta);
        bookManagerDialog.setVisible(true);
    }

    private void btnCloneActionPerformed(ActionEvent e) {
        BookMeta bookMeta = getModelFromForm();
        bookMeta.setImage(null);
        bookMetaTablePane.clearSelection();
        JOptionPane.showMessageDialog(this, "Cloned book. edit then press save to save new book");
        loadModelToForm(bookMeta);
    }

    private void btnImageActionPerformed(ActionEvent e) {
        imagePicker.showSelectImageDialog().ifPresent(selectedFile -> {
            String path = selectedFile.getAbsolutePath();
            imageViewer.loadImage(path);
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        mainTabbedPane = new JTabbedPane();
        bookMetaManagerTab = new JPanel();
        formPanel = new JPanel();
        lblSeries = new JLabel();
        lblID = new JLabel();
        lblTitle = new JLabel();
        lblCategory = new JLabel();
        txtTitle = new JTextField();
        txtID = new JTextField();
        selectSeries = new ComboBox<>();
        txtAuthor = new JTextField();
        lblPublisher = new JLabel();
        txtPublisher = new JTextField();
        lblAuthor = new JLabel();
        txtYear = new JTextField();
        selectCategory = new ComboBox<>();
        lblYear = new JLabel();
        btnNewSeries = new JButton();
        btnNewCategory = new JButton();
        btnImage = new JButton();
        panelImage = new JPanel();
        imageViewer = new ImageViewer();
        panel1 = new JPanel();
        btnClear = new JButton();
        btnViewBooks = new JButton();
        btnSave = new JButton();
        btnDelete = new JButton();
        btnClone = new JButton();
        panelSearch = new JPanel();
        panel2 = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        label4 = new JLabel();
        bookMetaTablePane = new BookMetaTablePane();

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

                    //---- selectSeries ----
                    selectSeries.setSelectedIndex(-1);

                    //---- lblPublisher ----
                    lblPublisher.setText("Publisher");

                    //---- lblAuthor ----
                    lblAuthor.setText("Author");

                    //---- selectCategory ----
                    selectCategory.setSelectedIndex(-1);

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
                    btnImage.addActionListener(e -> btnImageActionPerformed(e));

                    //======== panelImage ========
                    {
                        panelImage.setBorder(new TitledBorder("Image"));
                        panelImage.setLayout(new GridLayout());
                        panelImage.add(imageViewer);
                    }

                    GroupLayout formPanelLayout = new GroupLayout(formPanel);
                    formPanel.setLayout(formPanelLayout);
                    formPanelLayout.setHorizontalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(GroupLayout.Alignment.TRAILING, formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup()
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblPublisher)
                                            .addComponent(lblAuthor)
                                            .addComponent(lblCategory))
                                        .addGap(23, 23, 23)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtPublisher, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)
                                            .addComponent(txtAuthor, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)))
                                    .addComponent(lblSeries)
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addComponent(lblTitle)
                                            .addComponent(lblID))
                                        .addGap(48, 48, 48)
                                        .addGroup(formPanelLayout.createParallelGroup()
                                            .addGroup(GroupLayout.Alignment.TRAILING, formPanelLayout.createSequentialGroup()
                                                .addComponent(txtID, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
                                                .addGap(59, 59, 59)
                                                .addComponent(lblYear)
                                                .addGap(29, 29, 29)
                                                .addComponent(txtYear, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
                                            .addGroup(GroupLayout.Alignment.TRAILING, formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                                .addGroup(formPanelLayout.createSequentialGroup()
                                                    .addGroup(formPanelLayout.createParallelGroup()
                                                        .addComponent(selectSeries, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(selectCategory, GroupLayout.PREFERRED_SIZE, 307, GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                        .addComponent(btnNewCategory, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnNewSeries, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(formPanelLayout.createSequentialGroup()
                                                    .addGap(0, 0, Short.MAX_VALUE)
                                                    .addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, 388, GroupLayout.PREFERRED_SIZE))))))
                                .addGap(18, 18, 18)
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                    .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                    .addComponent(btnImage, GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                                .addContainerGap(22, Short.MAX_VALUE))
                    );
                    formPanelLayout.setVerticalGroup(
                        formPanelLayout.createParallelGroup()
                            .addGroup(formPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(formPanelLayout.createParallelGroup()
                                    .addGroup(formPanelLayout.createSequentialGroup()
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblYear)
                                            .addComponent(txtID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblID)
                                            .addComponent(txtYear, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                            .addComponent(lblTitle)
                                            .addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
                                        .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblPublisher)
                                            .addComponent(txtPublisher, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(panelImage, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(formPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblAuthor, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAuthor, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnImage))
                                .addContainerGap(42, Short.MAX_VALUE))
                    );
                }

                //======== panel1 ========
                {

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnViewBooks ----
                    btnViewBooks.setText("View Books");
                    btnViewBooks.addActionListener(e -> btnViewBooksActionPerformed(e));

                    //---- btnSave ----
                    btnSave.setText("Save");
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    //---- btnClone ----
                    btnClone.setText("Clone");
                    btnClone.addActionListener(e -> btnCloneActionPerformed(e));

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnViewBooks, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap(51, Short.MAX_VALUE)
                                .addComponent(btnClone)
                                .addGap(18, 18, 18)
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
                            .addGap(10, 10, 10)
                            .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(18, Short.MAX_VALUE))
                );
                bookMetaManagerTabLayout.setVerticalGroup(
                    bookMetaManagerTabLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, bookMetaManagerTabLayout.createSequentialGroup()
                            .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(bookMetaManagerTabLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(formPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGap(36, 36, 36))
                );
            }
            mainTabbedPane.addTab("Book Manager", bookMetaManagerTab);

            //======== panelSearch ========
            {
                panelSearch.setBorder(new EtchedBorder());

                //======== panel2 ========
                {
                    panel2.setBorder(new EtchedBorder());

                    //---- label1 ----
                    label1.setText("Tilte");

                    //---- label2 ----
                    label2.setText("ID");

                    //---- label3 ----
                    label3.setText("Author");

                    GroupLayout panel2Layout = new GroupLayout(panel2);
                    panel2.setLayout(panel2Layout);
                    panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel2Layout.createParallelGroup()
                                    .addComponent(label4)
                                    .addComponent(label3)
                                    .addComponent(label2)
                                    .addComponent(label1))
                                .addContainerGap(327, Short.MAX_VALUE))
                    );
                    panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                            .addGroup(panel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(label2)
                                .addGap(18, 18, 18)
                                .addComponent(label1)
                                .addGap(18, 18, 18)
                                .addComponent(label3)
                                .addGap(18, 18, 18)
                                .addComponent(label4)
                                .addContainerGap(79, Short.MAX_VALUE))
                    );
                }

                GroupLayout panelSearchLayout = new GroupLayout(panelSearch);
                panelSearch.setLayout(panelSearchLayout);
                panelSearchLayout.setHorizontalGroup(
                    panelSearchLayout.createParallelGroup()
                        .addGroup(panelSearchLayout.createSequentialGroup()
                            .addGap(38, 38, 38)
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(367, Short.MAX_VALUE))
                );
                panelSearchLayout.setVerticalGroup(
                    panelSearchLayout.createParallelGroup()
                        .addGroup(panelSearchLayout.createSequentialGroup()
                            .addGap(28, 28, 28)
                            .addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap(112, Short.MAX_VALUE))
                );
            }
            mainTabbedPane.addTab("Filter & Search", panelSearch);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                        .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 788, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 2, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JTabbedPane mainTabbedPane;
    private JPanel bookMetaManagerTab;
    private JPanel formPanel;
    private JLabel lblSeries;
    private JLabel lblID;
    private JLabel lblTitle;
    private JLabel lblCategory;
    private JTextField txtTitle;
    private JTextField txtID;
    private ComboBox<Series> selectSeries;
    private JTextField txtAuthor;
    private JLabel lblPublisher;
    private JTextField txtPublisher;
    private JLabel lblAuthor;
    private JTextField txtYear;
    private ComboBox<Category> selectCategory;
    private JLabel lblYear;
    private JButton btnNewSeries;
    private JButton btnNewCategory;
    private JButton btnImage;
    private JPanel panelImage;
    private ImageViewer imageViewer;
    private JPanel panel1;
    private JButton btnClear;
    private JButton btnViewBooks;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnClone;
    private JPanel panelSearch;
    private JPanel panel2;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private BookMetaTablePane bookMetaTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
