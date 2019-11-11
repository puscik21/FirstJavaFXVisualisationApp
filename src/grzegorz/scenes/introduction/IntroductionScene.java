package grzegorz.scenes.introduction;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.QBitState;
import grzegorz.scenes.choosingQBits.ChoosingQBitsScene;
import grzegorz.scenes.explanations.QBitExplanationScene;
import grzegorz.scenes.filters.FiltersScene;
import grzegorz.scenes.filtersCheck.FiltersCheckScene;
import grzegorz.scenes.measurement.MeasurementScene;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;


public class IntroductionScene {

    // TODO przerozne roznosci
    // ###############################
    // tooltip
//        Tooltip envelopeToolTip = new Tooltip("This is encrypted message");
//        Tooltip.install(image, envelopeToolTip);

    // Left -> Right transition
//        TranslateTransition transition = new TranslateTransition();
//        transition.setDuration(Duration.seconds(2));
//        transition.setToX(400);
//        transition.setNode(image);
//        transition.play();

    // fitProperty
//        image1.fitWidthProperty().bind(imagePane2.widthProperty());
//        image1.fitHeightProperty().bind(imagePane2.heightProperty());

    // dialog button
//        JFXButton button = new JFXButton("Click me!");
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> dialog.close());
//        dialogLayout.setActions(button);

    //fade image
