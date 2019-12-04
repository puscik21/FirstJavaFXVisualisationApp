package grzegorz.general;

public class QBitState {

    private final int VERTICAL = 0;
    private final int RIGHT_DIAGONAL = 1;
    private final int HORIZONTAL = 2;
    private final int LEFT_DIAGONAL = 3;

    private final int RECTILINEAR = 0;
    private final int DIAGONAL = 1;

    private int state;
    private int value;

    public QBitState(int state, int value) {
        this.state = state;
        this.value = value;
    }

    public static QBitState getNewQBit(int state) {
        switch (state) {
            case 0:
            case 1:
                return new QBitState(state, 1);
            case 2:
            case 3:
            default:
                return new QBitState(state, 0);
        }
    }

    public int getState() {
        return state;
    }

    public int getValue() {
        return value;
    }

    public void turnQBit(int direction) {
        state += direction;

        if (state == 4) {
            state = 0;
        } else if (state == -1) {
            state = 3;
        }
        updateValue();
    }

    private void updateValue() {
        if (state <= 1) {
            value = 1;
        } else {
            value = 0;
        }
    }

    public boolean isFilterWrong(int filterState) {
        if (filterState == RECTILINEAR) {
            return state != VERTICAL && state != HORIZONTAL;
        } else {
            return state != RIGHT_DIAGONAL && state != LEFT_DIAGONAL;
        }
    }

    public int getDirectionToState(QBitState qBitState) {
        int direction = qBitState.getState() - this.getState();
        if (direction < -1) {
            direction = 1;
        } else if (direction > 1) {
            direction = -1;
        }
        return direction;
    }
}
