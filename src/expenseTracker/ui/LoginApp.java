package expenseTracker.ui;

import expenseTracker.DBConnectivity;
import expenseTracker.PasswordUtil;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginApp extends Application {

    @Override
    public void start(Stage stage) {
        showLoginPage(stage);
    }

    public void showLoginPage(Stage stage) {

        Label title = new Label("Daily Expense Tracker");

        TextField email = new TextField();
        email.setPromptText("Enter Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Enter Password");

        Label message = new Label();

        Button login = new Button("Login");
        Button register = new Button("Create Account");

        login.setOnAction(e -> {

            String sql =
                    "SELECT name,password FROM users WHERE email=?";

            try (Connection con = DBConnectivity.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1,
                        email.getText().trim().toLowerCase());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    String storedPassword =
                            rs.getString("password");

                    if (PasswordUtil.verifyPassword(
                            password.getText(), storedPassword)) {

                        String name = rs.getString("name");

                        new DashboardPage(
                                stage, name,
                                email.getText().trim().toLowerCase()
                        ).show();

                    } else {
                        message.setText("Incorrect password");
                    }

                } else {
                    message.setText("Account not found");
                }

            } catch (SQLException ex) {
                message.setText("Database connection failed");
                ex.printStackTrace();
            }
        });

        register.setOnAction(e ->
                new RegisterPage(stage).show());

        VBox root = new VBox(
                15, title, email, password,
                login, register, message);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setTitle("Daily Expense Tracker - Login");
        stage.setScene(new Scene(root, 450, 450));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}