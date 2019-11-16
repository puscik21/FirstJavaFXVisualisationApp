package grzegorz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    // TODO: 13.11.2019
    //  fix transited objects
    //  class with animations, class with dialogs
    //  resizing
    //  different languages comments
    //  \/
    //  refresh for every scene
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("scenes/introduction/introductionScene.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("scenes/menu/menuScene.fxml"));
        primaryStage.setTitle("Quantum key distribution");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }
}