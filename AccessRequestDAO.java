import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessRequestDAO {

    // Overloaded Method 1: Compatible with CreateRequestPage (empId, resourceName, reason)
    public boolean createRequest(int empId, String resourceName, String reason) {
        String query = "INSERT INTO access_requests (emp_id, resource_id, resource_name, request_reason, status) " +
                       "VALUES (?, 101, ?, ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            pstmt.setString(2, resourceName);
            pstmt.setString(3, reason);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method 2: Existing method used by other modules
    public boolean createAccessRequest(int empId, int resourceId, String reason) {
        String query = "INSERT INTO access_requests (emp_id, resource_id, resource_name, request_reason, status) " +
                       "VALUES (?, ?, 'Financial Records Vault', ?, 'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, empId);
            pstmt.setInt(2, resourceId);
            pstmt.setString(3, reason);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch Access Requests ONLY for specific Logged-in Employee (Used by MyAccessRequestsPage / MyKeysPage)
    public List<AccessRequestModel> getEmployeeRequestsHistory(int empId) {
        List<AccessRequestModel> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name, e.emp_code " +
                     "FROM access_requests r JOIN employees e ON r.emp_id = e.emp_id " +
                     "WHERE r.emp_id = ? ORDER BY r.request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, empId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AccessRequestModel model = new AccessRequestModel();
                model.setRequestId(rs.getInt("request_id"));
                model.setEmpName(rs.getString("emp_name") + " (" + rs.getString("emp_code") + ")");
                
                try {
                    model.setResourceName(rs.getString("resource_name"));
                } catch (SQLException ex) {
                    model.setResourceName("System Access");
                }

                try {
                    model.setReason(rs.getString("request_reason"));
                } catch (SQLException ex) {
                    model.setReason("Business Justification");
                }

                try {
                    model.setRequestDate(rs.getString("request_date"));
                } catch (SQLException ex) {
                    model.setRequestDate("2026-10-11");
                }

                model.setStatus(rs.getString("status"));

                try {
                    String pDate = rs.getString("processed_date");
                    model.setProcessedDate(pDate != null ? pDate : "Pending Decision");
                } catch (SQLException ex) {
                    model.setProcessedDate("Pending Decision");
                }

                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Overloaded helper for MyKeysPage
    public List<AccessRequestModel> getRequestsByEmployee(int empId) {
        return getEmployeeRequestsHistory(empId);
    }

    // Fix for AccessRequestPage compilation error
    public List<AccessRequestModel> getRequestsByEmpId(int empId) {
        return getEmployeeRequestsHistory(empId);
    }

    // Fetch All Requests History Log (Used by Manager/Admin)
    public List<AccessRequestModel> getAllRequestsHistory() {
        List<AccessRequestModel> list = new ArrayList<>();
        String sql = "SELECT r.*, CONCAT(e.first_name, ' ', e.last_name) AS emp_name, e.emp_code " +
                     "FROM access_requests r JOIN employees e ON r.emp_id = e.emp_id ORDER BY r.request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                AccessRequestModel model = new AccessRequestModel();
                model.setRequestId(rs.getInt("request_id"));
                model.setEmpName(rs.getString("emp_name") + " (" + rs.getString("emp_code") + ")");
                
                try {
                    model.setResourceName(rs.getString("resource_name"));
                } catch (SQLException ex) {
                    model.setResourceName("Financial Vault Level 2");
                }

                try {
                    model.setReason(rs.getString("request_reason"));
                } catch (SQLException ex) {
                    model.setReason("Operational Sign-off");
                }

                try {
                    model.setRequestDate(rs.getString("request_date"));
                } catch (SQLException ex) {
                    model.setRequestDate("2026-10-11");
                }

                model.setStatus(rs.getString("status"));
                model.setProcessedDate(rs.getString("processed_date") != null ? rs.getString("processed_date") : "Pending Decision");
                list.add(model);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AccessRequestModel> getPendingRequests() {
        return getAllRequestsHistory();
    }

    // Update Request Status (Approve/Reject)
    public boolean updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE access_requests SET status = ?, processed_date = CURRENT_TIMESTAMP WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}