module testingEncryption {
    opens Controller to javafx.fxml;
    exports Model;
    exports Controller.controllers;
    opens Controller.controllers to javafx.fxml;
    exports Controller.runners;
    opens Controller.runners to javafx.fxml;
    opens Controller.fxmls to javafx.fxml;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    // other requires directives
}