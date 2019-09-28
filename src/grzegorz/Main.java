package grzegorz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {

    private Stage stage;
    private Scene scene;
    private Button button;
    private BorderPane layout;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("scenes/mainScene.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("scenes/menuScene.fxml"));
        primaryStage.setTitle("testGUI");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}