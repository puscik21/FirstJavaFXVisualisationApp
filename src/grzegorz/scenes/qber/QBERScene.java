package grzegorz.scenes.qber;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.SceneDisplay;
import grzegorz.scenes.introduction.IntroductionScene;
import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QBERScene {
    @FXML
    private AnchorPane scenePane;

    @FXML
    private HBox keyHBox;

    @FXML
    private HBox receivedKeyHBox;

    @FXML
    private Label qberLabel;

    @FXML
    private Label operationLabel;

    @FXML
    private Label resultLabel;

    private IntroductionScene introductionController;
    private List<SceneDisplay> sceneDisplays;
    private List<Image> valuesImages;
    private List<Image> valuesImagesRed;
    private Random generator;

    private int displayCounter = 0;
    private int outsideOffset = 2000;
    private int errorBitIndex;
    private boolean isDisplayToShow = true;


    @FXML
    private void initialize() {
        sceneDisplays = new ArrayList<>();
        generator = new Random();
        receivedKeyHBox.setTranslateX(-outsideOffset);
        prepareKeys();
    }

    public void start(IntroductionScene introductionController) {
        this.introductionController = introductionController;
        prepareSceneDisplays();
        initMouseEvents();
    }

    private void prepareKeys() {
        prepareImages();
        fillKeys();
        spoilOneBit();
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

    private void fillKeys() {
        for (int i = 0; i < 32; i++) {
            int bitValue = getRandomBitValue(2);
            keyHBox.getChildren().add(new ImageView(valuesImages.get(bitValue)));
            if (i < 16) {
                receivedKeyHBox.getChildren().add(new ImageView(valuesImages.get(bitValue)));
            }
        }
    }

    private void spoilOneBit() {
        errorBitIndex = getRandomBitValue(16);
        ImageView errorBitView = (ImageView) receivedKeyHBox.getChildren().get(errorBitIndex);
        changeBitImage(errorBitView);
    }

    private int getRandomBitValue(int bound) {
        return generator.nextInt(bound);
    }

    private void changeBitImage(ImageView imgView) {
        if (imgView.getImage() == valuesImages.get(0)) {
            imgView.setImage(valuesImages.get(1));
        } else {
            imgView.setImage(valuesImages.get(0));
        }
    }

    private void prepareSceneDisplays() {
        addIntroductionDialog();
        addCompareKeysTransition();
        addQBERDialog();
        addResultTransition();
    }

    private void addIntroductionDialog() {
        JFXDialog dialog = introductionController.returnDialog("Some info", "Title");
        sceneDisplays.add(new SceneDisplay(dialog));
    }

    private Animation addSeparatorTransition() {
        Separator separator = getVerticalSeparator();
        separator.setTranslateY(-outsideOffset);
        keyHBox.getChildren().add(keyHBox.getChildren().size() / 2, separator);
        TranslateTransition separatorTransition = getTranslateTransition(separator, 0, -outsideOffset, 0, 0);
        separatorTransition.setInterpolator(Interpolator.EASE_OUT);
        separatorTransition.setDuration(Duration.seconds(1.0));
        return separatorTransition;
    }

    private Separator getVerticalSeparator() {
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setMinHeight(200);
        separator.setStyle("-fx-background-color: red;");
        return separator;
    }

    private void addCompareKeysTransition() {
        Animation separatorTransition = addSeparatorTransition();
        Animation takeHalfOfKeyTransition = addTakeHalfOfKeyTransition();
        Animation alignKeysTransition = addAlignKeysTransition();
        SequentialTransition compareKeysTransition = new SequentialTransition(separatorTransition, takeHalfOfKeyTransition, alignKeysTransition);
        sceneDisplays.add(new SceneDisplay(compareKeysTransition));
    }

    private Animation addTakeHalfOfKeyTransition() {
        int firstHalfQuantity = keyHBox.getChildren().size() / 2;
        int secondHalfQuantity = keyHBox.getChildren().size() - keyHBox.getChildren().size() / 2;
        Animation moveNodesDownTransition = moveNodesDown(keyHBox, firstHalfQuantity);
        Animation moveRestOfKeyAwayTransition = moveRestOfKeyAway(keyHBox, secondHalfQuantity);

        Animation takeHalfOfKeyTransition = new ParallelTransition(moveNodesDownTransition, moveRestOfKeyAwayTransition);
        takeHalfOfKeyTransition.setDelay(Duration.seconds(0.75));
        return takeHalfOfKeyTransition;
    }

    private ParallelTransition moveNodesDown(HBox hbox, int quantity) {
        Animation[] animations = new Animation[quantity];
        for (int i = 0; i < quantity; i++) {
            Node node = hbox.getChildren().get(i);
            animations[i] = getTranslateTransition(node, 0, 0, 0, 200);
        }
        return new ParallelTransition(animations);
    }

    private ParallelTransition moveRestOfKeyAway(HBox hbox, int quantity){
        int last = hbox.getChildren().size() - 1;
        Animation[] animations = new Animation[quantity];
        for (int i = 0; i < quantity; i++) {
            Node node = hbox.getChildren().get(last - i);
            TranslateTransition moveTransition = getTranslateTransition(node, 0, 0, outsideOffset, 0);
            moveTransition.setDuration(Duration.seconds(1));
            moveTransition.setInterpolator(Interpolator.EASE_IN);
            animations[i] = moveTransition;
        }
        return new ParallelTransition(animations);
    }

    private Animation addAlignKeysTransition() {
        double keyHBoxWidth = keyHBox.getChildren().size() * ((ImageView)keyHBox.getChildren().get(0)).getImage().getWidth();
        double offset = 0.25 * keyHBoxWidth;

        TranslateTransition firstKeyTrans = getTranslateTransition(keyHBox, 0, 0, offset, 0);
        TranslateTransition receiveKeyTrans = getTranslateTransition(receivedKeyHBox, -outsideOffset, 0, offset, 0);
        firstKeyTrans.setDuration(Duration.seconds(1.0));
        receiveKeyTrans.setDuration(Duration.seconds(1.0));

        return new ParallelTransition(firstKeyTrans, receiveKeyTrans);
    }

    private void addQBERDialog() {
        JFXDialog dialog = introductionController.returnDialog("Search for errors and measure Quantum Bit Error Rate");
        EventHandler<? super JFXDialogEvent> currentEvent = dialog.getOnDialogClosed();
        dialog.setOnDialogClosed(e -> {
            currentEvent.handle(e);
            highlightErrorBits();
        });
        sceneDisplays.add(new SceneDisplay(dialog));
    }

    private void highlightErrorBits() {
        highlightBitInKey(keyHBox);
        highlightBitInKey(receivedKeyHBox);
    }

    private void highlightBitInKey(HBox keyHBox) {
        ImageView bit = (ImageView) keyHBox.getChildren().get(errorBitIndex);
        changeToRedImage(bit);
    }

    private void changeToRedImage(ImageView imgView) {
        if (imgView.getImage() == valuesImages.get(0)) {
            imgView.setImage(valuesImagesRed.get(0));
        } else {
            imgView.setImage(valuesImagesRed.get(1));
        }
    }

    private void addResultTransition() {
        Label[] resultLabels = new Label[] {qberLabel, operationLabel, resultLabel};
        Animation[] transitions = new Animation[resultLabels.length];
        for (int i = 0; i < resultLabels.length; i++) {
            Label label = resultLabels[i];
            transitions[i] = getDelayedShowLabelTransition(label, i);
        }
        ParallelTransition resultTransition = new ParallelTransition(transitions);
        sceneDisplays.add(new SceneDisplay(resultTransition));
    }

    private SequentialTransition getDelayedShowLabelTransition(Label label, double delay) {
        FadeTransition hideTransition = getShowTransition(label);
        hideTransition.setDelay(Duration.seconds(delay));
        hideTransition.setDuration(Duration.seconds(0.001));
        hideTransition.setOnFinished(e -> label.setVisible(true));
        FadeTransition showTransition = getShowTransition(label);
        return new SequentialTransition(hideTransition, showTransition);
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
