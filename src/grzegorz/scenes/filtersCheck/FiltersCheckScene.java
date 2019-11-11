package grzegorz.scenes.filtersCheck;

import grzegorz.QBitState;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class FiltersCheckScene {

    @FXML
    private VBox filtersVBox;

    @FXML
    private VBox qBitsVBox;

    @FXML
    private VBox ticksVBox;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;
    private int[] aliceFilters;
    private QBitState[] bobQBitStates;

    public void start(int[] aliceFilters, QBitState[] bobQBitStates) {
        this.aliceFilters = aliceFilters;
        this.bobQBitStates = bobQBitStates;
        prepareQBitsAndFilters();
        scheduleAnimationStart();
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

    private void prepareQBitsAndFilters() {
        prepareImages();
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

    private SequentialTransition prepareWholeAnimation() {
        int size = ticksVBox.getChildren().size();
        ArrayList<Animation> transitions = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (!bobQBitStates[i].isFilterProper(aliceFilters[i])) {
                continue;
            }
            Node node = ticksVBox.getChildren().get(i);
            node.setVisible(true);
            transitions.add(getFadeTransition(node, 0.0, 1.0));
        }
        Animation[] transArray = new Animation[transitions.size()];
        transArray = transitions.toArray(transArray);
        return new SequentialTransition(transArray);
    }

    private FadeTransition getFadeTransition(Node node, double fromVal, double toVal) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(1));
        fadeTransition.setFromValue(fromVal);
        fadeTransition.setToValue(toVal);
        return fadeTransition;
    }
}