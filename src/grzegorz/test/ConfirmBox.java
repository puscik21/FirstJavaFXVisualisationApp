package grzegorz.test;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

    private static boolean answer;

    public static boolean display(String title, String message){
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label(message);


        Button yesButton = new Button("Yes");
        yesButton.setOnAction(e -> {
            answer = true;
            stage.close();
        });

        Button noButton = new Button("No");
        noButton.setOnAction(e -> {
            answer = false;
            stage.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, yesButton, noButton);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        Scene scene = new Scene(layout, 300, 300);

        stage.setScene(scene);
        stage.showAndWait();

        return answer;
    }
}