//    pathTransition.setOnFinished(e -> fadeImage(publicKey));
    // ###############################


    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuItem menuItemRefresh;

    @FXML
    private MenuItem menuItemHelp;

    @FXML
    private JFXTabPane tabPane;

    // envelope Scene
    @FXML
    private Tab envTab;

    @FXML
    private AnchorPane envPane;

    @FXML
    private ImageView alicePC;

    @FXML
    private ImageView bobPC;

    @FXML
    private ImageView aliceMess;

    @FXML
    private ImageView bobMess;

    @FXML
    private ImageView publicKey;

    @FXML
    private ImageView privateKey;

    @FXML
    private ImageView electricalCable;

    @FXML
    private ImageView photonCable;

    @FXML
    private JFXButton showButton;

    @FXML
    private StackPane commentPane;


    // test items
    @FXML
    private Button openPopupSceneBtn;

    @FXML
    private Button openNextSceneBtn;

    @FXML
    private Button popupCloseBtn;

    // TODO: 10.10.2019 Eventually change that height and width values (or method to receive them)
    //  primaryStage.setOnShowing(event -> {});     - try it
    private final double START_PANE_WIDTH = 1076;
    private final double START_PANE_HEIGHT = 710;

    private final int FILTERS_TAB_NUMBER = 1;
    private final int FILTERS_CHECK_TAB_NUMBER = 2;
    private final int MEASUREMENT_TAB_NUMBER = 3;

    private final double TABS_Y = 50;
    private final double TABS_FIRST_X = 53;
    private final double TABS_SECOND_X = 206;
    private final double TABS_THIRD_X = 385;

    private final String LOCKED_ENVELOPE_PATH = "grzegorz\\images\\envelopeLocked.png";
    private final String DEFAULT_ENVELOPE_PATH = "grzegorz\\images\\envelope.jpg";
    // TODO: 01.11.2019 message sended by quantum cable could be yellow

    private ArrayList<CommentedAnimation> sceneCAnimations;
    private ArrayList<JFXDialog> sceneDialogs;
    private int animCounter;
    private int dialogCounter;
    private boolean nextIsDialog = false;
    private boolean animationsShowed = false;

    private MeasurementScene measurementController;
    private QBitState[] bobQBitsStates;
    private int[] aliceFiltersValues;

    private Circle secondHighlightCircle;
    private Circle thirdHighlightCircle;
    private DropShadow borderGlow;


    @FXML
    public void initialize() {
//        ///////

//        *FOR TEST PURPOSES*
//        tabPane.getSelectionModel().select(1);
//        loadMeasurementChartData(1000.0, 10);

//        ////////


        initEvents();

        sceneCAnimations = new ArrayList<>(10);
        animCounter = 0;
        sceneDialogs = new ArrayList<>(10);
        dialogCounter = 0;

        secondHighlightCircle = getHighlightCircle(TABS_SECOND_X, TABS_Y);
        thirdHighlightCircle = getHighlightCircle(TABS_THIRD_X, TABS_Y);

        returnDialog("Nowadays to send information safely, we use asynchronous algorithms like RSA. \n" +
                "Alice and Bob have two keys - public and private. \nAlice use Ben's public key to send the message to him \n" +
                "Message can be decrypted only with Bob's private key, which only Bob knows", "RSA algorithm")
                .show();

        // TODO: 13.10.2019 rsa algorithm schema
        //  Bob send public key     X
        //  Alice send encrypted message (change image of the envelope)     X
        //  Bob decrypt the message (make default image again)      X
        //  but this can be brake with specially prepared quantum computers, so there is algorithm called BB84      X
        //  random qbits - bubble dialog or just line to dialog     X
        //  Bob send message with qbits through quantum cable       X
        //  enable Filter scene, arrow or something showing its enable, go to the Filter Scene      X
        //  Alice send her filters combination by electrical cable   X
        //  comparison of filters
        //  take good values

        // TODO: 13.10.2019 eventually
        //  send back part of the current key to make sure that no one is eavesdropping
        //  charts scene
        //  eavesdropper and why he cannot read qubits in quantum cable


        returnFiltersComparisonDialog().show();      // TODO: 12.11.2019 TBR
    }


    private void initEvents() {
        initResizeEvents();
        initMouseEvents();
    }


    private void initResizeEvents() {
        electricalCable.setPreserveRatio(false);
        photonCable.setPreserveRatio(false);

        for (Node node : envPane.getChildren().stream().filter(e -> e instanceof ImageView).collect(Collectors.toList())) {
            ImageView imgView = (ImageView) node;
            setResizeEvent(imgView);
            setMoveEvent(node);
        }

        setMoveEvent(showButton);
    }

    // TODO: 26.10.2019 look at transited objects positions
    private void setResizeEvent(ImageView node) {
        double widthScale = START_PANE_WIDTH / node.getFitWidth();
        double heightScale = START_PANE_HEIGHT / node.getFitHeight();

        node.fitWidthProperty().bind(envPane.widthProperty().divide(widthScale));
        node.fitHeightProperty().bind(envPane.heightProperty().divide(heightScale));
    }


    private void setMoveEvent(Node node) {
        double layoutXScale = START_PANE_WIDTH / node.getLayoutX();
        double layoutYScale = START_PANE_HEIGHT / node.getLayoutY();

        envPane.widthProperty().addListener((observable, oldValue, newValue) ->
                node.setLayoutX(newValue.doubleValue() / layoutXScale)
        );
        envPane.heightProperty().addListener((observable, oldValue, newValue) ->
                node.setLayoutY(newValue.doubleValue() / layoutYScale)
        );
    }


    private void initMouseEvents() {
        initMainTabPane();
        initOnMouseClickedEvents();

        initBorderGlowEffectInstance();
        for (Node node : envPane.getChildren().stream().filter(e -> e instanceof ImageView).collect(Collectors.toList())) {
            setBorderGlowEffect(node);
        }
    }


    private void initMainTabPane() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observableVal, oldVal, newVal) -> {
            try {
                if (newVal.intValue() == 0) {
                    // TODO: 04.11.2019 do I want to reload it?
//                    reloadIntroductionScene();
                }
                else if (newVal.intValue() == FILTERS_TAB_NUMBER) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../filters/filtersScene.fxml"));
                    StackPane body = loader.load();
                    tabPane.getTabs().get(FILTERS_TAB_NUMBER).setContent(body);
                    FiltersScene filtersController = loader.getController();
                    filtersController.start(bobQBitsStates, aliceFiltersValues);
                    hideMess(bobMess);
                    showButton.setDisable(false);
                }
                else if (newVal.intValue() == FILTERS_CHECK_TAB_NUMBER) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../filtersCheck/filtersCheckScene.fxml"));
                        AnchorPane body = loader.load();
                        tabPane.getTabs().get(FILTERS_CHECK_TAB_NUMBER).setContent(body);
                        FiltersCheckScene filtersCheckScene = loader.getController();
                        filtersCheckScene.start(aliceFiltersValues, bobQBitsStates);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // chart
                else if (newVal.intValue() == MEASUREMENT_TAB_NUMBER) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../measurement/measurementScene.fxml"));
                        AnchorPane body = loader.load();
                        tabPane.getTabs().get(MEASUREMENT_TAB_NUMBER).setContent(body);
                        measurementController = loader.getController();
                        measurementController.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void reloadIntroductionScene() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("introductionScene.fxml"));
            Stage stage = (Stage) tabPane.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // TODO: 06.10.2019 .properties file for all comments
    private void initOnMouseClickedEvents() {
        showButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
//                Tab filterTab = new Tab("Test tab");
//                tabPane.getTabs().add(filterTab);
                if (animationsShowed) {
                    reloadIntroductionScene();
                } else {
                    showNextAnimation();
                }
            }
        });

        menuItemRefresh.setOnAction(e -> reloadIntroductionScene());
        menuItemHelp.setOnAction(e -> returnDialog("Here are many valuable things about this applications", "About").show());

        initCommentDialogs();
    }


    private void initCommentDialogs() {
        alicePC.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Alice's PC").show();
            }
        });

        bobPC.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Bob's PC").show();
            }
        });

        aliceMess.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("This is encrypted message").show();
            }
        });

        bobMess.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Bob's qubits for key distribution").show();
            }
        });

        electricalCable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Electrical Cable - used for communication in unsecure channel").show();
            }
        });

        photonCable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Quantum cable - used for the key establishment").show();
            }
        });

        publicKey.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Bob's public key").show();
            }
        });

        privateKey.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("Bob's private key").show();
            }
        });
    }


    private void initBorderGlowEffectInstance() {
        borderGlow = new DropShadow();
        borderGlow.setColor(Color.WHITESMOKE);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        borderGlow.setHeight(50);
        borderGlow.setWidth(50);
    }


    private void setBorderGlowEffect(Node node) {
        node.setOnMouseEntered(e -> node.setEffect(borderGlow));
        node.setOnMouseExited(e -> node.setEffect(null));
    }


    private void showNextAnimation() {
        if (animCounter == 0) {
            prepareAllAnimations();
        }

        if (nextIsDialog) {
            showDialog();
        } else {
            playAnimation();
        }

        if (animCounter == sceneCAnimations.size() && dialogCounter == sceneDialogs.size()) {
            showButton.setText("Replay scene");
            animationsShowed = true;
        }
    }


    private void prepareAllAnimations() {
        playShowButtonTransition();
        preparePublicKeyAnimation();
        preparePrivateKeyAnimation();
        prepareSceneDialogs();
        prepareQuantumAnimation();
        prepareAliceSendFiltersAnimation();
    }


    private void playShowButtonTransition() {
        showButton.setText("Next step");
        double toX = envPane.getWidth() - showButton.getLayoutX() - 1.25 * showButton.getWidth();
        double toY = 0.0;
        TranslateTransition buttonTrans = getTranslateTransition(showButton, 0, 0, toX, toY);
        buttonTrans.play();
    }


    private void playAnimation() {
        CommentedAnimation cAnimation = sceneCAnimations.get(animCounter);
        String comment = cAnimation.getComment();
        if (comment != null) {
            showCommentDialog(comment);
        }

        cAnimation.getAnimation().play();
        Transition trans = (Transition) cAnimation.getAnimation();
        EventHandler<ActionEvent> currentEvent = trans.getOnFinished();
        trans.setOnFinished(e -> {
            showButton.setDisable(false);
            if (currentEvent != null) {
                currentEvent.handle(e);
            }
        });

        showButton.setDisable(true);
        animCounter++;
    }

    private void showDialog() {
        JFXDialog dialog = sceneDialogs.get(dialogCounter);
        dialog.show();
        showButton.setDisable(true);
        dialogCounter++;
    }


    private void preparePublicKeyAnimation() {
        double moveX = aliceMess.getLayoutX() - publicKey.getLayoutX() - aliceMess.getFitWidth() / 2.0;
        double moveY = electricalCable.getLayoutY() - publicKey.getLayoutY() - publicKey.getFitHeight();

        SequentialTransition sendKeyTrans = getSendingTransition(publicKey, moveX, moveY);
        TranslateTransition lastTrans = (TranslateTransition) sendKeyTrans.getChildren().get(sendKeyTrans.getChildren().size() - 1);

        SequentialTransition encryptionAnimation = getEncryptionAnimation(lastTrans);
        SequentialTransition returnTrans = getAliceReturnTransition();

        addToCAnimations(sendKeyTrans, encryptionAnimation, returnTrans);
    }


    private SequentialTransition getEncryptionAnimation(TranslateTransition lastTrans) {
        double toMessageX = lastTrans.getToX() + publicKey.getFitWidth() + aliceMess.getFitWidth() / 2.0;
        double toMessageY = -aliceMess.getFitHeight() / 2.0;

        SequentialTransition showMessTrans = showMess(aliceMess);
        TranslateTransition toMessageTrans = getTranslateTransition(publicKey, lastTrans.getToX(), lastTrans.getToY(), toMessageX, toMessageY);
        SequentialTransition bumpUpAnimation = returnChangingEnvelopeAnimation(LOCKED_ENVELOPE_PATH);
        TranslateTransition keyGoBackTrans = getTranslateTransition(publicKey, toMessageX, toMessageY, lastTrans.getToX(), lastTrans.getToY());

        return new SequentialTransition(showMessTrans, toMessageTrans, bumpUpAnimation, keyGoBackTrans);
    }


    private SequentialTransition getAliceReturnTransition() {
        double toX = bobPC.getLayoutX() - alicePC.getLayoutX();
        double toY = -aliceMess.getFitHeight();
        return getSendingTransition(aliceMess, toX, toY);
    }


    private void addToCAnimations(SequentialTransition sendKeyTrans, SequentialTransition encryptionAnimation, SequentialTransition returnTrans) {
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendKeyTrans,"Bob send his public key to Alice");
        CommentedAnimation encryptionCAnimation = new CommentedAnimation(encryptionAnimation, "Alice encrypt message with Bob's public key");
        CommentedAnimation returnCAnimation = new CommentedAnimation(returnTrans, "Then she send it back to Bob");

        Collection<CommentedAnimation> cAnimations = Arrays.asList(sendKeyCAnimation, encryptionCAnimation, returnCAnimation);
        sceneCAnimations.addAll(cAnimations);
    }


    private void preparePrivateKeyAnimation() {
        TranslateTransition toMessageTrans = getBobUseKeyTransition();
        SequentialTransition bumpUpAnimation = returnChangingEnvelopeAnimation(DEFAULT_ENVELOPE_PATH);
        TranslateTransition fromMessageTrans = getTranslateTransition(privateKey, toMessageTrans.getToX(), toMessageTrans.getToY(), 0, 0);

        SequentialTransition privateKeyTransition = new SequentialTransition(toMessageTrans, bumpUpAnimation, fromMessageTrans);
        fromMessageTrans.setOnFinished(e -> nextIsDialog = true);       // TODO: 26.10.2019 why on privateKeyTransition doesn't it work

        CommentedAnimation privateKeyCAnimation = new CommentedAnimation(privateKeyTransition,
                "Bob decrypt the message with his private key. Now he can read what Annie wanted to tell him.");

        sceneCAnimations.add(privateKeyCAnimation);
    }


    private TranslateTransition getBobUseKeyTransition() {
        double toMessageX = bobPC.getLayoutX() - privateKey.getLayoutX() + privateKey.getFitWidth();
        double toMessageY = bobPC.getLayoutY() - privateKey.getLayoutY();

        return getTranslateTransition(privateKey, 0, 0, toMessageX, toMessageY);
    }


    private void prepareSceneDialogs() {
        sceneDialogs.add(getRSADialogs());
        sceneDialogs.add(returnExplanationDialog());
        sceneDialogs.add(returnBobDialog());
    }


    private JFXDialog getRSADialogs() {
        JFXDialog d1 = returnDialog("For ordinary computer this algorithm is nearly impossible to break in reasonable time");
        JFXDialog d2 = returnDialog("This can be quite easy for quantum computers", "BUT!");
        JFXDialog d3 = returnDialog("Thankfully there are quantum cryptography algorithms that can stop them thanks to the laws of physics. \n\n" +
                "One of them is algorithm called BB84, which now we will discuss");

        EventHandler<? super JFXDialogEvent> d1CurrentEvent = d1.getOnDialogOpened();
        d1.setOnDialogOpened(ev -> {
            if (d1CurrentEvent != null) {
                d1CurrentEvent.handle(ev);
            }
            hideMess(aliceMess);
        });
        d1.setOnDialogClosed(ev -> {
            d2.show();
            aliceMess.setTranslateX(0);
            aliceMess.setTranslateY(0);
        });

        d2.setOnDialogClosed(ev -> d3.show());
        d3.setOnDialogClosed(ev -> {
            nextIsDialog = true;
            removeSceneEffects();
            showButton.setDisable(false);
        });

        return d1;
    }


    private void prepareQuantumAnimation() {
        double moveX = aliceMess.getLayoutX() - bobMess.getLayoutX();
        double moveY = photonCable.getLayoutY() - bobMess.getLayoutY() + bobMess.getFitHeight() / 2.0;

        SequentialTransition sendKeyTrans = getSendingTransition(bobMess, moveX, moveY);
        sendKeyTrans.setOnFinished(e -> {
            secondHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Photons Measurement");
            tabPane.getTabs().add(filterTab);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(secondHighlightCircle);

        SequentialTransition sendAndHighlightTrans = new SequentialTransition(sendKeyTrans, highlightTransition);
        sendAndHighlightTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans,"Bob send random qubits by quantum cable");
        sceneCAnimations.add(sendKeyCAnimation);
    }


    private void prepareAliceSendFiltersAnimation() {
        ScaleTransition showMessTrans = getScaleTransition(aliceMess, 0.0, 1.0, 0.25);
        SequentialTransition sendingTrans = getAliceReturnTransition();
        sendingTrans.setOnFinished(e -> {
            thirdHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Filters Comparison");
            tabPane.getTabs().add(filterTab);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(thirdHighlightCircle);

        SequentialTransition aliceSendFiltersTrans = new SequentialTransition(showMessTrans, sendingTrans, highlightTransition);
        CommentedAnimation aliceSendFiltersCAnimation = new CommentedAnimation(aliceSendFiltersTrans, "Alice send filters that she chose");
        sceneCAnimations.add(aliceSendFiltersCAnimation);
    }


    private void hideMess(ImageView imgView) {
        getScaleTransition(imgView, 1.0, 0.0, 0.25).play();
    }


    private SequentialTransition showMess(ImageView imgView) {
        ScaleTransition hideTransition = getScaleTransition(imgView, 1.0, 0.0, 0.01);
        hideTransition.setOnFinished(e -> imgView.setVisible(true));
        ScaleTransition showTransition = getScaleTransition(imgView, 0.0, 1.0, 0.25);
        return new SequentialTransition(hideTransition, showTransition);
    }


    private FadeTransition getHighlightCircleAnimation(Circle circle) {
        return getHighlightTransition(circle);
    }


    private SequentialTransition getSendingTransition(ImageView imgView, double toX, double toY) {
        TranslateTransition tUp = getTranslateTransition(imgView, 0, 0, 0, toY);
        TranslateTransition tLeft = getTranslateTransition(imgView, tUp.getToX(), tUp.getToY(), toX, toY);
        TranslateTransition tDown = getTranslateTransition(imgView, tLeft.getToX(), tLeft.getToY(), toX, 0);

        return new SequentialTransition(tUp, tLeft, tDown);
    }


    private TranslateTransition getTranslateTransition(Node imageView, double fromX, double fromY, double toX, double toY) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.5));
        transition.setNode(imageView);
        transition.setFromX(fromX);
        transition.setFromY(fromY);
        transition.setToX(toX);
        transition.setToY(toY);

        return transition;
    }


    private SequentialTransition returnChangingEnvelopeAnimation(String url) {
        Image image = new Image(url);

        ScaleTransition scaleUpTransition = getScaleTransition(aliceMess, 1.0, 1.2, 0.25);
        scaleUpTransition.setOnFinished(e -> aliceMess.setImage(image));
        ScaleTransition scaleDownTransition = getScaleTransition(aliceMess, 1.2, 1.0, 0.25);

        return new SequentialTransition(scaleUpTransition, scaleDownTransition);
    }


    private ScaleTransition getScaleTransition(Node node, double from, double to, double time) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDuration(Duration.seconds(time));
        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setToX(to);
        scaleTransition.setToY(to);

        return scaleTransition;
    }


    private FadeTransition getFadeTransition(Node node) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(0.5));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        return fadeTransition;
    }


    private Circle getHighlightCircle(double x, double y) {
        Circle circle = new Circle(x, y, 50);
        circle.setFill(Color.TRANSPARENT);
        circle.setStroke(Color.WHITESMOKE);
        circle.setEffect(borderGlow);
        circle.setStrokeWidth(4);
        circle.setVisible(false);
        borderPane.getChildren().add(circle);
        return circle;
    }


    private FadeTransition getHighlightTransition(Node node) {
        FadeTransition fadeTransition = getFadeTransition(node);
        fadeTransition.setDuration(Duration.seconds(0.25));
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(5);
        fadeTransition.setOnFinished(e -> borderPane.getChildren().remove(node));

        return fadeTransition;
    }



    // others
    private JFXDialog returnDialog(String message) {
        return returnDialog(message, "");
    }


    private JFXDialog returnDialog(String message, String title) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty()) {
            dialogLayout.setHeading(new Text(title));
        }
        Text text = new Text(message);
