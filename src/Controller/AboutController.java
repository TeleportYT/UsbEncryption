package Controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
}
