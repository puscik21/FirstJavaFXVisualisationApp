package grzegorz.scenes.quantumScene;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.CommentedAnimation;
import grzegorz.QBitState;
import grzegorz.scenes.choosingQBits.ChoosingQBitsScene;
import grzegorz.scenes.enterQBitCombination.EnterQBitCombination;
import grzegorz.scenes.eveFilters.EveFiltersScene;
import grzegorz.scenes.eveFiltersCheck.EveFiltersCheckScene;
import grzegorz.scenes.explanations.QBitExplanationScene;
import grzegorz.scenes.filters.FiltersScene;
import grzegorz.scenes.filtersCheck.FiltersCheckScene;
import grzegorz.scenes.introduction.IntroductionScene;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

public class QuantumScene {
    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuItem menuItemRefresh;

    @FXML
    private MenuItem menuItemHelp;

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
    private ImageView electricalCable;

    @FXML
    private ImageView photonCable;

    @FXML
    private JFXButton showButton;

    @FXML
    private StackPane commentPane;

    private StackPane mainPane;
    // TODO: 10.10.2019 Eventually change that height and width values (or method to receive them)
    //  primaryStage.setOnShowing(event -> {});     - try it
    //  after initialize call method start I guess and take good value
    private final double START_PANE_WIDTH = 1076;
    private final double START_PANE_HEIGHT = 710;

    private final int EXPLANATION_TAB = 1;
    private final int FILTERS_TAB = 3;
    private final int FILTERS_CHECK_TAB = 4;
    private final int EVE_FILTERS_TAB = 5;
    private final int ALICE_AFTER_EVE_FILTERS_TAB = 6;
    private final int EVE_FILTERS_CHECK_TAB = 7;

    private final double TABS_Y = 50;
    private final double TABS_FOURTH_X = 560;
    private final double TABS_FIFTH_X = 700;
    private final double TABS_SIXTH_X = 825;
    private final double TABS_SEVEN_X = 950;
    private final double TABS_EIGHT_X = 1175;

    private ArrayList<CommentedAnimation> sceneCAnimations;
    private ArrayList<JFXDialog> sceneDialogs;
    private int animCounter;
    private int dialogCounter;
    private boolean nextIsDialog = false;
    private boolean animationsShowed = false;
    private boolean isUserInput;

    // TODO: 22.11.2019 special flag for every tab telling when showButton can be enabled
    private QBitState[] bobQBitsStates;
    private QBitState[] eveQBitsStates;
    private int[] aliceFiltersValues;
    private int[] eveFiltersValues;
    private int[] aliceQBitsValuesAfterEve;

    private Circle fourthHighlightCircle;
    private Circle fifthHighlightCircle;
    private Circle sixthHighlightCircle;
    private Circle seventhHighlightCircle;
    private Circle eighthHighlightCircle;
    private DropShadow borderGlow;

    private JFXDialog enterCombinationDialog;

    private IntroductionScene introductionController;
    private JFXTabPane tabPane;


    @FXML
    private void initialize() {
        initEvents();

        sceneCAnimations = new ArrayList<>(10);
        animCounter = 0;
        sceneDialogs = new ArrayList<>(10);
        dialogCounter = 0;

        fourthHighlightCircle = getHighlightCircle(TABS_FOURTH_X, TABS_Y);
        fifthHighlightCircle = getHighlightCircle(TABS_FIFTH_X, TABS_Y);
        sixthHighlightCircle = getHighlightCircle(TABS_SIXTH_X, TABS_Y);
        seventhHighlightCircle = getHighlightCircle(TABS_SEVEN_X, TABS_Y);
        eighthHighlightCircle = getHighlightCircle(TABS_EIGHT_X, TABS_Y);

        // TODO: 13.10.2019
        //  send back part of the current key to make sure that no one is eavesdropping
        //  eavesdropper and why he cannot read qubits in quantum cable (?)

// TODO: 12.11.2019 (tests) TBR

//         filters
//        isUserInput = true;
//        enterCombinationDialog = returnEnterCombinationDialog();
//        enterCombinationDialog.show();
//        Tab filterTab1 = new Tab("Test tab");
//        tabPane.getTabs().add(filterTab1);
//        Tab filterTab2 = new Tab("Test tab");
//        tabPane.getTabs().add(filterTab2);
    }

