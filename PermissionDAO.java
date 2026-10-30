import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDAO {

    public boolean generatePermission(Permission p) {
        String sql = "INSERT INTO permission (request_id, emp_id, resource_id, start_date, expiry_date, status) VALUES (?, ?, ?, ?, ?, 'Active')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, p.getRequestId());
            pst.setInt(2, p.getEmpId());
            pst.setInt(3, p.getResourceId());
            pst.setString(4, p.getStartDate());
            pst.setString(5, p.getExpiryDate());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Permission> getAllPermissions() {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.*, e.first_name, e.last_name, r.resource_name FROM permission p " +
                     "JOIN employee e ON p.emp_id = e.emp_id " +
                     "JOIN resource r ON p.resource_id = r.resource_id ORDER BY p.permission_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Permission p = new Permission();
                p.setPermissionId(rs.getInt("permission_id"));
                p.setRequestId(rs.getInt("request_id"));
                p.setEmpId(rs.getInt("emp_id"));
                p.setEmpName(rs.getString("first_name") + " " + rs.getString("last_name"));
                p.setResourceId(rs.getInt("resource_id"));
                p.setResourceName(rs.getString("resource_name"));
                p.setStartDate(rs.getString("start_date"));
                p.setExpiryDate(rs.getString("expiry_date"));
                p.setStatus(rs.getString("status"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}