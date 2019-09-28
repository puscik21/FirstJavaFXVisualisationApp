package grzegorz;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
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
    private ImageView filterImage2;

    // TODO photonImage
    @FXML
    private ImageView filterImage1;

    @FXML
    private AnchorPane comparisonPane;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;

    private ArrayList<Integer> chosenFilters;
    private ArrayList<Integer> chosenPhotons;


    @FXML
    public void initialize() {
        filterImages = new ArrayList<>(2);
        photonImages = new ArrayList<>(4);

        chosenFilters = new ArrayList<>(photonHBox.getChildren().size());
        chosenPhotons = new ArrayList<>(filterHBox.getChildren().size());
        setAllImages();

        // TODO Firstly invisible, then show proper image on mouseClickEvent
        filterImage1.setVisible(false);
        filterImage2.setVisible(false);
        filterImage1.toFront();

        prepareRandomImages();


        // refactor
        root.setOnMouseClicked(e -> compare(0));
    }

    private void setAllImages() {
        Image whiteFilter = new Image("grzegorz\\images\\whiteFilter.png");
        Image greenFilter = new Image("grzegorz\\images\\greenFilter.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");

        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
        photonImages.addAll(Arrays.asList(horPhoton, verPhoton, leftDiagPhoton, rightDiagPhoton));
    }

    private void prepareRandomImages() {
        prepareHBoxImages(photonHBox, photonImages, chosenPhotons);
        prepareHBoxImages(filterHBox, filterImages, chosenFilters);
    }

    private void prepareHBoxImages(HBox hBox, ArrayList<Image> images, ArrayList<Integer> chosen) {
        for (int i = 0; i < hBox.getChildren().size(); i++) {
            ImageView imageView = (ImageView) hBox.getChildren().get(i);

            int imageNumber = getRandomNumber(images.size());
            imageView.setImage(images.get(imageNumber));
            chosen.add(imageNumber);
        }
    }

    private int getRandomNumber(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    private void compare(int compNumber) {
        // TODO some effect on currently compared images
        // 1. showImagesToCompare
        filterImage1.setVisible(true);
        filterImage2.setVisible(true);

        ImageView photonImage = (ImageView) photonHBox.getChildren().get(compNumber);
        filterImage1.setImage(photonImage.getImage());

        ImageView filterImage = (ImageView) filterHBox.getChildren().get(compNumber);
        filterImage2.setImage(filterImage.getImage());

        double hideLength = 300;
        filterImage1.setTranslateX(-hideLength);
        filterImage2.setTranslateX(hideLength);

        double imageConstraint = 40.0;
        double photonPath = comparisonPane.getWidth() / 2.0 - filterImage1.getFitWidth() / 2.0 - imageConstraint;
        double filterPath = - comparisonPane.getWidth() / 2.0 + filterImage2.getFitWidth() / 2.0 + imageConstraint;


        makeTransition(compNumber, filterImage1, hideLength, photonPath);
        makeTransition(compNumber, filterImage2, -hideLength, filterPath);









        // 6. compare (i + 1)
    }

    private void makeTransition(int compNumber, ImageView imageView, double firstPath, double secondPath) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.5));
        transition.setNode(imageView);
        transition.setToX(firstPath);
        transition.play();
        transition.setOnFinished(e -> {
            // 3. moveImagesToCompare

            transition.setToX(secondPath);
            transition.setDelay(Duration.seconds(0.5));
            transition.setDuration(Duration.seconds(1));
            transition.play();
            transition.setOnFinished(e1 -> checkQBitState(compNumber, imageView));
        });
    }

    private void checkQBitState(int compNumber, ImageView imageView) {
        // photons > 1 => diagonal
        if ((chosenPhotons.get(compNumber) > 1 && chosenFilters.get(compNumber) == 0) ||
                 (chosenPhotons.get(compNumber) <= 1 && chosenFilters.get(compNumber) == 1)){
            // TODO some effect if isn't correct

            int direction = getRandomNumber(2) == 0 ? -1 : 1;
            RotateTransition transition = new RotateTransition();
            transition.setDuration(Duration.seconds(0.5));
            transition.setNode(filterImage1);
            transition.setByAngle(direction * 45);
            transition.play();
            transition.setOnFinished(e -> fadeImage(imageView, false));
        }
        else {
            fadeImage(imageView, true);
        }

        // TODO save qBit value (also for those after rotation)

    }


    // copy of Controller's method
    private void fadeImage(ImageView imageView, boolean withDelay){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(imageView);
        if (withDelay)
            fadeTransition.setDelay(Duration.seconds(0.5));
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

//        fadeTransition.setOnFinished(e -> envPane.getChildren().remove(envImage));
    }
}
