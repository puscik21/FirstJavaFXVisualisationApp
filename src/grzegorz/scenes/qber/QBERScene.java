package grzegorz.scenes.qber;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import grzegorz.CommentedAnimation;
import grzegorz.SceneDisplay;
import grzegorz.scenes.introduction.IntroductionScene;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class QBERScene {
    @FXML
    private AnchorPane scenePane;

    @FXML
    private HBox keyHBox;

    private IntroductionScene introductionController;
    private List<SceneDisplay> sceneDisplays;

    private int displayCounter = 0;
    private boolean isDisplayToShow = true;

    @FXML
    private void initialize() {
        sceneDisplays = new ArrayList<>();
    }

    public void start(IntroductionScene introductionController) {
        this.introductionController = introductionController;
        prepareSceneDisplays();
        initMouseEvents();
    }

    private void prepareSceneDisplays() {
        addIntroductionDialog();
        addSeparatorTransition();
        addMoveHalfDownTransition();
    }

    private void addIntroductionDialog() {
        JFXDialog dialog = introductionController.returnDialog("Some info", "Title");
        sceneDisplays.add(new SceneDisplay(dialog));
    }

    private void addSeparatorTransition() {
        Separator separator = getVerticalSeparator();
        keyHBox.getChildren().add(keyHBox.getChildren().size() / 2, separator);
    }

    private Separator getVerticalSeparator() {
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setMinHeight(100);
        separator.setStyle("-fx-background-color: red; -fx-padding: 0 -2 0 -2;");
        return separator;
    }

    private void addMoveHalfDownTransition() {
        Animation animation = moveNodes(keyHBox, keyHBox.getChildren().size() / 2);
        CommentedAnimation cAnimation = new CommentedAnimation(animation, null);
        sceneDisplays.add(new SceneDisplay(cAnimation));
    }

    private ParallelTransition moveNodes(HBox hbox, int quantity) {
        Animation[] animations = new Animation[quantity];
        for (int i = 0; i < quantity; i++) {
            Node node = hbox.getChildren().get(i);
            animations[i] = getTranslateTransition(node, 0, 0, 0, 200);
        }

        ParallelTransition moveDownTransition = new ParallelTransition(animations);
        return moveDownTransition;
    }

    private void initMouseEvents() {
        scenePane.setOnMouseClicked(e -> {
            if (isDisplayToShow && displayCounter < sceneDisplays.size() && e.getButton() == MouseButton.PRIMARY) {
                showDisplay();
            }
        });
    }

    private void showDisplay() {
        SceneDisplay sceneDisplay = sceneDisplays.get(displayCounter);
        useSceneDisplay(sceneDisplay);
        displayCounter++;
        isDisplayToShow = false;
    }

    private void useSceneDisplay(SceneDisplay sceneDisplay) {
        if (sceneDisplay.getState().equals("animation")) {
            Animation animation = sceneDisplay.getcAnimation().getAnimation();
            animation.setOnFinished(e -> isDisplayToShow = true);
            animation.play();
        } else {
            JFXDialog dialog = sceneDisplay.getDialog();
            EventHandler<? super JFXDialogEvent> currentEvent = dialog.getOnDialogClosed();
            dialog.setOnDialogClosed(e -> {
                currentEvent.handle(e);
                isDisplayToShow = true;
            });
            dialog.show();
        }
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









    private void rectThings() {
        double scale = 0.5625;
        Region rect1 = new Region();
//        rect1.setStyle("-fx-background-color: transparent; -fx-border-style: solid; -fx-border-width: 5; -fx-border-color: red; -fx-min-width: 200; -fx-min-height:100; -fx-max-width:200; -fx-max-height: 100;");
        rect1.setStyle("-fx-background-color: transparent; -fx-border-style: solid; -fx-border-width: 5; -fx-border-color: red;");
        rect1.setPrefSize(60, 60);
//        rect1.setLayoutX(280);
//        rect1.setLayoutX(360);
//        rect1.setLayoutY(50 / scale);
        scenePane.getChildren().add(rect1);
        rect1.toFront();
    }
}
