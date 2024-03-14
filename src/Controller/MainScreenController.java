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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainScreenController implements AESObserver {

    @FXML private AnchorPane ap;
    Stage main;

    @FXML
    private ChoiceBox disksList;

    @FXML
    private TextField passInput;

    @FXML
    private Button encrypt,decrypt;

    @FXML
    private ImageView encImg,decImg;

    private ModelControl model;

    public void initialize() {
        List<String> drives = UsbDriveFinder.getUsbDrives();

        // Convert the array to an ObservableList
        ObservableList<String> itemList = FXCollections.observableArrayList(drives);

        // Add the items to the ChoiceBox
        disksList.setItems(itemList);

        // Optional: Set a default selected item
        disksList.getSelectionModel().selectFirst();

        model = new ModelControl();
        model.addListener(this);


        passInput.textProperty().addListener((observable, oldValue, newValue) -> {
           checkPassword(newValue);
        });


    }

    private void checkPassword(String newValue){
        if(newValue.length()<6){
            encrypt.setCancelButton(true);
            ((ColorAdjust)decImg.getEffect()).brightnessProperty().set(-0.3);
            ((ColorAdjust)encImg.getEffect()).brightnessProperty().set(-0.3);
            return;
        }
        encrypt.setCancelButton(false);
        ((ColorAdjust)decImg.getEffect()).brightnessProperty().set(0);
        ((ColorAdjust)encImg.getEffect()).brightnessProperty().set(0);
    }



    public void Encrypt() throws Exception {
        LoadingPopup("Encrypting...");

        new Thread(() -> {
            try {
                //model.StartProcess((disksList.getSelectionModel().getSelectedItem().toString().substring(0,1)),passInput.getText(),"RandomForNow",Boolean.TRUE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void Decrypt() throws Exception {
        LoadingPopup("Decrypting...");
        updateProgress(0.5);
        new Thread(() -> {
            try {
               // model.StartProcess((disksList.getSelectionModel().getSelectedItem().toString().substring(0,1)),passInput.getText(),"RandomForNow",Boolean.FALSE);
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
}
