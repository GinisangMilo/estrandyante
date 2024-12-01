package textfields;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import application.dbconnect;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class studAnswer {

    @FXML
    private VBox questionsContainer;

    @FXML
    private Button btnSubmit;
    @FXML
    private Button btnNext;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnFinish;

    @FXML
    private Label lblRecommendation;
    @FXML
    private Label lblTotalScore;
    @FXML
    private Label lblStrandScores;

    private Map<Integer, Integer> studentAnswers = new HashMap<>();
    private Map<Integer, Integer> categoryScores = new HashMap<>();
    private Map<Integer, Question> allQuestions = new HashMap<>();
    private int totalPages = 0;
    private final int questionsPerPage = 1;
    private int currentPage = 0;
    private int userID;

    @FXML
    public void initialize() {
        loadQuestions();
        updatePagination();
        loadQuestionForPage();
    }

    public void initializeWithUser(int userID) {
        this.userID = userID;
        System.out.println("Student ID: " + userID);
    }

    private void loadQuestions() {
        String query = "SELECT QuestionID, QDescription, QAChoice, QBChoice, QCChoice, QDChoice, AnsID, StrandID FROM questionnaires";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            allQuestions.clear();

            while (rs.next()) {
                int questionID = rs.getInt("QuestionID");
                String questionText = rs.getString("QDescription");
                String choiceA = rs.getString("QAChoice");
                String choiceB = rs.getString("QBChoice");
                String choiceC = rs.getString("QCChoice");
                String choiceD = rs.getString("QDChoice");
                int correctAnsID = rs.getInt("AnsID");
                int strandID = rs.getInt("StrandID");

                if (questionID <= 0 || questionText == null || questionText.isEmpty()) {
                    continue;
                }

                Question question = new Question(questionID, questionText, strandID, correctAnsID);
                question.addOption(choiceA, 1);
                question.addOption(choiceB, 2);
                question.addOption(choiceC, 3);
                question.addOption(choiceD, 4);

                if (!allQuestions.containsKey(questionID)) {
                    allQuestions.put(questionID, question);
                }
            }

            totalPages = (int) Math.ceil((double) allQuestions.size() / questionsPerPage);
            if (totalPages == 0) {
                totalPages = 1;
            }

            System.out.println("Total questions loaded: " + allQuestions.size());

        } catch (SQLException e) {
            showErrorAlert("Database Error", "Error while loading questions: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadQuestionForPage() {
        questionsContainer.getChildren().clear();

        if (currentPage < 0 || currentPage >= totalPages) {
            showErrorAlert("Invalid Page", "The current page is out of bounds.");
            return;
        }

        Object[] questionsArray = allQuestions.values().toArray();

        if (questionsArray.length == 0) {
            showErrorAlert("No Questions", "No questions available to display.");
            return;
        }

        Question currentQuestion = (Question) questionsArray[currentPage];

        if (currentQuestion == null) {
            showErrorAlert("Error", "The selected question is null.");
            return;
        }

        questionsContainer.getChildren().add(createQuestionUI(currentQuestion));

        updatePagination();
    }

    private void updatePagination() {
        btnNext.setDisable(currentPage == totalPages - 1);
        btnPrevious.setDisable(currentPage == 0);

        btnFinish.setVisible(currentPage == totalPages - 1);
    }

    private VBox createQuestionUI(Question question) {
        if (question == null) {
            showErrorAlert("Error", "The question is null.");
            return null;
        }

        VBox questionVBox = new VBox(10);
        questionVBox.setId("question-" + question.getquestionID());

        Label questionLabel = new Label(question.getquestionText());
        questionVBox.getChildren().add(questionLabel);

        ToggleGroup toggleGroup = new ToggleGroup();

        for (int i = 0; i < question.getOptions().size(); i++) {
            RadioButton radioButton = new RadioButton(question.getOptions().get(i));
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setId("answer-" + (i + 1));

            final int answerID = i + 1;

            radioButton.setOnAction(event -> {
                studentAnswers.put(question.getquestionID(), answerID);
                System.out.println("Question " + question.getquestionID() + " - Selected Answer: " + answerID);

                if (answerID == question.getcorrectAnsID()) {
                    System.out.println("Correct answer!");
                    updateCategoryScores(question.getstrandID(), answerID);
                } else {
                    System.out.println("Incorrect answer.");
                }
            });

            questionVBox.getChildren().add(radioButton);
        }

        return questionVBox;
    }

    private void updateCategoryScores(int strandID, int selectedAnswerID) {
        for (Map.Entry<Integer, Question> entry : allQuestions.entrySet()) {
            Question question = entry.getValue();

            if (question.getstrandID() == strandID) {
                int correctAnswerID = question.getcorrectAnsID();
                if (selectedAnswerID == correctAnswerID) {
                    categoryScores.put(strandID, categoryScores.getOrDefault(strandID, 0) + 1);
                }
                break;
            }
        }
    }

    @FXML
    private void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadQuestionForPage();
        }
    }

    @FXML
    private void handlePrev() {
        if (currentPage > 0) {
            currentPage--;
            loadQuestionForPage();
        }
    }

    @FXML
    private void handleFinish() {
        try {
            // Calculate total score and recommended strand
            int totalScore = calculateTotalScore();
            int recommendedStrandID = getRecommendedStrand();
            String recommendedStrand = getStrandNameById(recommendedStrandID);

            // Update the recommended strand in the database
            updateRecommendedStrandInDatabase(userID, recommendedStrand);

            // Update the UI labels
            if (lblTotalScore != null) {
                lblTotalScore.setText("Total Score: " + totalScore);
            } else {
                System.err.println("lblTotalScore is null.");
            }

            if (lblRecommendation != null) {
                lblRecommendation.setText("Recommended Strand: " + recommendedStrand);
            }

            // Fetch user email from the database
            String userEmail = getUserEmail(userID);
            if (userEmail != null && !userEmail.isEmpty()) {
                sendEmailNotification(userEmail, recommendedStrand);
            } else {
                System.out.println("No email found for UserID: " + userID);
            }

            // Close current stage and open the next scene
            Stage currentStage = (Stage) btnFinish.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewstudinfo.fxml"));
            AnchorPane root = loader.load();

            viewstudinfo viewStudInfoController = loader.getController();
            viewStudInfoController.initialize(userID);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "An error occurred while loading the viewstudinfo screen: " + e.getMessage());
        }
    }

    private void updateRecommendedStrandInDatabase(int userID, String recommendedStrand) {
        String query = "UPDATE students SET RecommendedStrand = ? WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, recommendedStrand);
            stmt.setInt(2, userID);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Successfully updated recommended strand for UserID: " + userID);
            } else {
                System.out.println("No record found for UserID: " + userID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to update recommended strand: " + e.getMessage());
        }
    }

    private String getStrandNameById(int strandID) {
        String strandName = "Unknown";
        switch (strandID) {
            case 1:
                strandName = "STEM";
                break;
            case 2:
                strandName = "ABM";
                break;
            case 3:
                strandName = "HUMSS";
                break;
            case 4:
                strandName = "ICT";
                break;
        }
        return strandName;
    }

    private int calculateTotalScore() {
        int score = 0;
        for (Map.Entry<Integer, Integer> entry : categoryScores.entrySet()) {
            score += entry.getValue();
        }
        return score;
    }

    private int getRecommendedStrand() {
        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);
    }

    private String getUserEmail(int userID) {
        String email = null;
        String query = "SELECT Email FROM students WHERE UserID = ?";

        try (Connection conn = dbconnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("Email");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    private void sendEmailNotification(String userEmail, String recommendedStrand) {
        final String username = "estrandyante@gmail.com"; // Change this to your email
        final String password = "wavq cmkp sont cuaw"; // Change this to your email password

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userEmail));
            message.setSubject("Quiz Results and Recommended Strand");

            String emailContent = "Hello,\n\n" +
                    "Thank you for completing the quiz. Based on your responses, we recommend the following strand for you:\n" +
                    "Recommended Strand: " + recommendedStrand + "\n\n" +
                    "Best regards,\nEstrandyante Team";

            message.setText(emailContent);
            Transport.send(message);
            System.out.println("Email sent successfully to: " + userEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
            showErrorAlert("Email Error", "Failed to send the email notification: " + e.getMessage());
        }
    }
}