//        text.setWrappingWidth(START_PANE_WIDTH / 1.5);
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialog.setOnDialogOpened(e -> addSceneBlurEffect());
        dialog.setOnDialogClosed(e -> removeSceneEffects());
        return dialog;
    }


    private void showCommentDialog(String message) {
        removeCommentDialog();

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Text text = new Text(message);
        text.setWrappingWidth(commentPane.getWidth());
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(commentPane, dialogLayout, JFXDialog.DialogTransition.LEFT);
        dialog.show();
    }


    private void removeCommentDialog() {
        if (commentPane.getChildren().size() > 0) {
            Node comment = commentPane.getChildren().get(0);
            TranslateTransition moveTrans = getTranslateTransition(comment, 0, 0, 0, 2000);
            moveTrans.setInterpolator(Interpolator.EASE_IN);
            moveTrans.setDuration(Duration.seconds(1));
            FadeTransition fadeTrans = getFadeTransition(comment);

            ParallelTransition commentRemovalAnim = new ParallelTransition(moveTrans, fadeTrans);
            commentRemovalAnim.play();
            commentRemovalAnim.setOnFinished(e -> commentPane.getChildren().remove(0));
        }
    }


    private JFXDialog returnBobDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Bob is choosing the sequence of qubits to send"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../choosingQBits/choosingQBitsScene.fxml"));
            AnchorPane body = loader.load();
            ChoosingQBitsScene choosingQBitsController = loader.getController();

            dialogLayout.setBody(body);
            JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            dialog.setOnDialogOpened(e -> {
                addSceneBlurEffect();
                removeCommentDialog();
                bobQBitsStates = choosingQBitsController.start();
                // its kinda workaround to prepare filters with qbits values here
                aliceFiltersValues = getRandomFilterValues(bobQBitsStates.length);
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
                showMess(bobMess).play();
                nextIsDialog = false;
                showButton.setDisable(false);
            });
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
        }
    }


    private JFXDialog returnExplanationDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Text text = new Text("What is qubit?");
        text.setFont(Font.font(null, FontWeight.BOLD, 24));
        dialogLayout.setHeading(text);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../explanations/qBitExplanationScene.fxml"));
            AnchorPane body = loader.load();
            QBitExplanationScene qBitExplanationController = loader.getController();

            dialogLayout.setBody(body);
            JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            dialog.setOnDialogOpened(e -> {
                addSceneBlurEffect();
                removeCommentDialog();
                qBitExplanationController.start();
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
                showButton.setDisable(false);
            });
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
        }
    }

    // TODO: 12.11.2019 TBR - im using it in another scene
    private JFXDialog returnFiltersComparisonDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Text text = new Text("Filters check*");
        text.setFont(Font.font(null, FontWeight.BOLD, 24));
        dialogLayout.setHeading(text);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../filtersCheck/filtersCheckScene.fxml"));
            AnchorPane body = loader.load();
            FiltersCheckScene filtersCheckScene = loader.getController();

            dialogLayout.setBody(body);
            JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            dialog.setOnDialogOpened(e -> {
                addSceneBlurEffect();
                removeCommentDialog();

                //
                    aliceFiltersValues = new int[] {1, 0, 0, 0, 0, 0, 1};
                    bobQBitsStates = new QBitState[7];
                    bobQBitsStates[0] = QBitState.getNewQBit(1);
                    bobQBitsStates[1] = QBitState.getNewQBit(1);
                    bobQBitsStates[2] = QBitState.getNewQBit(2);
                    bobQBitsStates[3] = QBitState.getNewQBit(2);
                    bobQBitsStates[4] = QBitState.getNewQBit(0);
                    bobQBitsStates[5] = QBitState.getNewQBit(0);
                    bobQBitsStates[6] = QBitState.getNewQBit(2);
                //

                filtersCheckScene.start(aliceFiltersValues, bobQBitsStates);
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
                showButton.setDisable(false);
            });
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
        }
    }

    private int[] getRandomFilterValues(int length) {
        Random random = new Random();
        int[] values = new int[length];
        int bound = 2;

        for (int i = 0; i < length; i++) {
            values[i] = random.nextInt(bound);
        }
        return values;
    }


    private void addSceneBlurEffect() {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);
        rootAnchorPane.setEffect(blurEffect);
    }


    private void removeSceneEffects() {
        rootAnchorPane.setEffect(null);
    }
}