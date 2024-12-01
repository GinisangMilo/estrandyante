package textfields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.dbconnect;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class addquestions {
	  @FXML
	    private TextArea txtQuestion;
	    @FXML
	    private TextField txtQAChoice;
	    @FXML
	    private TextField txtQBChoice;
	    @FXML
	    private TextField txtQCChoice;
	    @FXML
	    private TextField txtQDChoice;
	    @FXML
	    private ComboBox<String> comboAns;
	    
	    @FXML
	    private ComboBox<String> comboStrand;
	    @FXML
	    private Button btnAddQues;
	    @FXML
		private Button btnClose;
	    
	    @FXML
	    private void initialize() {
	        loadChoices();
	        loadStrand();
	    }
	        @FXML
	        private void loadChoices() {
	            String query = "SELECT Answer FROM choices";

	            ArrayList<String> answerList = new ArrayList<>();

	            try (Connection conn = dbconnect.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {

	                while (rs.next()) {
	                    answerList.add(rs.getString("Answer"));
	                }

	                comboAns.setItems(FXCollections.observableArrayList(answerList));

	            } catch (SQLException e) {
	                showAlert("Database Error", "Error while loading choices: " + e.getMessage(), AlertType.ERROR);
	                e.printStackTrace();
	            }
	        }
	        @FXML
	        private void loadStrand() {
	            String query = "SELECT StrandName FROM strands";

	            ArrayList<String> strandList = new ArrayList<>();

	            try (Connection conn = dbconnect.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(query);
	                 ResultSet rs = stmt.executeQuery()) {

	                while (rs.next()) {
	                    strandList.add(rs.getString("StrandName"));
	                }

	                comboStrand.setItems(FXCollections.observableArrayList(strandList));

	            } catch (SQLException e) {
	                showAlert("Database Error", "Error while loading strands: " + e.getMessage(), AlertType.ERROR);
	                e.printStackTrace();
	            }
	        }

	        @FXML
	        private void addQues() {
	            String question = txtQuestion.getText();
	            String qa = txtQAChoice.getText();
	            String qb = txtQBChoice.getText();
	            String qc = txtQCChoice.getText();
	            String qd = txtQDChoice.getText();

	            String selectedChoice = comboAns.getValue();
	            String strand = comboStrand.getValue();

	            if (question.isEmpty() || qa.isEmpty() || qb.isEmpty() || qc.isEmpty() || qd.isEmpty() || selectedChoice == null || strand == null) {
	                showAlert("Error", "Question, Choices A, B, C, D, and Selected Answer must not be empty", AlertType.ERROR);
	                return;
	            }

	            int ansID = getChoiceIDByName(selectedChoice);

	            if (ansID == -1) {
	                showAlert("Error", "Invalid Answer selected", AlertType.ERROR);
	                return;
	            }

	            int strandID = getStrandIDByName(strand);

	            if (strandID == -1) {
	                showAlert("Error", "Invalid Strand selected", AlertType.ERROR);
	                return;
	            }

	            String query = "INSERT INTO questionnaires (QDescription, QAChoice, QBChoice, QCChoice, QDChoice, AnsID, StrandID) VALUES (?, ?, ?, ?, ?, ?, ?)";

	            try (Connection conn = dbconnect.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(query)) {

	                stmt.setString(1, question);
	                stmt.setString(2, qa);
	                stmt.setString(3, qb);
	                stmt.setString(4, qc);
	                stmt.setString(5, qd);
	                stmt.setInt(6, ansID);
	                stmt.setInt(7, strandID);

	                int rowsAffected = stmt.executeUpdate();
	                if (rowsAffected > 0) {
	                    showAlert("Success", "Question added successfully", AlertType.INFORMATION);
	                } else {
	                    showAlert("Error", "Question could not be added", AlertType.ERROR);
	                }

	            } catch (SQLException e) {
	                showAlert("Database Error", "Error while adding question: " + e.getMessage(), AlertType.ERROR);
	                e.printStackTrace();
	            }
	        }

	        private void showAlert(String title, String message, AlertType type) {
	            Alert alert = new Alert(type);
	            alert.setTitle(title);
	            alert.setHeaderText(null);
	            alert.setContentText(message);
	            alert.showAndWait();
	            
	        Stage stage = (Stage) btnAddQues.getScene().getWindow();
	        stage.close();
}
	        @FXML
	        private void handleCloseWindow() {
	            Stage stage = (Stage) btnClose.getScene().getWindow();
	            stage.close();
	        }
	        private int getChoiceIDByName(String choice) {
	            String query = "SELECT AnsID FROM choices WHERE Answer = ?";
	            try (Connection conn = dbconnect.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(query)) {

	                stmt.setString(1, choice);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    if (rs.next()) {
	                        return rs.getInt("AnsID");
	                    }
	                }
	            } catch (SQLException e) {
	                showAlert("Database Error", "Error while fetching AnsID: " + e.getMessage(), AlertType.ERROR);
	                e.printStackTrace();
	            }
	            return -1;
	            
	        }
	        
	        private int getStrandIDByName(String strand) {
	            String query = "SELECT StrandID FROM strands WHERE StrandName = ?";
	            try (Connection conn = dbconnect.getConnection();
	                 PreparedStatement stmt = conn.prepareStatement(query)) {

	                stmt.setString(1, strand);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    if (rs.next()) {
	                        return rs.getInt("StrandID");
	                    }
	                }
	            } catch (SQLException e) {
	                showAlert("Database Error", "Error while fetching StrandID: " + e.getMessage(), AlertType.ERROR);
	                e.printStackTrace();
	            }
	            return -1;
	        }

}
