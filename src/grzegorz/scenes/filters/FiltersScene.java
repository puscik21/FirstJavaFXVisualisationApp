package grzegorz.scenes.filters;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import grzegorz.QBitState;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FiltersScene {
    @FXML
    public StackPane rootStackPane;

    @FXML
    public BorderPane root;

    @FXML
    private VBox imagesVBox;

    @FXML
    private HBox qBitHBox;

    @FXML
    private HBox filterHBox;

    @FXML
    private HBox labelHBox;

    @FXML
    private ImageView qBitImage;
    
    @FXML
    private ImageView filterImage;

    @FXML
    private AnchorPane comparisonPane;

    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;

    private ArrayList<Integer> chosenFilters;
    private ArrayList<QBitState> chosenPhotons;
    private int[] qBitsValues;
    private int[] filtersValues;

    private boolean comparisonStarted = false;

    @FXML
    public void initialize() {
        // FIXME: 05.10.2019 if upper VBox is outside of the stage, change translateY in sceneBuilder

        qBitImage.setVisible(false);
        filterImage.setVisible(false);
        // FIXME: 03.10.2019 - a better way to resize it (at least that function can be done in fxml?)
        labelHBox.setMaxWidth(1100);

        setAllImages();
        initListeners();
    }


    public void start(int[] qBitsValues, int[] filtersValues) {
        this.qBitsValues = qBitsValues;
        this.filtersValues = filtersValues;
        prepareQBitsAndFilters();
    }


    private void setAllImages() {
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


    private void prepareQBitsAndFilters() {
        chosenPhotons = new ArrayList<>(filterHBox.getChildren().size());
        chosenFilters = new ArrayList<>(qBitHBox.getChildren().size());

        prepareQBitsHBox();
        prepareFiltersHBox();
    }


    private void prepareQBitsHBox() {
        for (int i = 0; i < qBitHBox.getChildren().size(); i++) {
            ImageView imageView = (ImageView) qBitHBox.getChildren().get(i);

            int imageNumber = qBitValToImageNumber(qBitsValues[i]);
            imageView.setImage(photonImages.get(imageNumber));
            chosenPhotons.add(QBitState.getNewQBit(imageNumber));
        }
    }


    private void prepareFiltersHBox() {
        for (int i = 0; i < filterHBox.getChildren().size(); i++) {
            ImageView imageView = (ImageView) filterHBox.getChildren().get(i);

            int imageNumber = filtersValues[i];
            imageView.setImage(filterImages.get(imageNumber));
            chosenFilters.add(imageNumber);
        }
    }


    private int qBitValToImageNumber(int val) {
        int imageNumber = getRandomNumber(2);
        if (val == 0) {
            imageNumber += 2;
        }
        return imageNumber;
    }


    private int getRandomNumber(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }


    private void initListeners() {
        root.setOnMouseClicked(e -> {
            if (!comparisonStarted && e.getButton() == MouseButton.PRIMARY) {
                comparisonStarted = true;
                compare(0);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Alice sends randomly chosen qubits for key establishment. \n" +
                        "if Bob choose the wrong basis of detector \n" +
                        "photon will take 1 or 0 value with probability of 50%");
            }
        });

        qBitHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
                showDialog("Randomly chosen qubits by Alice");
            }
        });

        filterHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {

                e.consume();
                showDialog("Randomly chosen detectors by Bob");
            }
        });

        labelHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {

                e.consume();
                showDialog("Result key that Bob receive");
            }
        });
    }


    private void compare(int compNumber) {
        double hideLength = 300;
        double imageConstraint = 40.0;

        highlightCompared(compNumber);

        qBitImage.setVisible(true);
        qBitImage.setEffect(null);
        filterImage.setVisible(true);

        // FIXME: 02.10.2019  is there a better way to move back FadeTransition effects?
        showImage(qBitImage);

        ImageView photonImg = (ImageView) qBitHBox.getChildren().get(compNumber);
        qBitImage.setImage(photonImg.getImage());

        ImageView filterImg = (ImageView) filterHBox.getChildren().get(compNumber);
        filterImage.setImage(filterImg.getImage());

        qBitImage.setTranslateX(-hideLength);
        filterImage.setTranslateX(hideLength);

        double photonPath = comparisonPane.getWidth() / 2.0 - qBitImage.getFitWidth() / 2.0 - imageConstraint;
        double filterPath = -comparisonPane.getWidth() / 2.0 + filterImage.getFitWidth() / 2.0 + imageConstraint;

        makeTransition(compNumber, qBitImage, hideLength, photonPath);
        makeTransition(compNumber, filterImage, -hideLength, filterPath);
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
                if (imageView == qBitImage) {
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
            transition.setNode(qBitImage);
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
            if (compNumber + 1 != qBitHBox.getChildren().size()) {
                compare(compNumber + 1);
            } else {
                qBitImage.setVisible(false);
                filterImage.setVisible(false);
                makeResultTransition();
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
            qBitHBox.getChildren().get(compNumber - 1).setEffect(null);
            filterHBox.getChildren().get(compNumber - 1).setEffect(null);
        }

        DropShadow highLightEffect = new DropShadow();
        highLightEffect.setColor(Color.WHITESMOKE);
        highLightEffect.setOffsetX(0f);
        highLightEffect.setOffsetY(0f);
        highLightEffect.setHeight(40);
        highLightEffect.setWidth(40);

        qBitHBox.getChildren().get(compNumber).setEffect(highLightEffect);
        filterHBox.getChildren().get(compNumber).setEffect(highLightEffect);
    }

    private void makeResultTransition() {
        double transitionLength = root.getHeight() - labelHBox.getLayoutY();
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(1));
        transition.setNode(labelHBox);
        transition.setToY(transitionLength);
        transition.play();

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(labelHBox);
        scaleTransition.setToX(1.75);
        scaleTransition.setToY(1.75);
        scaleTransition.setDuration(Duration.seconds(1));
        scaleTransition.play();
    }



    // TODO: 05.10.2019 1 class with many general methods like this one
    private void showDialog(String message) {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);

        StackPane mainRoot = (StackPane) root.getScene().getRoot();
        Node firstNode = mainRoot.getChildren().get(0);
        firstNode.setEffect(blurEffect);

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(new Text(message));

        JFXDialog dialog = new JFXDialog(mainRoot, dialogLayout, JFXDialog.DialogTransition.TOP);

        dialog.setOnDialogClosed(e -> firstNode.setEffect(null));
        dialog.show();
    }
}
