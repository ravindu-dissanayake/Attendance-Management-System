package com.sams;

import com.sams.service.AuthService;
import com.sams.ui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class SamsApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        AuthService authService = new AuthService();
        authService.seedDefaults();
        LoginView loginView = new LoginView(authService);
        loginView.show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
