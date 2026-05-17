package com.project.artconnect.ui;

import com.project.artconnect.model.Gallery;
import com.project.artconnect.service.GalleryService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class GalleryController {
    @FXML
    private TableView<Gallery> galleryTable;
    @FXML
    private TableColumn<Gallery, String> nameColumn;
    @FXML
    private TableColumn<Gallery, String> addressColumn;
    @FXML
    private TableColumn<Gallery, String> ownerColumn;
    @FXML
    private TableColumn<Gallery, String> phoneColumn;
    @FXML
    private TableColumn<Gallery, Double> ratingColumn;
    @FXML
    private TableColumn<Gallery, String> websiteColumn;

    private final GalleryService galleryService = ServiceProvider.getGalleryService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("contactPhone"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        Dialog<Gallery> dialog = createGalleryDialog("Add New Gallery", null);
        Optional<Gallery> result = dialog.showAndWait();
        result.ifPresent(gallery -> {
            try {
                galleryService.createGallery(gallery);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding gallery", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        Gallery selected = galleryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a gallery to edit.");
            return;
        }
        Dialog<Gallery> dialog = createGalleryDialog("Edit Gallery", selected);
        Optional<Gallery> result = dialog.showAndWait();
        result.ifPresent(gallery -> {
            try {
                galleryService.updateGallery(gallery);
                refreshTable();
            } catch (Exception e) {
                showError("Error editing gallery", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Gallery selected = galleryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a gallery to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Gallery: " + selected.getName());
        confirm.setContentText("Are you sure you want to delete this gallery? This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                galleryService.deleteGallery(selected.getName());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting gallery", e.getMessage());
            }
        }
    }

    private Dialog<Gallery> createGalleryDialog(String title, Gallery existing) {
        Dialog<Gallery> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new gallery:" : "Modify the gallery details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField ownerField = new TextField();
        ownerField.setPromptText("Owner Name");
        TextField hoursField = new TextField();
        hoursField.setPromptText("Opening Hours");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Contact Phone");
        TextField ratingField = new TextField();
        ratingField.setPromptText("Rating (0.0 - 5.0)");
        TextField websiteField = new TextField();
        websiteField.setPromptText("Website");

        if (existing != null) {
            nameField.setText(existing.getName());
            nameField.setDisable(true);
            addressField.setText(existing.getAddress() != null ? existing.getAddress() : "");
            ownerField.setText(existing.getOwnerName() != null ? existing.getOwnerName() : "");
            hoursField.setText(existing.getOpeningHours() != null ? existing.getOpeningHours() : "");
            phoneField.setText(existing.getContactPhone() != null ? existing.getContactPhone() : "");
            ratingField.setText(String.valueOf(existing.getRating()));
            websiteField.setText(existing.getWebsite() != null ? existing.getWebsite() : "");
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(new Label("Owner:"), 0, 2);
        grid.add(ownerField, 1, 2);
        grid.add(new Label("Opening Hours:"), 0, 3);
        grid.add(hoursField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Rating:"), 0, 5);
        grid.add(ratingField, 1, 5);
        grid.add(new Label("Website:"), 0, 6);
        grid.add(websiteField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Gallery gallery = new Gallery();
                gallery.setName(nameField.getText().trim());
                gallery.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                gallery.setOwnerName(ownerField.getText().trim().isEmpty() ? null : ownerField.getText().trim());
                gallery.setOpeningHours(hoursField.getText().trim().isEmpty() ? null : hoursField.getText().trim());
                gallery.setContactPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                gallery.setWebsite(websiteField.getText().trim().isEmpty() ? null : websiteField.getText().trim());
                try {
                    String ratingStr = ratingField.getText().trim();
                    if (!ratingStr.isEmpty()) gallery.setRating(Double.parseDouble(ratingStr));
                } catch (NumberFormatException e) { /* ignore */ }
                return gallery;
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

    private void refreshTable() {
        galleryTable.setItems(FXCollections.observableArrayList(galleryService.getAllGalleries()));
    }
}