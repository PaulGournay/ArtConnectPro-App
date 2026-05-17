package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import com.project.artconnect.util.UserContext;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class WorkshopController {
    @FXML
    private TableView<Workshop> workshopTable;
    @FXML
    private TableColumn<Workshop, String> titleColumn;
    @FXML
    private TableColumn<Workshop, LocalDateTime> dateColumn;
    @FXML
    private TableColumn<Workshop, String> instructorColumn;
    @FXML
    private TableColumn<Workshop, Double> priceColumn;
    @FXML
    private TableColumn<Workshop, String> levelColumn;
    @FXML
    private TableColumn<Workshop, Integer> participantsColumn;
    @FXML
    private ComboBox<CommunityMember> memberComboBox;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final CommunityService communityService = ServiceProvider.getCommunityService();
    private final UserContext userContext = ServiceProvider.getUserContext();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null ? cellData.getValue().getInstructor().getName()
                        : "Unknown"));

        participantsColumn.setCellValueFactory(cellData -> {
            Integer id = cellData.getValue() != null ? cellData.getValue().getId() : null;
            Long workshopId = id != null ? id.longValue() : null;
            return new ReadOnlyObjectWrapper<>(
                    workshopService.getWorkshopParticipantsCount(workshopId));
        });
        participantsColumn.visibleProperty().bind(userContext.adminProperty());

        memberComboBox.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));

        refreshTable();
    }

    @FXML
    private void handleRegister() {
        Workshop selectedWorkshop = workshopTable.getSelectionModel().getSelectedItem();
        CommunityMember selectedMember = memberComboBox.getSelectionModel().getSelectedItem();

        if (selectedWorkshop == null) {
            showError("No Workshop Selected", "Please select a workshop to register.");
            return;
        }

        if (selectedMember == null) {
            showError("No Member Selected", "Please select a member to register.");
            return;
        }

        try {
            workshopService.bookWorkshop(selectedWorkshop, selectedMember);
            refreshTable();
        } catch (Exception e) {
            showError("Booking Failed", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Workshop> dialog = createWorkshopDialog("Add New Workshop", null);
        Optional<Workshop> result = dialog.showAndWait();
        result.ifPresent(workshop -> {
            try {
                workshopService.createWorkshop(workshop);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding workshop", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a workshop to edit.");
            return;
        }
        Dialog<Workshop> dialog = createWorkshopDialog("Edit Workshop", selected);
        Optional<Workshop> result = dialog.showAndWait();
        result.ifPresent(workshop -> {
            try {
                workshopService.updateWorkshop(workshop);
                refreshTable();
            } catch (Exception e) {
                showError("Error editing workshop", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a workshop to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Workshop: " + selected.getTitle());
        confirm.setContentText("Are you sure you want to delete this workshop? This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                workshopService.deleteWorkshop(selected.getTitle());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting workshop", e.getMessage());
            }
        }
    }

    private Dialog<Workshop> createWorkshopDialog(String title, Workshop existing) {
        Dialog<Workshop> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new workshop:" : "Modify the workshop details:");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField dateField = new TextField();
        dateField.setPromptText("Date (yyyy-MM-dd HH:mm)");
        TextField instructorField = new TextField();
        instructorField.setPromptText("Instructor Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField levelField = new TextField();
        levelField.setPromptText("Level (beginner/intermediate/advanced)");
        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description");
        TextField durationField = new TextField();
        durationField.setPromptText("Duration (minutes)");
        TextField maxParticipantsField = new TextField();
        maxParticipantsField.setPromptText("Max Participants");

        if (existing != null) {
            titleField.setText(existing.getTitle());
            titleField.setDisable(true);
            dateField.setText(existing.getDate() != null ? existing.getDate().format(DATE_FORMATTER) : "");
            instructorField.setText(existing.getInstructor() != null ? existing.getInstructor().getName() : "");
            priceField.setText(String.valueOf(existing.getPrice()));
            levelField.setText(existing.getLevel() != null ? existing.getLevel() : "");
            locationField.setText(existing.getLocation() != null ? existing.getLocation() : "");
            descriptionField.setText(existing.getDescription() != null ? existing.getDescription() : "");
            durationField.setText(String.valueOf(existing.getDurationMinutes()));
            maxParticipantsField.setText(String.valueOf(existing.getMaxParticipants()));
        }

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Instructor:"), 0, 2);
        grid.add(instructorField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Level:"), 0, 4);
        grid.add(levelField, 1, 4);
        grid.add(new Label("Location:"), 0, 5);
        grid.add(locationField, 1, 5);
        grid.add(new Label("Description:"), 0, 6);
        grid.add(descriptionField, 1, 6);
        grid.add(new Label("Duration (min):"), 0, 7);
        grid.add(durationField, 1, 7);
        grid.add(new Label("Max Participants:"), 0, 8);
        grid.add(maxParticipantsField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Workshop workshop = new Workshop();
                workshop.setTitle(titleField.getText().trim());
                workshop.setLevel(levelField.getText().trim().isEmpty() ? null : levelField.getText().trim());
                workshop.setLocation(locationField.getText().trim().isEmpty() ? null : locationField.getText().trim());
                workshop.setDescription(descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim());
                try {
                    String dateStr = dateField.getText().trim();
                    if (!dateStr.isEmpty()) {
                        workshop.setDate(LocalDateTime.parse(dateStr, DATE_FORMATTER));
                    }
                } catch (DateTimeParseException e) {
                    // Ignore invalid date
                }
                try {
                    String priceStr = priceField.getText().trim();
                    if (!priceStr.isEmpty()) {
                        workshop.setPrice(Double.parseDouble(priceStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid price
                }
                try {
                    String durStr = durationField.getText().trim();
                    if (!durStr.isEmpty()) {
                        workshop.setDurationMinutes(Integer.parseInt(durStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid duration
                }
                try {
                    String maxStr = maxParticipantsField.getText().trim();
                    if (!maxStr.isEmpty()) {
                        workshop.setMaxParticipants(Integer.parseInt(maxStr));
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid max participants
                }
                String instructorName = instructorField.getText().trim();
                if (!instructorName.isEmpty()) {
                    Artist instructor = new Artist();
                    instructor.setName(instructorName);
                    workshop.setInstructor(instructor);
                }
                return workshop;
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
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
    }
}