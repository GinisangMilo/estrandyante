package application;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import textfields.studinfo;
import textfields.viewstudinfo;

public class loginController {

    @FXML
    private TextField username;
    @FXML
    private TextField password;

    private static final String ROLE_ADMIN = "1";
    private static final String ROLE_STUDENT = "2";
    private static final String ROLE_FACULTY = "3";

    @FXML
    private void login(ActionEvent event) {
        String user = username.getText();
        String pass = password.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Please enter both username and password.");
            return;
        }


        User userInfo = authenticateUser(user, pass);

        if (userInfo != null) {
            System.out.println("User ID: " + userInfo.getUserID() + " Role: " + userInfo.getRole());
        }


        if (userInfo != null) {
            loadNextScene(event, userInfo.getRole(), userInfo.getUserID());
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }


    private User authenticateUser(String username, String password) {
        User user = null;
        String query = "SELECT UserID, RoleID FROM users WHERE Username = ? AND Password = ?";

        try (Connection connection = dbconnect.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int userID = resultSet.getInt("UserID");
                String role = resultSet.getString("RoleID");
                user = new User(userID, role);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void loadNextScene(ActionEvent event, String role, int userID) {
        try {
            String sceneFile = "";

            if (ROLE_ADMIN.equals(role)) {
                sceneFile = "admindashboard.fxml";
            } else if (ROLE_STUDENT.equals(role)) {
       
                if (hasStudentInfo(userID)) {
                    sceneFile = "/textfields/viewstudinfo.fxml";
                } else {
                    sceneFile = "/textfields/studentquestionnaire.fxml";
                }
            } else if (ROLE_FACULTY.equals(role)) {
                sceneFile = "facultydashboard.fxml";
            } else {
                showAlert("Error", "Unknown user role.");
                return;
            }

   
            FXMLLoader loader = new FXMLLoader(getClass().getResource(sceneFile));
            Parent root = loader.load();

      
            if (ROLE_STUDENT.equals(role)) {
                if (sceneFile.equals("/textfields/viewstudinfo.fxml")) {
                    viewstudinfo controller = loader.getController();
                    controller.initialize(userID);
                } else if (sceneFile.equals("/textfields/studentquestionnaire.fxml")) {
                    studinfo controller = loader.getController();
                    controller.initializeWithUser(userID, role);
                }
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


            Stage primaryStage = (Stage) username.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean hasStudentInfo(int userId) {
        String query = "SELECT COUNT(*) AS count FROM Students WHERE UserID = ?";
        try (Connection connection = dbconnect.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public class User {
        private int userID;
        private String role;

        public User(int userID, String role) {
            this.userID = userID;
            this.role = role;
        }

        public int getUserID() {
            return userID;
        }

        public String getRole() {
            return role;
        }
    }
}
