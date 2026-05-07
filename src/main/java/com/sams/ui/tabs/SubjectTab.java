package com.sams.ui.tabs;

import com.sams.entity.Course;
import com.sams.entity.Subject;
import com.sams.service.CourseService;
import com.sams.service.SubjectService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class SubjectTab extends BorderPane {

    private final SubjectService subjectService = new SubjectService();
    private final CourseService courseService = new CourseService();

    private final TableView<Subject> table = new TableView<>();
    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final ComboBox<Course> courseCombo = new ComboBox<>();

    private Subject selected;

    public SubjectTab() {
        setPadding(new Insets(12));
        buildTable();
        setCenter(table);
        setBottom(buildForm());
        refresh();
    }

    private void buildTable() {
        TableColumn<Subject, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Subject, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Subject, String> courseCol = new TableColumn<>("Course");
        courseCol.setCellValueFactory(
                data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourse().getCode()));

        table.getColumns().clear();
        table.getColumns().add(codeCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(courseCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selected = newVal;
            if (newVal != null) {
                codeField.setText(newVal.getCode());
                nameField.setText(newVal.getName());
                courseCombo.setValue(newVal.getCourse());
            }
        });
    }

    private GridPane buildForm() {
        codeField.setPromptText("Subject Code");
        nameField.setPromptText("Subject Name");
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
        form.addRow(0, new Label("Code"), codeField, new Label("Name"), nameField, new Label("Course"), courseCombo);
        form.add(actions, 0, 1, 6, 1);
        return form;
    }

    private void save() {
        if (codeField.getText().isBlank() || nameField.getText().isBlank() || courseCombo.getValue() == null) {
            UiUtil.error("Validation", "All fields are required");
            return;
        }

        Subject target = selected == null ? new Subject() : selected;
        target.setCode(codeField.getText().trim());
        target.setName(nameField.getText().trim());
        target.setCourse(courseCombo.getValue());

        subjectService.save(target);
        refresh();
        clear();
    }

    private void delete() {
        if (selected == null) {
            UiUtil.error("Delete", "Select a subject first");
            return;
        }
        subjectService.delete(selected);
        refresh();
        clear();
    }

    private void clear() {
        selected = null;
        table.getSelectionModel().clearSelection();
        codeField.clear();
        nameField.clear();
        courseCombo.setValue(null);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(subjectService.findAll()));
        courseCombo.setItems(FXCollections.observableArrayList(courseService.findAll()));
    }
}
