package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.util.Optional;

public class ArtistController {
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Discipline> disciplineFilter;
    @FXML
    private TableView<Artist> artistTable;
    @FXML
    private TableColumn<Artist, String> nameColumn;
    @FXML
    private TableColumn<Artist, String> cityColumn;
    @FXML
    private TableColumn<Artist, String> emailColumn;
    @FXML
    private TableColumn<Artist, Integer> yearColumn;

    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        disciplineFilter.setItems(FXCollections.observableArrayList(artistService.getAllDisciplines()));
        refreshTable();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        Discipline d = disciplineFilter.getValue();
        String dName = (d != null) ? d.getName() : null;
        artistTable.setItems(FXCollections.observableArrayList(artistService.searchArtists(query, dName, null)));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        disciplineFilter.setValue(null);
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        Dialog<Artist> dialog = createArtistDialog("Add New Artist", null);
        Optional<Artist> result = dialog.showAndWait();
        result.ifPresent(artist -> {
            try {
                artistService.createArtist(artist);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding artist", e.getMessage());
            }
        });
    }

    @FXML
    private void handleModify() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an artist to modify.");
            return;
        }

        Dialog<Artist> dialog = createArtistDialog("Modify Artist", selected);
        Optional<Artist> result = dialog.showAndWait();
        result.ifPresent(artist -> {
            try {
                artistService.updateArtist(artist);
                refreshTable();
            } catch (Exception e) {
                showError("Error modifying artist", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select an artist to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Artist: " + selected.getName());
        confirm.setContentText("Are you sure you want to delete this artist? This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                artistService.deleteArtist(selected.getName());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting artist", e.getMessage());
            }
        }
    }

    private Dialog<Artist> createArtistDialog(String title, Artist existing) {
        Dialog<Artist> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new artist:" : "Modify the artist details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField cityField = new TextField();
        cityField.setPromptText("City");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField birthYearField = new TextField();
        birthYearField.setPromptText("Birth Year");
        TextField websiteField = new TextField();
        websiteField.setPromptText("Website");

        if (existing != null) {
            nameField.setText(existing.getName());
            nameField.setDisable(true); // Name is the key, cannot be changed
            emailField.setText(existing.getContactEmail() != null ? existing.getContactEmail() : "");
            cityField.setText(existing.getCity() != null ? existing.getCity() : "");
            phoneField.setText(existing.getPhone() != null ? existing.getPhone() : "");
            birthYearField.setText(existing.getBirthYear() != null ? String.valueOf(existing.getBirthYear()) : "");
            websiteField.setText(existing.getWebsite() != null ? existing.getWebsite() : "");
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("City:"), 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(new Label("Phone:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Birth Year:"), 0, 4);
        grid.add(birthYearField, 1, 4);
        grid.add(new Label("Website:"), 0, 5);
        grid.add(websiteField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Artist artist = new Artist();
                artist.setName(nameField.getText().trim());
                artist.setContactEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                artist.setCity(cityField.getText().trim().isEmpty() ? null : cityField.getText().trim());
                artist.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                artist.setWebsite(websiteField.getText().trim().isEmpty() ? null : websiteField.getText().trim());
                artist.setActive(true);
                try {
                    String yearStr = birthYearField.getText().trim();
                    if (!yearStr.isEmpty()) {
                        artist.setBirthYear(Integer.parseInt(yearStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid year
                }
                return artist;
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
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }
}
