package application;
public class Students {
    private int studID;
    private String email;
    private String fname;
    private String lname;
    private String mname;
    private String ename;
    private String birthdate;
    private String recommendedStrand;
    public Students(int studID, String email, String fname, String lname, String mname, String ename, String birthdate, String recommendedStrand) {
        this.studID = studID;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.mname = mname;
        this.ename = ename;
        this.birthdate = birthdate;
        this.recommendedStrand = recommendedStrand;
    }

    public int getStudID() {
        return studID;
    }

    public String getEmail() {
        return email;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getMname() {
        return mname;
    }

    public String getEname() {
        return ename;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getRecommendedStrand() {
        return recommendedStrand;
    }

    // Optionally, setters can be added here if needed
}
