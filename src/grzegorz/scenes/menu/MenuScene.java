package grzegorz.scenes.menu;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuScene {

    @FXML
    public AnchorPane rootPane;

    @FXML
    public JFXButton startButton;

    @FXML
    public JFXButton closeButton;


    @FXML
    public void openMainScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../introduction/introductionScene.fxml"));
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        stage.show();
    }

    @FXML
    public void close(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
