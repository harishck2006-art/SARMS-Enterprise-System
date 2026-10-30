public class Employee {

    private int empId;
    private String empCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String password;
    private int roleId;
    private String roleName;
    private String role;
    private String status;
    private int currentLogId;

    public Employee() {}

    public Employee(int empId, String empCode, String firstName, String lastName, String email, String phone, String username, int roleId, String roleName, String status) {
        this.empId = empId;
        this.empCode = empCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.username = username;
        this.roleId = roleId;
        this.roleName = roleName;
        this.role = roleName;
        this.status = status;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getEmpCode() { return empCode; }
    public void setEmpCode(String empCode) { this.empCode = empCode; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getRoleName() { return roleName != null ? roleName : role; }
    public void setRoleName(String roleName) { 
        this.roleName = roleName; 
        this.role = roleName; 
    }

    public String getRole() { return role != null ? role : roleName; }
    public void setRole(String role) { 
        this.role = role; 
        this.roleName = role; 
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCurrentLogId() { return currentLogId; }
    public void setCurrentLogId(int currentLogId) { this.currentLogId = currentLogId; }
}