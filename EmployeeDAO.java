import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // 1. Validate User Login Credentials (Required for LoginPage)
    public Employee validateUser(String username, String password) {
        String query = "SELECT e.*, r.role_name FROM employees e " +
                       "JOIN roles r ON e.role_id = r.role_id " +
                       "WHERE e.username = ? AND e.password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setEmpId(rs.getInt("emp_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setUsername(rs.getString("username"));
                emp.setRole(rs.getString("role_name")); // Fetches Role Name (Admin / Manager / Employee)
                return emp;
            }
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if login fails
    }

    // Alias Method for compatibility
    public Employee authenticateUser(String username, String password) {
        return validateUser(username, password);
    }

    // 2. Update Account Password
    public boolean updatePassword(int empId, String oldPassword, String newPassword) {
        String query = "UPDATE employees SET password = ? WHERE emp_id = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, empId);
            pstmt.setString(3, oldPassword);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 3. Fetch All Employees (Returns List<Employee>)
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT e.*, r.role_name FROM employees e JOIN roles r ON e.role_id = r.role_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmpId(rs.getInt("emp_id"));
                emp.setFirstName(rs.getString("first_name"));
                emp.setLastName(rs.getString("last_name"));
                emp.setUsername(rs.getString("username"));
                emp.setRole(rs.getString("role_name"));
                list.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employees: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // 4. Fetch All Employee Models for Table Directory (Used by EmployeePage)
    public List<EmployeeModel> getAllEmployeeModels() {
        List<EmployeeModel> list = new ArrayList<>();
        String sql = "SELECT e.emp_id, e.emp_code, e.first_name, e.last_name, e.email, r.role_name " +
                     "FROM employees e LEFT JOIN roles r ON e.role_id = r.role_id ORDER BY e.emp_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                EmployeeModel emp = new EmployeeModel();
                emp.setEmpId(rs.getInt("emp_id"));

                String code = rs.getString("emp_code");
                emp.setEmpCode((code != null && !code.isEmpty()) ? code : "EMP00" + rs.getInt("emp_id"));

                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                emp.setFullName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : ""));

                String email = rs.getString("email");
                emp.setEmail((email != null && !email.isEmpty()) ? email : (firstName != null ? firstName.toLowerCase() : "emp") + "@sarms.com");

                String role = rs.getString("role_name");
                emp.setRole(role != null ? role : "Employee");

                list.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching employee models: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}