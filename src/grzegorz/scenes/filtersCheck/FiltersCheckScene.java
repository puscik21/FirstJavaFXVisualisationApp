package grzegorz.scenes.filtersCheck;

import grzegorz.QBitState;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
    private int[] aliceFilters;
    private QBitState[] bobQBitStates;

    private double sizeScale = 1.0;
    private double timeScale = 4.0;

    private final String TICK_ICON_PATH = "grzegorz\\images\\tickIcon.png";
    private final String ONE_ICON_PATH = "grzegorz\\images\\oneIcon.png";
    private final String ZERO_ICON_PATH = "grzegorz\\images\\zeroIcon.png";


    // TODO: 16.11.2019 comments on right click
    // TODO: 17.11.2019 scene title
    // TODO: 20.11.2019 glow effect
    // TODO: 20.11.2019 what to do when there is no key in result - "X" icon meaning wrong filter, then disappear when changing to 1 or 0

    public void start(int[] aliceFilters, QBitState[] bobQBitStates) {
        this.aliceFilters = aliceFilters;
        this.bobQBitStates = bobQBitStates;

        prepareScene();
        scheduleAnimationStart();
    }

    private void prepareScene() {
        prepareImages();
        addImageViews();
        scaleComparePane();
        prepareQBitsHBox();
        prepareFiltersHBox();
    }

    private void prepareImages() {
        Image whiteFilter = new Image("grzegorz\\images\\whiteFilter.png");
        Image greenFilter = new Image("grzegorz\\images\\greenFilter.png");
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");

        photonImages = new ArrayList<>(4);
        filterImages = new ArrayList<>(2);
        photonImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
    }

    private void addImageViews() {
        int quantity = aliceFilters.length;
        for (int i = 0; i < quantity; i++) {
            filtersVBox.getChildren().add(new ImageView(filterImages.get(0)));
            qBitsVBox.getChildren().add(new ImageView(photonImages.get(0)));
            ImageView invisibleTick = new ImageView(TICK_ICON_PATH);
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
            if (!bobQBitStates[i].isFilterProper(aliceFilters[i])) {
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
        String path = qBitVal == 1 ? ONE_ICON_PATH : ZERO_ICON_PATH;
        node.setImage(new Image(path));
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