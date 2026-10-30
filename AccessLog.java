public class AccessLog {
    private int logId;
    private int empId;
    private String loginTime;
    private String logoutTime;
    private String status;

    public AccessLog() {}

    public AccessLog(int logId, int empId, String loginTime, String logoutTime, String status) {
        this.logId = logId;
        this.empId = empId;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.status = status;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

    public String getLogoutTime() { return logoutTime; }
    public void setLogoutTime(String logoutTime) { this.logoutTime = logoutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}