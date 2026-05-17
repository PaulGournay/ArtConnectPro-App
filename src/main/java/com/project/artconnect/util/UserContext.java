package com.project.artconnect.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UserContext {
    private final BooleanProperty admin = new SimpleBooleanProperty(false);

    public BooleanProperty adminProperty() {
        return admin;
    }

    public boolean isAdmin() {
        return admin.get();
    }

    public void setAdmin(boolean isAdmin) {
        admin.set(isAdmin);
    }
}