    public void start(IntroductionScene introductionController, JFXTabPane tabPane) {
        this.mainPane = introductionController.getRootPane();
        this.introductionController = introductionController;
        this.tabPane = tabPane;
        tabPane.getSelectionModel().selectedIndexProperty().removeListener(introductionController.getListener());
        JFXDialog bb84Dialog = returnDialog("This is very popular and quite simple algorithm, so we will take it as an example.", "BB84 algorithm");
        EventHandler<? super JFXDialogEvent> currentOnCloseEvent = bb84Dialog.getOnDialogClosed();
        bb84Dialog.setOnDialogClosed(e -> {
            currentOnCloseEvent.handle(e);
            getShowMessTransition(bobMess).play();
        });
        bb84Dialog.show();

        initMainTabPane();

        nextIsDialog = true;
    }

    public StackPane getMainPane() {
        return mainPane;
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
        showButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                if (animationsShowed) {
                    reloadScene();
                } else {
                    showNextAnimation();
                }
            }
        });
        initCommentDialogs();
    }

    private void initMainTabPane() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observableVal, oldVal, newVal) -> {
            // TODO: 28.11.2019 maybe remove this? maybe make flag for each tab
            if (oldVal.intValue() != 0) {
                hideMess(aliceMess);
                hideMess(bobMess);
                hideMess(eveMess);
            } else if (oldVal.intValue() == EVE_FILTERS_TAB) {
                hideMess(bobMess);
            }

            if (newVal.intValue() == EXPLANATION_TAB) {
                FXMLLoader loader = loadToTab(EXPLANATION_TAB, "../explanations/qBitExplanationScene.fxml");
                QBitExplanationScene qBitExplanationScene = loader.getController();
                qBitExplanationScene.start();
            }
            else if (newVal.intValue() == FILTERS_TAB) {
                FXMLLoader loader = loadToTab(FILTERS_TAB, "../filters/filtersScene.fxml");
                FiltersScene filtersController = loader.getController();
                filtersController.start(this, bobQBitsStates, aliceFiltersValues);
            }
            else if (newVal.intValue() == FILTERS_CHECK_TAB) {
                FXMLLoader loader = loadToTab(FILTERS_CHECK_TAB, "../filtersCheck/filtersCheckScene.fxml");
                FiltersCheckScene filtersCheckController = loader.getController();
                filtersCheckController.start(this, aliceFiltersValues, bobQBitsStates);
            }
            else if (newVal.intValue() == EVE_FILTERS_TAB) {
                FXMLLoader loader = loadToTab(EVE_FILTERS_TAB, "../eveFilters/eveFiltersScene.fxml");
                EveFiltersScene eveFiltersController = loader.getController();
                eveFiltersController.startEveScenario(this, bobQBitsStates, eveFiltersValues);
            }
            else if (newVal.intValue() == ALICE_AFTER_EVE_FILTERS_TAB) {
                FXMLLoader loader = loadToTab(ALICE_AFTER_EVE_FILTERS_TAB, "../eveFilters/eveFiltersScene.fxml");
                EveFiltersScene eveFiltersController = loader.getController();
                eveFiltersController.startAliceScenario(this, bobQBitsStates, eveQBitsStates, aliceFiltersValues);
            }
            else if (newVal.intValue() == EVE_FILTERS_CHECK_TAB) {
                FXMLLoader loader = loadToTab(EVE_FILTERS_CHECK_TAB, "../eveFiltersCheck/EveFiltersCheckScene.fxml");
                EveFiltersCheckScene filtersCheckScene = loader.getController();
                filtersCheckScene.start(this, aliceFiltersValues, bobQBitsStates, aliceQBitsValuesAfterEve);
            }
        });
    }

    private FXMLLoader loadToTab(int tabPosition, String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Pane body = loader.load();
            tabPane.getTabs().get(tabPosition).setContent(body);
            showButton.setDisable(false);
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void reloadScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../quantumScene/quantumScene.fxml"));
            AnchorPane body = loader.load();
            tabPane.getTabs().get(2).setContent(body);
            QuantumScene quantumScene = loader.getController();
            quantumScene.start(introductionController, tabPane);
            hideMess(bobMess);
            showButton.setDisable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCommentDialogs() {
        initBorderGlowEffectInstance();
        initCommentForNode(alicePC, "Alice's PC");
        initCommentForNode(evePC, "comment*");
        initCommentForNode(bobPC, "Bob's PC");
        initCommentForNode(aliceMess, "This is encrypted message");
        initCommentForNode(eveMess, "comment*");
        initCommentForNode(bobMess, "Bob's qubits for key distribution");
        initCommentForNode(electricalCable, "Electrical Cable - used for communication in unsecure channel");
        initCommentForNode(photonCable, "Quantum cable - used for the key establishment");
    }

    private void initBorderGlowEffectInstance() {
        borderGlow = new DropShadow();
        borderGlow.setColor(Color.WHITESMOKE);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        borderGlow.setHeight(50);
        borderGlow.setWidth(50);
    }

    private void initCommentForNode(Node node, String comment) {
        node.setOnMouseClicked(e -> setCommentOnSecondaryButton(e.getButton(), comment));
        setBorderGlowEffect(node);
    }

    private void setCommentOnSecondaryButton(MouseButton button, String comment) {
        if (button == MouseButton.SECONDARY) {
            returnDialog(comment).show();
        }
    }

    private void setBorderGlowEffect(Node node) {
        node.setOnMouseEntered(e -> node.setEffect(borderGlow));
        node.setOnMouseExited(e -> node.setEffect(null));
    }

    private void showNextAnimation() {
        if (animCounter == 0 && dialogCounter == 0) {
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
            if (currentOnCloseEvent != null) {
                currentOnCloseEvent.handle(e);
                showButton.setDisable(false);
            }
        });
        dialog.show();
        dialogCounter++;
    }

    private boolean noMoreAnimationsOrDialogs() {
        return animCounter == sceneCAnimations.size() && dialogCounter == sceneDialogs.size();
    }

    private void prepareQuantumAnimation() {
        double moveX = aliceMess.getLayoutX() - bobMess.getLayoutX();
        double moveY = photonCable.getLayoutY() - bobMess.getLayoutY() + bobMess.getFitHeight() / 2.0;

        SequentialTransition sendKeyTrans = getSendingTransition(bobMess, moveX, moveY);
        EventHandler<ActionEvent> currentOnFinishedEvent = sendKeyTrans.getOnFinished();
        sendKeyTrans.setOnFinished(e -> {
            if (currentOnFinishedEvent != null) {
                currentOnFinishedEvent.handle(e);
            }
            fourthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Photons Measurement");
            tabPane.getTabs().add(filterTab);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(fourthHighlightCircle);

        SequentialTransition sendAndHighlightTrans = new SequentialTransition(sendKeyTrans, highlightTransition);
        sendAndHighlightTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans, "Bob send random qubits by quantum cable");
        sceneCAnimations.add(sendKeyCAnimation);
    }

    private void prepareAliceSendFiltersAnimation() {
        SequentialTransition showMessTrans = getShowMessTransition(aliceMess);
        SequentialTransition sendingTrans = getAliceReturnTransition();
        sendingTrans.setOnFinished(e -> {
            fifthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Eavesdropping");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
            hideMess(aliceMess);
            showButton.setDisable(true);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(fifthHighlightCircle);

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
            sixthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Read disturbed qubits");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(sixthHighlightCircle);

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
            seventhHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Alice measure photons");
            tabPane.getTabs().add(filterTab);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(seventhHighlightCircle);

        SequentialTransition sendAndHighlightTrans = new SequentialTransition(sendKeyTrans, highlightTransition);
        sendAndHighlightTrans.setOnFinished(e -> showButton.setDisable(true));
        CommentedAnimation sendKeyCAnimation = new CommentedAnimation(sendAndHighlightTrans, "Eve send eavesdropped qubits to Alice");
        sceneCAnimations.add(sendKeyCAnimation);
    }

    private void prepareAliceSendFiltersAfterEveAnimation() {       // TODO: 22.11.2019 same as  "prepareAliceSendFiltersAnimation" just need proper circle as parameter
        ScaleTransition showMessTrans = getScaleTransition(aliceMess, 0.0, 1.0, 0.25);
        SequentialTransition sendingTrans = getAliceReturnTransition();
        sendingTrans.setOnFinished(e -> {
            eighthHighlightCircle.setVisible(true);
            Tab filterTab = new Tab("Filters Comparison after eavesdropping");
            tabPane.getTabs().add(filterTab);
            nextIsDialog = true;
            hideMess(aliceMess);
        });
        FadeTransition highlightTransition = getHighlightCircleAnimation(eighthHighlightCircle);

        SequentialTransition aliceSendFiltersTrans = new SequentialTransition(showMessTrans, sendingTrans, highlightTransition);
        CommentedAnimation aliceSendFiltersCAnimation = new CommentedAnimation(aliceSendFiltersTrans, "Alice send filters that she chose");
        sceneCAnimations.add(aliceSendFiltersCAnimation);
    }

    private SequentialTransition getAliceReturnTransition() {
        double toX = bobPC.getLayoutX() - alicePC.getLayoutX();
        double toY = -aliceMess.getFitHeight();
        return getSendingTransition(aliceMess, toX, toY);
    }

    private void prepareSceneDialogs() {
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
            getShowMessTransition(bobMess).play();
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
            getShowMessTransition(eveMess).play();
            nextIsDialog = false;
            currentCloseEvent.handle(e);
        });
        return dialog;
    }

    private JFXDialog returnEnterCombinationDialog() {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Bob is preparing the sequence of qubits to send"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../enterQBitCombination/enterQBitCombination.fxml"));
            AnchorPane body = loader.load();
            EnterQBitCombination enterQBitCombination = loader.getController();

            dialogLayout.setBody(body);
            JFXDialog dialog = new JFXDialog(mainPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            dialog.setOverlayClose(false);
            dialog.setOnDialogOpened(e -> {
                addSceneBlurEffect();
                removeCommentDialog();
                enterQBitCombination.start(this);
            });
            dialog.setOnDialogClosed(e -> {
                removeSceneEffects();
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
            JFXDialog dialog = new JFXDialog(mainPane, dialogLayout, JFXDialog.DialogTransition.TOP);
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

    private SequentialTransition getShowMessTransition(ImageView imgView) {
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
        fadeTransition.setCycleCount(3);
        fadeTransition.setOnFinished(e -> borderPane.getChildren().remove(node));

        return fadeTransition;
    }

    // TODO: 03.12.2019 use it from introduction scene
    public JFXDialog returnDialog(String message) {
        return returnDialog(message, "");
    }

    public JFXDialog returnDialog(String message, String title) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty()) {
            dialogLayout.setHeading(new Text(title));
        }
        Text text = new Text(message);
//        text.setWrappingWidth(START_PANE_WIDTH / 1.5);
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(mainPane, dialogLayout, JFXDialog.DialogTransition.TOP);
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
        introductionController.addSceneBlurEffect();
    }

    private void removeSceneEffects() {
        introductionController.removeSceneEffects();
    }
}