package com.sams.ui.tabs;

import com.sams.entity.Course;
import com.sams.service.CourseService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class CourseTab extends BorderPane {

    private final CourseService courseService = new CourseService();

    private final TableView<Course> table = new TableView<>();
    private final TextField codeField = new TextField();
    private final TextField nameField = new TextField();
    private final TextArea descriptionField = new TextArea();

    private Course selected;

    public CourseTab() {
        setPadding(new Insets(12));
        buildTable();
        setCenter(table);
        setBottom(buildForm());
        refresh();
    }

    private void buildTable() {
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().clear();
        table.getColumns().add(codeCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(descCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selected = newVal;
            if (newVal != null) {
                codeField.setText(newVal.getCode());
                nameField.setText(newVal.getName());
                descriptionField.setText(newVal.getDescription());
            }
        });
    }

    private GridPane buildForm() {
        codeField.setPromptText("Course Code");
        nameField.setPromptText("Course Name");
        descriptionField.setPromptText("Description");
        descriptionField.setPrefRowCount(2);

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
        form.addRow(0, new Label("Code"), codeField, new Label("Name"), nameField);
        form.addRow(1, new Label("Description"), descriptionField);
        form.add(actions, 0, 2, 4, 1);
        return form;
    }

    private void save() {
        if (codeField.getText().isBlank() || nameField.getText().isBlank()) {
            UiUtil.error("Validation", "Course code and name are required");
            return;
        }
        Course target = selected == null ? new Course() : selected;
        target.setCode(codeField.getText().trim());
        target.setName(nameField.getText().trim());
        target.setDescription(descriptionField.getText().trim());

        courseService.save(target);
        refresh();
        clear();
    }

    private void delete() {
        if (selected == null) {
            UiUtil.error("Delete", "Select a course first");
            return;
        }
        courseService.delete(selected);
        refresh();
        clear();
    }

    private void clear() {
        selected = null;
        table.getSelectionModel().clearSelection();
        codeField.clear();
        nameField.clear();
        descriptionField.clear();
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(courseService.findAll()));
    }
}
