package grzegorz.scenes.choosingQBits;

import grzegorz.QBitState;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChoosingQBitsScene {
    @FXML
    private HBox qBitValuesHBox;

    @FXML
    private HBox qBitImagesHBox;

    private QBitState[] bobQBitsStates;
    private ArrayList<Image> qBitImages;


    @FXML
    public void initialize() {
        prepareImages();
        getRandomQBits();
    }

    public QBitState[] start() {
        showChosenQBits(qBitValuesHBox.getChildren(), qBitImagesHBox.getChildren(), 0);
        return bobQBitsStates;
    }

    private void prepareImages() {
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");

        qBitImages = new ArrayList<>(4);
        qBitImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
    }

    private void getRandomQBits() {
        Random random = new Random();
        int size = qBitValuesHBox.getChildren().size();
        bobQBitsStates = new QBitState[size];
        int bound = 4;

        for (int i = 0; i < size; i++) {
            int randomStateValue = random.nextInt(bound);
            fillQBitValues(i, randomStateValue);
        }
    }

    private void fillQBitValues(int i, int state) {
        bobQBitsStates[i] = QBitState.getNewQBit(state);

        ImageView qBitImageView = (ImageView) qBitImagesHBox.getChildren().get(i);
        qBitImageView.setImage(qBitImages.get(state));
    }

    private void showChosenQBits(ObservableList<Node> valuesList, ObservableList<Node> imageViewsList, int index) {
        Label valueLabel = (Label) valuesList.get(index);
        valueLabel.setText(String.valueOf(bobQBitsStates[index].getValue()));
        valueLabel.setVisible(true);

        ImageView imageView = (ImageView) imageViewsList.get(index);
        imageView.setVisible(true);

        ScaleTransition valueScaleTrans = returnScaleTransition(valueLabel);
        FadeTransition valueFadeTrans = returnFadeTransition(valueLabel);
        ScaleTransition imageScaleTrans = returnScaleTransition(imageView);
        FadeTransition imageFadeTrans = returnFadeTransition(imageView);

        ParallelTransition parallelTransition = new ParallelTransition(valueScaleTrans, valueFadeTrans, imageScaleTrans, imageFadeTrans);
        if (index < valuesList.size() - 1) {
            parallelTransition.setOnFinished(e -> showChosenQBits(valuesList, imageViewsList, index + 1));
        }
        parallelTransition.play();
    }

    private ScaleTransition returnScaleTransition(Node node) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDuration(Duration.seconds(0.75));
        scaleTransition.setFromX(0.0);
        scaleTransition.setFromY(0.0);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        return scaleTransition;
    }

    private FadeTransition returnFadeTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(0.75));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        return fadeTransition;
    }
}
