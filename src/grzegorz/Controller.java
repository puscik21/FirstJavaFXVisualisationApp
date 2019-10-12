package grzegorz;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import javafx.animation.*;
import javafx.collections.ObservableList;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;


public class Controller {

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
    private ImageView envImage;

    @FXML
    private ImageView alicePC;

    @FXML
    private ImageView bobPC;

    @FXML
    private ImageView electricalCable;

    @FXML
    private ImageView photonCable;

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
    private DropShadow borderGlow;
    private boolean envelopeSent = false;

    @FXML
    public void initialize() {
//        ///////

//        *FOR TEST PURPOSES*
//        tabPane.getSelectionModel().select(1);
//        loadMeasurementChartData(1000.0, 10);

//        ////////


        initEvents();


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

        for (Node node : envPane.getChildren()){
            ImageView imgView = (ImageView) node;
            setResizeEvent(imgView);
        }
    }

    private void setResizeEvent(ImageView node) {
        double widthScale = START_PANE_WIDTH / node.getFitWidth();
        double heightScale = START_PANE_HEIGHT / node.getFitHeight();
        double layoutXScale = START_PANE_WIDTH / node.getLayoutX();
        double layoutYScale = START_PANE_HEIGHT / node.getLayoutY();

        node.fitWidthProperty().bind(envPane.widthProperty().divide(widthScale));
        node.fitHeightProperty().bind(envPane.heightProperty().divide(heightScale));

        envPane.widthProperty().addListener((observable, oldValue, newValue) ->
                node.setLayoutX(newValue.doubleValue() / layoutXScale)
        );
        envPane.heightProperty().addListener((observable, oldValue, newValue) ->
                node.setLayoutY(newValue.doubleValue() / layoutYScale)
        );
    }

    private void initMouseEvents() {
        initBorderGlowEffect();
        initMainTabPane();
        initOnMouseClickedEvents();

        setBorderGlowEffect(alicePC);
        setBorderGlowEffect(bobPC);
        setBorderGlowEffect(electricalCable);
        setBorderGlowEffect(photonCable);
    }

    private void initMainTabPane() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observableVal, oldVal, newVal) -> {
            try {
                if (newVal.intValue() == 0) {
                    // FIXME: 22.09.2019 add to tab only (without reloading everything?)
                    Parent root = FXMLLoader.load(getClass().getResource("scenes/mainScene.fxml"));
                    Stage stage = (Stage) tabPane.getScene().getWindow();
                    stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
                    stage.show();
                }
                // chart
                else if (newVal.intValue() == 1 && chart.getData().size() == 0) {
                    loadMeasurementChartData(1000.0, 10);
                }
                else if (newVal.intValue() == 2) {
                    StackPane root = FXMLLoader.load(getClass().getResource("scenes/filterScene.fxml"));
                    tabPane.getTabs().get(2).setContent(root);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // TODO: 06.10.2019 .properties file for all comments
    private void initOnMouseClickedEvents() {
        envImage.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("This is encrypted message");
            } else if (!envelopeSent){
                envelopeSent = true;
                moveImage();
            }
        });

        alicePC.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Alice's PC");
            } else if (!envelopeSent){
                envelopeSent = true;
                moveImage();
            }
        });

        bobPC.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Bob's PC");
            } else if (!envelopeSent){
                envelopeSent = true;
                moveImage();
            }
        });

        electricalCable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Electrical Cable - used for communication in unsecure channel");
            }
        });

        photonCable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("Quantum cable - used for the key establishment");
            }
        });
    }

    private void initBorderGlowEffect() {
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

    @FXML
    public void moveImage(){
        envImage.setVisible(true);

        int dir = -1;
        double moveX = bobPC.getLayoutX() - alicePC.getLayoutX();
        double moveY = dir * rootPane.getHeight() / 7;

        Polyline polylinePath = new Polyline();
        double startX = envImage.getFitWidth() / 2.0;
        double startY = envImage.getFitHeight() / 2.0;
        polylinePath.getPoints().addAll(startX, startY,
                startX + 0.0, startY + moveY,
                startX + moveX, startY + moveY,
                startX + moveX, startY + 0.0);

        PathTransition pathTransition = new PathTransition();
        pathTransition.setNode(envImage);
        pathTransition.setDuration(Duration.seconds(2));
        pathTransition.setPath(polylinePath);
        pathTransition.play();
        pathTransition.setOnFinished(e -> fadeImage());
    }


    private void fadeImage(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(envImage);
        fadeTransition.setDuration(Duration.seconds(1.5));
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
        fadeTransition.setOnFinished(e -> envPane.getChildren().remove(envImage));
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

        series.getData().add(new LineChart.Data<>(1,1 / keyLength * Math.log(1 / keyLength)));
        for (int i = distance; i <= keyLength; i += distance) {
            series.getData().add(new LineChart.Data<>(i,-1 * i / keyLength * Math.log(i / keyLength)));
        }

        chart.getData().add(series);
        chart.setCreateSymbols(false);
        chart.setTitle("Entropy of security");
    }


    // others
    private void showDialog(String message) {
        BoxBlur blurEffect = new BoxBlur(3, 3, 3);
        rootAnchorPane.setEffect(blurEffect);

        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(new Text(message));

        JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);

//        JFXButton button = new JFXButton("Click me!");
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> dialog.close());
//        dialogLayout.setActions(button);

        dialog.setOnDialogClosed(e -> rootAnchorPane.setEffect(null));
        dialog.show();
    }


    // *TEST THINGS*
    @FXML
    public void closePopup(){
        Stage stage = (Stage) popupCloseBtn.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void openPopupMessage() throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("scenes/popupScene.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Message window");
        stage.showAndWait();
    }


    @FXML
    public void openNextScene() throws IOException{
        Stage stage = (Stage) openNextSceneBtn.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("scenes/secondScene.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
}
