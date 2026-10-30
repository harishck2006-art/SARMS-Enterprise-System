import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;

public class LoginPage {

    private Stage stage;

    public LoginPage(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Main Screen Wrapper (Centered Layout)
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #0d1b2a;"); 

        // Center Login Card
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(380);
        card.setMaxHeight(520);
        card.setPadding(new Insets(35));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 5);");

        Label appTitle = new Label("SARMS LOGIN");
        appTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        Label appSub = new Label("Secure Access Management System");
        appSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        VBox fieldsBox = new VBox(12);
        fieldsBox.setAlignment(Pos.CENTER_LEFT);

        // Username Field
        Label lblUser = new Label("Username:");
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 12px;");
        TextField usernameFld = new TextField();
        usernameFld.setPromptText("Enter your username");
        usernameFld.setStyle("-fx-padding: 9; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        // Password Field with Eye Toggle
        Label lblPass = new Label("Password:");
        lblPass.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 12px;");

        PasswordField passwordFld = new PasswordField();
        passwordFld.setPromptText("Enter your password");
        passwordFld.setStyle("-fx-padding: 9; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");

        TextField visiblePasswordFld = new TextField();
        visiblePasswordFld.setPromptText("Enter your password");
        visiblePasswordFld.setStyle("-fx-padding: 9; -fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
        visiblePasswordFld.setManaged(false);
        visiblePasswordFld.setVisible(false);

        passwordFld.textProperty().bindBidirectional(visiblePasswordFld.textProperty());

        // Eye Toggle Button
        Button btnToggleEye = new Button("👁");
        btnToggleEye.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand; -fx-font-size: 13px;");

        btnToggleEye.setOnAction(e -> {
            if (passwordFld.isVisible()) {
                passwordFld.setVisible(false);
                passwordFld.setManaged(false);
                visiblePasswordFld.setVisible(true);
                visiblePasswordFld.setManaged(true);
                btnToggleEye.setText("🙈");
            } else {
                visiblePasswordFld.setVisible(false);
                visiblePasswordFld.setManaged(false);
                passwordFld.setVisible(true);
                passwordFld.setManaged(true);
                btnToggleEye.setText("👁");
            }
        });

        HBox passBox = new HBox(6);
        HBox.setHgrow(passwordFld, Priority.ALWAYS);
        HBox.setHgrow(visiblePasswordFld, Priority.ALWAYS);
        passBox.getChildren().addAll(passwordFld, visiblePasswordFld, btnToggleEye);

        // Forgot Password Hyperlink
        Hyperlink forgotPassLink = new Hyperlink("Forgot Password?");
        forgotPassLink.setStyle("-fx-text-fill: #3a86ff; -fx-font-size: 11px; -fx-font-weight: bold; -fx-border-color: transparent;");
        
        HBox forgotBox = new HBox();
        forgotBox.setAlignment(Pos.CENTER_RIGHT);
        forgotBox.getChildren().add(forgotPassLink);

        forgotPassLink.setOnAction(e -> handleCrossApprovalForgotPassword());

        fieldsBox.getChildren().addAll(lblUser, usernameFld, lblPass, passBox, forgotBox);

        Label errorLbl = new Label();
        errorLbl.setWrapText(true);
        errorLbl.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 11px; -fx-font-weight: bold;");

        // Clean Login Button
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");

        loginBtn.setOnAction(e -> {
            String u = usernameFld.getText().trim();
            String p = passwordFld.getText().trim();

            if (u.isEmpty() || p.isEmpty()) {
                errorLbl.setStyle("-fx-text-fill: #ef4444;");
                errorLbl.setText("⚠️ Please enter both username and password!");
                return;
            }

            // Step 1: Check Lockout Status and Attempt Counter from Database
            String checkSql = "SELECT failed_attempts, is_locked FROM employees WHERE username = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {

                pstmt.setString(1, u);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    boolean isLocked = rs.getBoolean("is_locked");
                    int failedAttempts = rs.getInt("failed_attempts");

                    if (isLocked) {
                        errorLbl.setStyle("-fx-text-fill: #ef4444;");
                        errorLbl.setText("🔒 Account LOCKED! 5 failed attempts reached.\nContact Security Officer to unlock.");
                        return;
                    }

                    EmployeeDAO empDao = new EmployeeDAO();
                    Employee user = empDao.validateUser(u, p);

                    if (user != null) {
                        resetFailedAttempts(u);

                        AccessLogDAO logDao = new AccessLogDAO();
                        logDao.createLoginSession(user.getEmpId());

                        String role = user.getRole();

                        if ("Admin".equalsIgnoreCase(role)) {
                            new AdminDashboard(stage, user).show();
                        } else if ("Manager".equalsIgnoreCase(role)) {
                            new ManagerDashboard(stage, user).show();
                        } else if ("Security Officer".equalsIgnoreCase(role) || "Security".equalsIgnoreCase(role)) {
                            new SecurityDashboard(stage, user).show();
                        } else {
                            new EmployeeDashboard(stage, user).show();
                        }
                    } else {
                        failedAttempts++;
                        if (failedAttempts >= 5) {
                            lockAccount(u);
                            errorLbl.setStyle("-fx-text-fill: #ef4444;");
                            errorLbl.setText("🚨 5 Failed Attempts! Your account is now LOCKED.\nContact Security Officer.");
                        } else {
                            updateFailedAttempts(u, failedAttempts);
                            errorLbl.setStyle("-fx-text-fill: #d97706;");
                            errorLbl.setText("❌ Invalid Password! Attempt " + failedAttempts + " of 5.");
                        }
                    }
                } else {
                    errorLbl.setStyle("-fx-text-fill: #ef4444;");
                    errorLbl.setText("❌ Username not found in system!");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                errorLbl.setStyle("-fx-text-fill: #ef4444;");
                errorLbl.setText("❌ Database Connection Error!");
            }
        });

        card.getChildren().addAll(appTitle, appSub, new Separator(), fieldsBox, errorLbl, loginBtn);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Login");
        stage.setMaximized(true);
        stage.show();
    }

