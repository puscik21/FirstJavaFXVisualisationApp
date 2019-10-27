package grzegorz.scenes.introduction;

import javafx.animation.Animation;
import javafx.util.Duration;

public class CommentedAnimation {
    private Animation animation;
    private String comment;

    public CommentedAnimation(Animation animation, String comment) {
        this.animation = animation;
        this.comment = comment;
    }

    public Animation getAnimation() {
        return animation;
    }

    public String getComment() {
        return comment;
    }
}
