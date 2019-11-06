package grzegorz.scenes.menu;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
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
    private ChoiceBox<String> languageChoiceBox;


    @FXML
    public void initialize() {
        languageChoiceBox.getItems().addAll("English", "Polski");
        languageChoiceBox.setValue(languageChoiceBox.getItems().get(0));
        languageChoiceBox.setOnAction(e -> changeLanguage());
    }

    private void changeLanguage() {
        // TODO: 06.11.2019 change language (languageChoiceBox.getValue())
    }

    @FXML
    private void openMainScene() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../introduction/introductionScene.fxml"));
        Stage stage = (Stage) startButton.getScene().getWindow();
        stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        stage.show();
    }

    @FXML
    private void close(){
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
