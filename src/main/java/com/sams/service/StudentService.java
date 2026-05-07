package com.sams.service;

import com.sams.dao.StudentDao;
import com.sams.entity.Student;

import java.util.List;

public class StudentService {

    private final StudentDao studentDao = new StudentDao();

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public List<Student> findByCourse(Long courseId) {
        if (courseId == null) {
            return findAll();
        }
        return studentDao.findByCourseId(courseId);
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            return studentDao.save(student);
        }
        return studentDao.update(student);
    }

    public void delete(Student student) {
        studentDao.delete(student);
    }
}
