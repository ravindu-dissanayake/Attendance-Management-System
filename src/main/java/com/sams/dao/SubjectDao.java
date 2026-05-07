package com.sams.dao;

import com.sams.entity.Subject;
import com.sams.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class SubjectDao extends AbstractDao<Subject> {
    public SubjectDao() {
        super(Subject.class);
    }

    public List<Subject> findByCourseId(Long courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Subject s where s.course.id = :courseId", Subject.class)
                    .setParameter("courseId", courseId)
                    .list();
        }
    }
}
