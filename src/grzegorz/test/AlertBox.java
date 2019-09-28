package grzegorz.test;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    public void display(String title, String message){
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label(message);
        Button closeButton = new Button("Exit");
        closeButton.setOnAction(e -> stage.close());
        VBox layout = new VBox();
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.BOTTOM_CENTER);
        Scene scene = new Scene(layout, 300, 300);

        stage.setScene(scene);
        stage.showAndWait();
    }
}
