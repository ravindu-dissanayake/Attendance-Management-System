package com.sams.service;

import com.sams.dao.ClassSessionDao;
import com.sams.entity.ClassSession;

import java.util.List;

public class ClassSessionService {

    private final ClassSessionDao classSessionDao = new ClassSessionDao();

    public List<ClassSession> findAll() {
        return classSessionDao.findAll();
    }

    public List<ClassSession> findByLecturer(Long lecturerId) {
        if (lecturerId == null) {
            return findAll();
        }
        return classSessionDao.findByLecturerId(lecturerId);
    }

    public ClassSession save(ClassSession session) {
        if (session.getId() == null) {
            return classSessionDao.save(session);
        }
        return classSessionDao.update(session);
    }

    public void delete(ClassSession session) {
        classSessionDao.delete(session);
    }
}
