package application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminController {

    @FXML
    private Button btnaccsetting;
    @FXML
    private Button btnstudinfo;
    @FXML
    private Button btnquestionnaire;
    @FXML
    private Button btnback;

    @FXML
    private Label lblQuestionCount;
    @FXML
    private Label lblAccountCount;
    @FXML
    private Label lblStudentCount;

    private int getQuestionCount() {
        String query = "SELECT COUNT(*) AS question_count FROM Questionnaires";
        try (Connection connection = dbconnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("question_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getUserCount() {
        String query = "SELECT COUNT(*) AS user_count FROM Users";
        try (Connection connection = dbconnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("user_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getStudentCount() {
        String query = "SELECT COUNT(*) AS student_count FROM Students";
        try (Connection connection = dbconnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("student_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void initialize() {
        int questionCount = getQuestionCount();
        int userCount = getUserCount();
        int studentCount = getStudentCount();
        lblQuestionCount.setText(String.valueOf(questionCount));
        lblAccountCount.setText(String.valueOf(userCount));
        lblStudentCount.setText(String.valueOf(studentCount));
    }

    @FXML
    private void handleviewaccounts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("accsettings.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("View User's Info");
            stage.show();
            
            Stage stages = (Stage) btnaccsetting.getScene().getWindow();
            stages.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handlequestionnaires() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questionnaire.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("View Question and Answers");
            stage.show();
            
            Stage stages = (Stage) btnquestionnaire.getScene().getWindow();
            stages.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handlestudinfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("studentinfo.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("View Student Info");
            stage.show();
            
            Stage stages = (Stage) btnstudinfo.getScene().getWindow();
            stages.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleback() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage currentStage = (Stage) btnback.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
