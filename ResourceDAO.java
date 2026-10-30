import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public List<Resource> getAllResources() {
        List<Resource> list = new ArrayList<>();
        String sql = "SELECT r.*, c.category_name FROM resource r LEFT JOIN resource_category c ON r.category_id = c.category_id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Resource r = new Resource();
                r.setResourceId(rs.getInt("resource_id"));
                r.setResourceName(rs.getString("resource_name"));
                r.setCategoryId(rs.getInt("category_id"));
                r.setCategoryName(rs.getString("category_name"));
                r.setResourceType(rs.getString("resource_type"));
                r.setLocation(rs.getString("location"));
                r.setStatus(rs.getString("status"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addResource(Resource r) {
        String sql = "INSERT INTO resource (resource_name, category_id, resource_type, location, status, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, r.getResourceName());
            pst.setInt(2, r.getCategoryId());
            pst.setString(3, r.getResourceType());
            pst.setString(4, r.getLocation());
            pst.setString(5, r.getStatus());
            pst.setString(6, r.getDescription());
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}