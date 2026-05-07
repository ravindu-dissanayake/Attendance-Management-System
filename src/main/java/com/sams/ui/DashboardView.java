package com.sams.ui;

import com.sams.entity.AppUser;
import com.sams.entity.Role;
import com.sams.ui.tabs.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DashboardView {

    private final AppUser loggedUser;

    public DashboardView(AppUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    public void show(Stage stage) {
        Label top = new Label("Logged in as: " + loggedUser.getUsername() + " (" + loggedUser.getRole() + ")");
        top.setPadding(new Insets(10));

        TabPane tabPane = new TabPane();

        if (loggedUser.getRole() == Role.ADMIN) {
            tabPane.getTabs().add(new Tab("Courses", new CourseTab()));
            tabPane.getTabs().add(new Tab("Students", new StudentTab()));
            tabPane.getTabs().add(new Tab("Lecturers", new LecturerTab()));
            tabPane.getTabs().add(new Tab("Subjects", new SubjectTab()));
            tabPane.getTabs().add(new Tab("Classes", new ClassSessionTab(loggedUser, true)));
        } else {
            tabPane.getTabs().add(new Tab("My Classes", new ClassSessionTab(loggedUser, false)));
        }

        tabPane.getTabs().add(new Tab("Attendance", new AttendanceTab(loggedUser)));
        tabPane.getTabs().add(new Tab("Reports", new ReportTab(loggedUser)));
        tabPane.getTabs().forEach(tab -> tab.setClosable(false));

        BorderPane root = new BorderPane(tabPane);
        root.setTop(top);

        Scene scene = new Scene(root, 1280, 760);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        stage.setTitle("SAMS Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
