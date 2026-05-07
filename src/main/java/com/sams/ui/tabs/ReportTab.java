package com.sams.ui.tabs;

import com.sams.dto.AttendanceReportRow;
import com.sams.entity.AppUser;
import com.sams.entity.Course;
import com.sams.entity.Role;
import com.sams.entity.Student;
import com.sams.service.CourseService;
import com.sams.service.ReportService;
import com.sams.service.StudentService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ReportTab extends BorderPane {

    private final AppUser loggedUser;

    private final ReportService reportService = new ReportService();
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();

    private final ComboBox<Student> studentCombo = new ComboBox<>();
    private final ComboBox<Course> courseCombo = new ComboBox<>();
    private final DatePicker fromDatePicker = new DatePicker();
    private final DatePicker toDatePicker = new DatePicker();
    private final TableView<AttendanceReportRow> table = new TableView<>();

    public ReportTab(AppUser loggedUser) {
        this.loggedUser = loggedUser;

        setPadding(new Insets(12));
        setTop(buildFilters());
        buildTable();
        setCenter(table);
        refreshLookups();
    }

    private GridPane buildFilters() {
        studentCombo.setPromptText("Student");
        courseCombo.setPromptText("Course");
        fromDatePicker.setPromptText("From Date");
        toDatePicker.setPromptText("To Date");

        Button runBtn = new Button("Generate Report");
        Button exportBtn = new Button("Export CSV");

        runBtn.setOnAction(e -> runReport());
        exportBtn.setOnAction(e -> exportCsv());

        GridPane filters = new GridPane();
        filters.setHgap(8);
        filters.setVgap(8);
        filters.setPadding(new Insets(0, 0, 12, 0));
        filters.addRow(0, new Label("Student"), studentCombo, new Label("Course"), courseCombo,
                new Label("From"), fromDatePicker, new Label("To"), toDatePicker, runBtn, exportBtn);
        return filters;
    }

    private void buildTable() {
        TableColumn<AttendanceReportRow, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));

        TableColumn<AttendanceReportRow, String> studentCol = new TableColumn<>("Student");
        studentCol.setCellValueFactory(new PropertyValueFactory<>("studentName"));

        TableColumn<AttendanceReportRow, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        TableColumn<AttendanceReportRow, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subjectCode"));

        TableColumn<AttendanceReportRow, java.time.LocalDateTime> dateCol = new TableColumn<>("Session DateTime");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDateTime"));

        TableColumn<AttendanceReportRow, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().clear();
        table.getColumns().add(regCol);
        table.getColumns().add(studentCol);
        table.getColumns().add(courseCol);
        table.getColumns().add(subjectCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    private void refreshLookups() {
        studentCombo.setItems(FXCollections.observableArrayList(studentService.findAll()));
        courseCombo.setItems(FXCollections.observableArrayList(courseService.findAll()));

        if (loggedUser.getRole() == Role.LECTURER) {
            // Lecturer still has filter access; data scope remains based on attendance
            // records.
        }
    }

    private void runReport() {
        LocalDate from = fromDatePicker.getValue();
        LocalDate to = toDatePicker.getValue();

        if (from != null && to != null && from.isAfter(to)) {
            UiUtil.error("Validation", "From date must be earlier than or equal to to date");
            return;
        }

        Long studentId = studentCombo.getValue() == null ? null : studentCombo.getValue().getId();
        Long courseId = courseCombo.getValue() == null ? null : courseCombo.getValue().getId();

        List<AttendanceReportRow> rows = reportService.findRows(studentId, courseId, from, to);
        table.setItems(FXCollections.observableArrayList(rows));
    }

    private void exportCsv() {
        if (table.getItems().isEmpty()) {
            UiUtil.error("Export", "Run a report first");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Attendance Report");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        chooser.setInitialFileName("attendance-report.csv");

        File file = chooser.showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            reportService.exportCsv(table.getItems(), file);
            UiUtil.info("Export", "Report exported successfully");
        } catch (Exception ex) {
            UiUtil.error("Export Error", ex.getMessage());
        }
    }
}
