package com.sams.dao;

import com.sams.entity.ClassSession;
import com.sams.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class ClassSessionDao extends AbstractDao<ClassSession> {
    public ClassSessionDao() {
        super(ClassSession.class);
    }

    public List<ClassSession> findByLecturerId(Long lecturerId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery(
                            "from ClassSession c where c.lecturer.id = :lecturerId order by c.sessionDateTime desc",
                            ClassSession.class)
                    .setParameter("lecturerId", lecturerId)
                    .list();
        }
    }
}
