package grzegorz.scenes.introduction;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.QBitState;
import grzegorz.scenes.choosingQBits.ChoosingQBitsScene;
import grzegorz.scenes.enterQBitCombination.EnterQBitCombination;
import grzegorz.scenes.eveFiltersCheck.EveFiltersCheckScene;
import grzegorz.scenes.explanations.QBitExplanationScene;
import grzegorz.scenes.filters.FiltersScene;
import grzegorz.scenes.filtersCheck.FiltersCheckScene;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

// TODO: (LAST) 20.11.2019 clicking fast can make envelope go back to Alice after moving to Bob
// TODO: 26.10.2019 look at transited objects positions
// TODO: 24.11.2019 before user go to third tab showButton goes available
public class IntroductionScene {

    // TODO przerozne roznosci
    // ###############################
    // tooltip
//        Tooltip envelopeToolTip = new Tooltip("This is encrypted message");
//        Tooltip.install(image, envelopeToolTip);

    // fitProperty
//        image1.fitWidthProperty().bind(imagePane2.widthProperty());
//        image1.fitHeightProperty().bind(imagePane2.heightProperty());

    // dialog button
//        JFXButton button = new JFXButton("Click me!");
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> dialog.close());
//        dialogLayout.setActions(button);
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

    @FXML
    private Tab envTab;

    @FXML
    private AnchorPane envPane;

    @FXML
    private ImageView alicePC;

    @FXML
    private ImageView bobPC;

    @FXML
    private ImageView evePC;

    @FXML
    private ImageView aliceMess;

    @FXML
    private ImageView bobMess;

    @FXML
    private ImageView eveMess;

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

    // TODO: 10.10.2019 Eventually change that height and width values (or method to receive them)
    //  primaryStage.setOnShowing(event -> {});     - try it
    //  after initialize call method start I guess and take good value
    private final double START_PANE_WIDTH = 1076;
    private final double START_PANE_HEIGHT = 710;

    // TODO: 22.11.2019 remove additional tabs
    private final int FILTERS_TAB = 1;
    private final int FILTERS_CHECK_TAB = 2;
    private final int EVE_FILTERS_TAB = 3;
    private final int ALICE_AFTER_EVE_FILTERS_TAB = 4;
    private final int EVE_FILTERS_CHECK_TAB = 5;

    private boolean isFiltersCheckClosed = false;
    private boolean isEveFiltersClosed = false;
    private boolean isAliceAfterEveFiltersClosed = false;
    private boolean isEveFiltersCheckClosed = false;
    private boolean isMeasurementClosed = false;

    // TODO: 19.11.2019  check if they are all correct
    // TODO: 22.11.2019 add the rest of circles
    private final double TABS_Y = 50;
    private final double TABS_FIRST_X = 53;     // TODO: 20.11.2019 use circle in another scenes
    private final double TABS_SECOND_X = 206;
    private final double TABS_THIRD_X = 385;
    private final double TABS_FOURTH_X = 560;
    private final double TABS_FIFTH_X = 800;
    private final double TABS_SIXTH_X = 1000;

    private final String LOCKED_ENVELOPE_PATH = "grzegorz\\images\\envelopeLocked.png";
    private final String DEFAULT_ENVELOPE_PATH = "grzegorz\\images\\envelope.jpg";
    // TODO: 01.11.2019 message sent by quantum cable could be yellow

    private ArrayList<CommentedAnimation> sceneCAnimations;
    private ArrayList<JFXDialog> sceneDialogs;
    private int animCounter;
    private int dialogCounter;
    private boolean nextIsDialog = false;
    private boolean animationsShowed = false;
    private boolean isUserInput;
    private boolean isAfterEavesdropping = false;       // TODO: 21.11.2019 if I create ALICE_AFTER_EVE tab in right time I probably wont even need this flag

    // TODO: 22.11.2019 special flag for every tab telling when showButton can be enabled
    private QBitState[] bobQBitsStates;
    private QBitState[] eveQBitsStates;
    private int[] aliceFiltersValues;
    private int[] eveFiltersValues;
    private int[] aliceQBitsValuesAfterEve;

    private Circle secondHighlightCircle;
    private Circle thirdHighlightCircle;
    private Circle fourthHighlightCircle;
    private Circle fifthHighlightCircle;
    private Circle sixthHighlightCircle;
    private DropShadow borderGlow;

    private JFXDialog enterCombinationDialog;


    @FXML
    private void initialize() {
        initEvents();

        sceneCAnimations = new ArrayList<>(10);
        animCounter = 0;
        sceneDialogs = new ArrayList<>(10);
        dialogCounter = 0;

        secondHighlightCircle = getHighlightCircle(TABS_SECOND_X, TABS_Y);
        thirdHighlightCircle = getHighlightCircle(TABS_THIRD_X, TABS_Y);
        fourthHighlightCircle = getHighlightCircle(TABS_FOURTH_X, TABS_Y);
        fifthHighlightCircle = getHighlightCircle(TABS_FIFTH_X, TABS_Y);
        sixthHighlightCircle = getHighlightCircle(TABS_SIXTH_X, TABS_Y);

        returnDialog("Nowadays to send information safely, we use asynchronous algorithms like RSA. \n" +
                "Alice and Bob have two keys - public and private. \nAlice use Ben's public key to send the message to him \n" +
                "Message can be decrypted only with Bob's private key, which only Bob knows", "RSA algorithm")
                .show();


        // TODO: 13.10.2019
        //  send back part of the current key to make sure that no one is eavesdropping
        //  charts scene
        //  eavesdropper and why he cannot read qubits in quantum cable

// TODO: 12.11.2019 TBR

//         filters
//        isUserInput = true;
//        enterCombinationDialog = returnEnterCombinationDialog();
//        enterCombinationDialog.show();
//        Tab filterTab1 = new Tab("Test tab");
//        tabPane.getTabs().add(filterTab1);
//        Tab filterTab2 = new Tab("Test tab");
//        tabPane.getTabs().add(filterTab2);
    }

    public StackPane getRootPane() {
        return rootPane;
    }

    public void setUserCombination(QBitState[] userCombination, boolean isRandom) {
        isUserInput = isRandom;
        bobQBitsStates = userCombination;
        // its kinda workaround to prepare filters with qbits values here
        aliceFiltersValues = getRandomFilterValues(bobQBitsStates.length);
        eveFiltersValues = getRandomFilterValues(bobQBitsStates.length);
        enterCombinationDialog.close();
    }

    public void setUserCombination(boolean isRandom) {
        isUserInput = isRandom;
        enterCombinationDialog.close();
    }

    public void setEavesdroppedQBits(QBitState[] eavesDroppedValues) {
        this.eveQBitsStates = eavesDroppedValues;
        this.isAfterEavesdropping = true;             // TODO: 21.11.2019 circle on filters scene
    }

    public void setAliceQBitsValuesAfterEve(int[] aliceQBitsValuesAfterEve) {
        this.aliceQBitsValuesAfterEve = aliceQBitsValuesAfterEve;
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
                if (oldVal.intValue() != 0) {
                    hideMess(aliceMess);
                    hideMess(bobMess);
                    hideMess(eveMess);
                } else if (oldVal.intValue() == EVE_FILTERS_TAB) {
                    hideMess(bobMess);
                } else if (oldVal.intValue() == EVE_FILTERS_CHECK_TAB && tabPane.getTabs().size() == 6) {
                    Tab filterTab = new Tab("Photons Measurement");
                    tabPane.getTabs().add(filterTab);
                }

                if (newVal.intValue() == FILTERS_TAB) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../filters/filtersScene.fxml"));
                    StackPane body = loader.load();
                    tabPane.getTabs().get(FILTERS_TAB).setContent(body);
                    FiltersScene filtersController = loader.getController();
                    filtersController.startDefaultScenario(this, bobQBitsStates, aliceFiltersValues, isAfterEavesdropping);
                    hideMess(bobMess);
                    showButton.setDisable(false);
                } else if (newVal.intValue() == FILTERS_CHECK_TAB) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../filtersCheck/filtersCheckScene.fxml"));
                        BorderPane body = loader.load();
                        tabPane.getTabs().get(FILTERS_CHECK_TAB).setContent(body);
                        FiltersCheckScene filtersCheckScene = loader.getController();
                        filtersCheckScene.start(aliceFiltersValues, bobQBitsStates);
                        showButton.setDisable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (newVal.intValue() == EVE_FILTERS_TAB) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../filters/filtersScene.fxml"));
                        StackPane body = loader.load();
                        tabPane.getTabs().get(EVE_FILTERS_TAB).setContent(body);
                        FiltersScene filtersController = loader.getController();
                        filtersController.startEveScenario(this, bobQBitsStates, eveFiltersValues);
                        showButton.setDisable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (newVal.intValue() == ALICE_AFTER_EVE_FILTERS_TAB) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../filters/filtersScene.fxml"));
                        StackPane body = loader.load();
                        tabPane.getTabs().get(ALICE_AFTER_EVE_FILTERS_TAB).setContent(body);
                        FiltersScene filtersController = loader.getController();
                        filtersController.startDefaultScenario(this, eveQBitsStates, aliceFiltersValues, isAfterEavesdropping);
                        showButton.setDisable(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (newVal.intValue() == EVE_FILTERS_CHECK_TAB) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../eveFiltersCheck/EveFiltersCheckScene.fxml"));
                        BorderPane body = loader.load();
                        tabPane.getTabs().get(EVE_FILTERS_CHECK_TAB).setContent(body);
                        EveFiltersCheckScene filtersCheckScene = loader.getController();
                        filtersCheckScene.start(aliceFiltersValues, bobQBitsStates, aliceQBitsValuesAfterEve);
                        showButton.setDisable(false);
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

    private void initOnMouseClickedEvents() {
        showButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
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

    // TODO: 24.11.2019 rest of comments
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
            prepareSceneDialogs();
        }

        if (nextIsDialog) {
            showDialog();
        } else {
            playAnimation();
        }
        if (noMoreAnimationsOrDialogs()) {
            showButton.setText("Replay scene");
            animationsShowed = true;
        }
    }

    private void prepareAllAnimations() {
        playShowButtonTransition();
        preparePublicKeyAnimation();
        preparePrivateKeyAnimation();
        prepareQuantumAnimation();
        prepareAliceSendFiltersAnimation();
        prepareBobToEveAnimation();
        prepareEveToAliceAnimation();
        prepareAliceSendFiltersAfterEveAnimation();
    }

    private void playShowButtonTransition() {
        showButton.setText("Next step");
        double toX = envPane.getWidth() - showButton.getLayoutX() - 1.25 * showButton.getWidth();
        double toY = 0.0;
        TranslateTransition buttonTrans = getTranslateTransition(showButton, 0, 0, toX, toY);
        buttonTrans.play();
    }

    private void playAnimation() {
        showButton.setDisable(true);
        CommentedAnimation cAnimation = sceneCAnimations.get(animCounter);
        String comment = cAnimation.getComment();
        if (comment != null) {
            showCommentDialog(comment);
        }
        cAnimation.getAnimation().play();
        animCounter++;

        Transition trans = (Transition) cAnimation.getAnimation();
        EventHandler<ActionEvent> currentEvent = trans.getOnFinished();
        trans.setOnFinished(e -> {
            if (noMoreAnimationsOrDialogs()) {
                showButton.setDisable(true);
            } else {
                showButton.setDisable(false);
            }
            if (currentEvent != null) {
                currentEvent.handle(e);
            }
        });
    }

    private void showDialog() {
        showButton.setDisable(true);
        JFXDialog dialog = sceneDialogs.get(dialogCounter);

        EventHandler<? super JFXDialogEvent> currentOnCloseEvent = dialog.getOnDialogClosed();
        dialog.setOnDialogClosed(e -> {
            currentOnCloseEvent.handle(e);
            showButton.setDisable(false);
        });

        dialog.show();
        dialogCounter++;

        if (noMoreAnimationsOrDialogs()) {
            disableButtonForLastDialog(dialog);
        }
    }

    private boolean noMoreAnimationsOrDialogs() {
        return animCounter == sceneCAnimations.size() && dialogCounter == sceneDialogs.size();
    }

    // TODO: 20.11.2019 (LAST) at the end check if its really used
    private void disableButtonForLastDialog(JFXDialog dialog) {
        EventHandler<? super JFXDialogEvent> currentCloseEvent = dialog.getOnDialogClosed();
        dialog.setOnDialogClosed(e -> {
            if (currentCloseEvent != null) {
                currentCloseEvent.handle(e);
            }
            showButton.setDisable(true);
        });
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
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendKeyTrans, "Bob send his public key to Alice");
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
        privateKeyTransition.setOnFinished(e -> nextIsDialog = true);
        CommentedAnimation privateKeyCAnimation = new CommentedAnimation(privateKeyTransition,
                "Bob decrypt the message with his private key. Now he can read what Annie wanted to tell him.");

        sceneCAnimations.add(privateKeyCAnimation);
    }


    private TranslateTransition getBobUseKeyTransition() {
        double toMessageX = bobPC.getLayoutX() - privateKey.getLayoutX() + privateKey.getFitWidth();
        double toMessageY = bobPC.getLayoutY() - privateKey.getLayoutY();

        return getTranslateTransition(privateKey, 0, 0, toMessageX, toMessageY);
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
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans, "Bob send random qubits by quantum cable");
        sceneCAnimations.add(sendKeyCAnimation);
    }

    private void prepareAliceSendFiltersAnimation() {
        ScaleTransition showMessTrans = getScaleTransition(aliceMess, 0.0, 1.0, 0.25);
        SequentialTransition sendingTrans = getAliceReturnTransition();
        sendingTrans.setOnFinished(e -> {
            thirdHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Filters Comparison");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
            hideMess(aliceMess);
            showButton.setDisable(true);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(thirdHighlightCircle);

        SequentialTransition aliceSendFiltersTrans = new SequentialTransition(showMessTrans, sendingTrans, highlightTransition);
        aliceSendFiltersTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation aliceSendFiltersCAnimation = new CommentedAnimation(aliceSendFiltersTrans, "Alice send filters that she chose");
        sceneCAnimations.add(aliceSendFiltersCAnimation);
    }

    private void prepareBobToEveAnimation() {
        double moveX = eveMess.getLayoutX() - bobMess.getLayoutX();
        double moveY = photonCable.getLayoutY() - bobMess.getLayoutY() + bobMess.getFitHeight() / 2.0;

        SequentialTransition sendKeyTrans = getSendingTransition(bobMess, moveX, moveY);
        sendKeyTrans.setOnFinished(e -> {
            fourthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Eavesdropping");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(fourthHighlightCircle);

        SequentialTransition sendAndHighlightTrans = new SequentialTransition(sendKeyTrans, highlightTransition);
        sendAndHighlightTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans, "Bob send random qubits by quantum cable, but now Eve is capturing the message");
        sceneCAnimations.add(sendKeyCAnimation);
    }

    private void prepareEveToAliceAnimation() {
        double moveX = aliceMess.getLayoutX() - eveMess.getLayoutX();
        double moveY = photonCable.getLayoutY() - eveMess.getLayoutY() + eveMess.getFitHeight() / 2.0;

        SequentialTransition sendKeyTrans = getSendingTransition(eveMess, moveX, moveY);
        sendKeyTrans.setOnFinished(e -> {
            fifthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Alice measure photons");
            tabPane.getTabs().add(filterTab);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(fifthHighlightCircle);

        SequentialTransition sendAndHighlightTrans = new SequentialTransition(sendKeyTrans, highlightTransition);
        sendAndHighlightTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans, "Eve send eavesdropped qubits to Alice");
        sceneCAnimations.add(sendKeyCAnimation);
    }

    private void prepareAliceSendFiltersAfterEveAnimation() {       // TODO: 22.11.2019 same as  "prepareAliceSendFiltersAnimation" just need proper circle as parameter
        ScaleTransition showMessTrans = getScaleTransition(aliceMess, 0.0, 1.0, 0.25);
        SequentialTransition sendingTrans = getAliceReturnTransition();
        sendingTrans.setOnFinished(e -> {
            sixthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Filters Comparison after eavesdropping");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
            hideMess(aliceMess);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(sixthHighlightCircle);

        SequentialTransition aliceSendFiltersTrans = new SequentialTransition(showMessTrans, sendingTrans, highlightTransition);
        CommentedAnimation aliceSendFiltersCAnimation = new CommentedAnimation(aliceSendFiltersTrans, "Alice send filters that she chose");
        sceneCAnimations.add(aliceSendFiltersCAnimation);
    }

    private void prepareSceneDialogs() {
        sceneDialogs.add(getRSADialogs());
        sceneDialogs.add(returnExplanationDialog());
        enterCombinationDialog = returnEnterCombinationDialog();
        sceneDialogs.add(enterCombinationDialog);
        sceneDialogs.add(returnBobDialog());
        sceneDialogs.add(returnEveEavesdroppingDialog());
        sceneDialogs.add(returnEveForwardingDialog());
    }

    private JFXDialog returnEveEavesdroppingDialog() {
        JFXDialog dialog = returnDialog("But assume world is not perfect and someone can be eavesdropping \n" +
                "Let's repeat sending scenario* but with Eve in scene and see how BB84 is dealing with situation like this");
        EventHandler<? super JFXDialogEvent> currentCloseEvent = dialog.getOnDialogClosed();
        dialog.setOnDialogClosed(e -> {
            bobMess.setTranslateX(0);
            showMess(bobMess).play();
            nextIsDialog = false;
            makeEveShowUpTransition();
            currentCloseEvent.handle(e);
        });
        return dialog;
    }

    private JFXDialog returnEveForwardingDialog() {
        JFXDialog dialog = returnDialog("Eve is forwarding qubits she has eavesdropped to Alice");
        EventHandler<? super JFXDialogEvent> currentCloseEvent = dialog.getOnDialogClosed();
        dialog.setOnDialogClosed(e -> {
            hideMess(eveMess);
            showMess(eveMess).play();
            nextIsDialog = false;
            currentCloseEvent.handle(e);
        });
        return dialog;
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
        d1.setOnDialogClosed(ev -> d2.show());
        d2.setOnDialogClosed(ev -> d3.show());
        d3.setOnDialogClosed(ev -> {
            nextIsDialog = true;
            removeSceneEffects();
        });
        return d1;
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
            dialog.setOnDialogClosed(e -> removeSceneEffects());
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
        }
    }

    private JFXDialog returnEnterCombinationDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Bob is preparing the sequence of qubits to send"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../enterQBitCombination/enterQBitCombination.fxml"));
            AnchorPane body = loader.load();
            EnterQBitCombination enterQBitCombination = loader.getController();

            dialogLayout.setBody(body);
            JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            dialog.setOverlayClose(false);
            dialog.setOnDialogOpened(e -> {
                addSceneBlurEffect();
                removeCommentDialog();
                enterQBitCombination.start(this);
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
                showMess(bobMess).play();
                nextIsDialog = true;
            });
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
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
                if (isUserInput) {
                    choosingQBitsController.startWithUserInput(bobQBitsStates);
                } else {
                    bobQBitsStates = choosingQBitsController.startRandom();
                    aliceFiltersValues = getRandomFilterValues(bobQBitsStates.length);
                    eveFiltersValues = getRandomFilterValues(bobQBitsStates.length);
                }
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
                nextIsDialog = false;
            });
            return dialog;

        } catch (IOException e) {
            e.printStackTrace();
            return new JFXDialog();
        }
    }

    private void makeEveShowUpTransition() {
        double fromY = rootAnchorPane.getHeight() + 500.0;
        evePC.setTranslateY(fromY);
        evePC.setVisible(true);
        TranslateTransition eveTrans = getTranslateTransition(evePC, 0, fromY, 0, 0);
        eveTrans.setDuration(Duration.seconds(2.0));
        eveTrans.play();
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

    // general methods
    private void hideMess(ImageView imgView) {
        ScaleTransition hideTrans = getScaleTransition(imgView, 1.0, 0.0, 0.25);
        hideTrans.setOnFinished(e -> {
            imgView.setTranslateX(0);
            imgView.setTranslateY(0);
        });
        hideTrans.play();
    }

    private SequentialTransition showMess(ImageView imgView) {
        ScaleTransition hideTransition = getScaleTransition(imgView, 0.0, 0.0, 0.01);
        hideTransition.setOnFinished(e -> imgView.setVisible(true));
        ScaleTransition showTransition = getScaleTransition(imgView, 0.0, 1.0, 0.25);
        return new SequentialTransition(hideTransition, showTransition);
    }

    private SequentialTransition getSendingTransition(ImageView imgView, double toX, double toY) {
        TranslateTransition tUp = getTranslateTransition(imgView, 0, 0, 0, toY);
        TranslateTransition tLeft = getTranslateTransition(imgView, tUp.getToX(), tUp.getToY(), toX, toY);
        TranslateTransition tDown = getTranslateTransition(imgView, tLeft.getToX(), tLeft.getToY(), toX, 0);

        return new SequentialTransition(tUp, tLeft, tDown);
    }

    private SequentialTransition returnChangingEnvelopeAnimation(String url) {
        Image image = new Image(url);
        ScaleTransition scaleUpTransition = getScaleTransition(aliceMess, 1.0, 1.2, 0.25);
        scaleUpTransition.setOnFinished(e -> aliceMess.setImage(image));
        ScaleTransition scaleDownTransition = getScaleTransition(aliceMess, 1.2, 1.0, 0.25);

        return new SequentialTransition(scaleUpTransition, scaleDownTransition);
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

    private FadeTransition getHighlightCircleAnimation(Circle circle) {
        return getHighlightTransition(circle);
    }

    private FadeTransition getHighlightTransition(Node node) {
        FadeTransition fadeTransition = getFadeTransition(node);
        fadeTransition.setDuration(Duration.seconds(0.25));
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(5);
        fadeTransition.setOnFinished(e -> borderPane.getChildren().remove(node));

        return fadeTransition;
    }

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

    private void addSceneBlurEffect() {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);
        rootAnchorPane.setEffect(blurEffect);
    }

    private void removeSceneEffects() {
        rootAnchorPane.setEffect(null);
    }
}