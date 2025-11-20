package lk.businessmanagement.staffcore.model;

public class Employee {
    private int id;
    private String name;
    private String nic;
    private String gender;
    private String dob;
    private String maritalStatus;
    private String mobileNumber;
    private String homeNumber;
    private String address;
    private String joinedDate;
    private String profilePhotoPath;
    private String idFrontPath;
    private String idBackPath;
    private String cvPath;

    public Employee() {
    }

    public Employee(String name, String nic, String gender, String dob, String maritalStatus,
                    String mobileNumber, String homeNumber, String address, String joinedDate,
                    String profilePhotoPath, String idFrontPath, String idBackPath, String cvPath) {
        this.name = name;
        this.nic = nic;
        this.gender = gender;
        this.dob = dob;
        this.maritalStatus = maritalStatus;
        this.mobileNumber = mobileNumber;
        this.homeNumber = homeNumber;
        this.address = address;
        this.joinedDate = joinedDate;
        this.profilePhotoPath = profilePhotoPath;
        this.idFrontPath = idFrontPath;
        this.idBackPath = idBackPath;
        this.cvPath = cvPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getHomeNumber() { return homeNumber; }
    public void setHomeNumber(String homeNumber) { this.homeNumber = homeNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }

    public String getProfilePhotoPath() { return profilePhotoPath; }
    public void setProfilePhotoPath(String profilePhotoPath) { this.profilePhotoPath = profilePhotoPath; }

    public String getIdFrontPath() { return idFrontPath; }
    public void setIdFrontPath(String idFrontPath) { this.idFrontPath = idFrontPath; }

    public String getIdBackPath() { return idBackPath; }
    public void setIdBackPath(String idBackPath) { this.idBackPath = idBackPath; }

    public String getCvPath() { return cvPath; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
}
