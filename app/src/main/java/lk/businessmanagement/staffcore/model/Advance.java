package lk.businessmanagement.staffcore.model;

public class Advance {
    private int id;
    private int empId;
    private String date;
    private double amount;

    public Advance() { }

    public Advance(int empId, String date, double amount) {
        this.empId = empId;
        this.date = date;
        this.amount = amount;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}