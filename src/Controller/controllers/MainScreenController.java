package Controller.controllers;

import Controller.runners.LoadingPopup;
import Model.AESObserver;
import Model.FunctionMode;
import Model.ModelControl;
import Model.USB_Controller.UsbDriveFinder;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class MainScreenController extends DefaultController {

    @FXML private AnchorPane ap;
    Stage main;

    @FXML
    private ChoiceBox disksList;


    @FXML
    private VBox menuList;


    @Override
    public void initialize() {
        super.initialize();
        mode = FunctionMode.Usb;
        List<String> drives = UsbDriveFinder.getUsbDrives();

        ObservableList<String> itemList = FXCollections.observableArrayList(drives);

        disksList.setItems(itemList);

        disksList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                path = disksList.getItems().get((Integer) number2).toString();
                System.out.println("Path "+ path);
            }
        });

        disksList.getSelectionModel().selectFirst();



    }


}