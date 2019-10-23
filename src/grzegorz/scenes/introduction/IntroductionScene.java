package grzegorz.scenes.introduction;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
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
    private JFXTabPane tabPane;

    // envelope Scene
    @FXML
    private Tab envTab;

    @FXML
    private AnchorPane envPane;

    @FXML
    private ImageView alicePC;

    @FXML
    private ImageView envImage;

    @FXML
    private ImageView bobPC;

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

    // chart
    @FXML
    private Tab chartTab;

    @FXML
    private AnchorPane chartPane;

    @FXML
    private LineChart<Integer, Double> chart;

    // test items
    @FXML
    private Button openPopupSceneBtn;

    @FXML
    private Button openNextSceneBtn;

    @FXML
    private Button popupCloseBtn;

    // TODO: 10.10.2019 Eventually change that height and width values (or method to receive them)
    private final double START_PANE_WIDTH = 1076;
    private final double START_PANE_HEIGHT = 710;

    private final String LOCKED_ENVELOPE_PATH = "grzegorz\\images\\envelopeLocked.png";
    private final String DEFAULT_ENVELOPE_PATH = "grzegorz\\images\\envelope.jpg";

    private DropShadow borderGlow;


    @FXML
    public void initialize() {
//        ///////

//        *FOR TEST PURPOSES*
//        tabPane.getSelectionModel().select(1);
//        loadMeasurementChartData(1000.0, 10);

//        ////////


        initEvents();

        // TODO: 20.10.2019 replace them in fxml
        envImage.toFront();
        publicKey.toFront();
        privateKey.toFront();

        returnDialog("Nowadays to send information safely, we use asynchronous algorithms like RSA. \n" +
                "Alice and Bob have two keys - public and private. \nAlice use Ben's public key to send the message to him \n" +
                "Message can be decrypted only with Bob's private key, which only Bob knows", "RSA algorithm").show();

        // TODO: 13.10.2019 rsa algorithm schema
        //  Bob send public key     X
        //  Alice send encrypted message (change image of the envelope)     X
        //  Bob decrypt the message (make default image again)      X
        //  but this can be brake with specially prepared quantum computers, so there is algorithm called BB84      X
        //  random qbits - bubble dialog or just line to dialog     X
        //  Bob send message with qbits through quantum cable
        //  go to the Filter Scene
        //  Alice send her filters combination
        //  comparison of filters
        //  take good values

        // TODO: 13.10.2019 eventually
        //  send back part of the current key to make sure that no one is eavesdropping
        //  charts scene


        // TODO: 05.10.2019 run on event (like end of previous tab)
        Tab filterTab = new Tab("Measure photons");
        tabPane.getTabs().add(filterTab);
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
                if (newVal.intValue() == 0) {
                    // FIXME: 22.09.2019 add to tab only (without reloading everything?)
                    Parent root = FXMLLoader.load(getClass().getResource("introductionScene.fxml"));
                    Stage stage = (Stage) tabPane.getScene().getWindow();
                    stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
                    stage.show();
                }
                // chart
                else if (newVal.intValue() == 1 && chart.getData().size() == 0) {
                    loadMeasurementChartData(1000.0, 10);
                } else if (newVal.intValue() == 2) {
                    StackPane root = FXMLLoader.load(getClass().getResource("../filters/filtersScene.fxml"));
                    tabPane.getTabs().get(2).setContent(root);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    // TODO: 06.10.2019 .properties file for all comments
    private void initOnMouseClickedEvents() {
        showButton.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                startScene();
            }
        });

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

        envImage.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                returnDialog("This is encrypted message").show();
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


    private void startScene() {
        envPane.getChildren().remove(showButton);

        SequentialTransition publicKeyTransition = preparePublicKeyAnimation();
        SequentialTransition privateKeyTransition = preparePrivateKeyAnimation();

        showIntroductionInfo(publicKeyTransition, privateKeyTransition);

        SequentialTransition wholeAnimation = new SequentialTransition(publicKeyTransition, privateKeyTransition);
//        SequentialTransition wholeAnimation = new SequentialTransition(privateKeyTransition);
        wholeAnimation.play();
    }

    private SequentialTransition preparePublicKeyAnimation() {
        double moveX = envImage.getLayoutX() - publicKey.getLayoutX() - envImage.getFitWidth() / 2.0;
        double moveY = electricalCable.getLayoutY() - publicKey.getLayoutY() - publicKey.getFitHeight();
        double toMessageX = moveX + publicKey.getFitWidth() + envImage.getFitWidth() / 2.0;
        double toMessageY = -envImage.getFitHeight() / 2.0;

        SequentialTransition sendKeyTrans = makeSendingTransition(publicKey, moveX, moveY);
        TranslateTransition lastTrans = (TranslateTransition) sendKeyTrans.getChildren().get(sendKeyTrans.getChildren().size() - 1);
        lastTrans.setOnFinished(e -> returnCommentDialog("Alice encrypt message with Bob's public key and send it to him"));

        TranslateTransition toMessageTrans = getTranslateTransition(publicKey, lastTrans.getToX(), lastTrans.getToY(), toMessageX, toMessageY);
        toMessageTrans.setDelay(Duration.seconds(1.0));

        ParallelTransition bumpUpAnimation = returnChangingEnvelopeAnimation(LOCKED_ENVELOPE_PATH);

        ParallelTransition returnTrans = prepareReturnTransitions(lastTrans, toMessageX, toMessageY);

        return new SequentialTransition(sendKeyTrans, toMessageTrans, bumpUpAnimation, returnTrans);
    }


    private ParallelTransition prepareReturnTransitions(TranslateTransition lastTrans, double toMessageX, double toMessageY) {
        TranslateTransition keyGoBackTrans = getTranslateTransition(publicKey, toMessageX, toMessageY, lastTrans.getToX(), lastTrans.getToY());
        keyGoBackTrans.setDelay(Duration.seconds(1.0));

        double toX = bobPC.getLayoutX() - alicePC.getLayoutX();
        double toY = -envImage.getFitHeight();
        SequentialTransition sendingTrans = makeSendingTransition(envImage, toX, toY);

        return new ParallelTransition(keyGoBackTrans, sendingTrans);
    }


    private SequentialTransition preparePrivateKeyAnimation() {
        double toMessageX = bobPC.getLayoutX() - privateKey.getLayoutX() + privateKey.getFitWidth();
        double toMessageY = bobPC.getLayoutY() - privateKey.getLayoutY();

        TranslateTransition toMessageTrans = getTranslateTransition(privateKey, 0, 0, toMessageX, toMessageY);
        toMessageTrans.setDelay(Duration.seconds(1.0));

        ParallelTransition bumpUpAnimation = returnChangingEnvelopeAnimation(DEFAULT_ENVELOPE_PATH);

        TranslateTransition fromMessageTrans = getTranslateTransition(privateKey, toMessageTrans.getToX(), toMessageTrans.getToY(), 0, 0);
        fromMessageTrans.setDelay(Duration.seconds(0.5));

        return new SequentialTransition(toMessageTrans, bumpUpAnimation, fromMessageTrans);
    }


    private void showIntroductionInfo(SequentialTransition publicKeyTransition, SequentialTransition privateKeyTransition) {
        returnCommentDialog("Bob send his public key to Alice");
        publicKeyTransition.setOnFinished(e -> returnCommentDialog("Bob use his private key to decrypt Alice's message"));
        privateKeyTransition.setOnFinished(e -> {
            JFXDialog d1 = returnDialog("For ordinary computer this algorithm is nearly impossible to break in reasonable time");
            JFXDialog d2 = returnDialog("This can be quite easy for quantum computers", "BUT!");
            JFXDialog d3 = returnDialog("Thankfully there are quantum cryptography algorithms that can stop them thanks to the laws of physics. \n\n" +
                    "One of them is algorithm called BB84, which now we will discuss");

            d1.show();
            d1.setOnDialogClosed(ev -> d2.show());
            d2.setOnDialogClosed(ev -> d3.show());

            d3.setOnDialogClosed(ev -> returnBobDialog().show());
        });
    }


    private SequentialTransition makeSendingTransition(ImageView imgView, double toX, double toY) {
        TranslateTransition tUp = getTranslateTransition(imgView, 0, 0, 0, toY);
        TranslateTransition tLeft = getTranslateTransition(imgView, tUp.getToX(), tUp.getToY(), toX, toY);
        TranslateTransition tDown = getTranslateTransition(imgView, tLeft.getToX(), tLeft.getToY(), toX, 0);

        SequentialTransition sendingAnimation = new SequentialTransition(tUp, tLeft, tDown);
        sendingAnimation.setDelay(Duration.seconds(0.5));
        return sendingAnimation;
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


    private ParallelTransition returnChangingEnvelopeAnimation(String url) {
        Image image = new Image(url);

        ScaleTransition scaleUpTransition = makeScaleTransition(envImage, 1.0, 1.2, 0.25, 0.0);
        scaleUpTransition.setOnFinished(e -> envImage.setImage(image));
        ScaleTransition scaleDownTransition = makeScaleTransition(envImage, 1.2, 1.0, 0.25, 0.25);

        return new ParallelTransition(scaleUpTransition, scaleDownTransition);
    }


    private ScaleTransition makeScaleTransition(Node node, double from, double to, double time, double delay) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDelay(Duration.seconds(delay));
        scaleTransition.setDuration(Duration.seconds(time));
        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setToX(to);
        scaleTransition.setToY(to);

        return scaleTransition;
    }


    private void fadeImage(ImageView imgView) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(imgView);
        fadeTransition.setDuration(Duration.seconds(1.5));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> envPane.getChildren().remove(imgView));
    }


    private void loadMeasurementChartData(double keyLength, int distance) {
        XYChart.Series<Integer, Double> series = new LineChart.Series<>();

        series.getData().add(new LineChart.Data<>(1, Math.log(1 / keyLength)));
        for (int i = distance; i <= keyLength; i += distance) {
            series.getData().add(new LineChart.Data<>(i, Math.log(i / keyLength)));
        }

        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setTitle("Measure of security");
    }


    // TODO use entropy chart
    private void loadEntropyChartData(double keyLength, int distance) {
        XYChart.Series<Integer, Double> series = new LineChart.Series<>();

        series.getData().add(new LineChart.Data<>(1, 1 / keyLength * Math.log(1 / keyLength)));
        for (int i = distance; i <= keyLength; i += distance) {
            series.getData().add(new LineChart.Data<>(i, -1 * i / keyLength * Math.log(i / keyLength)));
        }

        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setTitle("Entropy of security");
    }


    // others
    private JFXDialog returnDialog(String message) {
        return returnDialog(message, "");
    }


    private JFXDialog returnDialog(String message, String title) {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);
        rootAnchorPane.setEffect(blurEffect);

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty()) {
            dialogLayout.setHeading(new Text(title));
        }
        Text text = new Text(message);
