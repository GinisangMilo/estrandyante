package textfields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import application.Students;
import application.dbconnect;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class studinfoUpdate {

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

    private Students selectedStudent;
    public void initializeWithStudentData(Students student) {
        this.selectedStudent = student;
        txtEmail.setText(student.getEmail());
        txtFname.setText(student.getFname());
        txtMname.setText(student.getMname());
        txtLname.setText(student.getLname());
        txtEname.setText(student.getEname());
        birthdatePicker.setValue(LocalDate.parse(student.getBirthdate()));
    }

    @FXML
    private void handleUpdate() {
        String updatedEmail = txtEmail.getText();
        String updatedFname = txtFname.getText();
        String updatedMname = txtMname.getText();
        String updatedLname = txtLname.getText();
        String updatedEname = txtEname.getText();
        String updatedBirthdate = birthdatePicker.getValue().toString();
        updateStudentInDatabase(selectedStudent.getStudID(), updatedEmail, updatedFname, updatedMname, updatedLname, updatedEname, updatedBirthdate);
        Stage stage = (Stage) txtEmail.getScene().getWindow();
        stage.close();
    }

    private void updateStudentInDatabase(int studID, String email, String fname, String mname, String lname, String ename, String birthdate) {
        String query = "UPDATE students SET Email = ?, Fname = ?, Mname = ?, Lname = ?, Ename = ?, Birthdate = ? WHERE StudID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, fname);
            stmt.setString(3, mname);
            stmt.setString(4, lname);
            stmt.setString(5, ename);
            stmt.setString(6, birthdate);
            stmt.setInt(7, studID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "Student updated successfully.", AlertType.INFORMATION);
            } else {
                showAlert("Error", "Student could not be updated. Please try again.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error while updating student: " + e.getMessage(), AlertType.ERROR);
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
