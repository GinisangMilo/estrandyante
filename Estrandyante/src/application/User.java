package application;
public class User {
    private int userID;
    private String username;
    private String password;
    private String role;
    private String email;

    public User(int userID, String username, String password, String role, String email) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    // Add setter for email if needed
    public void setEmail(String email) {
        this.email = email;
    }
}
