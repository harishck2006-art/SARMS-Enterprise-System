import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    // 1. Check if Employee is currently on Approved Leave
    public boolean isEmployeeOnLeave(int empId) {
        String query = "SELECT COUNT(*) FROM leave_requests WHERE emp_id = ? AND status = 'Approved' " +
                       "AND CURDATE() BETWEEN start_date AND end_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Submit Leave Request
    public boolean submitLeave(int empId, String leaveType, String startDate, String endDate, String reason) {
        String query = "INSERT INTO leave_requests (emp_id, leave_type, start_date, end_date, reason, status) " +
                       "VALUES (?, ?, ?, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            pstmt.setString(2, leaveType);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);
            pstmt.setString(5, reason);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Alias method for Model object input
    public boolean applyLeave(LeaveModel model) {
        if (model == null) return false;
        return submitLeave(model.getEmpId(), model.getLeaveType(), model.getStartDate(), model.getEndDate(), model.getReason());
    }

    // 3. Get Personal Leaves for Employee
    public List<LeaveModel> getPersonalLeaves(int empId) {
        List<LeaveModel> list = new ArrayList<>();
        String query = "SELECT * FROM leave_requests WHERE emp_id = ? ORDER BY leave_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LeaveModel model = new LeaveModel();
                model.setLeaveId(rs.getInt("leave_id"));
                model.setEmpId(rs.getInt("emp_id"));
                model.setLeaveType(rs.getString("leave_type"));
                model.setStartDate(rs.getString("start_date"));
                model.setEndDate(rs.getString("end_date"));
                model.setReason(rs.getString("reason"));
                model.setStatus(rs.getString("status"));
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Alias method to prevent 'getLeavesByEmployee undefined'
    public List<LeaveModel> getLeavesByEmployee(int empId) {
        return getPersonalLeaves(empId);
    }

    // 4. Get All Pending/All Leaves
    public List<LeaveModel> getAllPendingLeaves() {
        List<LeaveModel> list = new ArrayList<>();
        String query = "SELECT l.*, e.first_name AS emp_full_name " +
                       "FROM leave_requests l LEFT JOIN employees e ON l.emp_id = e.emp_id " +
                       "ORDER BY l.leave_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LeaveModel model = new LeaveModel();
                model.setLeaveId(rs.getInt("leave_id"));
                model.setEmpId(rs.getInt("emp_id"));
                
                String full = rs.getString("emp_full_name");
                model.setEmpName(full != null ? full : "Employee #" + rs.getInt("emp_id"));
                
                model.setLeaveType(rs.getString("leave_type"));
                model.setStartDate(rs.getString("start_date"));
                model.setEndDate(rs.getString("end_date"));
                model.setReason(rs.getString("reason"));
                model.setStatus(rs.getString("status"));
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Alias method to prevent 'getAllLeaves() undefined'
    public List<LeaveModel> getAllLeaves() {
        return getAllPendingLeaves();
    }

    // 5. Get Active Approved Leaves
    public List<LeaveModel> getActiveApprovedLeaves() {
        List<LeaveModel> list = new ArrayList<>();
        String query = "SELECT l.*, e.first_name AS emp_full_name " +
                       "FROM leave_requests l LEFT JOIN employees e ON l.emp_id = e.emp_id " +
                       "WHERE l.status = 'Approved' ORDER BY l.leave_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LeaveModel model = new LeaveModel();
                model.setLeaveId(rs.getInt("leave_id"));
                model.setEmpId(rs.getInt("emp_id"));
                
                String full = rs.getString("emp_full_name");
                model.setEmpName(full != null ? full : "Employee #" + rs.getInt("emp_id"));
                
                model.setLeaveType(rs.getString("leave_type"));
                model.setStartDate(rs.getString("start_date"));
                model.setEndDate(rs.getString("end_date"));
                model.setReason(rs.getString("reason"));
                model.setStatus(rs.getString("status"));
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 6. Update Leave Status
    public boolean updateLeaveStatus(int leaveId, String status, String remarks) {
        String query = "UPDATE leave_requests SET status = ? WHERE leave_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, leaveId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}