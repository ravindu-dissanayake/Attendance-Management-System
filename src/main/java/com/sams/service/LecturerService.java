package com.sams.service;

import com.sams.dao.LecturerDao;
import com.sams.entity.Lecturer;

import java.util.List;

public class LecturerService {

    private final LecturerDao lecturerDao = new LecturerDao();

    public List<Lecturer> findAll() {
        return lecturerDao.findAll();
    }

    public Lecturer save(Lecturer lecturer) {
        if (lecturer.getId() == null) {
            return lecturerDao.save(lecturer);
        }
        return lecturerDao.update(lecturer);
    }

    public void delete(Lecturer lecturer) {
        lecturerDao.delete(lecturer);
    }
}
