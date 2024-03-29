package grzegorz.scenes.filters;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import grzegorz.general.Animator;
import grzegorz.general.QBitState;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

public class FiltersScene {
    @FXML
    public StackPane rootStackPane;

    @FXML
    public BorderPane root;

    @FXML
    private VBox imagesContainer;

    @FXML
    private HBox qBitHBox;

    @FXML
    private HBox filterHBox;

    @FXML
    private HBox valuesHBox;

    @FXML
    private ImageView qBitImage;

    @FXML
    private ImageView filterImage;

    @FXML
    private AnchorPane comparisonPane;
    private ArrayList<Image> filterImages;
    private ArrayList<Image> photonImages;
    private ArrayList<Image> valuesImages;
    private QBitState[] sentQBitsStates;
    private QBitState[] eveQBits;
    private int[] filtersValues;

    private QuantumScene parentController;
    private DropShadow borderGlow;

    private Random generator;
    private double timeScale = 4.0;
    private boolean comparisonStarted = false;
    private boolean isEveScenario = false;

    @FXML
    public void initialize() {
        setAllImages();
        initListeners();
    }

    public void start(QuantumScene parentController, QBitState[] sentQBitsStates, int[] filtersValues) {
        this.parentController = parentController;
        isEveScenario = false;
        prepare(sentQBitsStates, filtersValues);
        scheduleAnimationStart();
    }

    private void prepare(QBitState[] sentQBitsStates, int[] filtersValues) {
        this.sentQBitsStates = sentQBitsStates;
        this.filtersValues = filtersValues;
        comparisonStarted = false;
        generator = new Random();
        prepareScene();
        prepareEavesDroppedQBits();
        prepareQBitsAndFilters();
        initCommentDialogs();
    }

    private void prepareEavesDroppedQBits() {
        int size = sentQBitsStates.length;
        eveQBits = new QBitState[size];
        for (int i = 0; i < size; i++) {
            eveQBits[i] = QBitState.getNewQBit(sentQBitsStates[i].getState());
        }

        for (int i = 0; i < size; i++) {
            if (sentQBitsStates[i].isFilterWrong(filtersValues[i])) {
                int direction = getRandomNumber(2) == 0 ? -1 : 1;
                changeEveQBitState(i, direction);
            }
        }
    }

    private void changeEveQBitState(int compNumber, int direction) {
        eveQBits[compNumber].turnQBit(direction);
    }

    private void initCommentDialogs() {
        initBorderGlowEffectInstance();
        initCommentForNode(qBitHBox, "Comment");
        initCommentForNode(filterHBox, "Comment");
        initCommentForNode(valuesHBox, "Comment");
    }

    private void initBorderGlowEffectInstance() {
        borderGlow = Animator.getHighlightEffect();
    }

    private void initCommentForNode(Node node, String comment) {
        node.setOnMouseClicked(e -> setCommentOnSecondaryButton(e, comment));
        setBorderGlowEffect(node);
    }

