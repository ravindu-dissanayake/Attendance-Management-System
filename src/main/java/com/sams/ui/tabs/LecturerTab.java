package com.sams.ui.tabs;

import com.sams.entity.Lecturer;
import com.sams.service.LecturerService;
import com.sams.ui.UiUtil;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class LecturerTab extends BorderPane {

    private final LecturerService lecturerService = new LecturerService();

    private final TableView<Lecturer> table = new TableView<>();
    private final TextField nameField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField contactField = new TextField();

    private Lecturer selected;

    public LecturerTab() {
        setPadding(new Insets(12));
        buildTable();
        setCenter(table);
        setBottom(buildForm());
        refresh();
    }

    private void buildTable() {
        TableColumn<Lecturer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Lecturer, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Lecturer, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

        table.getColumns().clear();
        table.getColumns().add(nameCol);
        table.getColumns().add(emailCol);
        table.getColumns().add(contactCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selected = newVal;
            if (newVal != null) {
                nameField.setText(newVal.getName());
                emailField.setText(newVal.getEmail());
                contactField.setText(newVal.getContact());
            }
        });
    }

    private GridPane buildForm() {
        nameField.setPromptText("Name");
        emailField.setPromptText("Email");
        contactField.setPromptText("Contact");

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
        form.addRow(0, new Label("Name"), nameField, new Label("Email"), emailField, new Label("Contact"),
                contactField);
        form.add(actions, 0, 1, 6, 1);
        return form;
    }

    private void save() {
        if (nameField.getText().isBlank() || emailField.getText().isBlank() || contactField.getText().isBlank()) {
            UiUtil.error("Validation", "Name, email and contact are required");
            return;
        }
        Lecturer target = selected == null ? new Lecturer() : selected;
        target.setName(nameField.getText().trim());
        target.setEmail(emailField.getText().trim());
        target.setContact(contactField.getText().trim());

        lecturerService.save(target);
        refresh();
        clear();
    }

    private void delete() {
        if (selected == null) {
            UiUtil.error("Delete", "Select a lecturer first");
            return;
        }
        lecturerService.delete(selected);
        refresh();
        clear();
    }

    private void clear() {
        selected = null;
        table.getSelectionModel().clearSelection();
        nameField.clear();
        emailField.clear();
        contactField.clear();
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(lecturerService.findAll()));
    }
}
