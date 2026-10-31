package expenseTracker.ui;

import expenseTracker.DBConnectivity;
import expenseTracker.PasswordUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage {

    private Stage stage;

    public RegisterPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {

        Label title = new Label("Create Account");

        TextField name = new TextField();
        name.setPromptText("Enter Name");

        TextField email = new TextField();
        email.setPromptText("Enter Email");

        PasswordField password = new PasswordField();
        password.setPromptText("Enter Password");

        PasswordField confirm = new PasswordField();
        confirm.setPromptText("Confirm Password");

        Label message = new Label();

        Button register = new Button("Register");
        Button back = new Button("Back to Login");

        register.setOnAction(e -> {

            if (name.getText().trim().isEmpty()
                    || email.getText().trim().isEmpty()
                    || password.getText().isEmpty()
                    || confirm.getText().isEmpty()) {

                message.setText("Fill all fields");
                return;
            }

            if (!password.getText().equals(confirm.getText())) {
                message.setText("Passwords do not match");
                return;
            }

            if (password.getText().length() < 6) {
                message.setText("Password must have 6 characters");
                return;
            }

            String sql = "INSERT INTO users(name,email,password) "
                    + "VALUES(?,?,?)";

            try (Connection con = DBConnectivity.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name.getText().trim());
                ps.setString(2, email.getText().trim().toLowerCase());
                ps.setString(3,
                        PasswordUtil.hashPassword(password.getText()));

                ps.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION,
                        "Registration Successful").showAndWait();

                new LoginApp().showLoginPage(stage);

            } catch (SQLException ex) {

                if (ex.getErrorCode() == 1062) {
                    message.setText("Email already registered");
                } else {
                    message.setText("Registration failed");
                    ex.printStackTrace();
                }

            } catch (RuntimeException ex) {
                message.setText("Password processing failed");
            }
        });

        back.setOnAction(e ->
                new LoginApp().showLoginPage(stage));

        VBox root = new VBox(
                15, title, name, email,
                password, confirm, register, back, message);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setTitle("Register");
        stage.setScene(new Scene(root, 450, 500));
        stage.show();
    }
}