module ui {
    requires core;
    requires javafx.controls;
    requires javafx.fxml;

    opens ui.controllers to javafx.graphics, javafx.fxml;
    opens ui to javafx.graphics, javafx.fxml;
}