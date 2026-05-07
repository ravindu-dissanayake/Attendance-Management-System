package com.sams.service;

import com.sams.dto.AttendanceReportRow;
import com.sams.entity.Attendance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private final AttendanceService attendanceService = new AttendanceService();

    public List<AttendanceReportRow> findRows(Long studentId, Long courseId, LocalDate fromDate, LocalDate toDate) {
        List<Attendance> records = attendanceService.findForReport(studentId, courseId, fromDate, toDate);
        return records.stream()
                .map(a -> new AttendanceReportRow(
                        a.getStudent().getRegistrationNumber(),
                        a.getStudent().getName(),
                        a.getStudent().getCourse().getCode(),
                        a.getSession().getSubject().getCode(),
                        a.getSession().getSessionDateTime(),
                        a.getStatus().name()))
                .collect(Collectors.toList());
    }

    public void exportCsv(List<AttendanceReportRow> rows, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("RegNo,Student,Course,Subject,SessionDateTime,Status");
            writer.newLine();
            for (AttendanceReportRow row : rows) {
                writer.write(String.join(",",
                        esc(row.getRegistrationNumber()),
                        esc(row.getStudentName()),
                        esc(row.getCourseCode()),
                        esc(row.getSubjectCode()),
                        esc(row.getSessionDateTime().toString()),
                        esc(row.getStatus())));
                writer.newLine();
            }
        }
    }

    private String esc(String text) {
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
