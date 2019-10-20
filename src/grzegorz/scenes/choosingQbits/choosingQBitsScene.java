package grzegorz.scenes.choosingQbits;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.Random;

public class choosingQBitsScene {
    @FXML
    private HBox qBitsHBox;

    private int[] qBitsList;


    @FXML
    public void initialize() {
        getRandomQBits();
        chooseWithList(qBitsHBox.getChildren(), 0);
    }


    private void getRandomQBits() {
        Random random = new Random();
        int size = qBitsHBox.getChildren().size();
        int bound = 2;

        qBitsList = new int[size];
        for (int i = 0; i < size; i++) {
            qBitsList[i] = random.nextInt(bound);
        }
    }


    private void chooseWithList(ObservableList<Node> childrenList, int index) {
        Label label = (Label) childrenList.get(index);
        label.setText(String.valueOf(qBitsList[index]));
        label.setVisible(true);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(label);
        scaleTransition.setDuration(Duration.seconds(0.75));
        scaleTransition.setFromX(0.0);
        scaleTransition.setFromY(0.0);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(label);
        fadeTransition.setDuration(Duration.seconds(0.75));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        if (index < childrenList.size() - 1) {
            parallelTransition.setOnFinished(e -> chooseWithList(childrenList, index + 1));
        }
        parallelTransition.play();
    }
}
