package textfields;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.dbconnect;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Platform;

public class viewstudinfo {

    // UI elements
    @FXML private Label lblEmail;
    @FXML private Label lblFName;
    @FXML private Label lblMName;
    @FXML private Label lblLName;
    @FXML private Label lblEName;
    @FXML private Label lblBirthdate;
    @FXML private Button btntakequestion;
    @FXML private Label lblRecommendation;
    @FXML private Button btnClose;
    private int userID;

    public void initialize(int userID) {
        this.userID = userID;
        loadStudentData(userID);
    }

    void loadStudentData(int userID) {
        String query = "SELECT StudID, UserID, Email, Fname, Lname, Mname, Ename, Birthdate, RecommendedStrand " +
                       "FROM students WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                lblEmail.setText(rs.getString("Email"));
                lblFName.setText(rs.getString("Fname"));
                lblMName.setText(rs.getString("Mname"));
                lblLName.setText(rs.getString("Lname"));
                lblEName.setText(rs.getString("Ename"));
                lblBirthdate.setText(rs.getDate("Birthdate").toString());


                String recommendedStrand = rs.getString("RecommendedStrand");

                lblRecommendation.setText(recommendedStrand);
            } else {
                showAlert("Error", "No student profile found.");
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading student data: " + e.getMessage());
            Logger.getLogger(viewstudinfo.class.getName()).log(Level.SEVERE, "Database error", e);
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void handleTakeQuestionnaire() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/studquestionnaire.fxml"));
            AnchorPane root = loader.load();
            studAnswer questionnaireController = loader.getController();
            questionnaireController.initializeWithUser(userID);
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the questionnaire screen: " + e.getMessage());
            Logger.getLogger(viewstudinfo.class.getName()).log(Level.SEVERE, "FXML loading error", e);
        }
    }
    @FXML
    private void handleback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) btnClose.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setResults(String recommendation) {

        lblRecommendation.setText("Recommended Strand: " + recommendation);
    }
}
