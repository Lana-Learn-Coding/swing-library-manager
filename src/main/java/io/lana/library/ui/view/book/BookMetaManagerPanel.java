/*
 * Created by JFormDesigner on Mon Dec 21 15:21:57 ICT 2020
 */

package io.lana.library.ui.view.book;

import io.lana.library.core.model.book.BookMeta;
import io.lana.library.core.model.book.Category;
import io.lana.library.core.model.book.Series;
import io.lana.library.core.spi.BookMetaRepo;
import io.lana.library.core.spi.CategoryRepo;
import io.lana.library.core.spi.FileStorage;
import io.lana.library.core.spi.SeriesRepo;
import io.lana.library.ui.InputException;
import io.lana.library.ui.component.BookMetaTablePane;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Component
public class BookMetaManagerPanel extends JPanel implements CrudPanel<BookMeta> {
    private final ImagePicker imagePicker = new ImagePicker();
    private BookMetaFilterDialog filterDialog;

    private BookManagerDialog bookManagerDialog;
    private SeriesRepo seriesRepo;
    private CategoryRepo categoryRepo;
    private BookMetaRepo bookMetaRepo;
    private FileStorage fileStorage;

    @Autowired
    public void setup(SeriesRepo seriesRepo, CategoryRepo categoryRepo, BookMetaRepo bookMetaRepo,
                      BookManagerDialog bookManagerDialog, FileStorage fileStorage) {
        this.bookManagerDialog = bookManagerDialog;
        this.bookManagerDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                bookMetaTablePane.refreshSelectedRow();
            }
        });
        this.seriesRepo = seriesRepo;
        this.bookMetaRepo = bookMetaRepo;
        this.categoryRepo = categoryRepo;
        this.fileStorage = fileStorage;
        WorkerUtils.runAsync(() -> {
            this.categoryRepo.findAllByOrderByUpdatedAtDesc().forEach(selectCategory::addItem);
            this.seriesRepo.findAllByOrderByUpdatedAtDesc().forEach(selectSeries::addItem);
            selectSeries.setSelectedItem(null);
            selectCategory.setSelectedItem(null);
            filterDialog = new BookMetaFilterDialog(bookMetaTablePane, selectCategory);
            btnFilter.setEnabled(true);
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
        WorkerUtils.runAsync(() -> {
            bookMetaRepo.deleteById(bookMeta.getId());
            fileStorage.deleteFileFromStorage(bookMeta.getImage());
        });
        bookMetaTablePane.removeSelectedRow();
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
        updateFromForm();
        JOptionPane.showMessageDialog(this, "Update Success");
        bookMetaTablePane.refreshSelectedRow();
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
        WorkerUtils.runAsync(() -> bookMetaRepo.save(updated));
        return updated;
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
        selectSeries.setSelectedItem(model.getSeries());
        selectCategory.setSelectedItem(model.getCategory());
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
        bookMeta.setCategory(selectCategory.getSelectedItem());
        bookMeta.setSeries(selectSeries.getSelectedItem());
        bookMeta.setImage(imageViewer.getImagePath());
        if (StringUtils.isAnyBlank(bookMeta.getAuthor(), bookMeta.getTitle(), bookMeta.getPublisher())) {
            throw new InputException(this, "Please enter all information: author, title, publisher");
        }
        try {
            bookMeta.setYear(Integer.parseInt(txtYear.getText()));
            if (bookMeta.getYear() <= 0) {
                throw new InputException(this, "Year must > 0");
            }
        } catch (Exception e) {
            throw new InputException(this, "Year must a number");
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

    private void btnFilterActionPerformed(ActionEvent e) {
        filterDialog.setVisible(true);
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
        actionPanel = new JPanel();
        btnClear = new JButton();
        btnViewBooks = new JButton();
        btnSave = new JButton();
        btnDelete = new JButton();
        btnClone = new JButton();
        btnFilter = new JButton();
        bookMetaTablePane = new BookMetaTablePane();

        //======== this ========
        setBorder(new EmptyBorder(0, 10, 0, 10));

        //======== mainTabbedPane ========
        {
            mainTabbedPane.setBorder(null);

            //======== bookMetaManagerTab ========
            {
                bookMetaManagerTab.setBorder(new CompoundBorder(
                    new EtchedBorder(),
                    new EmptyBorder(15, 15, 20, 20)));
                bookMetaManagerTab.setLayout(new GridBagLayout());
                ((GridBagLayout) bookMetaManagerTab.getLayout()).columnWidths = new int[]{0, 100};
                ((GridBagLayout) bookMetaManagerTab.getLayout()).columnWeights = new double[]{1.0, 0.0};

                //======== formPanel ========
                {
                    formPanel.setBorder(new CompoundBorder(
                        new TitledBorder("Book Info"),
                        new EmptyBorder(15, 15, 20, 15)));
                    formPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPanel.getLayout()).columnWidths = new int[]{0, 0, 0, 0, 0, 25, 130, 0};
                    ((GridBagLayout) formPanel.getLayout()).rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) formPanel.getLayout()).columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) formPanel.getLayout()).rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

                    //---- lblSeries ----
                    lblSeries.setText("Series");
                    formPanel.add(lblSeries, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblID ----
                    lblID.setText("ID");
                    formPanel.add(lblID, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblTitle ----
                    lblTitle.setText("Title");
                    formPanel.add(lblTitle, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblCategory ----
                    lblCategory.setText("Category");
                    formPanel.add(lblCategory, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtTitle, new GridBagConstraints(1, 1, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- txtID ----
                    txtID.setEditable(false);
                    formPanel.add(txtID, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- selectSeries ----
                    selectSeries.setSelectedIndex(-1);
                    formPanel.add(selectSeries, new GridBagConstraints(1, 2, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtAuthor, new GridBagConstraints(1, 5, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 15), 0, 0));

                    //---- lblPublisher ----
                    lblPublisher.setText("Publisher");
                    formPanel.add(lblPublisher, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));
                    formPanel.add(txtPublisher, new GridBagConstraints(1, 4, 4, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblAuthor ----
                    lblAuthor.setText("Author");
                    formPanel.add(lblAuthor, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 15), 0, 0));
                    formPanel.add(txtYear, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- selectCategory ----
                    selectCategory.setSelectedIndex(-1);
                    formPanel.add(selectCategory, new GridBagConstraints(1, 3, 3, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- lblYear ----
                    lblYear.setText("Year");
                    formPanel.add(lblYear, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- btnNewSeries ----
                    btnNewSeries.setText("New");
                    btnNewSeries.addActionListener(e -> btnNewSeriesActionPerformed(e));
                    formPanel.add(btnNewSeries, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- btnNewCategory ----
                    btnNewCategory.setText("New");
                    btnNewCategory.addActionListener(e -> btnNewCategoryActionPerformed(e));
                    formPanel.add(btnNewCategory, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 15), 0, 0));

                    //---- btnImage ----
                    btnImage.setText("Select Image");
                    btnImage.addActionListener(e -> btnImageActionPerformed(e));
                    formPanel.add(btnImage, new GridBagConstraints(6, 5, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));

                    //======== panelImage ========
                    {
                        panelImage.setBorder(new TitledBorder("Image"));
                        panelImage.setLayout(new GridLayout());
                        panelImage.add(imageViewer);
                    }
                    formPanel.add(panelImage, new GridBagConstraints(6, 0, 1, 5, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 20, 0), 0, 0));
                }
                bookMetaManagerTab.add(formPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 20), 0, 0));

                //======== actionPanel ========
                {

                    //---- btnClear ----
                    btnClear.setText("Clear");
                    btnClear.addActionListener(e -> btnClearActionPerformed(e));

                    //---- btnViewBooks ----
                    btnViewBooks.setText("View Books");
                    btnViewBooks.setEnabled(false);
                    btnViewBooks.addActionListener(e -> btnViewBooksActionPerformed(e));

                    //---- btnSave ----
                    btnSave.setText("Save");
                    btnSave.addActionListener(e -> btnSaveActionPerformed(e));

                    //---- btnDelete ----
                    btnDelete.setText("Delete");
                    btnDelete.setEnabled(false);
                    btnDelete.addActionListener(e -> btnDeleteActionPerformed(e));

                    //---- btnClone ----
                    btnClone.setText("Clone");
                    btnClone.setEnabled(false);
                    btnClone.addActionListener(e -> btnCloneActionPerformed(e));

                    //---- btnFilter ----
                    btnFilter.setText("Filter Book");
                    btnFilter.setEnabled(false);
                    btnFilter.addActionListener(e -> btnFilterActionPerformed(e));

                    GroupLayout actionPanelLayout = new GroupLayout(actionPanel);
                    actionPanel.setLayout(actionPanelLayout);
                    actionPanelLayout.setHorizontalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addComponent(btnClone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnViewBooks, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClear, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFilter, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    );
                    actionPanelLayout.setVerticalGroup(
                        actionPanelLayout.createParallelGroup()
                            .addGroup(actionPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnFilter)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
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
                bookMetaManagerTab.add(actionPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            mainTabbedPane.addTab("Book Manage", bookMetaManagerTab);
        }

        //---- bookMetaTablePane ----
        bookMetaTablePane.setBorder(null);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(mainTabbedPane, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainTabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(20, 20, 20)
                    .addComponent(bookMetaTablePane, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
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
    private JPanel actionPanel;
    private JButton btnClear;
    private JButton btnViewBooks;
    private JButton btnSave;
    private JButton btnDelete;
    private JButton btnClone;
    private JButton btnFilter;
    private BookMetaTablePane bookMetaTablePane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
