package grzegorz.scenes.enterQBitCombination;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import grzegorz.QBitState;
import grzegorz.scenes.quantumScene.QuantumScene;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.math.BigInteger;
import java.util.Random;

public class EnterQBitCombination {
    @FXML
    private AnchorPane scenePane;

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

    private QuantumScene parentController;
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
            bitsText.setText("");
            return false;
        }

        String maxVal = "65535";
        BigInteger bigInt = new BigInteger(value);
        if (bigInt.compareTo(new BigInteger(maxVal)) > 0) {
            bitsText.setText("Value is too big!");
            return false;
        }
        return true;
    }

    public void start(QuantumScene parentController) {
        this.parentController = parentController;
        generator = new Random();
        binaryRepresentation = "";
    }

    private void convertToBits(String number) {
        binaryRepresentation = Integer.toBinaryString(Integer.parseInt(number));
        bitsText.setText(binaryRepresentation);
    }

    @FXML
    private void sendBitCombination() {
        if (binaryRepresentation.length() >= 4 && isNewValueProper(numberField.getText())) {
            QBitState[] userQBitStates = convertToQBitStates();
            parentController.setUserCombination(userQBitStates, true);
        } else {
            showErrorMessage("Binary value must be from 4 to 16 digits long", "Wrong input value");
        }
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

    private void showErrorMessage(String message, String title) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        if (!title.isEmpty()) {
            dialogLayout.setHeading(new Text(title));
        }
        Text text = new Text(message);
        dialogLayout.setBody(text);

        JFXDialog dialog = new JFXDialog(parentController.getRootPane(), dialogLayout, JFXDialog.DialogTransition.TOP);
        dialog.show();
    }

    @FXML
    private void sendRandomCombination() {
        parentController.setUserCombination(false);
    }
}
