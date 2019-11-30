package grzegorz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// TODO: 21.11.2019
//  eavesdropper scene "But assume world is not perfect and someone can be eavesdropping - Let's repeat sending scenario* but with Eve in scene and see how BB84 is dealing with situation like this"
//  something why it works
//  chart scene - checking if some1 was eavesdropping
//  right click everything
public class Main extends Application {

    // TODO: 25.11.2019 separate IntroductionScene
    //  + QBit explanation
    //  + Quantum piece

    // TODO: 25.11.2019 swap Alice with Bob
    // TODO: 25.11.2019 3 rows in filters comparison

    // TODO: 13.11.2019
    //  fix transited objects
    //  class with animations, class with dialogs
    //  resizing
    //  different languages comments
    //  \/
    //  refresh for every scene

    // TODO: 29.11.2019 think about something like preparation of every qbits/filters values on quantum scene start and then only get them by getters

    // TODO: 26.11.2019 try to make reloadScene for every tab by pressing MenuItem (or by replay button (showButton))

    // TODO: 26.10.2019 look at transited objects positions

    // TODO: 30.11.2019 circles (probably arrows) on every scene
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


// TODO: (LAST) 06.10.2019 .properties file for all comments