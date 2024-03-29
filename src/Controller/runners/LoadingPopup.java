package Controller.runners;

import Controller.controllers.LoadingPopupController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class LoadingPopup {

    private Stage stage;
    private LoadingPopupController controller;

    public LoadingPopup(Stage parentStage) {
        stage = new Stage();
        stage.setTitle("Loading");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.initStyle(StageStyle.DECORATED);
        stage.setOnCloseRequest(e->e.consume());
        stage.getIcons().add(new Image(getClass().getResource("/Controller/Resources/icon.jpg").toExternalForm()));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controller/fxmls/LoadingPopup.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Controller/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProgress(double progress){
        controller.setProgress(controller.getProgress()+progress);
    }

    public void show(String message, double progress) {
        controller.setMessage(message);
        controller.setProgress(progress);
        stage.show();
    }

    public void hide() {
        controller.setProgress(100);
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.hide());
        delay.play();
    }
}
