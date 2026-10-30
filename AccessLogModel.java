public class AccessLogModel {
    private int logId;
    private String empName;
    private String loginTime;
    private String logoutTime;
    private String status;
    private String resourceAccessed;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public String getEmpName() { return empName; }
    public void setEmpName(String empName) { this.empName = empName; }

    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

    public String getLogoutTime() { return logoutTime; }
    public void setLogoutTime(String logoutTime) { this.logoutTime = logoutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResourceAccessed() { return resourceAccessed; }
    public void setResourceAccessed(String resourceAccessed) { this.resourceAccessed = resourceAccessed; }
}