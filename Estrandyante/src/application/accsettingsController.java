package application;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import textfields.updateAccount;
import textfields.updateQuestion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class accsettingsController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, Integer> colUserID;

    @FXML
    private TableColumn<User, String> colUsername;

    @FXML
    private TableColumn<User, String> colPassword;

    @FXML
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colEmail;  // New column for Email

    @FXML
    private Button btnNavigate;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnDeleteUser;  // Button to delete user
    private ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML
    private Button btnback;

    @FXML
    private void initialize() {
        loadUserData();

        colUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));  // Set the email column
    }

    private void loadUserData() {
        String query = "SELECT u.UserID, u.Username, u.Password, r.RoleName, u.Email " +  // Include email in the query
                       "FROM users u " +
                       "JOIN Roles r ON u.RoleID = r.RoleID";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            userList.clear();
            while (rs.next()) {
                int userID = rs.getInt("UserID");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                String role = rs.getString("RoleName");
                String email = rs.getString("Email");  // Fetch email from the result set

                userList.add(new User(userID, username, password, role, email));  // Pass email to the User constructor
            }
            userTable.setItems(userList);

        } catch (SQLException e) {
            showAlert("Database Error", "Error while loading users: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNavigate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/addaccount.fxml"));
            Parent root = loader.load();

            Stage newStage = new Stage();
            Scene newScene = new Scene(root);

            newStage.setScene(newScene);
            newStage.setTitle("Add Account");
            newStage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the new panel: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadUserData();
        showAlert("Success", "User data refreshed!", AlertType.INFORMATION);
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete.", AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete the user?");
        confirmAlert.setContentText("User: " + selectedUser.getUsername());

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            deleteUserFromDatabase(selectedUser.getUserID());
        }
    }

    private void deleteUserFromDatabase(int userID) {
        String query = "DELETE FROM users WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Success", "User deleted successfully.", AlertType.INFORMATION);
                loadUserData();
            } else {
                showAlert("Error", "User could not be deleted. Please try again.", AlertType.ERROR);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Error while deleting user: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleUpdateUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Error", "Please select a user to update.", AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/textfields/updateAccount.fxml"));
            Parent root = loader.load();

            updateAccount controller = loader.getController();

            controller.initializeWithUserData(
                selectedUser.getUserID(),
                selectedUser.getUsername(),
                selectedUser.getPassword(),
                selectedUser.getRole(),
                selectedUser.getEmail()  // Pass email to the update screen
            );

            Stage stage = new Stage();
            stage.setTitle("Update User");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            showAlert("Error", "Failed to load the update user screen: " + e.getMessage(), AlertType.ERROR);
            e.printStackTrace();
        }
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
