package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

public class ArtworkController {
    @FXML
    private TableView<Artwork> artworkTable;
    @FXML
    private TableColumn<Artwork, String> titleColumn;
    @FXML
    private TableColumn<Artwork, String> typeColumn;
    @FXML
    private TableColumn<Artwork, Double> priceColumn;
    @FXML
    private TableColumn<Artwork, String> statusColumn;
    @FXML
    private TableColumn<Artwork, String> artistColumn;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : "Unknown"));

        refreshTable();
    }

    @FXML
    private void handleAdd() {
        Dialog<Artwork> dialog = createArtworkDialog("Add New Artwork", null);
        Optional<Artwork> result = dialog.showAndWait();
        result.ifPresent(artwork -> {
            try {
                artworkService.createArtwork(artwork);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding artwork", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModify() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an artwork to modify.");
            return;
        }

        Dialog<Artwork> dialog = createArtworkDialog("Modify Artwork", selected);
        Optional<Artwork> result = dialog.showAndWait();
        result.ifPresent(artwork -> {
            try {
                artworkService.updateArtwork(artwork);
                refreshTable();
            } catch (Exception e) {
                showError("Error modifying artwork", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an artwork to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Artwork: " + selected.getTitle());
        confirm.setContentText("Are you sure you want to delete this artwork? This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                artworkService.deleteArtwork(selected.getTitle());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting artwork", e.getMessage());
            }
        }
    }

    private Dialog<Artwork> createArtworkDialog(String title, Artwork existing) {
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new artwork:" : "Modify the artwork details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField typeField = new TextField();
        typeField.setPromptText("Type (painting, sculpture...)");
        TextField mediumField = new TextField();
        mediumField.setPromptText("Medium (oil, watercolor...)");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField yearField = new TextField();
        yearField.setPromptText("Creation Year");
        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList("FOR_SALE", "SOLD", "EXHIBITED"));
        statusBox.setPromptText("Status");
        ComboBox<Artist> artistBox = new ComboBox<>(
                FXCollections.observableArrayList(artistService.getAllArtists()));
        artistBox.setPromptText("Select Artist");

        if (existing != null) {
            titleField.setText(existing.getTitle());
            titleField.setDisable(true); // Title is the key
            typeField.setText(existing.getType() != null ? existing.getType() : "");
            mediumField.setText(existing.getMedium() != null ? existing.getMedium() : "");
            priceField.setText(String.valueOf(existing.getPrice()));
            yearField.setText(existing.getCreationYear() != null ? String.valueOf(existing.getCreationYear()) : "");
            if (existing.getStatus() != null) {
                statusBox.setValue(existing.getStatus().name());
            }
            // Try to select the artist in the combo box
            if (existing.getArtist() != null) {
                for (Artist a : artistBox.getItems()) {
                    if (a.getName().equals(existing.getArtist().getName())) {
                        artistBox.setValue(a);
                        break;
                    }
                }
            }
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);
        grid.add(artistBox, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeField, 1, 2);
        grid.add(new Label("Medium:"), 0, 3);
        grid.add(mediumField, 1, 3);
        grid.add(new Label("Price:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Year:"), 0, 5);
        grid.add(yearField, 1, 5);
        grid.add(new Label("Status:"), 0, 6);
        grid.add(statusBox, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (artistBox.getValue() == null) {
                    showError("Validation Error", "Please select an artist.");
                    return null;
                }
                Artwork artwork = new Artwork();
                artwork.setTitle(titleField.getText().trim());
                artwork.setType(typeField.getText().trim().isEmpty() ? null : typeField.getText().trim());
                artwork.setMedium(mediumField.getText().trim().isEmpty() ? null : mediumField.getText().trim());
                artwork.setArtist(artistBox.getValue());
                if (statusBox.getValue() != null) {
                    artwork.setStatus(Artwork.Status.valueOf(statusBox.getValue()));
                }
                try {
                    artwork.setPrice(Double.parseDouble(priceField.getText().trim()));
                } catch (NumberFormatException e) {
                    artwork.setPrice(0);
                }
                try {
                    String yr = yearField.getText().trim();
                    if (!yr.isEmpty()) {
                        artwork.setCreationYear(Integer.parseInt(yr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
                return artwork;
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
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }
}
