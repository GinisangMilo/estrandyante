package application;

public class Questionnaire {

    private int questionID;
    private String qDescription;
    private String qaChoice;
    private String qbChoice;
    private String qcChoice;
    private String qdChoice;
    private String answer;
    private String strand;
    private int ansID;


    public Questionnaire(int questionID, String qDescription, String qaChoice, String qbChoice, 
                         String qcChoice, String qdChoice, String answer, String strand, int ansID) {
        this.questionID = questionID;
        this.qDescription = qDescription;
        this.qaChoice = qaChoice;
        this.qbChoice = qbChoice;
        this.qcChoice = qcChoice;
        this.qdChoice = qdChoice;
        this.answer = answer;
        this.strand = strand;
        this.ansID = ansID;
    }


    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getQDescription() {
        return qDescription;
    }

    public void setQDescription(String qDescription) {
        this.qDescription = qDescription;
    }

    public String getQAChoice() {
        return qaChoice;
    }

    public void setQAChoice(String qaChoice) {
        this.qaChoice = qaChoice;
    }

    public String getQBChoice() {
        return qbChoice;
    }

    public void setQBChoice(String qbChoice) {
        this.qbChoice = qbChoice;
    }

    public String getQCChoice() {
        return qcChoice;
    }

    public void setQCChoice(String qcChoice) {
        this.qcChoice = qcChoice;
    }

    public String getQDChoice() {
        return qdChoice;
    }

    public void setQDChoice(String qdChoice) {
        this.qdChoice = qdChoice;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public int getAnsID() {
        return ansID;
    }

    public void setAnsID(int ansID) {
        this.ansID = ansID;
    }
}

