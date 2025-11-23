package lk.businessmanagement.staffcore.model;

public class DailyAttendance {
    private int empId;
    private String employeeName;
    private String photoPath;

    // Status: 1=Present, 2=Leave, 3=Not Marked
    private int status;

    private String inTime;
    private String outTime;

    public DailyAttendance(int empId, String employeeName, String photoPath, int status, String inTime, String outTime) {
        this.empId = empId;
        this.employeeName = employeeName;
        this.photoPath = photoPath;
        this.status = status;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    // Getters
    public int getEmpId() { return empId; }
    public String getEmployeeName() { return employeeName; }
    public String getPhotoPath() { return photoPath; }
    public int getStatus() { return status; }
    public String getInTime() { return inTime; }
    public String getOutTime() { return outTime; }
}