    private void setCommentOnSecondaryButton(MouseEvent event, String comment) {
        if (event.getButton() == MouseButton.SECONDARY) {
            event.consume();
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
                if (!comparisonStarted) {
                    compare(0);
                }
            }
        };
        Timer timer = new Timer();
        long delay = 4000L;
        timer.schedule(task, delay);
    }

    private void setAllImages() {
        Image whiteFilter = new Image("grzegorz\\images\\whiteFilter.png");
        Image greenFilter = new Image("grzegorz\\images\\greenFilter.png");
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");
        Image zeroValue = new Image("grzegorz\\images\\zeroIcon.png");
        Image oneValue = new Image("grzegorz\\images\\oneIcon.png");

        photonImages = new ArrayList<>(4);
        filterImages = new ArrayList<>(2);
        valuesImages = new ArrayList<>(2);
        photonImages.addAll(Arrays.asList(verPhoton, rightDiagPhoton, horPhoton, leftDiagPhoton));
        filterImages.addAll(Arrays.asList(whiteFilter, greenFilter));
        valuesImages.addAll(Arrays.asList(zeroValue, oneValue));
    }

    private void prepareScene() {
        addImageViews();
        scaleContainer();
        prepareQBitsAndFilters();
    }

    private void addImageViews() {
        int quantity = sentQBitsStates.length;
        for (int i = 0; i < quantity; i++) {
            filterHBox.getChildren().add(new ImageView(filterImages.get(0)));
            qBitHBox.getChildren().add(new ImageView(photonImages.get(0)));
            ImageView valueView = new ImageView(valuesImages.get(0));
            valueView.setVisible(false);
            valuesHBox.getChildren().add(valueView);
        }
    }

    private void scaleContainer() {
        setTimeScale();
        double sizeScale = 1.0;
        imagesContainer.setScaleX(sizeScale);
        imagesContainer.setScaleY(sizeScale);
    }

    private void setTimeScale() {
        int size = qBitHBox.getChildren().size();
        if (size == 0) {
            return;
        }

        timeScale = timeScale / size;
        if (timeScale > 0.5) {
            timeScale = 0.5;
        }
    }

    private void prepareQBitsAndFilters() {
        prepareQBitsHBox();
        prepareFiltersHBox();
    }

    private void prepareQBitsHBox() {
        for (int i = 0; i < qBitHBox.getChildren().size(); i++) {
            int imageNumber = sentQBitsStates[i].getState();
            ImageView imageView = (ImageView) qBitHBox.getChildren().get(i);
            imageView.setImage(photonImages.get(imageNumber));
        }
    }

    private void prepareFiltersHBox() {
        for (int i = 0; i < filterHBox.getChildren().size(); i++) {
            int imageNumber = filtersValues[i];
            ImageView imageView = (ImageView) filterHBox.getChildren().get(i);
            imageView.setImage(filterImages.get(imageNumber));
        }
    }

    private int getRandomNumber(int bound) {
        return generator.nextInt(bound);
    }

    private void initListeners() {
        root.setOnMouseClicked(e -> {
            if (!comparisonStarted && e.getButton() == MouseButton.PRIMARY) {
                compare(0);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Bob sends randomly chosen qubits for key establishment. \n" +
                        "if Alice choose the wrong basis of detector \n" +
                        "photon will take 1 or 0 value with probability of 50%");
            }
        });

        qBitHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
                showDialog("Randomly chosen qubits by Bob");
            }
        });

        filterHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
                showDialog("Randomly chosen detectors by Alice");
            }
        });

        valuesHBox.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                e.consume();
                showDialog("Result key that Alice receive");
            }
        });
    }

    private void compare(int compNumber) {
        prepareComparison(compNumber);
        prepareCompareQBitTransition(compNumber);
        prepareCompareFilterTransition(compNumber);
    }

    private void prepareComparison(int compNumber) {
        comparisonStarted = true;
        qBitImage.setEffect(null);
        qBitImage.setVisible(true);
        filterImage.setVisible(true);

        highlightCompared(compNumber);
        showImage(qBitImage);
    }

    private void prepareCompareQBitTransition(int compNumber) {
        double hideLength = 300;
        double imageConstraint = 40.0;

        ImageView photonImg = (ImageView) qBitHBox.getChildren().get(compNumber);
        qBitImage.setImage(photonImg.getImage());
        qBitImage.setTranslateX(-hideLength);
        double photonPath = comparisonPane.getWidth() / 2.0 - qBitImage.getFitWidth() / 2.0 - imageConstraint;
        makeTransition(compNumber, qBitImage, hideLength, photonPath);
    }

    private void prepareCompareFilterTransition(int compNumber) {
        double hideLength = 300;
        double imageConstraint = 40.0;

        ImageView filterImg = (ImageView) filterHBox.getChildren().get(compNumber);
        filterImage.setImage(filterImg.getImage());
        filterImage.setTranslateX(hideLength);
        double filterPath = -comparisonPane.getWidth() / 2.0 + filterImage.getFitWidth() / 2.0 + imageConstraint;
        makeTransition(compNumber, filterImage, -hideLength, filterPath);
    }

    private void makeTransition(int compNumber, ImageView imageView, double firstPath, double secondPath) {
        imageView.setRotate(0);     // For every qBit image the same imageView is used, so it's rotation have to be moved back to 0
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(timeScale));
        transition.setNode(imageView);
        transition.setToX(firstPath);
        transition.play();

        transition.setOnFinished(e -> {
            transition.setToX(secondPath);
            transition.setDelay(Duration.seconds(timeScale));
            transition.setDuration(Duration.seconds(2 * timeScale));
            transition.play();
            transition.setOnFinished(e1 -> {
                if (imageView == qBitImage) {
                    checkQBitState(compNumber);
                }
            });
        });
    }

    private void checkQBitState(int compNumber) {
        if (sentQBitsStates[compNumber].isFilterWrong(filtersValues[compNumber])) {
            Glow highlightEffect = new Glow(0.5);
            qBitImage.setEffect(highlightEffect);
            int direction = rotateQBit(compNumber);
            if (isEveScenario) {
                changeQBitImageState(compNumber, direction);
            }
        } else {
            fadeImage(compNumber, qBitImage, true);
        }
        showQBitValue(compNumber);
    }

    private int rotateQBit(int compNumber) {
        QBitState AliceQBit = sentQBitsStates[compNumber];
        QBitState eveQBit = eveQBits[compNumber];
        int direction = AliceQBit.getDirectionToState(eveQBit);

        RotateTransition transition = getRotateTransition(qBitImage, direction);
        transition.play();
        transition.setOnFinished(e -> fadeImage(compNumber, qBitImage, false));
        return direction;
    }

    private void changeQBitImageState(int compNumber, int direction) {
        Node qBit = qBitHBox.getChildren().get(compNumber);
        RotateTransition rotateTransition = getRotateTransition(qBit, direction);
        rotateTransition.play();
    }

    private void showQBitValue(int compNumber) {
        int qBitValue = eveQBits[compNumber].getValue();
        ImageView qBitValueView = (ImageView) valuesHBox.getChildren().get(compNumber);
        qBitValueView.setImage(valuesImages.get(qBitValue));
        qBitValueView.setVisible(true);
    }

    private RotateTransition getRotateTransition(Node node, int direction) {
        return Animator.getRotateTransition(node, direction, 45, timeScale);
    }

    private void fadeImage(int compNumber, ImageView imageView, boolean withDelay) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(imageView);
        if (withDelay) {
            fadeTransition.setDelay(Duration.seconds(timeScale));
        }
        fadeTransition.setDuration(Duration.seconds(timeScale));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();

        fadeTransition.setOnFinished(e -> {
            if (compNumber + 1 != qBitHBox.getChildren().size()) {
                compare(compNumber + 1);
            } else {
                removeEffectFromHBoxes(compNumber);
                qBitImage.setVisible(false);
                filterImage.setVisible(false);
                makeResultTransition();
            }
        });
    }

    private void showImage(ImageView node) {
        Animator.getFadeTransition(node, 0.0, 1.0, timeScale).play();
    }

    private void highlightCompared(int compNumber) {
        if (compNumber > 0) {
            removeEffectFromHBoxes(compNumber - 1);
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

    private void removeEffectFromHBoxes(int compNumber) {
        qBitHBox.getChildren().get(compNumber).setEffect(null);
        filterHBox.getChildren().get(compNumber).setEffect(null);
    }

    private void makeResultTransition() {
        double transitionLength = root.getHeight() - valuesHBox.getLayoutY();
        TranslateTransition transition = Animator.getTranslateTransition(valuesHBox, 0.0, 0.0, 0.0, transitionLength, 1.0);
        double scale = getScaleForResultTransition();
        ScaleTransition scaleTransition = Animator.getScaleTransition(valuesHBox, 1.0, scale, 1.0);
        new ParallelTransition(transition, scaleTransition).play();
    }

    private double getScaleForResultTransition() {
        double scaleFactor = 17;
        int nodesQuantity = valuesHBox.getChildren().size();

        double result = scaleFactor / nodesQuantity;
        if (result > 2.25) {
            result = 2.25;
        }
        return result;
    }

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
