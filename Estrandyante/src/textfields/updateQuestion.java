package textfields;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import application.dbconnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class updateQuestion {

    @FXML
    private TextArea txtQDesc;
    @FXML
    private TextField txtQAChoice;
    @FXML
    private TextField txtQBChoice;
    @FXML
    private TextField txtQCChoice;
    @FXML
    private TextField txtQDChoice;
    @FXML
    private ComboBox<String> comboAnswer;
    @FXML
    private ComboBox<String> comboStrand;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private int questionID;

    public void initializeWithQuestionData(int questionID, String qDescription, String qaChoice, 
                                           String qbChoice, String qcChoice, String qdChoice, 
                                           String answer, String strand) {
        this.questionID = questionID;

        txtQDesc.setText(qDescription);
        txtQAChoice.setText(qaChoice);
        txtQBChoice.setText(qbChoice);
        txtQCChoice.setText(qcChoice);
        txtQDChoice.setText(qdChoice);

        comboAnswer.getItems().setAll("A", "B", "C", "D");
        comboStrand.getItems().setAll(getStrandsFromDatabase());
        comboAnswer.setValue(answer);
        comboStrand.setValue(strand);
    }

    private String[] getStrandsFromDatabase() {
        ArrayList<String> strandList = new ArrayList<>();
        String query = "SELECT StrandName FROM strands";
        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                strandList.add(rs.getString("StrandName"));
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while fetching strands: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        return strandList.toArray(new String[0]);
    }

    private int getAnswerIDFromAnswer(String answer) {
        String query = "SELECT AnsID FROM choices WHERE Answer = ?";
        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, answer);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AnsID");
                } else {
                    showAlert("Error", "Answer ID not found for selected answer: " + answer, AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while fetching Answer ID: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        return -1;
    }

    private int getStrandIDFromStrand(String strandName) {
        String query = "SELECT StrandID FROM strands WHERE StrandName = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, strandName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("StrandID");
                } else {
                    showAlert("Error", "Strand ID not found for selected strand: " + strandName, AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while fetching Strand ID: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        return -1;
    }

    @FXML
    private void handleSaveChanges() {
        String updatedQDesc = txtQDesc.getText();
        String updatedQAChoice = txtQAChoice.getText();
        String updatedQBChoice = txtQBChoice.getText();
        String updatedQCChoice = txtQCChoice.getText();
        String updatedQDChoice = txtQDChoice.getText();
        String updatedAnswer = comboAnswer.getValue();
        String updatedStrand = comboStrand.getValue();

        if (updatedQDesc.isEmpty() || updatedQAChoice.isEmpty() || updatedQBChoice.isEmpty() || 
            updatedQCChoice.isEmpty() || updatedQDChoice.isEmpty() || updatedAnswer == null || updatedStrand == null) {
            showAlert("Error", "All fields must be filled in!", AlertType.ERROR);
            return;
        }

        int answerID = getAnswerIDFromAnswer(updatedAnswer);
        int strandID = getStrandIDFromStrand(updatedStrand);

        if (answerID == -1 || strandID == -1) {
            return;
        }

        updateQuestionInDatabase(updatedQDesc, updatedQAChoice, updatedQBChoice, updatedQCChoice, 
                                  updatedQDChoice, answerID, strandID);
    }

    private void updateQuestionInDatabase(String qDesc, String qaChoice, String qbChoice, String qcChoice,
                                          String qdChoice, int answerID, int strandID) {
        String updateQuery = "UPDATE questionnaires SET QDescription = ?, QAChoice = ?, QBChoice = ?, QCChoice = ?, "
                             + "QDChoice = ?, AnsID = ?, StrandID = ? WHERE QuestionID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, qDesc);
            stmt.setString(2, qaChoice);
            stmt.setString(3, qbChoice);
            stmt.setString(4, qcChoice);
            stmt.setString(5, qdChoice);
            stmt.setInt(6, answerID);
            stmt.setInt(7, strandID);
            stmt.setInt(8, questionID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Question updated successfully.", AlertType.INFORMATION);
                closeWindow();
            } else {
                showAlert("Error", "Failed to update the question.", AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while updating question: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }
}
