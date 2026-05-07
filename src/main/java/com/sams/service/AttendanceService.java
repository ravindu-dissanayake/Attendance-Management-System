package com.sams.service;

import com.sams.dao.AttendanceDao;
import com.sams.entity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceService {

    private final AttendanceDao attendanceDao = new AttendanceDao();

    public Attendance markAttendance(ClassSession session, Student student, AttendanceStatus status) {
        Attendance attendance = attendanceDao.findBySessionAndStudent(session.getId(), student.getId())
                .orElseGet(() -> new Attendance(session, student, status, LocalDateTime.now()));
        attendance.setStatus(status);
        attendance.setMarkedAt(LocalDateTime.now());

        if (attendance.getId() == null) {
            return attendanceDao.save(attendance);
        }
        return attendanceDao.update(attendance);
    }

    public List<Attendance> findBySession(Long sessionId) {
        return attendanceDao.findBySessionId(sessionId);
    }

    public List<Attendance> findForReport(Long studentId, Long courseId, LocalDate fromDate, LocalDate toDate) {
        return attendanceDao.findForReport(studentId, courseId, fromDate, toDate);
    }
}
