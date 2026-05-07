package com.sams.service;

import com.sams.dao.CourseDao;
import com.sams.entity.Course;

import java.util.List;

public class CourseService {

    private final CourseDao courseDao = new CourseDao();

    public List<Course> findAll() {
        return courseDao.findAll();
    }

    public Course save(Course course) {
        if (course.getId() == null) {
            return courseDao.save(course);
        }
        return courseDao.update(course);
    }

    public void delete(Course course) {
        courseDao.delete(course);
    }
}
