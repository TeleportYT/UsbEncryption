package Controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        stage.initStyle(StageStyle.UNDECORATED);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoadingPopup.fxml"));
            Parent root = loader.load();
            controller = loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
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
