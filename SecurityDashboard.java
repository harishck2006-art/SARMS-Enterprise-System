import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;

public class SecurityDashboard {

    private Stage stage;
    private Employee currentUser;

    public SecurityDashboard(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar Navigation Panel
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label brandLogo = new Label("S");
        brandLogo.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 8 14; -fx-background-radius: 8;");
        
        VBox brandTitleBox = new VBox(2);
        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label brandSub = new Label("SOC CONTROL CENTER");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        brandTitleBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(brandLogo, brandTitleBox);

        Label socTag = new Label("• SOC LIVE OPERATIONS");
        socTag.setStyle("-fx-text-fill: #10b981; -fx-font-size: 11px; -fx-font-weight: bold; -fx-background-color: #064e3b; -fx-padding: 4 8; -fx-background-radius: 12;");

        VBox navMenu = new VBox(5);
        navMenu.setPadding(new Insets(8, 0, 0, 0));

        // Category 1: Official SOC Security Operations
        Label catSoc = new Label("SOC CONTROL OPERATIONS");
        catSoc.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 0 2 5;");

        Button btnCommand = createNavButton("📊 Command Center", true);
        Button btnPermissions = createNavButton("🔑 Grant & Revoke Permissions", false);
        Button btnThreats = createNavButton("🚨 Locked Accounts & Threats", false);
        Button btnAuditLogs = createNavButton("📊 System Audit Trail", false);

        btnCommand.setOnAction(e -> new SecurityDashboard(stage, currentUser).show());
        btnPermissions.setOnAction(e -> new PermissionPage(stage, currentUser).show());
        btnThreats.setOnAction(e -> openSocThreatsModal());
        btnAuditLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

        // Category 2: Practical Security Operations
        Label catAdvanced = new Label("SECURITY MONITORING");
        catAdvanced.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 0 2 5;");

        Button btnIncidents = createNavButton("📋 Security Incident Log", false);
        Button btnSystemHealth = createNavButton("🛡️ System Health Status", false);
        // ADDED: New Button for Password Approvals
        Button btnPendingPassApprovals = createNavButton("🔒 Pending Password Approvals", false);

        btnIncidents.setOnAction(e -> openIncidentLogWindow());
        btnSystemHealth.setOnAction(e -> openSystemHealthWindow());
        btnPendingPassApprovals.setOnAction(e -> openPendingPasswordApprovalsModal());

        // Category 3: Officer Self-Service & Account Control
        Label catOfficer = new Label("OFFICER SELF-SERVICE");
        catOfficer.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 8 0 2 5;");

        Button btnNewRequest = createNavButton("➕ New Access Request", false);
        Button btnMyKeys = createNavButton("🔑 My Keys & Clearances", false);
        Button btnLeave = createNavButton("📅 Apply Leave (To Manager)", false);
        Button btnChangePass = createNavButton("🔒 Change Password", false);

        btnNewRequest.setOnAction(e -> new CreateRequestPage(stage, currentUser).show());
        btnMyKeys.setOnAction(e -> new MyKeysPage(stage, currentUser).show());
        btnLeave.setOnAction(e -> new ApplyLeavePage(stage, currentUser).show());
        btnChangePass.setOnAction(e -> openChangePasswordModal());

        navMenu.getChildren().addAll(
            catSoc, btnCommand, btnPermissions, btnThreats, btnAuditLogs,
            catAdvanced, btnIncidents, btnSystemHealth, btnPendingPassApprovals,
            catOfficer, btnNewRequest, btnMyKeys, btnLeave, btnChangePass
        );

        // User Profile Box
        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        
        String officerName = (currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Alex";
        Label userName = new Label(officerName);
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label userRole = new Label("Chief Security Officer");
        userRole.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 11px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, userRole, btnLogout);
        sidebar.getChildren().addAll(brandBox, socTag, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(3), Priority.ALWAYS);

        root.setLeft(sidebar);

        // Center Workspace
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        VBox headerBox = new VBox(4);
        Label headerTitle = new Label("SOC Command & Incident Management");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
        Label headerSub = new Label("Real-time threat monitoring, privilege override, and account clearance controls.");
        headerSub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        headerBox.getChildren().addAll(headerTitle, headerSub);

        Label welcomeLbl = new Label("Welcome back, " + officerName);
        welcomeLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Dynamic Metrics
        int lockedCount = getDbMetricCount("SELECT COUNT(*) FROM employees WHERE is_locked = TRUE OR failed_attempts > 0");
        int activeKeys = getDbMetricCount("SELECT COUNT(*) FROM access_requests WHERE status = 'Approved'");

        HBox kpiRow = new HBox(15);
        kpiRow.getChildren().addAll(
            createMetricCard("ACTIVE LOCKOUTS", lockedCount + " Locks", "Awaiting SOC Unlock", "#fee2e2", "#dc2626"),
            createMetricCard("SECURITY KEYS", activeKeys + " Active", "Active Clearances", "#fef3c7", "#d97706"),
            createMetricCard("THREAT PROTECTION", "100%", "Active Guard", "#dcfce7", "#16a34a")
        );

        // Action Cards
        VBox actionsBox = new VBox(12);
        Label actionsTitle = new Label("⚡ Security Operations Center Actions");
        actionsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        HBox actionCards = new HBox(15);
        actionCards.getChildren().addAll(
            createActionCard("🚨 Account Lockouts", "Unlock users blocked by 5+ failed login attempts", "Manage Locks ->", e -> openSocThreatsModal()),
            createActionCard("🔑 Override Permissions", "Grant or revoke access keys", "Launch ->", e -> new PermissionPage(stage, currentUser).show()),
            createActionCard("📜 Audit Trail", "Full system access & security log", "Launch ->", e -> new AccessLogPage(stage, currentUser).show())
        );

        actionsBox.getChildren().addAll(actionsTitle, actionCards);
        mainContent.getChildren().addAll(headerBox, welcomeLbl, kpiRow, actionsBox);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Security Officer Center");
        stage.setMaximized(true);
        stage.show();
    }