    // --- AUTO-CREATE TABLE & CROSS-APPROVAL LOGIC ---
    private void handleCrossApprovalForgotPassword() {
        TextInputDialog userDialog = new TextInputDialog();
        userDialog.setTitle("SARMS - Password Reset Request");
        userDialog.setHeaderText("Enterprise Recovery Mechanism");
        userDialog.setContentText("Enter your Registered Username / Email:");

        Optional<String> result = userDialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String inputUser = result.get().trim();

            try (Connection conn = DBConnection.getConnection()) {

                // Step 1: Ensure threat_log table exists automatically in database
                String createTableSql = "CREATE TABLE IF NOT EXISTS threat_log (" +
                        "log_id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "emp_id INT, " +
                        "event_type VARCHAR(50), " +
                        "status VARCHAR(20), " +
                        "details VARCHAR(255), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ")";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSql);
                }

                // Step 2: Fetch Employee & Role Details
                String checkSql = "SELECT e.emp_id, e.username, COALESCE(r.role_name, 'Employee') AS role_name " +
                        "FROM employees e " +
                        "LEFT JOIN role r ON e.role_id = r.role_id " +
                        "WHERE e.username = ? OR e.email = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                    pstmt.setString(1, inputUser);
                    pstmt.setString(2, inputUser);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        int empId = rs.getInt("emp_id");
                        String username = rs.getString("username");
                        String role = rs.getString("role_name");

                        String targetApprover = "";
                        String logSql = "";

                        if ("Admin".equalsIgnoreCase(role) || "Administrator".equalsIgnoreCase(role)) {
                            targetApprover = "Security Officer (SOC)";
                            logSql = "INSERT INTO threat_log (emp_id, event_type, status, details) VALUES (?, 'ADMIN_RESET_REQUEST', 'PENDING', 'Admin password reset request sent to SOC')";
                        } else {
                            targetApprover = "System Administrator (ADMIN)";
                            logSql = "INSERT INTO threat_log (emp_id, event_type, status, details) VALUES (?, 'USER_RESET_REQUEST', 'PENDING', 'User password reset request sent to Admin')";
                        }

                        try (PreparedStatement logPstmt = conn.prepareStatement(logSql)) {
                            logPstmt.setInt(1, empId);
                            logPstmt.executeUpdate();

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Request Routed");
                            alert.setHeaderText(null);
                            alert.setContentText("📩 Password Reset Request for " + username + " (" + role + ") successfully logged!\n\nTarget Approval Authority: " + targetApprover);
                            alert.showAndWait();
                        }

                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Not Found");
                        alert.setHeaderText(null);
                        alert.setContentText("❌ Username not found in system!");
                        alert.showAndWait();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Database Error");
                alert.setHeaderText(null);
                alert.setContentText("❌ Error logging password reset request: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Helper Methods for DB Updates
    private void updateFailedAttempts(String username, int attempts) {
        String sql = "UPDATE employees SET failed_attempts = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attempts);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lockAccount(String username) {
        String sql = "UPDATE employees SET failed_attempts = 5, is_locked = TRUE WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetFailedAttempts(String username) {
        String sql = "UPDATE employees SET failed_attempts = 0 WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}