package grzegorz.general;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


// TODO: 10.12.2019 FOR NOW
//  qber and parity scenes objects comments
//  check every comment
//  circles (probably arrows) on every scene
//  resizing
//  fix transited objects
//  refresh for every scene - then its required to calculate every qbit and filter value in the IntroductionScene
//  in EveFiltersCheckScene and FiltersCheckScene highlight rows that are incorrect (some1 was eavesdropping)

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../scenes/menu/menuScene.fxml"));
        primaryStage.setTitle("Quantum key distribution");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.show();
    }
}


// TODO: 10.12.2019 ABOUT DIALOGS
//  eavesdropper scene "But assume world is not perfect and someone can be eavesdropping - Let's repeat sending scenario* but with Eve in scene and see how BB84 is dealing with situation like this"
//  something why it works

// TODO: 10.12.2019 EVENTUALLY
//  In EveFiltersCheckScene for every wrong row transition move it outside the scene then remove
//  think about something like preparation of every qbits/filters values on quantum scene start and then only get them by getters
//  try to make reloadScene for every tab by pressing MenuItem (or by replay button (showButton))
//  way to move slider of tabpane
//  disable tabPane buttons when circle animation is being played


// TODO: 10.12.2019 EVEN MORE EVENTUALLY
//  in FiltersCheckScenes check incorrect, but when everyone matches write some info
//  -
//  in IntroductionScene and QuantumScene change that height and width values (or method to receive them)
//  primaryStage.setOnShowing(event -> {});     - try it
//  after initialize call method start I guess and take good value
//  -
//  in ParityScene probably remove 2 bits
//  (Introduction and Quantum scenes) special flag for every tab telling when showButton can be enabled and telling what tab has been closed
//  -
//  in QuantumScene prepareAliceSendFiltersAfterEveAnimation() is same as  "prepareAliceSendFiltersAnimation", just need proper circle as parameter




// TODO: (LAST) 06.10.2019 .properties file for all comments
// TODO: (LAST) 20.11.2019 clicking fast can make envelope go back to Alice after moving to Bob
// TODO: (LAST) 05.12.2019 Do I want it to look in ParityScene like the whole half is checked for errors
// TODO: (LAST) 13.11.2019 different languages comments
// TODO: (LAST) 10.11.2019 check for the same methods like transitions or some effects(?)  that repeat and put them in
//  a single static class like Animator