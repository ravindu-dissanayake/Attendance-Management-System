package com.sams.dto;

import java.time.LocalDateTime;

public class AttendanceReportRow {

    private final String registrationNumber;
    private final String studentName;
    private final String courseCode;
    private final String subjectCode;
    private final LocalDateTime sessionDateTime;
    private final String status;

    public AttendanceReportRow(String registrationNumber, String studentName, String courseCode,
            String subjectCode, LocalDateTime sessionDateTime, String status) {
        this.registrationNumber = registrationNumber;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.subjectCode = subjectCode;
        this.sessionDateTime = sessionDateTime;
        this.status = status;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public LocalDateTime getSessionDateTime() {
        return sessionDateTime;
    }

    public String getStatus() {
        return status;
    }
}
