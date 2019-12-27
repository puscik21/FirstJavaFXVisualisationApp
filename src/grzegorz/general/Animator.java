package grzegorz.general;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Animator {

    public static TranslateTransition getTranslateTransition(Node imageView, double fromX, double fromY, double toX, double toY, double duration) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(duration));
        transition.setNode(imageView);
        transition.setFromX(fromX);
        transition.setFromY(fromY);
        transition.setToX(toX);
        transition.setToY(toY);
        return transition;
    }

    public static  ScaleTransition getScaleTransition(Node node, double from, double to, double duration) {
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDuration(Duration.seconds(duration));
        scaleTransition.setFromX(from);
        scaleTransition.setFromY(from);
        scaleTransition.setToX(to);
        scaleTransition.setToY(to);
        return scaleTransition;
    }

    public static  FadeTransition getFadeTransition(Node node, double from, double to, double duration) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.seconds(duration));
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        return fadeTransition;
    }

    public static RotateTransition getRotateTransition(Node node, int direction, double angle, double duration) {
        RotateTransition transition = new RotateTransition();
        transition.setDuration(Duration.seconds(duration));
        transition.setNode(node);
        transition.setByAngle(direction * angle);
        return transition;
    }

    public static DropShadow getHighlightEffect() {
        DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.WHITESMOKE);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        borderGlow.setHeight(50);
        borderGlow.setWidth(50);
        return borderGlow;
    }
}
