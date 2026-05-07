package com.sams.dao;

import com.sams.entity.Student;
import com.sams.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class StudentDao extends AbstractDao<Student> {
    public StudentDao() {
        super(Student.class);
    }

    public List<Student> findByCourseId(Long courseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Student s where s.course.id = :courseId", Student.class)
                    .setParameter("courseId", courseId)
                    .list();
        }
    }
}
