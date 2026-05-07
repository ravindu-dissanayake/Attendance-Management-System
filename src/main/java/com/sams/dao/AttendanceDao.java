package com.sams.dao;

import com.sams.entity.Attendance;
import com.sams.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AttendanceDao extends AbstractDao<Attendance> {
    public AttendanceDao() {
        super(Attendance.class);
    }

    public Optional<Attendance> findBySessionAndStudent(Long sessionId, Long studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Attendance attendance = session.createQuery(
                    "from Attendance a where a.session.id = :sessionId and a.student.id = :studentId",
                    Attendance.class)
                    .setParameter("sessionId", sessionId)
                    .setParameter("studentId", studentId)
                    .uniqueResult();
            return Optional.ofNullable(attendance);
        }
    }

    public List<Attendance> findBySessionId(Long sessionId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Attendance a where a.session.id = :sessionId", Attendance.class)
                    .setParameter("sessionId", sessionId)
                    .list();
        }
    }

    public List<Attendance> findForReport(Long studentId, Long courseId, LocalDate fromDate, LocalDate toDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "from Attendance a where (:studentId is null or a.student.id = :studentId) " +
                    "and (:courseId is null or a.student.course.id = :courseId) " +
                    "and (:fromDate is null or date(a.session.sessionDateTime) >= :fromDate) " +
                    "and (:toDate is null or date(a.session.sessionDateTime) <= :toDate) " +
                    "order by a.session.sessionDateTime desc";
            return session.createQuery(hql, Attendance.class)
                    .setParameter("studentId", studentId)
                    .setParameter("courseId", courseId)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .list();
        }
    }
}
