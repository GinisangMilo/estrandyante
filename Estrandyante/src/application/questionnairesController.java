package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import textfields.updateQuestion;

public class questionnairesController {

    @FXML
    private TableView<Questionnaire> QuesTable;

    @FXML
    private TableColumn<Questionnaire, Integer> colQuesID;

    @FXML
    private TableColumn<Questionnaire, String> colQDesc;

    @FXML
    private TableColumn<Questionnaire, String> colQA;

    @FXML
    private TableColumn<Questionnaire, String> colQB;

    @FXML
    private TableColumn<Questionnaire, String> colQC;

    @FXML
    private TableColumn<Questionnaire, String> colQD;

    @FXML
    private TableColumn<Questionnaire, String> colAns;
    
    @FXML
    private TableColumn<Questionnaire, String> colStrand;

    @FXML
    private Button btnAddQuestion;
    @FXML
    private Button btnDeleteQuestion;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnback;

    private ObservableList<Questionnaire> QuesList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        colQuesID.setCellValueFactory(new PropertyValueFactory<>("QuestionID"));
        colQDesc.setCellValueFactory(new PropertyValueFactory<>("QDescription"));
        colQA.setCellValueFactory(new PropertyValueFactory<>("QAChoice"));
        colQB.setCellValueFactory(new PropertyValueFactory<>("QBChoice"));
        colQC.setCellValueFactory(new PropertyValueFactory<>("QCChoice"));
        colQD.setCellValueFactory(new PropertyValueFactory<>("QDChoice"));
        colAns.setCellValueFactory(new PropertyValueFactory<>("Answer"));
        colStrand.setCellValueFactory(new PropertyValueFactory<>("Strand"));
        loadQuesData();
    }

    @FXML
    private void loadQuesData() {
        String query = "SELECT q.QuestionID, q.QDescription, "
                + "q.QAChoice, q.QBChoice, q.QCChoice, q.QDChoice, "
                + "c.AnsID, c.Answer, "
                + "s.StrandName "
                + "FROM questionnaires q "
                + "JOIN choices c ON c.AnsID = q.AnsID "
                + "LEFT JOIN strands s ON s.StrandID = q.StrandID;";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            QuesList.clear();

            while (rs.next()) {
                int questionID = rs.getInt("QuestionID");
                String qDescription = rs.getString("QDescription");
                String qaChoice = rs.getString("QAChoice");
                String qbChoice = rs.getString("QBChoice");
                String qcChoice = rs.getString("QCChoice");
                String qdChoice = rs.getString("QDChoice");
                String answer = rs.getString("Answer");
                String strand = rs.getString("StrandName");
                int ansID = rs.getInt("AnsID");


                if (strand == null) {
                    strand = "No Strand";
                }


                QuesList.add(new Questionnaire(questionID, qDescription, qaChoice, qbChoice, qcChoice, qdChoice, answer, strand, ansID));
            }

            QuesTable.setItems(QuesList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error while loading questions: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddquestion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/addquestions.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            Scene newScene = new Scene(root);

            newStage.setScene(newScene);
            newStage.setTitle("Add Question");
            newStage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the new panel: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateQuestion() {

        Questionnaire selectedQuestion = QuesTable.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            showAlert("Error", "Please select a question to update.", AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/updateQuestion.fxml"));
            Parent root = loader.load();

            updateQuestion controller = loader.getController();

            controller.initializeWithQuestionData(
                selectedQuestion.getQuestionID(),
                selectedQuestion.getQDescription(),
                selectedQuestion.getQAChoice(),
                selectedQuestion.getQBChoice(),
                selectedQuestion.getQCChoice(),
                selectedQuestion.getQDChoice(),
                selectedQuestion.getAnswer(),
                selectedQuestion.getStrand()
            );

            Stage stage = new Stage();
            stage.setTitle("Update Question");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the update screen: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteQuestion() {
        Questionnaire selectedQuestion = QuesTable.getSelectionModel().getSelectedItem();

        if (selectedQuestion == null) {
            showAlert("Error", "Please select a question to delete.", AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this question?");
        confirmAlert.setContentText("Question ID: " + selectedQuestion.getQuestionID() + "\n" + selectedQuestion.getQDescription());

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            deleteQuestionFromDatabase(selectedQuestion.getQuestionID());
        }
    }

    private void deleteQuestionFromDatabase(int questionID) {
        String query = "DELETE FROM questionnaires WHERE QuestionID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, questionID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Question deleted successfully.", AlertType.INFORMATION);
                loadQuesData();
            } else {
                showAlert("Error", "Question could not be deleted. Please try again.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error while deleting question: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadQuesData();
        showAlert("Success", "Questionnaire data refreshed!", AlertType.INFORMATION);
    }

    @FXML
    private void handleback() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("admindashboard.fxml"));
            Parent root = loader.load();

 
            Scene scene = new Scene(root);
            
  
            Stage currentStage = (Stage) btnback.getScene().getWindow();
            
   
            currentStage.setScene(scene);
            currentStage.setTitle("");
            
           

            currentStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handlebackf() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("facultydashboard.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            
            Stage currentStage = (Stage) btnback.getScene().getWindow();
            
            currentStage.setScene(scene);
            currentStage.setTitle("");
           

            currentStage.show();
            
        } catch (IOException e) {
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
}
