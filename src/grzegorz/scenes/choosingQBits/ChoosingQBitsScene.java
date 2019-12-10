package grzegorz.scenes.choosingQBits;

import grzegorz.general.QBitState;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChoosingQBitsScene {
    @FXML
    private AnchorPane scenePane;

    @FXML
    private HBox qBitValuesHBox;

    @FXML
    private HBox qBitImagesHBox;

    private QBitState[] aliceQBitsStates;
    private ArrayList<Image> qBitImages;
    private ArrayList<Image> valuesImages;
    private Random generator;

    private double sizeScale = 1.0;
    private double timeScale = 4.0;


    @FXML
    public void initialize() {
        prepareImages();
        generator = new Random();
    }

    public QBitState[] startRandom() {
        getRandomQBits();
        prepareScene();
        showChosenQBits(qBitValuesHBox.getChildren(), qBitImagesHBox.getChildren(), 0);
        return aliceQBitsStates;
    }

    public void startWithUserInput(QBitState[] userInput) {
        aliceQBitsStates = userInput;
        prepareScene();
        showChosenQBits(qBitValuesHBox.getChildren(), qBitImagesHBox.getChildren(), 0);
    }

    private void prepareImages() {
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");
        Image zeroValue = new Image("grzegorz\\images\\zeroIconGreen.png");
        Image oneValue = new Image("grzegorz\\images\\oneIconGreen.png");

        qBitImages = new ArrayList<>(4);
        valuesImages = new ArrayList<>(2);
        qBitImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
        valuesImages.addAll(Arrays.asList(zeroValue, oneValue));
    }

    private void getRandomQBits() {
        int size = getRandomInt(9) + 8;
        aliceQBitsStates = new QBitState[size];
        int bound = 4;

        for (int i = 0; i < size; i++) {
            int randomStateValue = getRandomInt(bound);
            aliceQBitsStates[i] = QBitState.getNewQBit(randomStateValue);
        }
    }

    private void prepareScene() {
        addImageViews();
        scaleScenePane();
    }

    private void addImageViews() {
        for (QBitState aliceQBitsState : aliceQBitsStates) {
            fillQBitValue(aliceQBitsState);
        }
    }

    private void fillQBitValue(QBitState qBitState) {
        int state = qBitState.getState();
        int value = qBitState.getValue();

        ImageView qBitView = new ImageView(qBitImages.get(state));
        ImageView valueView = new ImageView(valuesImages.get(value));
        qBitView.setVisible(false);
        valueView.setVisible(false);
        qBitImagesHBox.getChildren().add(qBitView);
        qBitValuesHBox.getChildren().add(valueView);
    }

    private void scaleScenePane() {
        prepareScaleValues();
        scenePane.setScaleX(sizeScale);
        scenePane.setScaleY(sizeScale);
    }

    private void prepareScaleValues() {
        int size = aliceQBitsStates.length;
        if (size == 0) {
            return;
        }

        setSizeScale(size);
        setTimeScale(size);
    }

    private void setSizeScale(double size) {
        double multiplier = 7.0 / size;
        sizeScale *= multiplier;

        if (sizeScale < 0.6) {
            sizeScale = 0.6;
        } else if (sizeScale > 1.25) {
            sizeScale = 1.25;
        }
    }

    private void setTimeScale(double size) {
        timeScale = timeScale / size;
        if (timeScale < 0.4) {
            timeScale = 0.4;
        } else if (timeScale > 0.8) {
            timeScale = 0.8;
        }
    }

    private int getRandomInt(int bound) {
        return generator.nextInt(bound);
    }

    private void showChosenQBits(ObservableList<Node> valuesList, ObservableList<Node> imageViewsList, int index) {
        Node valueLabel =  valuesList.get(index);
        Node imageView = imageViewsList.get(index);
        valueLabel.setVisible(true);
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
        scaleTransition.setDuration(Duration.seconds(timeScale));
        scaleTransition.setFromX(0.0);
        scaleTransition.setFromY(0.0);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        return scaleTransition;
    }

    private FadeTransition returnFadeTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(timeScale));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        return fadeTransition;
    }
}
