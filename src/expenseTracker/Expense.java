package expenseTracker;
public class Expense {
    private int id;
    private String date;
    private String category;
    private double amount;
    private String description;
    public Expense(int id, String date, String category,
                   double amount, String description) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    public int getId() {
        return id;
    }
    public String getDate() {
        return date;
    }
    public String getCategory() {
        return category;
    }
    public double getAmount() {
        return amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}