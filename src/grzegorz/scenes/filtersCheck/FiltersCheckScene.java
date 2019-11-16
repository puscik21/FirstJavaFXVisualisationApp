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

    // TODO: 12.11.2019 values on the right
    // TODO: 12.11.2019 some title or any necessary information
    // TODO: 16.11.2019 comments on right click

    public void start(int[] aliceFilters, QBitState[] bobQBitStates) {
        this.aliceFilters = aliceFilters;
        this.bobQBitStates = bobQBitStates;
        prepareQBitsAndFilters();

        int imagesToView = aliceFilters.length;
        addImageViews(imagesToView);
        double scaleValue = getScaleValue();
        System.out.println(ticksVBox.getScaleX());
        scaleNodes(scaleValue, filtersVBox, ticksVBox, qBitsVBox);      /// scale AnchorPane? but w8 for numbers
        System.out.println(ticksVBox.getScaleX());


        prepareQBitsAndFilters();
        scheduleAnimationStart();
    }

    private void addImageViews(int quantity) {
        for (int i = 0; i < quantity; i++) {
            filtersVBox.getChildren().add(new ImageView(filterImages.get(0)));
            ticksVBox.getChildren().add(new ImageView("grzegorz\\images\\tickIcon.png"));
////            ticksVBox.getChildren().add(new ImageView("grzegorz\\images\\oneIcon.png"));
//            ticksVBox.getChildren().add(new ImageView("grzegorz\\images\\zeroIcon.png"));
            qBitsVBox.getChildren().add(new ImageView(photonImages.get(0)));
        }
    }

    private double getScaleValue() {
        int size = qBitsVBox.getChildren().size();
//        double scaleFor7 = 0.35;
        double scaleFor7 = 1;
        if (size == 0) {
            return scaleFor7;
        }

        double multiplier = 7.0 / size;
        double scaleValue = multiplier * scaleFor7;

        if (scaleValue < 0.3) {
            scaleValue = 0.3;
        } else if (scaleValue > 1.75) {
            scaleValue = 1.75;
        }
        return scaleValue;
//        return 0.6;
    }

    private void scaleNodes(double scaleValue, Node... nodes) {
        for (Node node : nodes) {
            node.setScaleX(scaleValue);
            node.setScaleY(scaleValue);
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