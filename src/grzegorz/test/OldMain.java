package grzegorz.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class OldMain extends Application {

    private Stage stage;
    private Scene scene;
    private Button button;
    private BorderPane layout;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        stage = primaryStage;
        stage.setTitle("This Is The Best Stage");
        button = new Button("CLick me!");

        Menu fileMenu = prepareFileMenu();

        Menu helpMenu = prepareHelpMenu();

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        // Standard stage layout
//        VBox layout = new VBox(10);
//        layout.setPadding(new Insets(20, 20, 20, 20));

        layout = new BorderPane();
        layout.setTop(menuBar);

        scene = new Scene(layout, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private Menu prepareFileMenu(){
        Menu fileMenu = new Menu("_File");

        MenuItem newFile = new MenuItem("New");
        newFile.setOnAction(e -> System.out.println("Hello there!"));
        fileMenu.getItems().add(newFile);

        MenuItem openFile = new MenuItem("Open...");
        openFile.setOnAction(e -> System.out.println("Opening"));
        openFile.setDisable(true);
        fileMenu.getItems().add(openFile);

        fileMenu.getItems().addAll(new MenuItem("Open URL..."));
        fileMenu.getItems().addAll(new MenuItem("Open Recent"));
        fileMenu.getItems().addAll(new SeparatorMenuItem());

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> stage.close());
        fileMenu.getItems().add(exit);

        return fileMenu;
    }

    private Menu prepareHelpMenu(){
        Menu helpMenu = new Menu("Help");
        CheckMenuItem showLines = new CheckMenuItem("Show Line Numbers");
        showLines.setOnAction(e -> {
            if (showLines.isSelected()){
                System.out.println("Displaying");
            }
            else {
                System.out.println("Hiding");
            }
        });

        CheckMenuItem autoSave = new CheckMenuItem("Enable Autosave");
        autoSave.setSelected(true);

        helpMenu.getItems().addAll(showLines, autoSave);

        return helpMenu;
    }
}