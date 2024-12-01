package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import textfields.studinfoUpdate;
import javafx.scene.control.cell.PropertyValueFactory;

public class studentinfoController {

    @FXML
    private TableView<Students> studentTable;

    @FXML
    private TableColumn<Students, Integer> colStudentID;
    @FXML
    private TableColumn<Students, String> colEmail;
    @FXML
    private TableColumn<Students, String> colFname;
    @FXML
    private TableColumn<Students, String> colMname;
    @FXML
    private TableColumn<Students, String> colLname;
    @FXML
    private TableColumn<Students, String> colEname;
    @FXML
    private TableColumn<Students, String> colBirthdate;

    @FXML
    private TableColumn<Students, String> colRecommendedStrand;

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtFname;
    @FXML
    private TextField txtMname;
    @FXML
    private TextField txtLname;
    @FXML
    private TextField txtEname;
    @FXML
    private DatePicker birthdatePicker;

    @FXML
    private Button btnAddStud;
    @FXML
    private Button btnDeleteStud;

    private ObservableList<Students> studentList = FXCollections.observableArrayList();
    @FXML
	private Button btnback;

    @FXML
    private void initialize() {
        loadStudentData();
    }

    private void loadStudentData() {
        String query = "SELECT s.StudID, s.Email, s.Fname, s.Lname, s.Mname, s.Ename, s.Birthdate, s.RecommendedStrand " +
                       "FROM students s";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            studentList.clear();
            while (rs.next()) {
                int studID = rs.getInt("StudID");
                String email = rs.getString("Email");
                String fname = rs.getString("Fname");
                String lname = rs.getString("Lname");
                String mname = rs.getString("Mname");
                String ename = rs.getString("Ename");
                String bdate = rs.getString("Birthdate");
                String recommendedStrand = rs.getString("RecommendedStrand");

                studentList.add(new Students(studID, email, fname, lname, mname, ename, bdate, recommendedStrand));

            }

            colStudentID.setCellValueFactory(new PropertyValueFactory<>("StudID"));
            colEmail.setCellValueFactory(new PropertyValueFactory<>("Email"));
            colFname.setCellValueFactory(new PropertyValueFactory<>("Fname"));
            colMname.setCellValueFactory(new PropertyValueFactory<>("Mname"));
            colLname.setCellValueFactory(new PropertyValueFactory<>("Lname"));
            colEname.setCellValueFactory(new PropertyValueFactory<>("Ename"));
            colBirthdate.setCellValueFactory(new PropertyValueFactory<>("Birthdate"));
            colRecommendedStrand.setCellValueFactory(new PropertyValueFactory<>("RecommendedStrand"));

            studentTable.setItems(studentList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error while loading student data: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateReportWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reportGeneration.fxml"));
            Parent root = loader.load();

            ReportGenerationController reportController = loader.getController();
            reportController.setStudentList(studentList);

            Stage stage = new Stage();
            stage.setTitle("Generate Report by Strand");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void handleDeleteStudent() {
        Students selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert("Error", "Please select a student to delete.", AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this student?");
        confirmAlert.setContentText("Student ID: " + selectedStudent.getStudID() + "\n" + selectedStudent.getEmail());

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            deleteStudentFromDatabase(selectedStudent.getStudID());
        }
    }

    private void deleteStudentFromDatabase(int studID) {
        String query = "DELETE FROM students WHERE StudID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, studID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Student deleted successfully.", AlertType.INFORMATION);
                loadStudentData();
            } else {
                showAlert("Error", "Student could not be deleted. Please try again.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error while deleting student: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadStudentData();
        showAlert("Success", "Student data refreshed!", AlertType.INFORMATION);
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
    private void handlefback() {
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
    @FXML
    private void handleUpdateStudent() {
        Students selectedStudent = studentTable.getSelectionModel().getSelectedItem();

        if (selectedStudent == null) {
            showAlert("Error", "Please select a student to update.", AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/updateStudInfo.fxml"));
            Parent root = loader.load();
            studinfoUpdate updateController = loader.getController();
            updateController.initializeWithStudentData(selectedStudent);
            Stage stage = new Stage();
            stage.setTitle("Update Student");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the update student form: " + e.getMessage(), AlertType.ERROR);
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
    public void loadStudentDataForUser(int userID) {
        String query = "SELECT s.StudID, s.Email, s.Fname, s.Lname, s.Mname, s.Ename, s.Birthdate, s.RecommendedStrand " +
                       "FROM students s " +
                       "JOIN users u ON s.UserID = u.UserID " +
                       "WHERE u.UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            studentList.clear();
            if (rs.next()) {
                int studID = rs.getInt("StudID");
                String email = rs.getString("Email");
                String fname = rs.getString("Fname");
                String lname = rs.getString("Lname");
                String mname = rs.getString("Mname");
                String ename = rs.getString("Ename");
                String bdate = rs.getString("Birthdate");
                String recommendedStrand = rs.getString("RecommendedStrand");
                studentList.add(new Students(studID, email, fname, lname, mname, ename, bdate, recommendedStrand));
            }

            studentTable.setItems(studentList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error while loading student data for user: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
