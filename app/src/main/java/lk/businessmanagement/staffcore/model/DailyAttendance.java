package lk.businessmanagement.staffcore.model;

public class DailyAttendance {
    private int empId;
    private String employeeName;
    private String photoPath;
    private String inTime;
    private String outTime;
    private boolean isLeave;

    private String joinedDate;

    // Constructor (Empty)
    public DailyAttendance() { }

    // --- Getters & Setters ---
    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getInTime() { return inTime; }
    public void setInTime(String inTime) { this.inTime = inTime; }

    public String getOutTime() { return outTime; }
    public void setOutTime(String outTime) { this.outTime = outTime; }

    public boolean isLeave() { return isLeave; }
    public void setLeave(boolean leave) { isLeave = leave; }

    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }

    // Status Logic (1=Present, 2=Leave, 3=Pending)
    public int getStatus() {
        if (isLeave) return 2;
        if (inTime != null && !inTime.isEmpty()) return 1;
        return 3;
    }
}