package expenseTracker.ui;

import expenseTracker.Expense;
import expenseTracker.ExpenseManager;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class DashboardPage {

    private Stage stage;
    private String name;
    private String email;
    private ExpenseManager manager;

    public DashboardPage(Stage stage, String name, String email) {
        this.stage = stage;
        this.name = name;
        this.email = email;
        this.manager = new ExpenseManager(email);
    }

    public void show() {

        Label title = new Label("Daily Expense Tracker");
        Label welcome = new Label("Welcome " + name);

        Button add = new Button("Add Expense");
        Button view = new Button("View Expenses");
        Button update = new Button("Update Expense");
        Button delete = new Button("Delete Expense");
        Button search = new Button("Search Expense");
        Button report = new Button("Expense Report");
        Button logout = new Button("Logout");

        add.setMaxWidth(Double.MAX_VALUE);
        view.setMaxWidth(Double.MAX_VALUE);
        update.setMaxWidth(Double.MAX_VALUE);
        delete.setMaxWidth(Double.MAX_VALUE);
        search.setMaxWidth(Double.MAX_VALUE);
        report.setMaxWidth(Double.MAX_VALUE);
        logout.setMaxWidth(Double.MAX_VALUE);

        add.setOnAction(e -> addExpense());
        view.setOnAction(e -> viewExpenses());
        update.setOnAction(e -> updateExpense());
        delete.setOnAction(e -> deleteExpense());
        search.setOnAction(e -> searchExpense());
        report.setOnAction(e -> showReport());

        logout.setOnAction(e ->
                new LoginApp().showLoginPage(stage));

        VBox root = new VBox(
                15, title, welcome, add, view,
                update, delete, search, report, logout);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setMaxWidth(350);

        stage.setTitle("Daily Expense Tracker - Dashboard");
        stage.setScene(new Scene(root, 500, 600));
        stage.show();
    }

    private void addExpense() {

        Dialog<Expense> dialog = new Dialog<>();

        TextField date = new TextField();
        TextField category = new TextField();
        TextField amount = new TextField();
        TextField description = new TextField();

        date.setPromptText("Date (yyyy-MM-dd)");
        category.setPromptText("Category");
        amount.setPromptText("Amount");
        description.setPromptText("Description");

        ButtonType save = new ButtonType(
                "Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(
                save, ButtonType.CANCEL);

        VBox fields = new VBox(
                10, date, category, amount, description);

        fields.setPadding(new Insets(15));

        dialog.setTitle("Add Expense");
        dialog.getDialogPane().setContent(fields);

        dialog.setResultConverter(button -> {

            if (button == save) {
                try {
                    return new Expense(
                            0,
                            date.getText().trim(),
                            category.getText().trim(),
                            Double.parseDouble(amount.getText().trim()),
                            description.getText().trim()
                    );
                } catch (Exception ex) {
                    return null;
                }
            }

            return null;
        });

        Optional<Expense> result = dialog.showAndWait();

        if (result.isPresent()) {

            Expense expense = result.get();

            if (expense.getDate().isEmpty()
                    || expense.getCategory().isEmpty()
                    || expense.getDescription().isEmpty()
                    || expense.getAmount() <= 0) {
                showMessage("Enter valid expense details.");
                return;
            }

            try {
                if (manager.addExpense(expense)) {
                    showMessage("Expense added successfully.");
                } else {
                    showMessage("Unable to add expense.");
                }
            } catch (IllegalArgumentException ex) {
                showMessage("Date must be yyyy-MM-dd.");
            }
        }
    }

    private void viewExpenses() {

        List<Expense> list = manager.getExpenses();

        if (list.isEmpty()) {
            showMessage("No expenses found.");
            return;
        }

        TableView<Expense> table = createTable(list);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("View Expenses");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setContent(table);
        dialog.getDialogPane().setPrefSize(750, 400);
        dialog.showAndWait();
    }

    private TableView<Expense> createTable(List<Expense> list) {

        TableView<Expense> table = new TableView<>();

        TableColumn<Expense, Integer> id =
                new TableColumn<>("ID");

        id.setCellValueFactory(c ->
                new javafx.beans.property.SimpleIntegerProperty(
                        c.getValue().getId()).asObject());

        TableColumn<Expense, String> date =
                new TableColumn<>("Date");

        date.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getDate()));

        TableColumn<Expense, String> category =
                new TableColumn<>("Category");

        category.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getCategory()));

        TableColumn<Expense, Double> amount =
                new TableColumn<>("Amount");

        amount.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(
                        c.getValue().getAmount()).asObject());

        TableColumn<Expense, String> description =
                new TableColumn<>("Description");

        description.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getDescription()));

        table.getColumns().addAll(
                id, date, category, amount, description);

        table.setItems(
                FXCollections.observableArrayList(list));

        return table;
    }

    private void updateExpense() {

        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Enter Expense ID to update");

        Optional<String> result = input.showAndWait();

        if (!result.isPresent()) return;

        try {
            int id = Integer.parseInt(result.get().trim());

            Expense current = null;

            for (Expense e : manager.getExpenses()) {
                if (e.getId() == id) {
                    current = e;
                    break;
                }
            }

            if (current == null) {
                showMessage("Expense not found.");
                return;
            }

            TextInputDialog date = new TextInputDialog(
                    current.getDate());

            date.setHeaderText("Enter new date (yyyy-MM-dd)");

            Optional<String> d = date.showAndWait();
            if (!d.isPresent()) return;

            TextInputDialog category = new TextInputDialog(
                    current.getCategory());

            category.setHeaderText("Enter new category");

            Optional<String> c = category.showAndWait();
            if (!c.isPresent()) return;

            TextInputDialog amount = new TextInputDialog(
                    String.valueOf(current.getAmount()));

            amount.setHeaderText("Enter new amount");

            Optional<String> a = amount.showAndWait();
            if (!a.isPresent()) return;

            TextInputDialog description = new TextInputDialog(
                    current.getDescription());

            description.setHeaderText("Enter new description");

            Optional<String> desc = description.showAndWait();
            if (!desc.isPresent()) return;

            Expense updated = new Expense(
                    id, d.get(), c.get(),
                    Double.parseDouble(a.get()),
                    desc.get());

            if (updated.getDate().isEmpty()
                    || updated.getCategory().isEmpty()
                    || updated.getDescription().isEmpty()
                    || updated.getAmount() <= 0) {
                showMessage("Enter valid details.");
                return;
            }

            if (manager.updateExpense(updated)) {
                showMessage("Expense updated successfully.");
            } else {
                showMessage("Update failed.");
            }

        } catch (NumberFormatException ex) {
            showMessage("Enter valid ID and amount.");
        } catch (IllegalArgumentException ex) {
            showMessage("Date must be yyyy-MM-dd.");
        }
    }

    private void deleteExpense() {

        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Enter Expense ID to delete");

        Optional<String> result = input.showAndWait();

        if (!result.isPresent()) return;

        try {
            int id = Integer.parseInt(result.get().trim());

            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION);

            confirm.setHeaderText("Are you sure?");

            Optional<ButtonType> answer = confirm.showAndWait();

            if (answer.isPresent()
                    && answer.get() == ButtonType.OK) {

                if (manager.deleteExpense(id)) {
                    showMessage("Expense deleted.");
                } else {
                    showMessage("Expense not found.");
                }
            }

        } catch (NumberFormatException ex) {
            showMessage("Enter a valid ID.");
        }
    }

    private void searchExpense() {

        TextInputDialog input = new TextInputDialog();
        input.setHeaderText("Enter category to search");

        Optional<String> result = input.showAndWait();

        if (!result.isPresent()) return;

        List<Expense> list =
                manager.searchByCategory(result.get().trim());

        if (list.isEmpty()) {
            showMessage("No matching expenses found.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Results");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setContent(createTable(list));
        dialog.getDialogPane().setPrefSize(750, 400);
        dialog.showAndWait();
    }

    private void showReport() {

        double total = manager.getTotalExpenses();

        showMessage("Total Expenses: Rs. "
                + String.format("%.2f", total));
    }

    private void showMessage(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Daily Expense Tracker");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}