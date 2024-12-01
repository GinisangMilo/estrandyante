package textfields;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private int questionID;
    private String questionText;
    private int strandID;
    private int correctAnsID;
    private List<String> options; // List of answer choices (Strings)
    private List<Integer> answerIDs; // List of corresponding Answer IDs

    public Question(int questionID, String questionText, int strandID2, int correctAnsID) {
        this.questionID = questionID;
        this.questionText = questionText;
        this.strandID = strandID2;
        this.correctAnsID = correctAnsID;
        this.options = new ArrayList<>();
        this.answerIDs = new ArrayList<>();
    }

    // Getters and Setters
    public int getquestionID() {
        return questionID;
    }

    public void setquestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getquestionText() {
        return questionText;
    }

    public void setquestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getstrandID() {
        return strandID;
    }

    public void setstrandID(int strandID) {
        this.strandID = strandID;
    }

    public int getcorrectAnsID() {
        return correctAnsID;
    }

    public void setcorrectAnsID(int correctAnsID) {
        this.correctAnsID = correctAnsID;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option, int ansID) {
        this.options.add(option);
        this.answerIDs.add(ansID);
    }

    public List<Integer> getAnswerIDs() {
        return answerIDs;
    }

    // Create the UI for the answer options as RadioButtons
    public VBox createAnswerOptions() {
        VBox optionsBox = new VBox(10);  // VBox to hold the radio buttons

        // Create a toggle group for the radio buttons (only one can be selected)
        ToggleGroup toggleGroup = new ToggleGroup();

        // Create a radio button for each option
        for (int i = 0; i < options.size(); i++) {
            RadioButton radioButton = new RadioButton(options.get(i));
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setId("answer-" + answerIDs.get(i));
            optionsBox.getChildren().add(radioButton);
        }

        return optionsBox;
    }
}

