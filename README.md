# Student Attendance Management System (SAMS)

Desktop application for managing students, lecturers, courses, subjects, class sessions, attendance, and reports.

## Requirements

- Java 21
- Maven
- MySQL

## Configuration

Update `src/main/resources/hibernate.cfg.xml` with your MySQL JDBC URL, username, and password.

The app seeds demo users at startup:

- Admin: `admin` / `admin123`
- Lecturer: `lecturer` / `lec123`

## Run

From the project root:

```powershell
mvn clean javafx:run
```

## Main Features

- Manage students, lecturers, courses, and subjects
- Create class sessions and mark attendance
- View attendance reports
- Use role-based login for admin and lecturer accounts

