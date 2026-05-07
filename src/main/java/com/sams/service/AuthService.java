package com.sams.service;

import com.sams.dao.*;
import com.sams.entity.*;

import java.time.LocalDateTime;
import java.util.Optional;

public class AuthService {

    private final UserDao userDao = new UserDao();
    private final LecturerDao lecturerDao = new LecturerDao();
    private final CourseDao courseDao = new CourseDao();
    private final SubjectDao subjectDao = new SubjectDao();
    private final StudentDao studentDao = new StudentDao();
    private final ClassSessionDao classSessionDao = new ClassSessionDao();

    public Optional<AppUser> authenticate(String username, String password) {
        return userDao.findByUsername(username)
                .filter(user -> user.getPassword().equals(password));
    }

    public void seedDefaults() {
        if (userDao.findByUsername("admin").isPresent()) {
            return;
        }

        Course course = courseDao.save(new Course("SE", "Software Engineering", "SE diploma"));
        Subject subject = subjectDao.save(new Subject("SE101", "Programming Fundamentals", course));
        Lecturer lecturer = lecturerDao.save(new Lecturer("Dr. Kavinda", "kavinda@ijse.lk", "0771234567"));
        studentDao.save(new Student("REG001", "Nadeesha Perera", "nadeesha@ijse.lk", "0710000001", course));
        studentDao.save(new Student("REG002", "Ishara Silva", "ishara@ijse.lk", "0710000002", course));
        classSessionDao.save(new ClassSession(course, subject, lecturer, LocalDateTime.now().plusDays(1), "Lab 01"));

        userDao.save(new AppUser("admin", "admin123", Role.ADMIN, null));
        userDao.save(new AppUser("lecturer", "lec123", Role.LECTURER, lecturer));
    }
}
