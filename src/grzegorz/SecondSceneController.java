package grzegorz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SecondSceneController {

    @FXML
    public Button openFirstSceneBtn;

    @FXML
    public void openFirstScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("scenes/mainScene.fxml"));
        Stage stage = (Stage) openFirstSceneBtn.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
