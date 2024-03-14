module testingEncryption {
    opens Controller to javafx.fxml;
    exports Controller;
    exports Model;
    requires javafx.controls;
    requires javafx.fxml;
    // other requires directives
}