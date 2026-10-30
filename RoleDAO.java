import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<String[]> getAllRoles() {
        List<String[]> list = new ArrayList<>();
        String query = "SELECT * FROM roles";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("role_id"));
                String name = rs.getString("role_name");
                String desc = rs.getMetaData().getColumnCount() >= 3 ? rs.getString(3) : "System Defined Privilege Group";
                list.add(new String[]{id, name, desc});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addRole(String roleName, String description) {
        String query = "INSERT INTO roles (role_name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, roleName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(int roleId, String newName) {
        String query = "UPDATE roles SET role_name = ? WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, roleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRole(int roleId) {
        String query = "DELETE FROM roles WHERE role_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roleId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}