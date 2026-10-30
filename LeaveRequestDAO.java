import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    // Helper: Check if Employee is currently on Approved Leave
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

    public List<LeaveRequest> getAllLeaveRequests() {
        List<LeaveRequest> list = new ArrayList<>();
        String query = "SELECT l.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name, r.role_name " +
                       "FROM leave_requests l JOIN employees e ON l.emp_id = e.emp_id " +
                       "JOIN roles r ON e.role_id = r.role_id ORDER BY l.leave_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                LeaveRequest req = new LeaveRequest();
                req.setLeaveId(rs.getInt("leave_id"));
                req.setEmpId(rs.getInt("emp_id"));
                req.setEmpName(rs.getString("emp_name"));
                req.setRoleName(rs.getString("role_name"));
                req.setLeaveType(rs.getString("leave_type"));
                req.setStartDate(rs.getString("start_date"));
                req.setEndDate(rs.getString("end_date"));
                req.setReason(rs.getString("reason"));
                req.setStatus(rs.getString("status"));
                list.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateLeaveStatus(int leaveId, String status) {
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