package grzegorz.scenes.choosingQBits;

import com.jfoenix.controls.JFXButton;
import grzegorz.QBitState;
import grzegorz.scenes.introduction.IntroductionScene;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.math.BigInteger;
import java.util.Random;

public class EnterQBitCombination {
    @FXML
    private Label sceneTitle;

    @FXML
    private TextField numberField;

    @FXML
    private Label bitsText;

    @FXML
    private JFXButton randomButton;

    @FXML
    private JFXButton sendButton;

    private IntroductionScene parentController;
    private String binaryRepresentation;
    private Random generator;

    @FXML
    public void initialize() {
        numberField.textProperty().addListener((observable, oldVal, newVal) -> {
            newVal = toOnlyNumericString(newVal);
            numberField.setText(newVal);

            if (isNewValueProper(newVal)) {
                convertToBits(newVal);
            }
        });
    }

    private String toOnlyNumericString(String val) {
        return val.replaceAll("\\D+", "");
    }

    private boolean isNewValueProper(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String maxVal = "65535";
        BigInteger bigInt = new BigInteger(value);
        return bigInt.compareTo(new BigInteger(maxVal)) < 0;
    }

    public void start(IntroductionScene parentController) {
        this.parentController = parentController;
        generator = new Random();
    }

    private void convertToBits(String number) {
        binaryRepresentation = Integer.toBinaryString(Integer.parseInt(number));
        System.out.println(binaryRepresentation);
        bitsText.setText(binaryRepresentation);
    }

    @FXML
    private void sendRandomCombination() {
        QBitState[] userQBitStates = convertToQBitStates();
        parentController.setUserCombination(userQBitStates, false);
    }

    private QBitState[] convertToQBitStates() {
        char[] asArray = binaryRepresentation.toCharArray();
        int size = asArray.length;
        QBitState[] result = new QBitState[size];

        for (int i = 0; i < size; i++) {
            int val = Character.getNumericValue(asArray[i]);
            result[i] = getQBitStateFromValue(val);
        }
        return result;
    }

    private QBitState getQBitStateFromValue(int val) {
        int reversedVal = val == 0 ? 1 : 0;
        int rand = generator.nextInt(2);
        int state = 2 * reversedVal + rand;
        return new QBitState(state, val);
    }

    @FXML
    private void sendBitCombination() {
        parentController.setUserCombination(true);
    }
}
