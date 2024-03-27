package Controller.controllers;

import Model.FunctionMode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URI;

public class AboutController {

    @FXML
    private AnchorPane ap;

    private double mousePressX;
    private double mousePressY;

    private Stage primaryStage;

    @FXML
    public void onMousePressed(MouseEvent event) {
        mousePressX = event.getSceneX();
        mousePressY = event.getSceneY();
        System.out.println("Mouse: x "+mousePressX+" y: "+mousePressY);
    }

    @FXML
    public void onMouseDragged(MouseEvent event) {
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        primaryStage.setX(event.getScreenX()-mousePressX);
        primaryStage.setY(event.getScreenY()-mousePressY);
    }
    @FXML
    public void mouseEnter(MouseEvent event) {
        Node node = (Node) event.getSource();
        ((ColorAdjust)node.getEffect()).brightnessProperty().set(-0.1);
    }

    @FXML
    public void mouseLeave(MouseEvent event) {
        Node node = (Node) event.getSource();
        ((ColorAdjust)node.getEffect()).brightnessProperty().set(0);
    }

    @FXML
    public void handle(MouseEvent me) {
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        primaryStage.setIconified(true);
    }

    @FXML
    public void Exit(){
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        primaryStage.close();
    }


    FunctionMode mode;

    public void showNextStage(String showNextStage){
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(showNextStage));
            Parent root = loader.load();
            Scene scene = new Scene(root, 780, 521);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setMode(FunctionMode mode){
        System.out.println(mode);
        this.mode = mode;
    }
    @FXML
    public void Back(){
        switch (mode.name()){
            case "Usb" -> showNextStage("/Controller/fxmls/sample.fxml");
            case "Folder" -> showNextStage("/Controller/fxmls/FolderForm.fxml");
            case "File" -> showNextStage("/Controller/fxmls/FileForm.fxml");

        }
    }

    @FXML
    public void openGithub(){
        String url = "https://github.com/TeleportYT";
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
