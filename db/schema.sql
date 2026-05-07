DROP DATABASE IF EXISTS sams_db;
CREATE DATABASE sams_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams_db;

CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000)
) ENGINE=InnoDB;

CREATE TABLE lecturers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE subjects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_subject_course
        FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registrationNumber VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contact VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT fk_student_course
        FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE class_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    sessionDateTime DATETIME NOT NULL,
    room VARCHAR(255) NOT NULL,
    CONSTRAINT fk_session_course
        FOREIGN KEY (course_id) REFERENCES courses (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_session_subject
        FOREIGN KEY (subject_id) REFERENCES subjects (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_session_lecturer
        FOREIGN KEY (lecturer_id) REFERENCES lecturers (id)
        ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE attendance_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    markedAt DATETIME NOT NULL,
    CONSTRAINT uk_attendance_session_student UNIQUE (session_id, student_id),
    CONSTRAINT fk_attendance_session
        FOREIGN KEY (session_id) REFERENCES class_sessions (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id) REFERENCES students (id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    lecturer_id BIGINT NULL,
    CONSTRAINT fk_user_lecturer
        FOREIGN KEY (lecturer_id) REFERENCES lecturers (id)
        ON DELETE SET NULL
) ENGINE=InnoDB;

INSERT INTO courses (code, name, description) VALUES
    ('SE', 'Software Engineering', 'SE diploma');

INSERT INTO lecturers (name, email, contact) VALUES
    ('Dr. Kavinda', 'kavinda@ijse.lk', '0771234567');

INSERT INTO subjects (code, name, course_id) VALUES
    ('SE101', 'Programming Fundamentals', (SELECT id FROM courses WHERE code = 'SE'));

INSERT INTO students (registrationNumber, name, email, contact, course_id) VALUES
    ('REG001', 'Nadeesha Perera', 'nadeesha@ijse.lk', '0710000001', (SELECT id FROM courses WHERE code = 'SE')),
    ('REG002', 'Ishara Silva', 'ishara@ijse.lk', '0710000002', (SELECT id FROM courses WHERE code = 'SE'));

INSERT INTO class_sessions (course_id, subject_id, lecturer_id, sessionDateTime, room) VALUES
    (
        (SELECT id FROM courses WHERE code = 'SE'),
        (SELECT id FROM subjects WHERE code = 'SE101'),
        (SELECT id FROM lecturers WHERE email = 'kavinda@ijse.lk'),
        NOW() + INTERVAL 1 DAY,
        'Lab 01'
    );

INSERT INTO app_users (username, password, role, lecturer_id) VALUES
    ('admin', 'admin123', 'ADMIN', NULL),
    ('lecturer', 'lec123', 'LECTURER', (SELECT id FROM lecturers WHERE email = 'kavinda@ijse.lk'));