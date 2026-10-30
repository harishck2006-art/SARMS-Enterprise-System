import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;
import java.util.Optional;

public class AdminDashboard {

    private Stage stage;
    private Employee currentUser;

    public AdminDashboard(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // 1. LEFT SIDEBAR (SCROLLABLE MENU + FIXED LOGOUT)
        VBox sidebar = new VBox(10);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 15;");

        // Brand Header
        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label brandLogo = new Label("S");
        brandLogo.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 6 12; -fx-background-radius: 8;");
        
        VBox brandTitleBox = new VBox(2);
        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label brandSub = new Label("ADMIN CONTROL CENTER");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        brandTitleBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(brandLogo, brandTitleBox);

        // Scrollable Navigation Menu Box
        ScrollPane menuScroll = new ScrollPane();
        menuScroll.setFitToWidth(true);
        menuScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox navMenu = new VBox(4);
        navMenu.setPadding(new Insets(5, 0, 5, 0));

        Button btnOverview = createNavButton("📊 Command Overview", true);
        Button btnEmployees = createNavButton("👥 Manage Employees", false);
        Button btnResources = createNavButton("📦 Manage Resources", false);
        Button btnRequests = createNavButton("📜 All Access Requests", false);
        Button btnLeaves = createNavButton("📅 Leave Approvals", false);
        Button btnRoles = createNavButton("🔐 Role Management", false);
        Button btnAlerts = createNavButton("🚨 Security Alerts", false);
        Button btnSocUnlock = createNavButton("🔓 SOC Unlock Override", false);
        // ADDED: Pending Password Approvals Menu Button
        Button btnPendingPassApprovals = createNavButton("🔑 Pending Password Approvals", false);
        Button btnLogs = createNavButton("📊 System Access Logs", false);
        Button btnPolicies = createNavButton("⚙ Security Policies", false);
        Button btnAnalytics = createNavButton("📈 Analytics & Reports", false);
        Button btnChangePass = createNavButton("🔐 Change Password", false);

        // Button Actions
        btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
        btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
        btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());
        btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
        btnLeaves.setOnAction(e -> new AdminLeaveApprovalPage(stage, currentUser).show());
        btnRoles.setOnAction(e -> new RoleManagementPage(stage, currentUser).show());
        btnAlerts.setOnAction(e -> new SecurityAlertsPage(stage, currentUser).show());
        btnSocUnlock.setOnAction(e -> new AdminSecurityUnlockPage(stage, currentUser).show());
        btnPendingPassApprovals.setOnAction(e -> openAdminPendingPasswordModal());
        btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());
        btnPolicies.setOnAction(e -> new SecurityPoliciesPage(stage, currentUser).show());
        btnAnalytics.setOnAction(e -> new AnalyticsPage(stage, currentUser).show());
        btnChangePass.setOnAction(e -> showAdminChangePasswordModal());

        navMenu.getChildren().addAll(
            btnOverview, btnEmployees, btnResources, btnRequests, 
            btnLeaves, btnRoles, btnAlerts, btnSocUnlock, btnPendingPassApprovals,
            btnLogs, btnPolicies, btnAnalytics, btnChangePass
        );
        menuScroll.setContent(navMenu);

        // Footer Profile Section (Always Visible at Bottom)
        VBox profileBox = new VBox(4);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() + " (Admin)" : "System Administrator");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: #e63946; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 6 12; -fx-background-radius: 6; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        
        sidebar.getChildren().addAll(brandBox, menuScroll, profileBox);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);

        root.setLeft(sidebar);

        // 2. MAIN WORKSPACE (ADMIN OVERVIEW DASHBOARD)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("System Overview & Quick Control");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Dynamic KPI Counts from MySQL Database
        int totalUsers = getDbMetricCount("SELECT COUNT(*) FROM employees");
        int totalResources = getDbMetricCount("SELECT COUNT(*) FROM resources");
        int pendingRequests = getDbMetricCount("SELECT COUNT(*) FROM access_requests WHERE status = 'Pending'");
        int activeAlerts = getDbMetricCount("SELECT COUNT(*) FROM employees WHERE is_locked = TRUE OR failed_attempts > 0");

        // KPI Metric Cards Row
        HBox statsRow = new HBox(18);
        statsRow.getChildren().addAll(
            createKpiCard("TOTAL USERS", String.valueOf(totalUsers), "Registered accounts", "#e0f2fe", "#0284c7"),
            createKpiCard("ACTIVE RESOURCES", String.valueOf(totalResources), "Secured assets", "#dcfce7", "#16a34a"),
            createKpiCard("PENDING REQUESTS", String.valueOf(pendingRequests), "Requires approval", "#fef3c7", "#d97706"),
            createKpiCard("SECURITY ALERTS", String.valueOf(activeAlerts), "Active threat monitor", "#fee2e2", "#dc2626")
        );

        // Quick Management Shortcuts
        VBox shortcutCard = new VBox(15);
        shortcutCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label cardTitle = new Label("Quick Administrative Actions");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        HBox btnGroup = new HBox(12);

        Button btnManageUsers = new Button("👥 Manage Users / Roles");
        btnManageUsers.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnManageUsers.setOnAction(e -> new EmployeePage(stage, currentUser).show());

        Button btnAddResource = new Button("📦 Add System Resource");
        btnAddResource.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAddResource.setOnAction(e -> new AddResourcePage(stage, currentUser).show());

        Button btnUnlockSoc = new Button("🔓 Unlock SOC Account");
        btnUnlockSoc.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnUnlockSoc.setOnAction(e -> new AdminSecurityUnlockPage(stage, currentUser).show());

        // ADDED: Quick Action Button for Password Approvals
        Button btnPassApprovals = new Button("🔑 Password Reset Approvals");
        btnPassApprovals.setStyle("-fx-background-color: #d97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnPassApprovals.setOnAction(e -> openAdminPendingPasswordModal());

        Button btnQuickPass = new Button("🔐 Change Password");
        btnQuickPass.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnQuickPass.setOnAction(e -> showAdminChangePasswordModal());

        btnGroup.getChildren().addAll(btnManageUsers, btnAddResource, btnUnlockSoc, btnPassApprovals, btnQuickPass);
        shortcutCard.getChildren().addAll(cardTitle, btnGroup);

        mainContent.getChildren().addAll(headerTitle, statsRow, shortcutCard);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Admin Dashboard");
        stage.setMaximized(true);
        stage.show();
    }

    // Modal: Admin Pending Password Approvals Queue
    private void openAdminPendingPasswordModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS Admin - Pending User Password Approvals");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🔑 Pending User Password Reset Requests (Admin Queue)");
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
                         "WHERE t.status = 'PENDING' AND (t.event_type = 'USER_RESET_REQUEST' OR t.event_type LIKE '%USER%') " +
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

        Button btnApprove = new Button("✅ Approve Reset & Set Password");
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

            TextInputDialog passDialog = new TextInputDialog("UserPass123#");
            passDialog.setTitle("SARMS Admin - Reset Password");
            passDialog.setHeaderText("Set New Password for User: " + targetUser + " (ID: " + empId + ")");
            passDialog.setContentText("Enter New Password:");

            Optional<String> result = passDialog.showAndWait();
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String newPassword = result.get().trim();

                try (Connection conn = DBConnection.getConnection()) {
                    conn.setAutoCommit(false);

                    // Step 1: Update Employee Password & Reset Lock
                    String updateEmpSql = "UPDATE employees SET password = ?, failed_attempts = 0, is_locked = FALSE WHERE emp_id = ?";
                    try (PreparedStatement pstmt1 = conn.prepareStatement(updateEmpSql)) {
                        pstmt1.setString(1, newPassword);
                        pstmt1.setInt(2, empId);
                        pstmt1.executeUpdate();
                    }

                    // Step 2: Mark Request APPROVED in threat_log
                    String updateLogSql = "UPDATE threat_log SET status = 'APPROVED', details = ? WHERE log_id = ?";
                    try (PreparedStatement pstmt2 = conn.prepareStatement(updateLogSql)) {
                        pstmt2.setString(1, "Admin Approved password reset for " + targetUser);
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

    private int getDbMetricCount(String query) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void showAdminChangePasswordModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS - Admin Change Password");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🔐 System Admin Password Update");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        PasswordField oldPassFld = new PasswordField();
        oldPassFld.setPromptText("Enter Current Password");

        PasswordField newPassFld = new PasswordField();
        newPassFld.setPromptText("Enter New Password");

        PasswordField confirmPassFld = new PasswordField();
        confirmPassFld.setPromptText("Confirm New Password");

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Button btnUpdate = new Button("Update Password");
        btnUpdate.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUpdate.setOnAction(e -> {
            String oldP = oldPassFld.getText().trim();
            String newP = newPassFld.getText().trim();
            String confP = confirmPassFld.getText().trim();

            StringBuilder errors = new StringBuilder();

            if (oldP.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
                errors.append("• All password fields are required!\n");
            }

            if (!newP.isEmpty() && !confP.isEmpty() && !newP.equals(confP)) {
                errors.append("• New Password and Confirm Password do not match!\n");
            }

            if (!newP.isEmpty() && newP.length() < 6) {
                errors.append("• New password must be at least 6 characters long!\n");
            }

            if (errors.length() > 0) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ Validation Failed!");
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Password Validation Error");
                alert.setHeaderText("Password Update Blocked!");
                alert.setContentText(errors.toString());
                alert.showAndWait();
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
                    statusMsg.setText("✓ Admin password updated successfully in Database!");
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

        Scene scene = new Scene(layout, 400, 380);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private Button createNavButton(String text, boolean isActive) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(isActive ? 
            "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 7 10; -fx-background-radius: 6;" :
            "-fx-background-color: transparent; -fx-text-fill: #e0e1dd; -fx-font-size: 12px; -fx-padding: 7 10; -fx-cursor: hand;");
        return b;
    }

    private VBox createKpiCard(String title, String val, String sub, String badgeBg, String badgeFg) {
        VBox card = new VBox(8);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label t = new Label(title);
        t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6c757d;");
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
        Label s = new Label(sub);
        s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + badgeFg + "; -fx-background-color: " + badgeBg + "; -fx-padding: 3 8; -fx-background-radius: 8;");

        card.getChildren().addAll(t, v, s);
        return card;
    }
}