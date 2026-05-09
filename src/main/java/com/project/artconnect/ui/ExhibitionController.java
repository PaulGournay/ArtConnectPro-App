package com.project.artconnect.ui;

import com.project.artconnect.model.Exhibition;
import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.ExhibitionService;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.time.LocalDate;
import java.util.Optional;

public class ExhibitionController {
    @FXML
    private TableView<Exhibition> exhibitionTable;
    @FXML
    private TableColumn<Exhibition, String> titleColumn;
    @FXML
    private TableColumn<Exhibition, LocalDate> dateColumn;
    @FXML
    private TableColumn<Exhibition, String> themeColumn;
    @FXML
    private TableColumn<Exhibition, String> galleryColumn;

    private final ExhibitionService exhibitionService = ServiceProvider.getExhibitionService();
    private final GalleryService galleryService = ServiceProvider.getGalleryService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        galleryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getGallery() != null ? cellData.getValue().getGallery().getName() : "Unknown"));

        refreshData();
    }

    @FXML
    private void handleAdd() {
        Dialog<Exhibition> dialog = createExhibitionDialog("Add New Exhibition", null);
        Optional<Exhibition> result = dialog.showAndWait();
        result.ifPresent(exhibition -> {
            try {
                exhibitionService.createExhibition(exhibition);
                refreshData();
            } catch (Exception e) {
                showError("Error adding exhibition", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModify() {
        Exhibition selected = exhibitionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an exhibition to modify.");
            return;
        }

        Dialog<Exhibition> dialog = createExhibitionDialog("Modify Exhibition", selected);
        Optional<Exhibition> result = dialog.showAndWait();
        result.ifPresent(exhibition -> {
            try {
                exhibitionService.updateExhibition(exhibition);
                refreshData();
            } catch (Exception e) {
                showError("Error modifying exhibition", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Exhibition selected = exhibitionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an exhibition to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Exhibition: " + selected.getTitle());
        confirm.setContentText("Are you sure you want to delete this exhibition? This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                exhibitionService.deleteExhibition(selected.getTitle());
                refreshData();
            } catch (Exception e) {
                showError("Error deleting exhibition", e.getMessage());
            }
        }
    }

    private Dialog<Exhibition> createExhibitionDialog(String title, Exhibition existing) {
        Dialog<Exhibition> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new exhibition:" : "Modify the exhibition details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("End Date");
        TextField descField = new TextField();
        descField.setPromptText("Description");
        TextField curatorField = new TextField();
        curatorField.setPromptText("Curator Name");
        TextField themeField = new TextField();
        themeField.setPromptText("Theme");
        ComboBox<Gallery> galleryBox = new ComboBox<>(
                FXCollections.observableArrayList(galleryService.getAllGalleries()));
        galleryBox.setPromptText("Select Gallery");

        if (existing != null) {
            titleField.setText(existing.getTitle());
            titleField.setDisable(true); // Title is the key
            startDatePicker.setValue(existing.getStartDate());
            endDatePicker.setValue(existing.getEndDate());
            descField.setText(existing.getDescription() != null ? existing.getDescription() : "");
            curatorField.setText(existing.getCuratorName() != null ? existing.getCuratorName() : "");
            themeField.setText(existing.getTheme() != null ? existing.getTheme() : "");
            if (existing.getGallery() != null) {
                for (Gallery g : galleryBox.getItems()) {
                    if (g.getName().equals(existing.getGallery().getName())) {
                        galleryBox.setValue(g);
                        break;
                    }
                }
            }
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Gallery:"), 0, 1);
        grid.add(galleryBox, 1, 1);
        grid.add(new Label("Start Date:"), 0, 2);
        grid.add(startDatePicker, 1, 2);
        grid.add(new Label("End Date:"), 0, 3);
        grid.add(endDatePicker, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descField, 1, 4);
        grid.add(new Label("Curator:"), 0, 5);
        grid.add(curatorField, 1, 5);
        grid.add(new Label("Theme:"), 0, 6);
        grid.add(themeField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (galleryBox.getValue() == null) {
                    showError("Validation Error", "Please select a gallery.");
                    return null;
                }
                if (startDatePicker.getValue() == null) {
                    showError("Validation Error", "Please select a start date.");
                    return null;
                }
                Exhibition exhibition = new Exhibition();
                exhibition.setTitle(titleField.getText().trim());
                exhibition.setStartDate(startDatePicker.getValue());
                exhibition.setEndDate(endDatePicker.getValue());
                exhibition.setDescription(descField.getText().trim().isEmpty() ? null : descField.getText().trim());
                exhibition.setCuratorName(curatorField.getText().trim().isEmpty() ? null : curatorField.getText().trim());
                exhibition.setTheme(themeField.getText().trim().isEmpty() ? null : themeField.getText().trim());
                exhibition.setGallery(galleryBox.getValue());
                return exhibition;
            }
            return null;
        });

        return dialog;
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void refreshData() {
        exhibitionTable.setItems(FXCollections.observableArrayList(exhibitionService.getAllExhibitions()));
    }
}
