public class EmployeeModel {
    private int empId;
    private String empCode;
    private String fullName;
    private String email;
    private String role;

    public EmployeeModel() {}

    public EmployeeModel(int empId, String empCode, String fullName, String email, String role) {
        this.empId = empId;
        this.empCode = empCode;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}