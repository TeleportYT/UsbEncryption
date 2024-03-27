package Controller.controllers;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

public class FileEncryption extends DefaultController {

    @FXML
    public void chooseFile(){
        FileChooser fil_chooser = new FileChooser();
        File file = fil_chooser.showOpenDialog(main);
        this.path = file.getAbsolutePath();
    }
}
