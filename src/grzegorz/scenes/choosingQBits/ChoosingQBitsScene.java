package grzegorz.scenes.choosingQBits;

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

    private int[] qBitsValues;
    private int[] filtersValues;
    private ArrayList<Image> positiveQBitImages;
    private ArrayList<Image> negativeQBitImages;


    @FXML
    public void initialize() {
        prepareImages();
        getRandomQBits();
    }


    public int[][] start() {
        showChosenQBits(qBitValuesHBox.getChildren(), qBitImagesHBox.getChildren(), 0);
        return new int[][] {qBitsValues, filtersValues};
    }


    private void prepareImages() {
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");

        positiveQBitImages = new ArrayList<>(2);
        negativeQBitImages = new ArrayList<>(2);
        positiveQBitImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton));
        negativeQBitImages.addAll(Arrays.asList(horPhoton, leftDiagPhoton));
    }


    private void getRandomQBits() {
        Random random = new Random();
        int size = qBitValuesHBox.getChildren().size();
        int bound = 2;

        qBitsValues = new int[size];
        filtersValues = new int[size];

        for (int i = 0; i < size; i++) {
            int randomValue = random.nextInt(bound);
            int randomRotation = random.nextInt(bound);
            fillQBitValues(i, randomValue, randomRotation);
        }
    }


    private void fillQBitValues(int i, int value, int rotation) {
        qBitsValues[i] = value;
        filtersValues[i] = rotation;

        ImageView qBitImageView = (ImageView) qBitImagesHBox.getChildren().get(i);
        qBitImageView.setImage(getQBitImageView(value, rotation));
    }


    private Image getQBitImageView(int value, int rotation) {
        Image image;
        if (value == 1) {
            image = positiveQBitImages.get(rotation);
        } else {
            image = negativeQBitImages.get(rotation);
        }

        return image;
    }


    private void showChosenQBits(ObservableList<Node> valuesList, ObservableList<Node> imageViewsList, int index) {
        Label valueLabel = (Label) valuesList.get(index);
        valueLabel.setText(String.valueOf(qBitsValues[index]));
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
