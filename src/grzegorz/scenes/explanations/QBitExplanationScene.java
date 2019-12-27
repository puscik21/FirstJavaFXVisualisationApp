package grzegorz.scenes.explanations;

import grzegorz.general.Animator;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class QBitExplanationScene {

    @FXML
    private Text basicField;

    @FXML
    private Text expField;

    @FXML
    private Text rotationExpField;

    @FXML
    private Text valTextField;

    @FXML
    private Text valField;

    @FXML
    private ImageView imgView;

    private ArrayList<Image> rectPhotonsImages;
    private ArrayList<Image> diagPhotonsImages;
    private final int[] qBitsStatesValues = new int[]{1, 1, 0, 0};
    private int rectImgCounter = 0;
    private int diagImgCounter = 0;
    private int stateCounter = 2;

    public void initialize() {
        basicField.setText("Qubit is a basic unit of quantum information");
        expField.setText("Like normal bit, it can has value of 0 or 1, but also can be a superposition of both");
        rotationExpField.setText("It can also be rotated by 45 degrees, what gives us 4 possible values overall");
        valTextField.setText("Value:");
        valField.setText(String.valueOf(rectImgCounter));

        expField.setVisible(false);
        rotationExpField.setVisible(false);
    }

    public void start() {
        prepareImages();
        int cycles = 2;
        SequentialTransition rectPhotonsAnimation = getRectPhotonsPresentation(cycles);
        SequentialTransition diagPhotonsAnimation = getDiagPhotonsPresentation(cycles);
        SequentialTransition rotationTransition = getRotationAnimation();
        SequentialTransition presentationAnimation = new SequentialTransition(rectPhotonsAnimation, diagPhotonsAnimation, rotationTransition);
        scheduleAnimationStart(presentationAnimation);
    }

    private void scheduleAnimationStart(SequentialTransition presentationAnimation) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                expField.setVisible(true);
                presentationAnimation.play();
            }
        };
        Timer timer = new Timer();
        long delay = 1500L;
        timer.schedule(task, delay);
    }

    private SequentialTransition getRectPhotonsPresentation(int cycles) {
        ParallelTransition hideTrans = hideNode();
        hideTrans.setOnFinished(e -> changeRectImage());
        SequentialTransition sequentialTransition = new SequentialTransition(hideTrans, showNode());
        sequentialTransition.setCycleCount(cycles);

        sequentialTransition.setOnFinished(e -> {
            rotationExpField.setVisible(true);
            ParallelTransition hideNodeTrans = hideNode();
            hideNodeTrans.setOnFinished(ev -> changeDiagImage());
            hideNodeTrans.play();
        });
        return sequentialTransition;
    }

    private SequentialTransition getDiagPhotonsPresentation(int cycles) {
        ParallelTransition hideTrans = hideNode();
        hideTrans.setOnFinished(e -> changeDiagImage());
        SequentialTransition sequentialTransition = new SequentialTransition(showNode(), hideTrans);
        sequentialTransition.setCycleCount(cycles);
        sequentialTransition.setDelay(Duration.seconds(1.0));

        sequentialTransition.setOnFinished(e -> showNode().play());
        return sequentialTransition;
    }

    private void prepareImages() {
        Image verPhoton = new Image("grzegorz\\images\\verPhoton.png");
        Image rightDiagPhoton = new Image("grzegorz\\images\\rightDiagPhoton.png");
        Image horPhoton = new Image("grzegorz\\images\\horPhoton.png");
        Image leftDiagPhoton = new Image("grzegorz\\images\\leftDiagPhoton.png");

        rectPhotonsImages = new ArrayList<>(2);
        diagPhotonsImages = new ArrayList<>(2);

        rectPhotonsImages.addAll(Arrays.asList(horPhoton, verPhoton));
        diagPhotonsImages.addAll(Arrays.asList(leftDiagPhoton, rightDiagPhoton));
    }

    private void changeRectImage() {
        rectImgCounter++;
        rectImgCounter = rectImgCounter >= 2 ? 0 : rectImgCounter;

        imgView.setImage(rectPhotonsImages.get(rectImgCounter));
        valField.setText(Integer.toString(rectImgCounter));
    }

    private void changeDiagImage() {
        diagImgCounter++;
        diagImgCounter = diagImgCounter >= 2 ? 0 : diagImgCounter;

        imgView.setImage(diagPhotonsImages.get(diagImgCounter));
        valField.setText(Integer.toString(diagImgCounter));
    }

    private ParallelTransition showNode() {
        ScaleTransition scaleTransition = getScaleTransition(imgView, 0.0, 1.0, 0.75);
        FadeTransition fadeTransition = getFadeTransition(imgView, 0.0, 1.0);
        return new ParallelTransition(scaleTransition, fadeTransition);
    }

    private ParallelTransition hideNode() {
        ScaleTransition scaleTransition = getScaleTransition(imgView, 1.0, 0.0, 0.75);
        FadeTransition fadeTransition = getFadeTransition(imgView, 1.0, 0.0);
        return new ParallelTransition(scaleTransition, fadeTransition);
    }

    private ScaleTransition getScaleTransition(Node node, double from, double to, double duration) {
        return Animator.getScaleTransition(node, from, to, duration);
    }

    private FadeTransition getFadeTransition(Node node, double fromVal, double toVal) {
        return Animator.getFadeTransition(node, fromVal, toVal, 0.5);
    }

    private SequentialTransition getRotationAnimation() {
        RotateTransition[] rotations = new RotateTransition[8];
        for (int i = 0; i < 8; i++) {
            rotations[i] = getToNextStateRotation(i * 45.0, i * 45.0 + 45.0);
        }
        rotations[0].setDelay(Duration.seconds(0));

        SequentialTransition wholeRotationTransition = new SequentialTransition(rotations);
        wholeRotationTransition.setDelay(Duration.seconds(1));
        wholeRotationTransition.setCycleCount(-1);
        return wholeRotationTransition;
    }

    private RotateTransition getToNextStateRotation(double fromAngle, double toAngle) {
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setNode(imgView);
        rotateTransition.setDelay(Duration.seconds(0.5));
        rotateTransition.setDuration(Duration.seconds(0.5));
        rotateTransition.setFromAngle(fromAngle);
        rotateTransition.setToAngle(toAngle);
        rotateTransition.setOnFinished(e -> changeQbitVal());
        return rotateTransition;
    }

    private void changeQbitVal() {
        String val = String.valueOf(qBitsStatesValues[stateCounter]);
        valField.setText(val);

        stateCounter++;
        if (stateCounter == 4) {
            stateCounter = 0;
        }
    }
}
