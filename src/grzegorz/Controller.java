package grzegorz;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTabPane;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
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
    public StackPane rootPane;

    @FXML
    public AnchorPane rootAnchorPane;

    @FXML
    public BorderPane borderPane;

    @FXML
    public JFXTabPane tabPane;

    // envelope
    @FXML
    public Tab envTab;

    @FXML
    public AnchorPane envPane;

    @FXML
    public ImageView envImage;

    // chart
    @FXML
    public Tab chartTab;

    @FXML
    public AnchorPane chartPane;

    @FXML
    public LineChart<Integer, Double> chart;

    // test items
    @FXML
    public Button openPopupSceneBtn;

    @FXML
    public Button openNextSceneBtn;

    @FXML
    public Button popupCloseBtn;


    @FXML
    public void initialize() {
//        ///////
//        tabPane.getSelectionModel().select(1);
//        loadMeasurementChartData(1000.0, 10);
//        ////////


        final double envStartHeight = envImage.getTranslateX();
        envImage.minWidth(100);
        envImage.minHeight(75);

        envPane.heightProperty().addListener((observable, oldValue, newValue) ->
                envImage.setFitHeight(newValue.doubleValue() / 3)
        );

        envPane.widthProperty().addListener((observable, oldVal, newVal) ->
                envImage.setFitWidth(newVal.doubleValue() / 3)
        );

        envImage.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                showDialog("This is encrypted message");
                // TODO also translateY
            } else if (envStartHeight == envImage.getTranslateX()){
                moveImage();
            }
        });

        Tab filterTab = new Tab("Measure photons");
        tabPane.getTabs().add(filterTab);

        tabPane.getSelectionModel().selectedIndexProperty().addListener((observableVal, oldVal, newVal) -> {
            try {
                if (newVal.intValue() == 0) {
                    // FIXME: 22.09.2019 add to tab only
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
                    BorderPane root = FXMLLoader.load(getClass().getResource("scenes/filterScene.fxml"));
                    tabPane.getTabs().get(2).setContent(root);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            //TODO add 3'th tab from code, load filterScene there
        });

//        // chart
//        tabPane.getSelectionModel().selectedIndexProperty().addListener((observableVal, oldVal, newVal) -> {
//            if (newVal.intValue() == 1 && chart.getData().size() == 0) {
//                loadMeasurementChartData(1000.0, 10);
//            }
//        });
    }

    @FXML
    public void moveImage(){
        double moveX = rootPane.getWidth() / 2;
        double moveY = rootPane.getHeight() / 4;

        Polyline polylinePath = new Polyline();
        double startX = envImage.getX() + envImage.getFitWidth() / 2;
        double startY = envImage.getY() + envImage.getFitHeight() / 2;
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

        JFXDialog dialog = new JFXDialog(rootPane, dialogLayout, JFXDialog.DialogTransition.TOP);

//        JFXButton button = new JFXButton("Click me!");
//        button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> dialog.close());
//        dialogLayout.setActions(button);

        dialogLayout.setBody(new Text(message));

        dialog.setOnDialogClosed(e -> rootAnchorPane.setEffect(null));
        dialog.show();
    }

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
