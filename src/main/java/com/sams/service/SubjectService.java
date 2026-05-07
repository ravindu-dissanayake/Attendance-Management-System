package com.sams.service;

import com.sams.dao.SubjectDao;
import com.sams.entity.Subject;

import java.util.List;

public class SubjectService {

    private final SubjectDao subjectDao = new SubjectDao();

    public List<Subject> findAll() {
        return subjectDao.findAll();
    }

    public List<Subject> findByCourse(Long courseId) {
        if (courseId == null) {
            return findAll();
        }
        return subjectDao.findByCourseId(courseId);
    }

    public Subject save(Subject subject) {
        if (subject.getId() == null) {
            return subjectDao.save(subject);
        }
        return subjectDao.update(subject);
    }

    public void delete(Subject subject) {
        subjectDao.delete(subject);
    }
}
