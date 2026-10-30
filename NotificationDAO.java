import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public void addNotification(int empId, String message, String type) {
        String sql = "INSERT INTO notification (emp_id, message, type) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, empId);
            pst.setString(2, message);
            pst.setString(3, type);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getNotifications(int empId) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT message, created_at, is_read FROM notification WHERE emp_id = ? ORDER BY notification_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, empId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String status = rs.getBoolean("is_read") ? "" : " [NEW]";
                list.add(rs.getString("created_at") + " - " + rs.getString("message") + status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void markAllAsRead(int empId) {
        String sql = "UPDATE notification SET is_read = TRUE WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, empId);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}