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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class accsettings {

    @FXML
    private TextField txtUsername;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtpassword;

    @FXML
    private ComboBox<String> comboRole;
    
    @FXML
    private Button btnAddUser;
    
    @FXML
    private Button btnClose;

    private static final String FROM_EMAIL = "estrandyante@gmail.com"; 
    private static final String FROM_PASSWORD = "wavq cmkp sont cuaw";

    @FXML  
    private void initialize() {    	
        loadRoles();
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
    private void addUser() {
        String username = txtUsername.getText();
        String password = txtpassword.getText();
        String selectedRole = comboRole.getValue();
        String email = txtEmail.getText();

        if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert("Error", "Username, Password, and Role must not be empty", AlertType.ERROR);
            return;
        }

        int roleID = getRoleIDByName(selectedRole);

        if (roleID == -1) {
            showAlert("Error", "Invalid Role selected", AlertType.ERROR);
            return;
        }

        String query = "INSERT INTO users (Username, Password, RoleID, Email) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setInt(3, roleID);
            stmt.setString(4, email);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Success", "User added successfully", AlertType.INFORMATION);
                sendEmail(email, username, password, selectedRole);  // Send email notification
            } else {
                showAlert("Error", "User could not be added", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error while adding user: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        
        Stage stage = (Stage) btnAddUser.getScene().getWindow();
        stage.close();
    }

    private int getRoleIDByName(String roleName) {
        String query = "SELECT RoleID FROM roles WHERE RoleName = ?";
        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoleID");
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error while fetching RoleID: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
        return -1;
    }

    private void sendEmail(String recipientEmail, String username, String password, String role) {
        String subject = "Good Day, " + username;
        String body = "You're invited to participate and become one of our great team members with the role of: " + role + ".\n\n" +
                      "This is a step forward in your journey to achieve greatness!\n\n" +
                      "Login using these credentials:\n" +
                      "Username: " + username + "\n" +
                      "Password: " + password + "\n\n" +
                      "Please keep this information safe and secure.\n" +
                      "Best regards,\nEstrandyante Team";

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

    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
