package com.sams.ui.tabs;

import com.sams.entity.*;
import com.sams.service.AttendanceService;
import com.sams.service.ClassSessionService;
import com.sams.service.StudentService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class AttendanceTab extends BorderPane {

    private final AppUser loggedUser;

    private final ClassSessionService classSessionService = new ClassSessionService();
    private final StudentService studentService = new StudentService();
    private final AttendanceService attendanceService = new AttendanceService();

    private final ComboBox<ClassSession> sessionCombo = new ComboBox<>();
    private final ComboBox<Student> studentCombo = new ComboBox<>();
    private final ComboBox<AttendanceStatus> statusCombo = new ComboBox<>();

    private final TableView<Attendance> table = new TableView<>();

    public AttendanceTab(AppUser loggedUser) {
        this.loggedUser = loggedUser;

        setPadding(new Insets(12));
        setTop(buildForm());
        buildTable();
        setCenter(table);

        refreshSessions();
        statusCombo.setItems(FXCollections.observableArrayList(AttendanceStatus.values()));
        statusCombo.setValue(AttendanceStatus.PRESENT);
    }

    private GridPane buildForm() {
        sessionCombo.setPromptText("Class Session");
        studentCombo.setPromptText("Student");
        statusCombo.setPromptText("Status");

        sessionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            refreshStudentsForSession(newVal);
            refreshAttendanceTable();
        });

        Button markBtn = new Button("Mark / Update Attendance");
        markBtn.setOnAction(e -> mark());

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(0, 0, 12, 0));
        form.addRow(0, new Label("Session"), sessionCombo, new Label("Student"), studentCombo, new Label("Status"),
                statusCombo, markBtn);
        return form;
    }

    private void buildTable() {
        TableColumn<Attendance, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getStudent().getRegistrationNumber()));

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Student");
        nameCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStudent().getName()));

        TableColumn<Attendance, AttendanceStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, java.time.LocalDateTime> markedAtCol = new TableColumn<>("Marked At");
        markedAtCol.setCellValueFactory(new PropertyValueFactory<>("markedAt"));

        table.getColumns().clear();
        table.getColumns().add(regCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(markedAtCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void mark() {
        if (sessionCombo.getValue() == null || studentCombo.getValue() == null || statusCombo.getValue() == null) {
            UiUtil.error("Validation", "Session, student and status are required");
            return;
        }

        attendanceService.markAttendance(sessionCombo.getValue(), studentCombo.getValue(), statusCombo.getValue());
        refreshAttendanceTable();
        UiUtil.info("Saved", "Attendance saved successfully");
    }

    private void refreshSessions() {
        if (loggedUser.getRole() == Role.LECTURER && loggedUser.getLecturer() != null) {
            sessionCombo.setItems(FXCollections
                    .observableArrayList(classSessionService.findByLecturer(loggedUser.getLecturer().getId())));
        } else {
            sessionCombo.setItems(FXCollections.observableArrayList(classSessionService.findAll()));
        }
    }

    private void refreshStudentsForSession(ClassSession session) {
        if (session == null) {
            studentCombo.setItems(FXCollections.emptyObservableList());
            return;
        }
        studentCombo
                .setItems(FXCollections.observableArrayList(studentService.findByCourse(session.getCourse().getId())));
    }

    private void refreshAttendanceTable() {
        ClassSession session = sessionCombo.getValue();
        if (session == null) {
            table.setItems(FXCollections.emptyObservableList());
            return;
        }
        table.setItems(FXCollections.observableArrayList(attendanceService.findBySession(session.getId())));
    }
}
