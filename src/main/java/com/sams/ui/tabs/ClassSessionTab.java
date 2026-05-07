package com.sams.ui.tabs;

import com.sams.entity.*;
import com.sams.service.*;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClassSessionTab extends BorderPane {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AppUser loggedUser;
    private final boolean editable;

    private final ClassSessionService classSessionService = new ClassSessionService();
    private final CourseService courseService = new CourseService();
    private final SubjectService subjectService = new SubjectService();
    private final LecturerService lecturerService = new LecturerService();

    private final TableView<ClassSession> table = new TableView<>();
    private final ComboBox<Course> courseCombo = new ComboBox<>();
    private final ComboBox<Subject> subjectCombo = new ComboBox<>();
    private final ComboBox<Lecturer> lecturerCombo = new ComboBox<>();
    private final TextField dateTimeField = new TextField();
    private final TextField roomField = new TextField();

    private ClassSession selected;

    public ClassSessionTab(AppUser loggedUser, boolean editable) {
        this.loggedUser = loggedUser;
        this.editable = editable;

        setPadding(new Insets(12));
        buildTable();
        setCenter(table);
        if (editable) {
            setBottom(buildForm());
        }
        refresh();
    }

    private void buildTable() {
        TableColumn<ClassSession, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getCode()));

        TableColumn<ClassSession, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubject().getCode()));

        TableColumn<ClassSession, String> lecturerCol = new TableColumn<>("Lecturer");
        lecturerCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getLecturer().getName()));

        TableColumn<ClassSession, LocalDateTime> dateCol = new TableColumn<>("DateTime");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDateTime"));

        TableColumn<ClassSession, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        table.getColumns().clear();
        table.getColumns().add(courseCol);
        table.getColumns().add(subjectCol);
        table.getColumns().add(lecturerCol);
        table.getColumns().add(dateCol);
        table.getColumns().add(roomCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selected = newVal;
            if (newVal != null && editable) {
                courseCombo.setValue(newVal.getCourse());
                refreshSubjectCombo();
                subjectCombo.setValue(newVal.getSubject());
                lecturerCombo.setValue(newVal.getLecturer());
                dateTimeField.setText(newVal.getSessionDateTime().format(FORMATTER));
                roomField.setText(newVal.getRoom());
            }
        });
    }

    private GridPane buildForm() {
        courseCombo.setPromptText("Course");
        subjectCombo.setPromptText("Subject");
        lecturerCombo.setPromptText("Lecturer");
        dateTimeField.setPromptText("yyyy-MM-dd HH:mm");
        roomField.setPromptText("Room");

        courseCombo.valueProperty().addListener((obs, oldVal, newVal) -> refreshSubjectCombo());

        Button saveBtn = new Button("Save/Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");

        saveBtn.setOnAction(e -> save());
        deleteBtn.setOnAction(e -> delete());
        clearBtn.setOnAction(e -> clear());

        HBox actions = new HBox(8, saveBtn, deleteBtn, clearBtn);

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.setPadding(new Insets(12, 0, 0, 0));
        form.addRow(0, new Label("Course"), courseCombo, new Label("Subject"), subjectCombo, new Label("Lecturer"),
                lecturerCombo);
        form.addRow(1, new Label("Date Time"), dateTimeField, new Label("Room"), roomField);
        form.add(actions, 0, 2, 6, 1);
        return form;
    }

    private void save() {
        if (courseCombo.getValue() == null || subjectCombo.getValue() == null || lecturerCombo.getValue() == null ||
                dateTimeField.getText().isBlank() || roomField.getText().isBlank()) {
            UiUtil.error("Validation", "All fields are required");
            return;
        }

        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(dateTimeField.getText().trim(), FORMATTER);
        } catch (Exception ex) {
            UiUtil.error("Validation", "Use date time format: yyyy-MM-dd HH:mm");
            return;
        }

        ClassSession target = selected == null ? new ClassSession() : selected;
        target.setCourse(courseCombo.getValue());
        target.setSubject(subjectCombo.getValue());
        target.setLecturer(lecturerCombo.getValue());
        target.setSessionDateTime(dateTime);
        target.setRoom(roomField.getText().trim());

        classSessionService.save(target);
        refresh();
        clear();
    }

    private void delete() {
        if (selected == null) {
            UiUtil.error("Delete", "Select a class session first");
            return;
        }
        classSessionService.delete(selected);
        refresh();
        clear();
    }

    private void clear() {
        selected = null;
        table.getSelectionModel().clearSelection();
        courseCombo.setValue(null);
        subjectCombo.setValue(null);
        lecturerCombo.setValue(null);
        dateTimeField.clear();
        roomField.clear();
    }

    private void refresh() {
        if (loggedUser.getRole() == Role.LECTURER && loggedUser.getLecturer() != null) {
            table.setItems(FXCollections
                    .observableArrayList(classSessionService.findByLecturer(loggedUser.getLecturer().getId())));
        } else {
            table.setItems(FXCollections.observableArrayList(classSessionService.findAll()));
        }

        if (editable) {
            courseCombo.setItems(FXCollections.observableArrayList(courseService.findAll()));
            lecturerCombo.setItems(FXCollections.observableArrayList(lecturerService.findAll()));
            refreshSubjectCombo();
        }
    }

    private void refreshSubjectCombo() {
        if (!editable) {
            return;
        }
        if (courseCombo.getValue() == null) {
            subjectCombo.setItems(FXCollections.observableArrayList(subjectService.findAll()));
            return;
        }
        subjectCombo.setItems(
                FXCollections.observableArrayList(subjectService.findByCourse(courseCombo.getValue().getId())));
    }
}
