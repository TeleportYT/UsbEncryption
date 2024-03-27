package Controller.controllers;

import Controller.runners.LoadingPopup;
import Model.AESObserver;
import Model.ModelControl;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class DefaultController implements AESObserver {
    @FXML private AnchorPane ap;
    protected Stage main;

    @FXML
    private TextField passInput,saltInput;
    @FXML
    private Text salt;

    @FXML
    private Button encrypt,decrypt;

    private ModelControl model;

    protected String path;

    @FXML
    private VBox menuList;
    public void initialize() {

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
                model.startUsbProcess(path,passInput.getText(),saltInput.getText(),Boolean.TRUE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void Decrypt() throws Exception {
        LoadingPopup("Decrypting...");
        new Thread(() -> {
            try {
                model.startUsbProcess(path,passInput.getText(),saltInput.getText(),Boolean.FALSE);
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


    private boolean isSalt=false;

    @FXML
    public void changeSalt(){
        salt.setVisible(!isSalt);
        saltInput.setVisible(!isSalt);
        isSalt = !isSalt;
    }


    @FXML
    public void handle(MouseEvent me) {
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        primaryStage.setIconified(true);
    }
    @FXML
    public void showAbout(){
        if (primaryStage==null){
            primaryStage = (Stage) ap.getScene().getWindow();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxmls/aboutMe.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 780, 521);
            scene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isList = false;
    private int ignoreOnce = 1;
    @FXML
    public void controlMenu(){
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5),menuList);
        if (!isList){
            System.out.println("Opening Menu");
            transition.setToX(161.0);
            ap.setOnMouseClicked(this::CloseMenu);
            ignoreOnce = 1;
        }
        else{
            System.out.println("Closing Menu");
            transition.setToX(0);
            ap.setOnMouseClicked(null);
        }
        isList = !isList;
        transition.play();
    }

    @FXML
    private void CloseMenu(MouseEvent event) {
        System.out.println("Closing Menu");
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5),menuList);
        if (isList && ignoreOnce == 0){
            transition.setToX(menuList.getTranslateX()-161.0);
            ap.setOnMouseClicked(null);
            isList = false;
            transition.play();
        }
        else{
            ignoreOnce--;
        }

    }
}
