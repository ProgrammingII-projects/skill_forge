package model;

import java.time.LocalDate;
import java.time.LocalDate;

import org.json.JSONObject;

public class Certificate {
    private String certificateId;
    private String studentId;
    private String courseId;
    private String issueDate;

    public Certificate(String certificateId, String studentID, String courseId, String issueDate) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.courseId = courseId;
        LocalDate today=LocalDate.now();
        this.issueDate =today.toString();
    }

    public String getCertificateId() {
        return certificateId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("certificateId", certificateId);
        o.put("studentId", studentId);
        o.put("courseId", courseId);
        o.put("issueDate", issueDate);
        return o;
    }

    public static Certificate fromJson(JSONObject o) {
        String certificateId = o.getString("certificateId");
        String studentId = o.getString("studentId");
        String courseId = o.getString("courseId");
        String issueDate = o.getString("issueDate");
        return new Certificate(certificateId, studentId, courseId, issueDate);
    }
}
