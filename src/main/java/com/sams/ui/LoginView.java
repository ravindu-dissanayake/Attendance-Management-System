package com.sams.ui;

import com.sams.entity.AppUser;
import com.sams.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginView {

    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage) {
        Label title = new Label("Student Attendance Management System");
        title.getStyleClass().add("title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Username"), usernameField);
        form.addRow(1, new Label("Password"), passwordField);
        form.add(loginButton, 1, 2);

        VBox root = new VBox(16, title, form,
                new Label("Default Admin: admin / admin123 | Lecturer: lecturer / lec123"));
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> {
            Optional<AppUser> user = authService.authenticate(usernameField.getText().trim(), passwordField.getText());
            if (user.isPresent()) {
                DashboardView dashboardView = new DashboardView(user.get());
                dashboardView.show(stage);
                return;
            }
            UiUtil.error("Login Failed", "Invalid username or password");
        });

        Scene scene = new Scene(root, 680, 360);
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("SAMS Login");
        stage.show();
    }
}
