public class LeaveModel {
    private int leaveId;
    private int empId;
    private String empName;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status;

    // 1. Default Constructor (Required for 'new LeaveModel()')
    public LeaveModel() {
    }

    // 2. Full Parameterized Constructor
    public LeaveModel(int leaveId, int empId, String empName, String leaveType, String startDate, String endDate, String reason, String status) {
        this.leaveId = leaveId;
        this.empId = empId;
        this.empName = empName;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
    }

    // Getters and Setters
    public int getLeaveId() { 
        return leaveId; 
    }
    public void setLeaveId(int leaveId) { 
        this.leaveId = leaveId; 
    }

    public int getEmpId() { 
        return empId; 
    }
    public void setEmpId(int empId) { 
        this.empId = empId; 
    }

    public String getEmpName() { 
        return empName; 
    }
    public void setEmpName(String empName) { 
        this.empName = empName; 
    }

    public String getLeaveType() { 
        return leaveType; 
    }
    public void setLeaveType(String leaveType) { 
        this.leaveType = leaveType; 
    }

    public String getStartDate() { 
        return startDate; 
    }
    public void setStartDate(String startDate) { 
        this.startDate = startDate; 
    }

    public String getEndDate() { 
        return endDate; 
    }
    public void setEndDate(String endDate) { 
        this.endDate = endDate; 
    }

    public String getReason() { 
        return reason; 
    }
    public void setReason(String reason) { 
        this.reason = reason; 
    }

    public String getStatus() { 
        return status; 
    }
    public void setStatus(String status) { 
        this.status = status; 
    }
}