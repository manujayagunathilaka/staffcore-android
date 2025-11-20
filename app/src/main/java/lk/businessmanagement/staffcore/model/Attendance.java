package lk.businessmanagement.staffcore.model;

public class Attendance {
    private int id;
    private int empId;
    private String date;
    private String inTime;
    private String outTime;
    private boolean isLeave;
    private String leaveReason;

    public Attendance() {
    }

    public Attendance(int empId, String date, String inTime, String outTime, boolean isLeave, String leaveReason) {
        this.empId = empId;
        this.date = date;
        this.inTime = inTime;
        this.outTime = outTime;
        this.isLeave = isLeave;
        this.leaveReason = leaveReason;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getInTime() { return inTime; }
    public void setInTime(String inTime) { this.inTime = inTime; }

    public String getOutTime() { return outTime; }
    public void setOutTime(String outTime) { this.outTime = outTime; }

    public boolean isLeave() { return isLeave; }
    public void setLeave(boolean leave) { isLeave = leave; }

    public String getLeaveReason() { return leaveReason; }
    public void setLeaveReason(String leaveReason) { this.leaveReason = leaveReason; }
}