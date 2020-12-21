/*
 * Created by JFormDesigner on Mon Dec 21 19:11:40 ICT 2020
 */

package io.lana.library.ui.component.book;

import java.awt.event.*;

import io.lana.library.core.model.book.Category;
import io.lana.library.core.model.book.Series;
import io.lana.library.core.repo.CategoryRepo;
import io.lana.library.core.repo.SeriesRepo;
import io.lana.library.ui.component.app.NamedEntityCreationDialog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;

@Component
public class BookMetaFormPanel extends JPanel {
    private CategoryRepo categoryRepo;
    private SeriesRepo seriesRepo;

    public BookMetaFormPanel() {
        initComponents();
    }

    @Autowired
    public void setup(CategoryRepo categoryRepo, SeriesRepo seriesRepo) {
        this.categoryRepo = categoryRepo;
        this.seriesRepo = seriesRepo;
        categoryRepo.findAll().forEach(selectCategory::addItem);
        seriesRepo.findAll().forEach(selectSeries::addItem);
    }

    private void btnNewCategoryActionPerformed(ActionEvent e) {
        NamedEntityCreationDialog dialog = new NamedEntityCreationDialog(null);
        dialog.setTitle("Category");
        dialog.onCreate((name) -> {
            name = StringUtils.capitalize(StringUtils.trim(name));
            if (categoryRepo.existsByName(name)) {
                JOptionPane.showMessageDialog(dialog, "Name already exist");
                return;
            }
            Category category = new Category();
            category.setName(name);
            categoryRepo.save(category);
            selectCategory.addItem(category);
            dialog.creationSucceed();
        });
        dialog.setVisible(true);
    }

    private void btnNewSeriesActionPerformed(ActionEvent e) {
        NamedEntityCreationDialog dialog = new NamedEntityCreationDialog(null);
        dialog.setTitle("Series");
        dialog.onCreate((name) -> {
            name = StringUtils.capitalize(StringUtils.trim(name));
            if (seriesRepo.existsByName(name)) {
                JOptionPane.showMessageDialog(dialog, "Name already exist");
                return;
            }
            Series series = new Series();
            series.setName(name);
            seriesRepo.save(series);
            selectSeries.addItem(series);
            dialog.creationSucceed();
        });
        dialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        lblID = new JLabel();
        lblTitle = new JLabel();
        lblSeries = new JLabel();
        lblCategory = new JLabel();
        txtID = new JTextField();
        txtTitle = new JTextField();
        selectSeries = new JComboBox<>();
        textField4 = new JTextField();
        lblPublisher = new JLabel();
        txtPublisher = new JTextField();
        lblAuthor = new JLabel();
        txtYear = new JTextField();
        selectCategory = new JComboBox<>();
        lblYear = new JLabel();
        imageViewPanel = new JPanel();
        btnPickImage = new JButton();
        btnNewSeries = new JButton();
        btnNewCategory = new JButton();

        //======== this ========
        setBorder(null);

        //---- lblID ----
        lblID.setText("ID");

        //---- lblTitle ----
        lblTitle.setText("Title");

        //---- lblSeries ----
        lblSeries.setText("Series");

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

        //======== imageViewPanel ========
        {
            imageViewPanel.setBorder(new TitledBorder("Image"));

            GroupLayout imageViewPanelLayout = new GroupLayout(imageViewPanel);
            imageViewPanel.setLayout(imageViewPanelLayout);
            imageViewPanelLayout.setHorizontalGroup(
                imageViewPanelLayout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
            imageViewPanelLayout.setVerticalGroup(
                imageViewPanelLayout.createParallelGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
            );
        }

        //---- btnPickImage ----
        btnPickImage.setText("Select Image");

        //---- btnNewSeries ----
        btnNewSeries.setText("New");
        btnNewSeries.addActionListener(e -> btnNewSeriesActionPerformed(e));

        //---- btnNewCategory ----
        btnNewCategory.setText("New");
        btnNewCategory.addActionListener(e -> btnNewCategoryActionPerformed(e));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(lblCategory)
                                .addGroup(GroupLayout.Alignment.TRAILING, layout.createParallelGroup()
                                    .addComponent(lblAuthor)
                                    .addComponent(lblPublisher)))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup()
                                        .addComponent(selectSeries, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                                        .addComponent(selectCategory, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnNewSeries, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                        .addComponent(btnNewCategory, GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))
                                .addComponent(txtPublisher, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                                .addComponent(textField4, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)))
                        .addComponent(lblSeries)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup()
                                .addComponent(lblTitle)
                                .addComponent(lblID, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                            .addGap(2, 2, 2)
                            .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(txtID, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
                                    .addGap(70, 70, 70)
                                    .addComponent(lblYear)
                                    .addGap(18, 18, 18)
                                    .addComponent(txtYear, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE))
                                .addComponent(txtTitle, GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup()
                        .addComponent(btnPickImage, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                        .addComponent(imageViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGap(0, 1, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblID)
                                .addComponent(lblYear)
                                .addComponent(txtID)
                                .addComponent(txtYear))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblTitle)
                                .addComponent(txtTitle, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblSeries)
                                .addComponent(btnNewSeries)
                                .addComponent(selectSeries, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lblCategory)
                                .addComponent(btnNewCategory)
                                .addComponent(selectCategory, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(txtPublisher, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblPublisher))
                            .addGap(9, 9, 9))
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(imageViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(textField4, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblAuthor)
                        .addComponent(btnPickImage, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
                    .addGap(98, 98, 98))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel lblID;
    private JLabel lblTitle;
    private JLabel lblSeries;
    private JLabel lblCategory;
    private JTextField txtID;
    private JTextField txtTitle;
    private JComboBox<Series> selectSeries;
    private JTextField textField4;
    private JLabel lblPublisher;
    private JTextField txtPublisher;
    private JLabel lblAuthor;
    private JTextField txtYear;
    private JComboBox<Category> selectCategory;
    private JLabel lblYear;
    private JPanel imageViewPanel;
    private JButton btnPickImage;
    private JButton btnNewSeries;
    private JButton btnNewCategory;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
