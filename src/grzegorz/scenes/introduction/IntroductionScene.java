package grzegorz.scenes.introduction;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.CommentedAnimation;
import grzegorz.scenes.eveFiltersCheck.EveFiltersCheckScene;
import grzegorz.scenes.quantumScene.QuantumScene;
import grzegorz.scenes.explanations.QBitExplanationScene;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

// TODO: (LAST) 20.11.2019 clicking fast can make envelope go back to Alice after moving to Bob
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

    // TODO: 10.10.2019 Eventually change that height and width values (or method to receive them)
    //  primaryStage.setOnShowing(event -> {});     - try it
    //  after initialize call method start I guess and take good value
    private final double START_PANE_WIDTH = 1076;
    private final double START_PANE_HEIGHT = 710;

    private final int EXPLANATION_TAB = 1;
    private final int INTRODUCTION_TAB = 2;

    private final double TABS_Y = 50;
    private final double TABS_FIRST_X = 53;
    private final double TABS_SECOND_X = 206;
    private final double TABS_THIRD_X = 385;

    private final String LOCKED_ENVELOPE_PATH = "grzegorz\\images\\envelopeLocked.png";
    private final String DEFAULT_ENVELOPE_PATH = "grzegorz\\images\\envelope.jpg";

    private ArrayList<CommentedAnimation> sceneCAnimations;
    private ArrayList<JFXDialog> sceneDialogs;
    private int animCounter;
    private int dialogCounter;
    private boolean nextIsDialog = false;
    private boolean animationsShowed = false;

    // TODO: 26.11.2019 some flags(?) to recognize what tab has been closed
    
    private Circle secondHighlightCircle;
    private Circle thirdHighlightCircle;
    private DropShadow borderGlow;
    private ChangeListener<? super Number> listener;

    public ChangeListener<? super Number> getListener() {
        return listener;
    }

    // TODO: 26.11.2019 refactor
    @FXML
    private void initialize() {
        initEvents();

        secondHighlightCircle = getHighlightCircle(TABS_SECOND_X, TABS_Y);
        thirdHighlightCircle = getHighlightCircle(TABS_THIRD_X, TABS_Y);

        returnDialog("Nowadays to send information safely, we use asynchronous algorithms like RSA. \n" +
                "Alice and Bob have two keys - public and private. \nAlice use Ben's public key to send the message to him \n" +
                "Message can be decrypted only with Bob's private key, which only Bob knows", "RSA algorithm")
                .show();

        Tab explanationTab = new Tab("Qubit explanation");
        Tab quantumTab = new Tab("BB84");
        tabPane.getTabs().add(explanationTab);
        tabPane.getTabs().add(quantumTab);
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
        listener = (ChangeListener<Number>) (observable, oldVal, newVal) -> {
            if (oldVal.intValue() != 0) {
                hideMess(aliceMess);
                hideMess(bobMess);
            }

            if (newVal.intValue() == EXPLANATION_TAB) {
                FXMLLoader loader = loadToTab(EXPLANATION_TAB, "../explanations/qBitExplanationScene.fxml");
                QBitExplanationScene qBitExplanationScene = loader.getController();
                qBitExplanationScene.start();
            }
            else if (newVal.intValue() == INTRODUCTION_TAB) {
                FXMLLoader loader = loadToTab(INTRODUCTION_TAB, "../quantumScene/quantumScene.fxml");
                QuantumScene quantumScene = loader.getController();
                quantumScene.start(IntroductionScene.this, tabPane);
            }
        };
        tabPane.getSelectionModel().selectedIndexProperty().addListener(listener);
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
        initCommentDialogForNode(alicePC, "Alice's PC");
        initCommentDialogForNode(bobPC, "Bob's PC");
        initCommentDialogForNode(aliceMess, "This is encrypted message");
        initCommentDialogForNode(bobMess, "Bob's qubits for key distribution");
        initCommentDialogForNode(electricalCable, "Electrical Cable - used for communication in unsecure channel");
        initCommentDialogForNode(photonCable, "Quantum cable - used for the key establishment");
        initCommentDialogForNode(publicKey, "Bob's public key");
        initCommentDialogForNode(privateKey, "Bob's private key");
    }

    private void initCommentDialogForNode(Node node, String comment) {
        node.setOnMouseClicked(e -> setCommentOnSecondaryButton(e.getButton(), comment));
    }

    private void setCommentOnSecondaryButton(MouseButton button, String comment) {
        if (button == MouseButton.SECONDARY) {
            returnDialog(comment).show();
        }
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
        sceneCAnimations = new ArrayList<>(10);
        playShowButtonTransition();
        preparePublicKeyAnimation();
        preparePrivateKeyAnimation();
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
            showButton.setDisable(false);
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
    }

    private boolean noMoreAnimationsOrDialogs() {
        return animCounter == sceneCAnimations.size() && dialogCounter == sceneDialogs.size();
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

        SequentialTransition showMessTrans = getShowMessTransition(aliceMess);
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

    private void prepareSceneDialogs() {
        sceneDialogs = new ArrayList<>(10);
        sceneDialogs.add(getRSADialogs());
    }

    private JFXDialog getRSADialogs() {
        JFXDialog d1 = returnDialog("For ordinary computer this algorithm is nearly impossible to break in reasonable time");
        JFXDialog d2 = returnDialog("This can be quite easy for quantum computers", "BUT!");
        JFXDialog d3 = returnDialog("Thankfully there are quantum cryptography algorithms that can stop them thanks to the laws of physics. \n\n" +
                "One of them is algorithm called BB84.");

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
        fadeTransition.setCycleCount(3);
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