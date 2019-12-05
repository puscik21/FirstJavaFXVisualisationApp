package grzegorz.scenes.parity;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.general.SceneDisplay;
import grzegorz.scenes.introduction.IntroductionScene;
import grzegorz.scenes.qber.QBERScene;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParityScene {

    @FXML
    private HBox keyHBox;

    @FXML
    private AnchorPane scenePane;

    private IntroductionScene introductionController;
    private JFXTabPane tabPane;
    private List<SceneDisplay> sceneDisplays;
    private List<Image> valuesImages;
    private List<Image> valuesImagesRed;
    private int[] keyValues;

    private int displayCounter = 0;
    private int outsideOffset = 2000;
    private boolean isDisplayToShow = true;


    @FXML
    private void initialize() {
        sceneDisplays = new ArrayList<>();
    }

    public void start(QBERScene qberController, IntroductionScene introductionController) {
        this.introductionController = introductionController;
        this.tabPane = introductionController.getTabPane();
        keyValues = qberController.getKeyValues();
        prepareKey();
        prepareSceneDisplays();
        initMouseEvents();

        showDisplay();
    }

    private void prepareKey() {
        prepareImages();
        fillKey();
        keyHBox.setTranslateX(-outsideOffset);
    }

    private void prepareImages() {
        Image zeroIcon = new Image("grzegorz\\images\\zeroIcon.png");
        Image oneIcon = new Image("grzegorz\\images\\oneIcon.png");
        Image zeroIconRed = new Image("grzegorz\\images\\zeroIconRed.png");
        Image oneIconRed = new Image("grzegorz\\images\\oneIconRed.png");
        valuesImages = new ArrayList<>(2);
        valuesImagesRed = new ArrayList<>(2);
        valuesImages.addAll(Arrays.asList(zeroIcon, oneIcon));
        valuesImagesRed.addAll(Arrays.asList(zeroIconRed, oneIconRed));
    }

    private void fillKey() {
        keyHBox.getChildren().add(getVerticalSeparator());
        for (int i = 0; i < 16; i++) {
            int bitValue = keyValues[i];
            keyHBox.getChildren().add(new ImageView(valuesImages.get(bitValue)));
        }
    }

    private Separator getVerticalSeparator() {
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefHeight(200);
        separator.setStyle("-fx-background-color: red;");
        return separator;
    }

    private void prepareSceneDisplays() {
        addReceiveKeyTransition();
        addSearchParityTransition(1, 17, 0, 0); // +1 to indexes because of separator
    }

    private void addReceiveKeyTransition(){
        Animation comingKeyTransition = getComingKeyTransition();
        Animation throwOutSeparatorTransition = getThrowOutSeparatorTransition();
        SequentialTransition receiveKeyTransition = new SequentialTransition(comingKeyTransition, throwOutSeparatorTransition);
        sceneDisplays.add(new SceneDisplay(receiveKeyTransition));
    }

    private Animation getComingKeyTransition() {
        TranslateTransition moveTransition = getTranslateTransition(keyHBox, -outsideOffset, 0, 0, 0);
        moveTransition.setDelay(Duration.seconds(1.0));
        moveTransition.setDuration(Duration.seconds(1.0));
        moveTransition.setInterpolator(Interpolator.EASE_OUT);
        return moveTransition;
    }

    private Animation getThrowOutSeparatorTransition() {
        Node separator = keyHBox.getChildren().get(0);
        TranslateTransition moveTransition = getTranslateTransition(separator, 0, 0, 0, outsideOffset);
        moveTransition.setDelay(Duration.seconds(0.5));
        moveTransition.setDuration(Duration.seconds(1.0));
        moveTransition.setInterpolator(Interpolator.EASE_IN);
        moveTransition.setOnFinished(e -> keyHBox.getChildren().remove(separator));
        return moveTransition;
    }

    private void addSearchParityTransition(int from, int to, int cycle, double xOffset) {
        if (to - from <= 2) {
            return;
        }

        int mid = from +  (to - from) / 2;
        ParallelTransition takeLeftTransition = moveLeftSideDown(keyHBox, from, mid, -1, cycle, xOffset);
        ParallelTransition takeRightTransition = moveRightSideDown(keyHBox, mid, to, 1, cycle, xOffset);
        ParallelTransition divideTransition = new ParallelTransition(takeLeftTransition, takeRightTransition);
        sceneDisplays.add(new SceneDisplay(divideTransition));

        double nextXOffset = 200 / (1 + cycle);
        cycle++;
        addSearchParityTransition(from, mid, cycle, xOffset - nextXOffset);
        addSearchParityTransition(mid, to, cycle, xOffset + nextXOffset);
    }

    private ParallelTransition moveLeftSideDown(HBox hbox, int from, int to, int direction, int cycle, double xOffset) {
        double xpath = 200 / (1 + cycle);
        double yOffset = cycle * 100;

        Animation[] animations = new Animation[to - from];
        for (int i = from; i < to; i++) {
            Node node = hbox.getChildren().get(i);
            TranslateTransition trans = getTranslateTransition(node, xOffset, yOffset, xOffset + direction * xpath, yOffset + 100);
            trans.setDuration(Duration.seconds(1.5));
            animations[i - from] = trans;
        }
        return new ParallelTransition(animations);
    }

    private ParallelTransition moveRightSideDown(HBox hbox, int from, int to, int direction, int cycle, double xOffset) {
        double xpath = 200 / (1 + cycle);
        double yOffset = cycle * 100;

        Animation[] animations = new Animation[to - from];
        for (int i = from; i < to; i++) {
            Node node = hbox.getChildren().get(i);
            TranslateTransition trans = getTranslateTransition(node, xOffset, yOffset, xOffset + direction * xpath, yOffset + 100);
            trans.setDuration(Duration.seconds(1.5));
            animations[i - from] = trans;
        }
        return new ParallelTransition(animations);
    }

    private void initMouseEvents() {
        scenePane.setOnMouseClicked(e -> {
            if (isDisplayToShow && displayCounter < sceneDisplays.size() && e.getButton() == MouseButton.PRIMARY) {
                showDisplay();
            }
        });
    }

    private void showDisplay() {
        SceneDisplay sceneDisplay = sceneDisplays.get(displayCounter);
        useSceneDisplay(sceneDisplay);
        displayCounter++;
        isDisplayToShow = false;
    }

    private void useSceneDisplay(SceneDisplay sceneDisplay) {
        if (sceneDisplay.getState().equals("animation")) {
            Animation animation = sceneDisplay.getAnimation();
            animation.setOnFinished(e -> isDisplayToShow = true);
            animation.play();
        } else if (sceneDisplay.getState().equals("cAnimation")) {
            Animation animation = sceneDisplay.getCAnimation().getAnimation();
            animation.setOnFinished(e -> isDisplayToShow = true);
            animation.play();
        } else {
            JFXDialog dialog = sceneDisplay.getDialog();
            EventHandler<? super JFXDialogEvent> currentEvent = dialog.getOnDialogClosed();
            dialog.setOnDialogClosed(e -> {
                currentEvent.handle(e);
                isDisplayToShow = true;
            });
            dialog.show();
        }
    }

    private TranslateTransition getTranslateTransition(Node imageView, double fromX, double fromY, double toX, double toY) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.5));
        transition.setNode(imageView);
        transition.setFromX(fromX);
        transition.setFromY(fromY);
        transition.setToX(toX);
        transition.setToY(toY);

        return transition;
    }

    private FadeTransition getShowTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        return fadeTransition;
    }
}