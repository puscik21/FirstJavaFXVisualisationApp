package grzegorz.scenes.parity;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.general.SceneDisplay;
import grzegorz.scenes.qber.QBERScene;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ParityScene {

    @FXML
    private HBox keyHBox;

    @FXML
    private AnchorPane scenePane;

    @FXML
    private Label leftLabel;

    @FXML
    private Label rightLabel;

    @FXML
    private Label hashLabel;

    private QuantumScene quantumController;
    private QBERScene qberController;
    private List<SceneDisplay> sceneDisplays;
    private List<Image> valuesImages;
    private List<Image> valuesImagesRed;
    private int[] keyValues;
    private int[] keyValuesWithError;

    private int displayCounter = 0;
    private int outsideOffset = 2000;
    private int keySize = 16;
    private int errorBitIndex;
    private boolean isDisplayToShow = true;
    private boolean isErrorBitFound = false;

    @FXML
    private void initialize() {
        sceneDisplays = new ArrayList<>();
        prepareImages();
        initiallyFillKey();
    }

    public void start(QBERScene qberController, QuantumScene quantumController) {
        this.qberController = qberController;
        this.quantumController = quantumController;
        keyValues = qberController.getKeyValues();
        prepareKeyValuesWithError();
        prepareKey();
        prepareSceneDisplays();
        initMouseEvents();
        showDisplay();
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

    private void initiallyFillKey() {
        keyHBox.getChildren().add(getVerticalSeparator());
        for (int i = 0; i < 16; i++) {
            int bitValue = 0;
            keyHBox.getChildren().add(new ImageView(valuesImages.get(bitValue)));
        }
    }

    private void prepareKeyValuesWithError() {
        errorBitIndex = qberController.getRandomBitValue(keySize);
        keyValuesWithError = new int[keySize];
        for (int i = 0; i < keySize; i++) {
            keyValuesWithError[i] = keyValues[i];
        }
        changeBit(keyValuesWithError, errorBitIndex);
    }

    private void changeBit(int[] bits, int index) {
        if (bits[index] == 1) {
            bits[index] = 0;
        } else {
            bits[index] = 1;
        }
    }

    private void prepareKey() {
        fillKey();
        keyHBox.setTranslateX(-outsideOffset);
        hashLabel.setTranslateY(scenePane.getHeight() * 0.4 * 0.5625);
    }

    private void fillKey() {
        if (keyHBox.getChildren().size() == keySize) {
            keyHBox.getChildren().add(0, getVerticalSeparator());
        }
        for (int i = 1; i < keyHBox.getChildren().size(); i++) {
            int bitValue = keyValuesWithError[i - 1];
            setBitView(i, bitValue);
        }
    }

    private void setBitView(int i, int value) {
        Node node = keyHBox.getChildren().get(i);
        if (node instanceof ImageView) {
            ImageView bitView = (ImageView) keyHBox.getChildren().get(i);
            bitView.setImage(valuesImages.get(value));
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
        addSearchParityTransition();
        addErrorBitTransition();
        addHashDialog();
    }

    private void addReceiveKeyTransition() {
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

    private void addSearchParityTransition() {
        List<Animation> animations = new LinkedList<>();
        prepareSearchParityTransition(animations, 1, keySize + 1, 0, 0); // +1 to indexes because of separator
        Animation[] animationsArray = new Animation[animations.size()];
        animationsArray = animations.toArray(animationsArray);
        SequentialTransition searchParityTransition = new SequentialTransition(animationsArray);
        sceneDisplays.add(new SceneDisplay(searchParityTransition));
    }

    private void prepareSearchParityTransition(List<Animation> animations, int from, int to, int cycle, double xOffset) {
        if (to - from <= 2 || isErrorBitFound) {
            return;
        }

        int mid = from + (to - from) / 2;
        double nextXOffset = 200 / (1 + cycle);
        animations.add(getDivideTransition(from, to, cycle, xOffset));
        cycle++;
        isErrorBitFound = checkIfErrorIsFound(from, mid, to);

        if (checkIfParityIsWrong(from - 1, mid - 1)) {
            prepareSearchParityTransition(animations, from, mid, cycle, xOffset - nextXOffset);
        }
        prepareSearchParityTransition(animations, mid, to, cycle, xOffset + nextXOffset);
    }

    private Animation getDivideTransition(int from, int to, int cycle, double xOffset) {
        int mid = from + (to - from) / 2;
        ParallelTransition takeLeftTransition = moveBitsDown(leftLabel, from, mid, -1, cycle, xOffset);
        ParallelTransition takeRightTransition = moveBitsDown(rightLabel, mid, to, 1, cycle, xOffset);
        ParallelTransition divideTransition = new ParallelTransition(takeLeftTransition, takeRightTransition);
        divideTransition.setDelay(Duration.seconds(0.5));
        return divideTransition;
    }

    private ParallelTransition moveBitsDown(Label label, int from, int to, int direction, int cycle, double xOffset) {
        double xpath = 200 / (1 + cycle);
        double yOffset = cycle * 100;
        Animation[] animations = new Animation[to - from + 1];

        for (int i = from; i < to; i++) {
            Node node = keyHBox.getChildren().get(i);
            TranslateTransition trans = getTranslateTransition(node, xOffset, yOffset, xOffset + direction * xpath, yOffset + 100);
            trans.setDuration(Duration.seconds(1.0));
            animations[i - from] = trans;
        }
        animations[to - from] = getLabelTransition(label, from, to, direction, xOffset, yOffset, xpath);
        return new ParallelTransition(animations);
    }

    private Animation getLabelTransition(Label label, int from, int to, int direction, double xOffset, double yOffset, double nodesXPath) {
        double scale = 0.5625;
        double xInParent = keyHBox.getChildren().get(from + (to - from) / 2).getBoundsInParent().getMaxX();
        double labelXOffset = (xOffset + xInParent + direction * nodesXPath) * scale;
        double labelYOffset = scale * (yOffset + 100);
        int paritySum = getSum(keyValues, from - 1, to - 1);

        FadeTransition hideTransition = getHideTransition(label, 0.001);
        hideTransition.setOnFinished(e -> {
            changeParityLabelText(label, paritySum);
            leftLabel.setVisible(true);
            rightLabel.setVisible(true);
        });
        Animation translateTransition = getTranslateTransition(label, scale * xOffset, scale * yOffset, labelXOffset, labelYOffset);
        FadeTransition showTransition = getShowTransition(label);
        return new SequentialTransition(hideTransition, translateTransition, showTransition);
    }

    private boolean checkIfErrorIsFound(int from, int mid, int to) {
        return to - mid == 2 && (checkIfParityIsWrong(from - 1, mid - 1) || checkIfParityIsWrong(mid - 1, to - 1));
    }

    private boolean checkIfParityIsWrong(int from, int to) {
        int correctSum = getSum(keyValues, from, to);
        int sum = getSum(keyValuesWithError, from, to);
        return correctSum != sum;
    }

    private int getSum(int[] values, int from, int to) {
        int sum = 0;
        for (int i = from; i < to; i++) {
            sum += values[i];
        }
        return sum;
    }

    private void changeParityLabelText(Label label, int sum) {
        if (sum % 2 == 0) {
            label.setText("0");
        } else {
            label.setText("1");
        }
    }

    private void addErrorBitTransition() {
        Animation showErrorBitTransition = getShowErrorBitTransition();
        addLeaveProperKeyTransition(showErrorBitTransition);
        sceneDisplays.add(new SceneDisplay(showErrorBitTransition));
    }

    private Animation getShowErrorBitTransition() {
        ImageView bitView = (ImageView) keyHBox.getChildren().get(errorBitIndex + 1);
        Image redImage = valuesImagesRed.get(keyValuesWithError[errorBitIndex]);

        FadeTransition hideTransition = getHideTransition(bitView, 0.001);
        hideTransition.setOnFinished(e -> bitView.setImage(redImage));
        FadeTransition showTransition = getShowTransition(bitView);
        return new SequentialTransition(hideTransition, showTransition);
    }

    private void addLeaveProperKeyTransition(Animation lastAnimation) {
        lastAnimation.setOnFinished(e -> {
            leftLabel.setVisible(false);
            rightLabel.setVisible(false);
            returnLeaveProperKeyTransition().play();
        });
    }

    private Animation returnLeaveProperKeyTransition() {
        Animation mergeBitsTransition = returnMergeBitsTransition();
        mergeBitsTransition.setOnFinished(e -> getKickErrorBitTransition().play());
        return mergeBitsTransition;
    }

    private ParallelTransition returnMergeBitsTransition() {
        Animation[] animations = new Animation[keySize];
        for (int i = 0; i < keySize; i++) {
            Node node = keyHBox.getChildren().get(i);
            double xOffset = node.getTranslateX();
            double yOffset = node.getTranslateY();
            TranslateTransition trans = getTranslateTransition(node, xOffset, yOffset, 0, scenePane.getHeight() * 0.4);
            trans.setDuration(Duration.seconds(1.0));
            animations[i] = trans;
        }
        ParallelTransition mergeBitsTransition = new ParallelTransition(animations);
        mergeBitsTransition.setDelay(Duration.seconds(1.0));
        return mergeBitsTransition;
    }

    private Animation getKickErrorBitTransition() {
        Node errorBitView = keyHBox.getChildren().get(errorBitIndex);
        Animation kickErrorBitTransition = getTranslateTransition(errorBitView, 0, scenePane.getHeight() * 0.4, 0, outsideOffset);
        kickErrorBitTransition.setOnFinished(e -> keyHBox.getChildren().remove(errorBitView));
        return kickErrorBitTransition;
    }

    private void addHashDialog() {
        JFXDialog hashDialog = quantumController.returnDialog("Some text about hash", "Title");
        EventHandler<? super JFXDialogEvent> currentEvent = hashDialog.getOnDialogClosed();
        hashDialog.setOnDialogClosed(e -> {
            currentEvent.handle(e);
            showHashFunctionTransition();
        });
        sceneDisplays.add(new SceneDisplay(hashDialog));
    }

    private void showHashFunctionTransition() {
        hashLabel.setText("Size: " + keyHBox.getChildren().size());
        Label leftLabel = createHashLabel("H( ", 0);
        Label rightLabel = createHashLabel(" ) ", keyHBox.getChildren().size());

        Animation showHashSizeLabelTransition = getShowInvisibleNodeTransition(hashLabel);
        Animation showLeftLabelTransition = getShowInvisibleNodeTransition(leftLabel);
        Animation showRightLabelTransition = getShowInvisibleNodeTransition(rightLabel);
        ParallelTransition showHashSignTransition = new ParallelTransition(showLeftLabelTransition, showRightLabelTransition, showHashSizeLabelTransition);
        Animation hashOperationTransition = getHashOperationTransition();

        SequentialTransition hashFunctionTransition = new SequentialTransition(showHashSignTransition, hashOperationTransition);
        sceneDisplays.add(new SceneDisplay(hashFunctionTransition));
    }

    private Label createHashLabel(String text, int index) {
        Label leftLabel = new Label(text);
        Font font = new Font("System Bold", 60.0);
        leftLabel.setTranslateY(scenePane.getHeight() * 0.4);
        leftLabel.setFont(font);
        leftLabel.setVisible(false);
        keyHBox.getChildren().add(index, leftLabel);
        return leftLabel;
    }

    private Animation getHashOperationTransition() {
        ParallelTransition hideTransition = getScaleWithFadeForNodesTransition(1.0, 0.0, 0.75, keyHBox, hashLabel);
        hideTransition.setOnFinished(e -> removeBitsForHashOperation());
        ParallelTransition showTransition = getScaleWithFadeForNodesTransition(0.0, 1.0, 0.75, keyHBox, hashLabel);
        SequentialTransition hashOperationTransition = new SequentialTransition(hideTransition, showTransition);
        hashOperationTransition.setDelay(Duration.seconds(0.5));
        return hashOperationTransition;
    }

    private void removeBitsForHashOperation() {
        for (int i = 0; i < 3; i++) {
            int size = keyHBox.getChildren().size() - 2;
            int index = qberController.getRandomBitValue(size) + 1;
            if (index == size) {
                index--;
            }
            keyHBox.getChildren().remove(index);
        }
        hashLabel.setText("Size: " + (keyHBox.getChildren().size() - 2));
    }

    private ParallelTransition getScaleWithFadeForNodesTransition(double from, double to, double time, Node... nodes) {
        int size = nodes.length;
        Animation[] animations = new Animation[2 * size];
        for (int i = 0; i < size; i++) {
            Node node = nodes[i];
            ScaleTransition scaleTransition = getScaleTransition(node, from, to, time);
            FadeTransition fadeTransition = getFadeTransition(node, from, to, time);
            animations[2 * i] = scaleTransition;
            animations[2 * i + 1] = fadeTransition;
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
            playAnimation(animation);
        } else if (sceneDisplay.getState().equals("cAnimation")) {
            Animation animation = sceneDisplay.getCAnimation().getAnimation();
            playAnimation(animation);
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

    private void playAnimation(Animation animation) {
        EventHandler<ActionEvent> currentEvent = animation.getOnFinished();
        animation.setOnFinished(e -> {
            if (currentEvent != null) {
                currentEvent.handle(e);
            }
            isDisplayToShow = true;
        });
        animation.play();
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

    private ScaleTransition getScaleTransition(Node node, double from, double to, double time) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDuration(Duration.seconds(time));
        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setToX(to);
        scaleTransition.setToY(to);
        return scaleTransition;
    }

    private SequentialTransition getShowInvisibleNodeTransition(Node node) {
        FadeTransition hideTransition = getHideTransition(node, 0.001);
        hideTransition.setOnFinished(e -> node.setVisible(true));
        FadeTransition showTransition = getShowTransition(node);
        return new SequentialTransition(hideTransition, showTransition);
    }

    private FadeTransition getHideTransition(Node node, double time) {
        return getFadeTransition(node, 1.0, 0.0, time);
    }

    private FadeTransition getShowTransition(Node node) {
        return getFadeTransition(node, 0.0, 1.0, 0.5);
    }

    private FadeTransition getFadeTransition(Node node, double from, double to, double time) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(time));
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        return fadeTransition;
    }
}