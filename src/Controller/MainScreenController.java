package Controller;

import Model.AESObserver;
import Model.ModelControl;
import Model.USB_Controller.UsbDriveFinder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.System.exit;

public class MainScreenController implements AESObserver {

    @FXML private AnchorPane ap;
    Stage main;

    @FXML
    private ChoiceBox disksList;

    @FXML
    private TextField passInput,saltInput;

    @FXML
    private Button encrypt,decrypt;

    private ModelControl model;

    @FXML
    private Text salt;
    private boolean isSalt=false;

    public void initialize() {
        List<String> drives = UsbDriveFinder.getUsbDrives();

        ObservableList<String> itemList = FXCollections.observableArrayList(drives);

        disksList.setItems(itemList);
        disksList.getSelectionModel().selectFirst();

        model = new ModelControl();
        model.addListener(this);


        passInput.textProperty().addListener((observable, oldValue, newValue) -> {
           checkPassword(newValue);
        });


    }

    private void checkPassword(String newValue){
        if(newValue.length()<6){
            encrypt.setDisable(true);
            decrypt.setDisable(true);
            return;
        }
        encrypt.setDisable(false);
        decrypt.setDisable(false);
    }

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


    public void Encrypt() throws Exception {
        LoadingPopup("Encrypting...");

        new Thread(() -> {
            try {
                model.StartProcess((disksList.getSelectionModel().getSelectedItem().toString().substring(0,1)),passInput.getText(),saltInput.getText(),Boolean.TRUE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void Decrypt() throws Exception {
        LoadingPopup("Decrypting...");
        new Thread(() -> {
            try {
               model.StartProcess((disksList.getSelectionModel().getSelectedItem().toString().substring(0,1)),passInput.getText(),saltInput.getText(),Boolean.FALSE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    LoadingPopup lp;
    private void LoadingPopup(String thing){
        main = (Stage) ap.getScene().getWindow();
        lp = new LoadingPopup(main);
        lp.show(thing, 0.0);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            lp.hide();
        });
    }

    @Override
    public void updateProgress(double n) {
        System.out.println("Done "+n+" procent");
        lp.updateProgress(n);

    }

    @FXML
    public void Exit(){
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        primaryStage.close();
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
    public void changeSalt(){
            salt.setVisible(!isSalt);
            saltInput.setVisible(!isSalt);
            isSalt = !isSalt;
    }
}
