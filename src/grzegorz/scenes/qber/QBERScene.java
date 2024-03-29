package grzegorz.scenes.qber;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.general.Animator;
import grzegorz.general.SceneDisplay;
import grzegorz.scenes.parity.ParityScene;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.io.IOException;
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

    private final int PARITY_TAB = 9;

    private QuantumScene quantumController;
    private JFXTabPane tabPane;
    private ChangeListener<? super Number> listener;
    private List<SceneDisplay> sceneDisplays;
    private List<Image> valuesImages;
    private List<Image> valuesImagesRed;
    private int[] keyValues;
    private Random generator;

    private int displayCounter = 0;
    private int outsideOffset = 2000;
    private int errorBitIndex;
    private boolean isDisplayToShow = true;


    @FXML
    private void initialize() {
        sceneDisplays = new ArrayList<>();
        keyValues = new int[16];
        generator = new Random();
        receivedKeyHBox.setTranslateX(-outsideOffset);
        prepareKeys();
    }
    
    public void start(QuantumScene quantumController) {
        this.quantumController = quantumController;
        this.tabPane = quantumController.getTabPane();
        initMainTabPane();
        prepareSceneDisplays();
        initMouseEvents();
    }

    public int[] getKeyValues() {
        return keyValues;
    }

    public int getRandomBitValue(int bound) {
        return generator.nextInt(bound);
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
            } else {
                keyValues[i - 16] = bitValue;
            }
        }
    }

    private void spoilOneBit() {
        errorBitIndex = getRandomBitValue(16);
        ImageView errorBitView = (ImageView) receivedKeyHBox.getChildren().get(errorBitIndex);
        int bitValue = keyValues[errorBitIndex];
        changeBitImage(errorBitView, bitValue);
    }

    private void changeBitImage(ImageView imgView, int bitValue) {
        if (bitValue == 0) {
            imgView.setImage(valuesImages.get(0));
        } else {
            imgView.setImage(valuesImages.get(1));
        }
    }

    private void initMainTabPane() {
        addParityTab();
        FXMLLoader explanationLoader = loadToParityTab();
        ParityScene parityController = explanationLoader.getController();

        listener = getParityTabListener(parityController);
        tabPane.getSelectionModel().selectedIndexProperty().addListener(listener);
        tabPane.getSelectionModel().selectedIndexProperty().addListener(quantumController.getExplanationTabListener());
    }

    private void addParityTab() {
        if (tabPane.getTabs().size() < PARITY_TAB + 1) {
            Tab parityTab = new Tab("Key reconciliation");
            tabPane.getTabs().add(parityTab);
        }
    }

    private FXMLLoader loadToParityTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../parity/parityScene.fxml"));
            Pane body = loader.load();
            tabPane.getTabs().get(PARITY_TAB).setContent(body);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ChangeListener<? super Number> getParityTabListener(ParityScene parityController) {
        return (ChangeListener<Number>) (observable, oldVal, newVal) -> {
            if (newVal.intValue() == PARITY_TAB) {
                parityController.start(this, quantumController);
            }
        };
    }

    private void prepareSceneDisplays() {
        addIntroductionDialog();
        addCompareKeysTransition();
        addQBERDialog();
        addResultTransition();
    }

    private void addIntroductionDialog() {
        JFXDialog dialog = quantumController.returnDialog("Some info", "Title");
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
        JFXDialog dialog = quantumController.returnDialog("Search for errors and measure Quantum Bit Error Rate");
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

    private TranslateTransition getTranslateTransition(Node node, double fromX, double fromY, double toX, double toY) {
        return Animator.getTranslateTransition(node, fromX, fromY, toX, toY, 0.5);
    }

    private FadeTransition getShowTransition(Node node) {
        return Animator.getFadeTransition(node, 0.0, 1.0, 0.5);
    }
}
