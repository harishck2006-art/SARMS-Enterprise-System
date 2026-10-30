import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessLogDAO {

    // 1. Fetch All Access / Session Logs
    public List<AccessLog> getAllLogs() {
        List<AccessLog> list = new ArrayList<>();
        String sql = "SELECT request_id AS log_id, emp_id, request_date AS login_time, " +
                     "COALESCE(processed_date, 'Active Session') AS logout_time, status " +
                     "FROM access_requests ORDER BY request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AccessLog log = new AccessLog(
                    rs.getInt("log_id"),
                    rs.getInt("emp_id"),
                    rs.getString("login_time"),
                    rs.getString("logout_time"),
                    rs.getString("status")
                );
                list.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all access logs: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 2. Fetch Logs Filtered By Specific Employee ID
    public List<AccessLog> getLogsByEmpId(int empId) {
        List<AccessLog> list = new ArrayList<>();
        String sql = "SELECT request_id AS log_id, emp_id, request_date AS login_time, " +
                     "COALESCE(processed_date, 'Active Session') AS logout_time, status " +
                     "FROM access_requests WHERE emp_id = ? ORDER BY request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AccessLog log = new AccessLog(
                    rs.getInt("log_id"),
                    rs.getInt("emp_id"),
                    rs.getString("login_time"),
                    rs.getString("logout_time"),
                    rs.getString("status")
                );
                list.add(log);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee access logs: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 3. Create Login Session Entry
    public boolean createLoginSession(int empId) {
        String sql = "INSERT INTO access_requests (emp_id, resource_id, request_date, status) VALUES (?, 1, NOW(), 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error creating login session: " + e.getMessage());
            return false;
        }
    }
}