package aryan.digipodium.npgclocker.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentModel {

    public String mobile;
    public String username;
    public String collegeid;
    public String dob;
    public String created_on;
    public boolean approved;

    public StudentModel() {
        // for firebase
    }

    public StudentModel(String username, String collegeid, String mobile, String dob) {
        this.mobile = mobile;
        this.username = username;
        this.collegeid = collegeid;
        this.dob = dob;
        this.created_on = getDateTime();
        this.approved = false;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
