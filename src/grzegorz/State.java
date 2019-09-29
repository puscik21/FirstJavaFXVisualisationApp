package grzegorz;

public class State {

    public enum QBitState {
        VERTICAL(0),
        RIGHT_DIAGONAL(1),
        HORIZONTAL(2),
        LEFT_DIAGONAL(3);

        int state;

        private QBitState(int state) {
            this.state = state;
        }

        public int getQBitValue() {
            if (state <= 1) {
                return 1;
            } else {
                return 0;
            }
        }

        public void turnQBit(int direction){
            this.state += direction;

            if (state == 4) {
                state = 0;
            } else if (state == -1) {
                state = 3;
            }
        }
    }

    public static QBitState getQBitState(int i) {
        return QBitState.values()[i];
    }


    public enum FilterState {
        RECTILINEAR(0),
        DIAGONAL(1);

        int state;

        private FilterState(int state) {
            this.state = state;
        }

        public boolean isQBitProper(QBitState qBitState) {
            if (this == RECTILINEAR) {
                return qBitState == QBitState.VERTICAL || qBitState == QBitState.HORIZONTAL;
            } else {
                return qBitState == QBitState.RIGHT_DIAGONAL || qBitState == QBitState.LEFT_DIAGONAL;
            }
        }
    }

    public static FilterState getFilterState(int i) {
        return FilterState.values()[i];
    }
}
