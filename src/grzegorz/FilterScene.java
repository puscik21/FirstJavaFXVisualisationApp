package grzegorz;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FilterScene {
    @FXML
    public BorderPane root;

    @FXML
    private VBox imagesVBox;

    @FXML
    private HBox photonHBox;

    @FXML
    private HBox filterHBox;

    @FXML
    private HBox labelHBox;

    @FXML
    private ImageView filterImage2;

    // TODO photonImage
    @FXML
    private ImageView filterImage1;

    @FXML
    private AnchorPane comparisonPane;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;

    private ArrayList<Integer> chosenFilters;
    private ArrayList<QBitState> chosenPhotons;


    @FXML
    public void initialize() {
        // if upper VBox is outside of the stage, change translateY in scenebuilder
        /*
         * 1. go over all images
         * 2. write all results
         * TODO 3. make images and labels fit - probably use width properties
         * TODO 4. block mouse event
         */
        filterImages = new ArrayList<>(2);
        photonImages = new ArrayList<>(4);

        chosenFilters = new ArrayList<>(photonHBox.getChildren().size());
        chosenPhotons = new ArrayList<>(filterHBox.getChildren().size());
        setAllImages();

        filterImage1.setVisible(false);
        filterImage2.setVisible(false);
        filterImage1.toFront();

        prepareRandomImages();

        root.setOnMouseClicked(e -> compare(0));
    }

    private void setAllImages() {
        Image whiteFilter = new Image("grzegorz\\images\\whiteFilter.png");
        Image greenFilter = new Image("grzegorz\\images\\greenFilter.png");
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");


        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
        photonImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
    }

    private void prepareRandomImages() {
        prepareHBoxQBitImages(photonHBox, photonImages, chosenPhotons);
        prepareHBoxFilterImages(filterHBox, filterImages, chosenFilters);
    }

    private void prepareHBoxFilterImages(HBox hBox, ArrayList<Image> images, ArrayList<Integer> chosen) {
        for (int i = 0; i < hBox.getChildren().size(); i++) {
            ImageView imageView = (ImageView) hBox.getChildren().get(i);

            int imageNumber = getRandomNumber(images.size());
            imageView.setImage(images.get(imageNumber));
            chosen.add(imageNumber);
        }
    }

    private void prepareHBoxQBitImages(HBox hBox, ArrayList<Image> images, ArrayList<QBitState> chosen) {
        for (int i = 0; i < hBox.getChildren().size(); i++) {
            ImageView imageView = (ImageView) hBox.getChildren().get(i);

            int imageNumber = getRandomNumber(images.size());
            imageView.setImage(images.get(imageNumber));
            chosen.add(QBitState.getNewQBit(imageNumber));
        }
    }

    private int getRandomNumber(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    private void compare(int compNumber) {
        double hideLength = 300;
        double imageConstraint = 40.0;

        highlightCompared(compNumber);

        filterImage1.setVisible(true);
        filterImage1.setEffect(null);
        filterImage2.setVisible(true);

        // FIXME: 02.10.2019  is there a better way to move back FadeTransition effects?
        showImage(filterImage1);

        ImageView photonImage = (ImageView) photonHBox.getChildren().get(compNumber);
        filterImage1.setImage(photonImage.getImage());

        ImageView filterImage = (ImageView) filterHBox.getChildren().get(compNumber);
        filterImage2.setImage(filterImage.getImage());

        filterImage1.setTranslateX(-hideLength);
        filterImage2.setTranslateX(hideLength);

        double photonPath = comparisonPane.getWidth() / 2.0 - filterImage1.getFitWidth() / 2.0 - imageConstraint;
        double filterPath = -comparisonPane.getWidth() / 2.0 + filterImage2.getFitWidth() / 2.0 + imageConstraint;

        makeTransition(compNumber, filterImage1, hideLength, photonPath);
        makeTransition(compNumber, filterImage2, -hideLength, filterPath);
    }

    private void makeTransition(int compNumber, ImageView imageView, double firstPath, double secondPath) {
        imageView.setRotate(0);     // For every qBit image the same imageView is used, so it's rotation have to be changed to 0
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.5));
        transition.setNode(imageView);
        transition.setToX(firstPath);
        transition.play();

        transition.setOnFinished(e -> {
            transition.setToX(secondPath);
            transition.setDelay(Duration.seconds(0.5));
            transition.setDuration(Duration.seconds(1));
            transition.play();
            transition.setOnFinished(e1 -> {
                if (imageView == filterImage1) {
                    checkQBitState(compNumber, imageView);
                }
            });
        });
    }

    private void checkQBitState(int compNumber, ImageView imageView) {
        int qBitState;
        if (!chosenPhotons.get(compNumber).isFilterProper(chosenFilters.get(compNumber))) {
            Glow highlightEffect = new Glow(0.5);
            imageView.setEffect(highlightEffect);

            int direction = getRandomNumber(2) == 0 ? -1 : 1;
            RotateTransition transition = new RotateTransition();
            transition.setDuration(Duration.seconds(0.5));
            transition.setNode(filterImage1);
            transition.setByAngle(direction * 45);
            transition.play();
            transition.setOnFinished(e -> fadeImage(compNumber, imageView, false));

            chosenPhotons.get(compNumber).turnQBit(direction);
        } else {
            fadeImage(compNumber, imageView, true);
        }

        qBitState = chosenPhotons.get(compNumber).getValue();
        Label qBitValueLabel = ((Label) labelHBox.getChildren().get(compNumber));
        qBitValueLabel.setText(String.valueOf(qBitState));
    }

    // FIXME: 02.10.2019  copy of Controller's method - maybe put it in one class later
    private void fadeImage(int compNumber, ImageView imageView, boolean withDelay) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(imageView);
        if (withDelay)
            fadeTransition.setDelay(Duration.seconds(0.5));
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

        fadeTransition.setOnFinished(e -> {
            if (compNumber + 1 != photonHBox.getChildren().size()) {
                compare(compNumber + 1);
            }
        });
    }

    private void showImage(ImageView imageView) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(imageView);
        fadeTransition.setDuration(Duration.seconds(0.1));
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void highlightCompared(int compNumber) {
        if (compNumber > 0) {
            photonHBox.getChildren().get(compNumber - 1).setEffect(null);
            filterHBox.getChildren().get(compNumber - 1).setEffect(null);
        }

        Bloom highLightEffect = new Bloom(0.1);
        photonHBox.getChildren().get(compNumber).setEffect(highLightEffect);
        filterHBox.getChildren().get(compNumber).setEffect(highLightEffect);
    }
}
