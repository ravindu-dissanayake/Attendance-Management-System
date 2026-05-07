package com.sams.ui.tabs;

import com.sams.entity.Course;
import com.sams.entity.Student;
import com.sams.service.CourseService;
import com.sams.service.StudentService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class StudentTab extends BorderPane {

    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();

    private final TableView<Student> table = new TableView<>();
    private final TextField regNoField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField contactField = new TextField();
    private final ComboBox<Course> courseCombo = new ComboBox<>();

    private Student selected;

    public StudentTab() {
        setPadding(new Insets(12));
        buildTable();
        setCenter(table);
        setBottom(buildForm());
        refresh();
    }

    private void buildTable() {
        TableColumn<Student, String> regCol = new TableColumn<>("Reg No");
        regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Student, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        TableColumn<Student, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getCode()));

        table.getColumns().clear();
        table.getColumns().add(regCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(emailCol);
        table.getColumns().add(contactCol);
        table.getColumns().add(courseCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selected = newVal;
            if (newVal != null) {
                regNoField.setText(newVal.getRegistrationNumber());
                nameField.setText(newVal.getName());
                emailField.setText(newVal.getEmail());
                contactField.setText(newVal.getContact());
                courseCombo.setValue(newVal.getCourse());
            }
        });
    }

    private GridPane buildForm() {
        regNoField.setPromptText("Registration No");
        nameField.setPromptText("Name");
        emailField.setPromptText("Email");
        contactField.setPromptText("Contact");
        courseCombo.setPromptText("Course");

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
        form.addRow(0, new Label("Reg No"), regNoField, new Label("Name"), nameField, new Label("Course"), courseCombo);
        form.addRow(1, new Label("Email"), emailField, new Label("Contact"), contactField);
        form.add(actions, 0, 2, 6, 1);
        return form;
    }

    private void save() {
        if (regNoField.getText().isBlank() || nameField.getText().isBlank() || emailField.getText().isBlank() ||
                contactField.getText().isBlank() || courseCombo.getValue() == null) {
            UiUtil.error("Validation", "All fields are required");
            return;
        }

        Student target = selected == null ? new Student() : selected;
        target.setRegistrationNumber(regNoField.getText().trim());
        target.setName(nameField.getText().trim());
        target.setEmail(emailField.getText().trim());
        target.setContact(contactField.getText().trim());
        target.setCourse(courseCombo.getValue());

        studentService.save(target);
        refresh();
        clear();
    }

    private void delete() {
        if (selected == null) {
            UiUtil.error("Delete", "Select a student first");
            return;
        }
        studentService.delete(selected);
        refresh();
        clear();
    }

    private void clear() {
        selected = null;
        table.getSelectionModel().clearSelection();
        regNoField.clear();
        nameField.clear();
        emailField.clear();
        contactField.clear();
        courseCombo.setValue(null);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(studentService.findAll()));
        courseCombo.setItems(FXCollections.observableArrayList(courseService.findAll()));
    }
}