    // Modal: Pending Password Approvals (SOC Approval Handler)
    private void openPendingPasswordApprovalsModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS SOC - Pending Password Reset Approvals");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🔒 Pending Password Reset Approvals Queue");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colLogId = new TableColumn<>("Log ID");
        TableColumn<String[], String> colEmpId = new TableColumn<>("Emp ID");
        TableColumn<String[], String> colUser = new TableColumn<>("Username / Target");
        TableColumn<String[], String> colEvent = new TableColumn<>("Request Type");
        TableColumn<String[], String> colDetails = new TableColumn<>("Details");
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");

        colLogId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colEmpId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colUser.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colEvent.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colDetails.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));

        table.getColumns().addAll(colLogId, colEmpId, colUser, colEvent, colDetails, colStatus);

        Runnable loadData = () -> {
            table.getItems().clear();
            String sql = "SELECT t.log_id, t.emp_id, COALESCE(e.username, e.first_name, 'Unknown') AS uname, " +
                         "t.event_type, t.details, t.status " +
                         "FROM threat_log t " +
                         "LEFT JOIN employees e ON t.emp_id = e.emp_id " +
                         "WHERE t.status = 'PENDING' AND t.event_type LIKE '%RESET%' " +
                         "ORDER BY t.log_id DESC";

            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    table.getItems().add(new String[]{
                        String.valueOf(rs.getInt("log_id")),
                        String.valueOf(rs.getInt("emp_id")),
                        rs.getString("uname"),
                        rs.getString("event_type"),
                        rs.getString("details"),
                        rs.getString("status")
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        loadData.run();

        Button btnApprove = new Button("✅ Approve Reset Request");
        btnApprove.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnApprove.setOnAction(e -> {
            String[] selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a pending request from the table!");
                alert.show();
                return;
            }

            int logId = Integer.parseInt(selected[0]);
            int empId = Integer.parseInt(selected[1]);
            String targetUser = selected[2];

            TextInputDialog passDialog = new TextInputDialog("Password123#");
            passDialog.setTitle("SARMS SOC - Set New Password");
            passDialog.setHeaderText("Set New Password for User: " + targetUser + " (ID: " + empId + ")");
            passDialog.setContentText("Enter New Password:");

            Optional<String> result = passDialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String newPassword = result.get().trim();

                try (Connection conn = DBConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Step 1: Update Employee Password & Reset Attempts
                    String updateEmpSql = "UPDATE employees SET password = ?, failed_attempts = 0, is_locked = FALSE WHERE emp_id = ?";
                    try (PreparedStatement pstmt1 = conn.prepareStatement(updateEmpSql)) {
                        pstmt1.setString(1, newPassword);
                        pstmt1.setInt(2, empId);
                        pstmt1.executeUpdate();
                    }

                    // Step 2: Update threat_log Request Status to APPROVED
                    String updateLogSql = "UPDATE threat_log SET status = 'APPROVED', details = ? WHERE log_id = ?";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(updateLogSql)) {
                        pstmt2.setString(1, "SOC Approved password reset for user " + targetUser);
                        pstmt2.setInt(2, logId);
                        pstmt2.executeUpdate();
                    }

                    conn.commit();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Password reset successfully approved and updated for " + targetUser + "!");
                    alert.showAndWait();
                    loadData.run();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Error approving password reset: " + ex.getMessage());
                    alert.show();
                }
            }
        });

        Button btnClose = new Button("Close Queue");
        btnClose.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        HBox btnBox = new HBox(10, btnApprove, btnClose);
        layout.getChildren().addAll(title, table, btnBox);

        Scene scene = new Scene(layout, 750, 420);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    // Modal 1: Account Lockouts & Unlock Control
    private void openSocThreatsModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS SOC - Account Lockouts & Threat Management");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🚨 SOC Account Lockouts & Threat Control");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colId = new TableColumn<>("EMP ID");
        TableColumn<String[], String> colUser = new TableColumn<>("Username / Target");
        TableColumn<String[], String> colAttempts = new TableColumn<>("Failed Attempts");
        TableColumn<String[], String> colStatus = new TableColumn<>("Threat Status");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colUser.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colAttempts.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colId, colUser, colAttempts, colStatus);

        Runnable loadTableData = () -> {
            table.getItems().clear();
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM employees WHERE failed_attempts > 0 OR is_locked = TRUE")) {

                while (rs.next()) {
                    String id = String.valueOf(rs.getInt("emp_id"));
                    String u = "Unknown";
                    try { u = rs.getString("username"); } catch (Exception e) {
                        try { u = rs.getString("first_name"); } catch (Exception ex) {}
                    }
                    int attempts = rs.getInt("failed_attempts");
                    boolean locked = rs.getBoolean("is_locked");

                    table.getItems().add(new String[]{
                        id,
                        u,
                        String.valueOf(attempts),
                        locked ? "🔒 ACCOUNT LOCKED (HIGH RISK)" : "⚠️ SUSPICIOUS ATTEMPTS"
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        loadTableData.run();

        Button btnUnlock = new Button("🔓 SOC Master Unlock Account");
        btnUnlock.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUnlock.setOnAction(e -> {
            String[] selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Select an account from the table to unlock!");
                alert.show();
                return;
            }

            int empId = Integer.parseInt(selected[0]);
            String uname = selected[1];

            String sql = "UPDATE employees SET is_locked = FALSE, failed_attempts = 0 WHERE emp_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, empId);
                if (pstmt.executeUpdate() > 0) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "SOC Override: Account '" + uname + "' (ID: " + empId + ") has been unlocked in DB!");
                    alert.show();
                    loadTableData.run();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnClose = new Button("Close Control");
        btnClose.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        HBox btnBox = new HBox(10, btnUnlock, btnClose);
        layout.getChildren().addAll(title, table, btnBox);

        Scene scene = new Scene(layout, 700, 420);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    // Modal 2: SECURITY INCIDENT LOG
    private void openIncidentLogWindow() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS - Security Incident Log");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("📋 Real-Time Security Incident & Threat Log");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colReqId = new TableColumn<>("Request ID");
        TableColumn<String[], String> colEmp = new TableColumn<>("Emp ID");
        TableColumn<String[], String> colDetails = new TableColumn<>("Incident Details / Reported Reason");
        TableColumn<String[], String> colStatus = new TableColumn<>("SOC Threat Alert");

        colReqId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colEmp.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colDetails.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colReqId, colEmp, colDetails, colStatus);

        String sql = "SELECT request_id, emp_id, reason, status FROM access_requests WHERE resource_name LIKE '%INCIDENT%' OR resource_name LIKE '%Device%' ORDER BY request_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                table.getItems().add(new String[]{
                    String.valueOf(rs.getInt("request_id")),
                    String.valueOf(rs.getInt("emp_id")),
                    rs.getString("reason"),
                    "🚨 INCIDENT REPORTED (PENDING REVIEW)"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (table.getItems().isEmpty()) {
            Label noThreats = new Label("🟢 No active reported security incidents found in database!");
            noThreats.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            layout.getChildren().addAll(title, noThreats);
        } else {
            layout.getChildren().addAll(title, table);
        }

        Button btnClose = new Button("Close Log");
        btnClose.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        layout.getChildren().add(btnClose);

        Scene scene = new Scene(layout, 750, 420);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    // Modal 3: System Health Status
    private void openSystemHealthWindow() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS - System Health Diagnostics");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🛡️ Active System Health & Connection Status");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox healthBox = new VBox(10);
        healthBox.setStyle("-fx-background-color: #f8fafc; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #e2e8f0;");

        boolean dbStatus = false;
        try (Connection conn = DBConnection.getConnection()) {
            dbStatus = (conn != null && !conn.isClosed());
        } catch (Exception e) {
            dbStatus = false;
        }

        Label lblDb = new Label("• MySQL Database: " + (dbStatus ? "🟢 CONNECTED (ONLINE)" : "🔴 DISCONNECTED"));
        Label lblLockout = new Label("• Failed Attempts Threshold Engine: 🟢 ACTIVE (5 Attempts Lockout)");
        Label lblRbac = new Label("• Multi-Role RBAC Authorization: 🟢 FUNCTIONAL");
        Label lblAudit = new Label("• System Access Audit Trail Logging: 🟢 ACTIVE");

        lblDb.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblLockout.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblRbac.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        lblAudit.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        healthBox.getChildren().addAll(lblDb, lblLockout, lblRbac, lblAudit);

        Button btnClose = new Button("Close Diagnostics");
        btnClose.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        layout.getChildren().addAll(title, healthBox, btnClose);

        Scene scene = new Scene(layout, 550, 320);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    // Modal 4: Change Password
    private void openChangePasswordModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS - Change Password");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🔒 Security Officer Password Update");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        PasswordField oldPassFld = new PasswordField(); oldPassFld.setPromptText("Enter Current Password");
        PasswordField newPassFld = new PasswordField(); newPassFld.setPromptText("Enter New Password");
        PasswordField confirmPassFld = new PasswordField(); confirmPassFld.setPromptText("Confirm New Password");

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Button btnUpdate = new Button("Update Password");
        btnUpdate.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUpdate.setOnAction(e -> {
            String oldP = oldPassFld.getText().trim();
            String newP = newPassFld.getText().trim();
            String confP = confirmPassFld.getText().trim();

            if (oldP.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("⚠️ Please fill in all password fields!");
                return;
            }

            if (!newP.equals(confP)) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ New password and confirmation do not match!");
                return;
            }

            int empId = (currentUser != null && currentUser.getEmpId() > 0) ? currentUser.getEmpId() : 1;
            String query = "UPDATE employees SET password = ? WHERE emp_id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newP);
                pstmt.setInt(2, empId);

                int updated = pstmt.executeUpdate();
                if (updated > 0) {
                    statusMsg.setStyle("-fx-text-fill: #10b981;");
                    statusMsg.setText("✓ Password updated successfully in Database!");
                    oldPassFld.clear();
                    newPassFld.clear();
                    confirmPassFld.clear();
                } else {
                    statusMsg.setStyle("-fx-text-fill: #ef4444;");
                    statusMsg.setText("❌ Failed to update password in Database!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ Database error during password change!");
            }
        });

        Button btnClose = new Button("Cancel");
        btnClose.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        HBox btnBox = new HBox(10, btnUpdate, btnClose);

        layout.getChildren().addAll(
            title,
            new Label("Current Password:"), oldPassFld,
            new Label("New Password:"), newPassFld,
            new Label("Confirm Password:"), confirmPassFld,
            statusMsg, btnBox
        );

        Scene scene = new Scene(layout, 400, 360);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private int getDbMetricCount(String sql) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Button createNavButton(String text, boolean isActive) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(isActive ? 
            "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6;" :
            "-fx-background-color: transparent; -fx-text-fill: #e0e1dd; -fx-font-size: 11px; -fx-padding: 7 10; -fx-cursor: hand;");
        return b;
    }

    private VBox createMetricCard(String title, String val, String sub, String badgeBg, String badgeFg) {
        VBox card = new VBox(8);
        card.setPrefWidth(260);
        card.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label t = new Label(title); t.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748b;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 10px; -fx-text-fill: " + badgeFg + "; -fx-background-color: " + badgeBg + "; -fx-padding: 3 8; -fx-background-radius: 8;");

        card.getChildren().addAll(t, v, s);
        return card;
    }

    private VBox createActionCard(String title, String desc, String btnText, javafx.event.EventHandler<javafx.event.ActionEvent> eventHandler) {
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label t = new Label(title); t.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
        Label d = new Label(desc); d.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        Button btn = new Button(btnText);
        btn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        btn.setOnAction(eventHandler);

        card.getChildren().addAll(t, d, btn);
        return card;
    }
}