package textfields;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import application.dbconnect;

public class studinfo {

    @FXML
    private TextField txtemail;
    @FXML
    private TextField txtfname;
    @FXML
    private TextField txtmname;
    @FXML
    private TextField txtlname;
    @FXML
    private TextField txtename;
    @FXML
    private Button btnClose;
    @FXML
    private DatePicker Birthdate;
    @FXML
    private Button btnAddInfo;

    private int userID;
    private String role;


    public void initializeWithUser(int userID, String role) {
        System.out.println("Initializing studinfo controller with userID: " + userID + " and role: " + role);
        this.userID = userID;
        this.role = role;
        loadStudentInfo();
    }


    private void loadStudentInfo() {
        // Query to fetch the email from the users table based on the UserID
        String query = "SELECT Email FROM users WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the UserID to fetch the email from users table
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Set the email from the users table into txtemail field
                txtemail.setText(rs.getString("Email"));
            } else {
                showAlert("Error", "No user found with the provided UserID.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading email from database: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }

        // Now load the other student data from the students table
        loadAdditionalStudentInfo();
    }

    // Method to load other student information (like Fname, Mname, etc.)
    private void loadAdditionalStudentInfo() {
        String query = "SELECT Fname, Mname, Lname, Ename, Birthdate FROM students WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the UserID to fetch other student information
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Set the other student details
                txtfname.setText(rs.getString("Fname"));
                txtmname.setText(rs.getString("Mname"));
                txtlname.setText(rs.getString("Lname"));
                txtename.setText(rs.getString("Ename"));
                Birthdate.setValue(rs.getDate("Birthdate").toLocalDate());
            } else {
               showWelcomeDialog();
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error loading student information: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void saveProfile() throws IOException {

        String email = txtemail.getText();
        String fname = txtfname.getText();
        String mname = txtmname.getText();
        String lname = txtlname.getText();
        String ename = txtename.getText();
        LocalDate localDate = Birthdate.getValue();

        if (email.isEmpty() || fname.isEmpty() || lname.isEmpty() || localDate == null) {
            showAlert("Error", "Email, Firstname, Lastname, and Birthdate must not be empty", AlertType.ERROR);
            return;
        }

        java.sql.Date bdate = java.sql.Date.valueOf(localDate);

        saveStudentProfile(email, fname, mname, lname, ename, bdate);
    }

    // Save the student profile to the database (Insert or Update based on userID existence)
    private void saveStudentProfile(String email, String fname, String mname, String lname, String ename, java.sql.Date bdate) {
        String query;

        if (profileExists(userID)) {
            query = "UPDATE students SET Email = ?, Fname = ?, Mname = ?, Lname = ?, Ename = ?, Birthdate = ? WHERE UserID = ?";
        } else {
            query = "INSERT INTO students (UserID, Email, Fname, Mname, Lname, Ename, Birthdate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userID);
            stmt.setString(2, email);
            stmt.setString(3, fname);
            stmt.setString(4, mname);
            stmt.setString(5, lname);
            stmt.setString(6, ename);
            stmt.setDate(7, bdate);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "Student profile saved successfully.");

                // Load the next screen (viewstudinfo.fxml) after successful save
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/viewstudinfo.fxml"));
                try {
                    AnchorPane root = loader.load();
                    viewstudinfo controller = loader.getController();
                    controller.initialize(userID);

                    Scene scene = new Scene(root);
                    Stage stage = (Stage) btnAddInfo.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();

                } catch (IOException e) {
                    showAlert("Error", "Failed to load the next screen: " + e.getMessage(), AlertType.ERROR);
                    e.printStackTrace();
                }
            } else {
                showAlert("Error", "Failed to save the student profile.");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while saving student profile: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean profileExists(int userID) {
        String query = "SELECT COUNT(*) FROM students WHERE UserID = ?";
        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            showAlert("Error", "Error checking profile existence: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        return false;
    }

    // Show an alert message (information, error, etc.)
    private void showAlert(String title, String message) {
        showAlert(title, message, AlertType.INFORMATION);
    }

    // Show an alert message with the specified type
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void showWelcomeDialog() {
        Alert dialog = new Alert(AlertType.INFORMATION);
        dialog.setTitle("Welcome to Estrandyante!");
        dialog.setHeaderText("We're excited to have you here.");
        dialog.setContentText("Let's get started with setting up your profile.");
        dialog.showAndWait();
    }
}