//        text.setWrappingWidth(START_PANE_WIDTH / 1.5);
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialog.setOnDialogClosed(e -> rootAnchorPane.setEffect(null));
        return dialog;
    }


    private void returnCommentDialog(String message) {
        if (commentPane.getChildren().size() > 0) {
            TranslateTransition transition = getTranslateTransition(commentPane.getChildren().get(0), 0, 0, -2000, 0);
            transition.play();
            transition.setOnFinished(e -> commentPane.getChildren().remove(0));
        }

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Text text = new Text(message);
        text.setWrappingWidth(commentPane.getWidth());
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(commentPane, dialogLayout, JFXDialog.DialogTransition.RIGHT);
        dialog.show();
    }


    private JFXDialog returnBobDialog() {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);
        rootAnchorPane.setEffect(blurEffect);

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Text("Bob is choosing the sequence of qBits to send"));

        try {
            AnchorPane body = FXMLLoader.load(getClass().getResource("../choosingQBits/choosingQBitsScene.fxml"));
            dialogLayout.setBody(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);
        dialog.setOnDialogClosed(e -> rootAnchorPane.setEffect(null));
        return dialog;
    }


    // *TEST THINGS*
    @FXML
    public void closePopup() {
        Stage stage = (Stage) popupCloseBtn.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void openPopupMessage() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../popupScene.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Message window");
        stage.showAndWait();
    }


    @FXML
    public void openNextScene() throws IOException {
        Stage stage = (Stage) openNextSceneBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("../secondScene.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}