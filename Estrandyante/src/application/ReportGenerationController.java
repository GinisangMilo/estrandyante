package application;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import java.util.HashMap;
import java.util.Map;

public class ReportGenerationController {

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button btnGenerateReport;

    @FXML
    private Button btnClose;

    private ObservableList<Students> studentList;

    public void setStudentList(ObservableList<Students> studentList) {
        this.studentList = studentList;
    }

    @FXML
    private void handleGenerateReport() {
        if (studentList == null || studentList.isEmpty()) {
            showAlert("Error", "No student data available to generate the report.", AlertType.ERROR);
            return;
        }

        Map<String, Integer> strandReport = new HashMap<>();
        for (Students student : studentList) {
            String strand = student.getRecommendedStrand();
            strandReport.put(strand, strandReport.getOrDefault(strand, 0) + 1);
        }

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Student Report by Strand:\n\n");

        for (Map.Entry<String, Integer> entry : strandReport.entrySet()) {
            reportContent.append("Strand: ").append(entry.getKey())
                          .append(" | Number of Students: ").append(entry.getValue()).append("\n");
        }

        reportTextArea.setText(reportContent.toString());
    }

    @FXML
    private void handleClose() {
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
