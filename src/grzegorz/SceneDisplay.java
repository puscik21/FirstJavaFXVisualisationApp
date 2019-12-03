package grzegorz;

import com.jfoenix.controls.JFXDialog;

public class SceneDisplay {
    private JFXDialog dialog;
    private CommentedAnimation cAnimation;
    private final String state;

    public SceneDisplay(CommentedAnimation cAnimation) {
        this.cAnimation = cAnimation;
        state = "animation";
    }

    public SceneDisplay(JFXDialog dialog) {
        this.dialog = dialog;
        state = "dialog";
    }

    public JFXDialog getDialog() {
        return dialog;
    }

    public CommentedAnimation getcAnimation() {
        return cAnimation;
    }

    public String getState() {
        return state;
    }
}
