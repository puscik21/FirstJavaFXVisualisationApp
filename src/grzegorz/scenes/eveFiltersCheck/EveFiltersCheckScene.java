package grzegorz.scenes.eveFiltersCheck;

import com.jfoenix.controls.JFXTabPane;
import grzegorz.QBitState;
import grzegorz.scenes.introduction.IntroductionScene;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class EveFiltersCheckScene {
    @FXML
    private AnchorPane comparePane;

    @FXML
    private VBox aliceValuesVBox;

    @FXML
    private VBox filtersVBox;

    @FXML
    private VBox ticksVBox;

    @FXML
    private VBox qBitsVBox;

    @FXML
    private VBox bobValuesVBox;

    @FXML
    private Label sceneTitle;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;
    private ArrayList<Image> valuesImages;
    private int[] aliceQBitsValuesAfterEve;
    private int[] aliceFilters;
    private QBitState[] bobQBitStates;

    private QuantumScene parentController;
    private DropShadow borderGlow;

    private double sizeScale = 1.0;
    private double timeScale = 4.0;

    // TODO: 20.11.2019 what to do when there is no key in result - "X" icon meaning wrong filter, then disappear when changing to 1 or 0
    // TODO: 22.11.2019 check incorrect, but when everyone matches write some info

    public void start(QuantumScene parentController, int[] aliceFilters, QBitState[] bobQBitStates, int[] aliceQBitsValuesAfterEve) {
        this.parentController = parentController;
        this.aliceFilters = aliceFilters;
        this.bobQBitStates = bobQBitStates;
        this.aliceQBitsValuesAfterEve = aliceQBitsValuesAfterEve;

        prepareScene();
        scheduleAnimationStart();
    }

    private void prepareScene() {
        prepareImages();
        addImageViews();
        scaleComparePane();
        prepareQBitsHBox();
        prepareFiltersHBox();
        initCommentDialogs();
    }

    private void prepareImages() {
        Image whiteFilter = new Image("grzegorz\\images\\whiteFilter.png");
        Image greenFilter = new Image("grzegorz\\images\\greenFilter.png");

        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");

        Image zeroIcon = new Image("grzegorz\\images\\zeroIcon.png");
        Image oneIcon = new Image("grzegorz\\images\\oneIcon.png");
        Image tickIcon = new Image("grzegorz\\images\\tickIcon.png");

        photonImages = new ArrayList<>(4);
        filterImages = new ArrayList<>(2);
        valuesImages = new ArrayList<>(3);
        photonImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
        valuesImages.addAll(Arrays.asList(zeroIcon, oneIcon, tickIcon));
    }

    private void addImageViews() {
        int quantity = aliceFilters.length;
        for (int i = 0; i < quantity; i++) {
            int aliceValueAfterEve = aliceQBitsValuesAfterEve[i];
            int bobValue = bobQBitStates[i].getValue();
            aliceValuesVBox.getChildren().add(new ImageView(valuesImages.get(aliceValueAfterEve)));
            bobValuesVBox.getChildren().add(new ImageView(valuesImages.get(bobValue)));
            filtersVBox.getChildren().add(new ImageView(filterImages.get(0)));
            qBitsVBox.getChildren().add(new ImageView(photonImages.get(0)));
            ImageView invisibleTick = new ImageView(valuesImages.get(2));
            invisibleTick.setVisible(false);
            ticksVBox.getChildren().add(invisibleTick);
        }
    }

    private void scaleComparePane() {
        prepareScaleValues();
        comparePane.setScaleX(sizeScale);
        comparePane.setScaleY(sizeScale);
    }

    private void prepareScaleValues() {
        int size = qBitsVBox.getChildren().size();
        if (size == 0) {
            return;
        }

        setSizeScale(size);
        setTimeScale(size);
    }

    private void setSizeScale(double size) {
        double multiplier = 7.0 / size;
        sizeScale *= multiplier;

        if (sizeScale < 0.3) {
            sizeScale = 0.3;
        } else if (sizeScale > 1.0) {
            sizeScale = 1.0;
        }
    }

    private void setTimeScale(double size) {
        timeScale = timeScale / size;
        if (timeScale > 0.5) {
            timeScale = 0.5;
        }
    }

    private void prepareQBitsHBox() {
        for (int i = 0; i < qBitsVBox.getChildren().size(); i++) {
            int imageNumber = bobQBitStates[i].getState();
            ImageView imageView = (ImageView) qBitsVBox.getChildren().get(i);
            imageView.setImage(photonImages.get(imageNumber));
        }
    }

    private void prepareFiltersHBox() {
        for (int i = 0; i < filtersVBox.getChildren().size(); i++) {
            int imageNumber = aliceFilters[i];
            ImageView imageView = (ImageView) filtersVBox.getChildren().get(i);
            imageView.setImage(filterImages.get(imageNumber));
        }
    }

    private void initCommentDialogs() {
        initBorderGlowEffectInstance();
        initCommentForNode(aliceValuesVBox, "Comment");
        initCommentForNode(filtersVBox, "Comment");
        initCommentForNode(ticksVBox, "Comment");
        initCommentForNode(qBitsVBox, "Comment");
        initCommentForNode(bobValuesVBox, "Comment");
    }

    private void initBorderGlowEffectInstance() {
        borderGlow = new DropShadow();
        borderGlow.setColor(Color.WHITESMOKE);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        borderGlow.setHeight(50);
        borderGlow.setWidth(50);
    }

    private void initCommentForNode(Node node, String comment) {
        node.setOnMouseClicked(e -> setCommentOnSecondaryButton(e.getButton(), comment));
        setBorderGlowEffect(node);
    }

    private void setCommentOnSecondaryButton(MouseButton button, String comment) {
        if (button == MouseButton.SECONDARY) {
            parentController.returnDialog(comment).show();
        }
    }

    private void setBorderGlowEffect(Node node) {
        node.setOnMouseEntered(e -> node.setEffect(borderGlow));
        node.setOnMouseExited(e -> node.setEffect(null));
    }

    private void scheduleAnimationStart() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                prepareWholeAnimation().play();
            }
        };
        Timer timer = new Timer();
        long delay = 1500L;
        timer.schedule(task, delay);
    }

    private SequentialTransition prepareWholeAnimation() {
        int size = ticksVBox.getChildren().size();
        ArrayList<Animation> transitions = new ArrayList<>();
        ArrayList<Integer> indexesOfCorrect = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (bobQBitStates[i].isFilterWrong(aliceFilters[i])) {
                continue;
            }
            indexesOfCorrect.add(i);
            Node node = ticksVBox.getChildren().get(i);
            node.setVisible(true);
            transitions.add(getFadeTransition(node, 0.0, 1.0, 2 * timeScale));
        }
        Animation[] transArray = new Animation[transitions.size()];
        transArray = transitions.toArray(transArray);
        SequentialTransition showTicksTransition = new SequentialTransition(transArray);
        SequentialTransition showNumbersTransition = getTicksToNumbersTransition(indexesOfCorrect);
        return new SequentialTransition(showTicksTransition, showNumbersTransition);
    }

    private SequentialTransition getTicksToNumbersTransition(ArrayList<Integer> indexes) {
        Animation[] transitions = new Animation[indexes.size()];
        int counter = 0;
        for (int i : indexes) {
            int qBitVal = bobQBitStates[i].getValue();
            ImageView node = (ImageView) ticksVBox.getChildren().get(i);
            SequentialTransition transition = getChangeNumberTransition(node, qBitVal);
            transitions[counter] = transition;
            counter++;
        }
        return new SequentialTransition(transitions);
    }

    private SequentialTransition getChangeNumberTransition(ImageView node, int qBitVal) {
        FadeTransition hideTransition = getFadeTransition(node, 1.0, 0.0, timeScale);
        hideTransition.setOnFinished(e -> changeNumberImage(node, qBitVal));
        FadeTransition showTransition = getFadeTransition(node, 0.0, 1.0, timeScale);

        return new SequentialTransition(hideTransition, showTransition);
    }

    private void changeNumberImage(ImageView node, int qBitVal) {
        node.setImage(valuesImages.get(qBitVal));
    }

    private FadeTransition getFadeTransition(Node node, double fromVal, double toVal, double time) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(time));
        fadeTransition.setFromValue(fromVal);
        fadeTransition.setToValue(toVal);
        return fadeTransition;
    }
}