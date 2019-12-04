package grzegorz;

import com.jfoenix.controls.JFXDialog;
import javafx.animation.Animation;

public class SceneDisplay {
    private JFXDialog dialog;
    private Animation animation;
    private CommentedAnimation cAnimation;
    private final String state;

    public SceneDisplay(Animation animation) {
        this.animation = animation;
        state = "animation";
    }

    public SceneDisplay(CommentedAnimation cAnimation) {
        this.cAnimation = cAnimation;
        state = "cAnimation";
    }

    public SceneDisplay(JFXDialog dialog) {
        this.dialog = dialog;
        state = "dialog";
    }

    public Animation getAnimation() {
        return animation;
    }

    public CommentedAnimation getCAnimation() {
        return cAnimation;
    }

    public JFXDialog getDialog() {
        return dialog;
    }

    public String getState() {
        return state;
    }
}
