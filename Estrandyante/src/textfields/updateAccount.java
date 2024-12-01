package textfields;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import application.dbconnect;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class updateAccount {

    private int userID;

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;

    @FXML
    private ComboBox<String> comboRole;

    @FXML
    private Button btnSaveChanges;

    private static final String FROM_EMAIL = "estrandyante@gmail.com"; 
    private static final String FROM_PASSWORD = "wavq cmkp sont cuaw"; // Use your app password here

    // Method to initialize the fields with the user's current data
    public void initializeWithUserData(int userID, String username, String password, String role, String email) {
        this.userID = userID;

        txtUsername.setText(username);
        txtEmail.setText(email);
        txtPassword.setText(password);
        loadRoles();
        comboRole.setValue(role);
    }

    private void loadRoles() {
        String query = "SELECT RoleID, RoleName FROM Roles";
        ArrayList<String> roleNames = new ArrayList<>();

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                roleNames.add(rs.getString("RoleName"));
            }

            comboRole.setItems(FXCollections.observableArrayList(roleNames));

        } catch (SQLException e) {
            showAlert("Database Error", "Error while loading roles: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveChanges() {
        String updatedEmail = txtEmail.getText();
        String updatedUsername = txtUsername.getText();
        String updatedPassword = txtPassword.getText();
        String updatedRole = comboRole.getValue();

        updateUserInDatabase(updatedEmail, updatedUsername, updatedPassword, updatedRole);
    }

    private void updateUserInDatabase(String email, String username, String password, String role) {
        String updateQuery = "UPDATE users SET Email = ?, Username = ?, Password = ?, RoleID = (SELECT RoleID FROM roles WHERE RoleName = ?) WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, role);
            stmt.setInt(5, userID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "User updated successfully.", AlertType.INFORMATION);
                sendUpdateEmailNotification(email, username, password, role);
                closeWindow();
            } else {
                showAlert("Error", "Failed to update the user.", AlertType.ERROR);
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while updating user: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void sendUpdateEmailNotification(String recipientEmail, String username, String password, String role) {
        String subject = "Account Updated Successfully";
        String body = "Hello " + username + ",\n\n" +
                      "Your account details have been successfully updated. \n\nYour current password is: " + password + ".\nYour current role is :" +role + ".\n\n" +
                      "If you have any questions or concerns, please contact support.\n\n" +
                      "Best regards,\nEstrandyante Team";

        if (role.equals("Admin")) {
            body += "\nAs an Admin, you have full access to manage the system. Make sure to review system activities regularly.";
        } else if (role.equals("Manager")) {
            body += "\nAs a Manager, you are responsible for overseeing your team's progress. Keep up the great work!";
        }

        try {
            sendGmail(recipientEmail, subject, body);
            showAlert("Success", "Email sent successfully to " + recipientEmail, AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Error", "Failed to send email: " + e.getMessage(), AlertType.ERROR);
        }
    }

    private void sendGmail(String recipientEmail, String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSaveChanges.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClose() {
        closeWindow();
    }
}
