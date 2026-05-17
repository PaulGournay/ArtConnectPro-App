package com.project.artconnect.ui;

import com.project.artconnect.util.ServiceProvider;
import com.project.artconnect.util.UserContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;

public class MainController {
    @FXML
    private TabPane mainTabPane;

    @FXML
    private ToggleButton adminToggle;

    private final UserContext userContext = ServiceProvider.getUserContext();

    @FXML
    public void initialize() {
        adminToggle.selectedProperty().bindBidirectional(userContext.adminProperty());
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}
