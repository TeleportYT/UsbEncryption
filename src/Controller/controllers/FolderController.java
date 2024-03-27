package Controller.controllers;

import Model.FunctionMode;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class FolderController extends DefaultController{
    @FXML
    public Text chooseTxt;

    @Override
    public void initialize() {
        super.initialize();
        mode = FunctionMode.Folder;
    }

    @FXML
    public void chooseFile(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(main);
        if (file == null){
            return;
        }
        this.path = file.getAbsolutePath();
        String fileName = file.getName();


        Text text = new Text(fileName);
        text.setFont(chooseTxt.getFont());
        double textWidth = text.getLayoutBounds().getWidth();

        double availableWidth = chooseTxt.getWrappingWidth();
        int MAX_DISPLAY_LENGTH = (int) Math.floor(availableWidth / textWidth * fileName.length())- "Folder: ".length();
        if (fileName.length() > MAX_DISPLAY_LENGTH) {
            fileName = fileName.substring(0, MAX_DISPLAY_LENGTH - 3 ) + "...";
        }
        chooseTxt.setText("Folder: " + fileName);

    }

}
