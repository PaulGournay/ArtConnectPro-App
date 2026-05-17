package com.project.artconnect.ui;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class CommunityController {
    @FXML
    private TableView<CommunityMember> memberTable;
    @FXML
    private TableColumn<CommunityMember, String> nameColumn;
    @FXML
    private TableColumn<CommunityMember, String> emailColumn;
    @FXML
    private TableColumn<CommunityMember, String> cityColumn;

    private final CommunityService communityService = ServiceProvider.getCommunityService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        Dialog<CommunityMember> dialog = createMemberDialog("Add New Member", null);
        Optional<CommunityMember> result = dialog.showAndWait();
        result.ifPresent(member -> {
            try {
                communityService.createMember(member);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding member", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        CommunityMember selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a member to edit.");
            return;
        }
        Dialog<CommunityMember> dialog = createMemberDialog("Edit Member", selected);
        Optional<CommunityMember> result = dialog.showAndWait();
        result.ifPresent(member -> {
            try {
                communityService.updateMember(member);
                refreshTable();
            } catch (Exception e) {
                showError("Error editing member", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        CommunityMember selected = memberTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No Selection", "Please select a member to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Member: " + selected.getName());
        confirm.setContentText("Are you sure you want to delete this member? This action cannot be undone.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                communityService.deleteMember(selected.getName());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting member", e.getMessage());
            }
        }
    }

    private Dialog<CommunityMember> createMemberDialog(String title, CommunityMember existing) {
        Dialog<CommunityMember> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existing == null ? "Enter the details of the new member:" : "Modify the member details:");

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
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField cityField = new TextField();
        cityField.setPromptText("City");
        TextField birthYearField = new TextField();
        birthYearField.setPromptText("Birth Year");
        TextField membershipField = new TextField();
        membershipField.setPromptText("Membership Type (free/premium)");

        if (existing != null) {
            nameField.setText(existing.getName());
            nameField.setDisable(true);
            emailField.setText(existing.getEmail() != null ? existing.getEmail() : "");
            phoneField.setText(existing.getPhone() != null ? existing.getPhone() : "");
            cityField.setText(existing.getCity() != null ? existing.getCity() : "");
            birthYearField.setText(existing.getBirthYear() != null ? String.valueOf(existing.getBirthYear()) : "");
            membershipField.setText(existing.getMembershipType() != null ? existing.getMembershipType() : "");
        }

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("City:"), 0, 3);
        grid.add(cityField, 1, 3);
        grid.add(new Label("Birth Year:"), 0, 4);
        grid.add(birthYearField, 1, 4);
        grid.add(new Label("Membership:"), 0, 5);
        grid.add(membershipField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                CommunityMember member = new CommunityMember();
                member.setName(nameField.getText().trim());
                member.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                member.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                member.setCity(cityField.getText().trim().isEmpty() ? null : cityField.getText().trim());
                member.setMembershipType(membershipField.getText().trim().isEmpty() ? null : membershipField.getText().trim());
                try {
                    String yearStr = birthYearField.getText().trim();
                    if (!yearStr.isEmpty()) member.setBirthYear(Integer.parseInt(yearStr));
                } catch (NumberFormatException e) { /* ignore */ }
                return member;
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
        memberTable.setItems(FXCollections.observableArrayList(communityService.getAllMembers()));
    }
}