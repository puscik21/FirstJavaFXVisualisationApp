package grzegorz.scenes.filtersCheck;

import grzegorz.general.Animator;
import grzegorz.general.QBitState;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class FiltersCheckScene {
    @FXML
    private AnchorPane comparePane;

    @FXML
    private VBox filtersVBox;

    @FXML
    private VBox qBitsVBox;

    @FXML
    private VBox ticksVBox;

    @FXML
    private Label sceneTitle;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;
    private ArrayList<Image> valuesImages;
    private int[] bobFilters;
    private QBitState[] aliceQBitStates;

    private QuantumScene parentController;
    private DropShadow borderGlow;

    private double sizeScale = 1.0;
    private double timeScale = 4.0;


    public void start(QuantumScene parentController, int[] bobFilters, QBitState[] aliceQBitStates) {
        this.parentController = parentController;
        this.bobFilters = bobFilters;
        this.aliceQBitStates = aliceQBitStates;

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
        Image xIcon = new Image("grzegorz\\images\\xIcon.png");

        photonImages = new ArrayList<>(4);
        filterImages = new ArrayList<>(2);
        valuesImages = new ArrayList<>(3);
        photonImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
        valuesImages.addAll(Arrays.asList(zeroIcon, oneIcon, tickIcon, xIcon));
    }

    private void addImageViews() {
        int quantity = bobFilters.length;
        for (int i = 0; i < quantity; i++) {
            filtersVBox.getChildren().add(new ImageView(filterImages.get(0)));
            qBitsVBox.getChildren().add(new ImageView(photonImages.get(0)));
            addValueImageView(i);
        }
    }

    private void addValueImageView(int i) {
        ImageView valView;
        if (aliceQBitStates[i].isFilterWrong(bobFilters[i])) {
            valView = new ImageView(valuesImages.get(3));
        } else {
            valView = new ImageView(valuesImages.get(2));
        }
        valView.setVisible(false);
        ticksVBox.getChildren().add(valView);
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
            int imageNumber = aliceQBitStates[i].getState();
            ImageView imageView = (ImageView) qBitsVBox.getChildren().get(i);
            imageView.setImage(photonImages.get(imageNumber));
        }
    }

    private void prepareFiltersHBox() {
        for (int i = 0; i < filtersVBox.getChildren().size(); i++) {
            int imageNumber = bobFilters[i];
            ImageView imageView = (ImageView) filtersVBox.getChildren().get(i);
            imageView.setImage(filterImages.get(imageNumber));
        }
    }

    private void initCommentDialogs() {
        initBorderGlowEffectInstance();
        initCommentForNode(filtersVBox, "Comment");
        initCommentForNode(ticksVBox, "Comment");
        initCommentForNode(qBitsVBox, "Comment");
    }

    private void initBorderGlowEffectInstance() {
        borderGlow = Animator.getHighlightEffect();
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
        int size = qBitsVBox.getChildren().size();
        ArrayList<Animation> transitions = new ArrayList<>();
        ArrayList<Integer> indexesOfCorrect = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            if (!aliceQBitStates[i].isFilterWrong(bobFilters[i])) {
                indexesOfCorrect.add(i);
            }
            Node node = ticksVBox.getChildren().get(i);
            transitions.add(getShowInvisibleIconTransition(node));
        }
        Animation[] transArray = new Animation[transitions.size()];
        transArray = transitions.toArray(transArray);
        SequentialTransition showTicksTransition = new SequentialTransition(transArray);
        SequentialTransition showNumbersTransition = getTicksToNumbersTransition(indexesOfCorrect);
        Animation removeWrongRowsTransition = getRemoveWrongRows(indexesOfCorrect);

        return new SequentialTransition(showTicksTransition, showNumbersTransition, removeWrongRowsTransition);
    }

    private SequentialTransition getShowInvisibleIconTransition(Node node) {
        FadeTransition hideTrans = getFadeTransition(node, 1.0, 0.0, 0.001);
        hideTrans.setOnFinished(e -> node.setVisible(true));
        FadeTransition showTrans = getFadeTransition(node, 0.0, 1.0, 2 * timeScale);
        return new SequentialTransition(hideTrans, showTrans);
    }

    private SequentialTransition getTicksToNumbersTransition(ArrayList<Integer> indexes) {
        Animation[] transitions = new Animation[indexes.size()];
        int counter = 0;
        for (int i : indexes) {
            int qBitVal = aliceQBitStates[i].getValue();
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

    private Animation getRemoveWrongRows(ArrayList<Integer> indexesOfCorrect) {
        List<Animation> animationList = new LinkedList<>();
        int dir = 1;

        for (int i = 0; i < qBitsVBox.getChildren().size(); i++) {
            if (indexesOfCorrect.contains(i)) {
                continue;
            }
            animationList.add(getMoveOutsideTrans(i, dir));
            dir *= -1;
        }
        Animation[] animations = new Animation[animationList.size()];
        animations = animationList.toArray(animations);
        return new ParallelTransition(animations);
    }

    private Animation getMoveOutsideTrans(int i, int dir) {
        double outsideOffset = 2500;

        ObservableList<Node> qBits = qBitsVBox.getChildren();
        ObservableList<Node> ticks = ticksVBox.getChildren();
        ObservableList<Node> filters = filtersVBox.getChildren();

        Node qBit = qBits.get(i);
        Node tick = ticks.get(i);
        Node filter = filters.get(i);

        Animation qBitTrans = getTranslateTransitionAndRemove(qBit, qBits, 0, 0, dir * outsideOffset, 0);
        Animation tickTrans = getTranslateTransitionAndRemove(tick, ticks, 0, 0, dir * outsideOffset, 0);
        Animation filterTrans = getTranslateTransitionAndRemove(filter, filters, 0, 0, dir * outsideOffset, 0);
        ParallelTransition moveOutsideTrans = new ParallelTransition(qBitTrans, tickTrans, filterTrans);
        moveOutsideTrans.setDelay(Duration.seconds(0.15 * i));
        return moveOutsideTrans;
    }

    private FadeTransition getFadeTransition(Node node, double fromVal, double toVal, double duration) {
        return Animator.getFadeTransition(node, fromVal, toVal, duration);
    }

    private TranslateTransition getTranslateTransitionAndRemove(Node node, ObservableList<Node> parent, double fromX, double fromY, double toX, double toY) {
        TranslateTransition transition = getTranslateTransition(node, fromX, fromY, toX, toY);
        transition.setOnFinished(e -> parent.remove(node));
        return transition;
    }

    private TranslateTransition getTranslateTransition(Node node, double fromX, double fromY, double toX, double toY) {
        return Animator.getTranslateTransition(node, fromX, fromY, toX, toY, 1.5);
    }
}