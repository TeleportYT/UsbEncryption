package Controller;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

public class LoadingPopupController {

    @FXML
    private ProgressBar loadingIndicator;

    @FXML
    private Label loadingMessage;


    @FXML
    private ImageView loadingGif;


    private static final double progressWidth  = 306;
    public void initialize() {
        loadingGif.setTranslateX(-1*progressWidth/2);
        setProgress(0.5);
    }

        public void setMessage(String message) {
        loadingMessage.setText(message);
    }



    double position = -153;
    public void setProgress(double progress) {
        double move = progress*progressWidth;
        System.out.println("move: "+move);
        if(position+move>progressWidth/2){
            loadingGif.setTranslateX(progressWidth/2);
        }
        else {
            loadingGif.setTranslateX(position+move);
        }
        loadingIndicator.setProgress(progress);
    }

    public double getProgress() {
        return loadingIndicator.getProgress();
    }
}