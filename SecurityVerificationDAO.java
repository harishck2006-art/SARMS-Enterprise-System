import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SecurityVerificationDAO {

    public List<SecurityVerification> getAllVerifications() {
        List<SecurityVerification> list = new ArrayList<>();
        String sql = "SELECT sv.*, CONCAT(e.first_name, ' ', IFNULL(e.last_name, '')) AS emp_name " +
                     "FROM security_verification sv JOIN employee e ON sv.emp_id = e.emp_id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                SecurityVerification sv = new SecurityVerification();
                sv.setVerificationId(rs.getInt("verification_id"));
                sv.setEmpId(rs.getInt("emp_id"));
                sv.setEmpName(rs.getString("emp_name"));
                sv.setDegreeStatus(rs.getString("degree_status"));
                sv.setBackgroundStatus(rs.getString("background_status"));
                sv.setTrainingStatus(rs.getString("training_status"));
                sv.setOverallEligibility(rs.getString("overall_eligibility"));
                list.add(sv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(int empId, String field, String status) {
        String sql = "UPDATE security_verification SET " + field + " = ? WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, empId);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}