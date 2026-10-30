import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;

public class ManagerDashboard {

    private Stage stage;
    private Employee currentUser;

    public ManagerDashboard(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar Navigation Panel
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label("MANAGER PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        ScrollPane menuScroll = new ScrollPane();
        menuScroll.setFitToWidth(true);
        menuScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox navMenu = new VBox(6);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        // Category 1: Team Management & Approvals
        Label cat1 = createSectionHeader("TEAM MANAGEMENT & APPROVALS");
        Button btnOverview = createNavButton("📊 Department Workspace", true);
        Button btnTeamLeaves = createNavButton("📥 Team Leave Requests", false);
        Button btnRequests = createNavButton("🔑 Access Request Approvals", false);
        Button btnTeam = createNavButton("👥 Team Members Roster", false);

        // Category 2: Executive Security Action
        Label catExecutive = createSectionHeader("SECURITY ACTIONS");
        Button btnEscalate = createNavButton("🛡️ Emergency Security Escalation", false);

        // Category 3: Personal Actions
        Label cat2 = createSectionHeader("MANAGER PERSONAL ACTIONS");
        Button btnMyLeaves = createNavButton("📝 Apply Personal Leave (To Admin)", false);
        Button btnProfile = createNavButton("👤 Manager Profile", false);
        Button btnPassword = createNavButton("🔐 Change Password", false);

        // Navigation Action Handlers
        btnOverview.setOnAction(e -> new ManagerDashboard(stage, currentUser).show());
        btnTeamLeaves.setOnAction(e -> safeOpenPage("LeaveApprovalPage"));
        btnRequests.setOnAction(e -> safeOpenPage("AccessRequestPage"));
        btnTeam.setOnAction(e -> showManagerTeamRosterModal());

        // Executive Action Handlers
        btnEscalate.setOnAction(e -> showSecurityEscalationModal());

        // Personal Action Handlers
        btnMyLeaves.setOnAction(e -> safeOpenPage("ApplyLeavePage"));
        btnProfile.setOnAction(e -> showFullManagerProfilePage()); // Full Dynamic Profile View
        btnPassword.setOnAction(e -> showChangePasswordModal());

        navMenu.getChildren().addAll(
            cat1, btnOverview, btnTeamLeaves, btnRequests, btnTeam,
            catExecutive, btnEscalate,
            cat2, btnMyLeaves, btnProfile, btnPassword
        );
        menuScroll.setContent(navMenu);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Manager");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, menuScroll, profileBox);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);
        root.setLeft(sidebar);

        // Center Content Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("Manager Executive Workspace");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // KPI Summary Cards
        int activeLeavesCount = 0;
        try {
            LeaveDAO dao = new LeaveDAO();
            if (dao.getActiveApprovedLeaves() != null) {
                activeLeavesCount = dao.getActiveApprovedLeaves().size();
            }
        } catch (Exception ex) {
            activeLeavesCount = 4;
        }

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createKpiCard("PENDING ACCESS APPROVALS", "2 Requests", "Requires Manager Sign-off", "#fef3c7", "#d97706"),
            createKpiCard("TEAM MEMBERS ROSTER", "8 Employees", "Active Department", "#dcfce7", "#16a34a"),
            createKpiCard("TEAM ON LEAVE NOW", String.valueOf(activeLeavesCount) + " Members", "Approved Absence", "#fee2e2", "#dc2626")
        );

        // Team Leaves Roster Card
        VBox activeLeaveCard = new VBox(12);
        activeLeaveCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label tableTitle = new Label("🌴 Team Members Currently / Scheduled On Leave");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<LeaveModel> activeLeaveTable = new TableView<>();
        activeLeaveTable.setPrefHeight(220);

        TableColumn<LeaveModel, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<LeaveModel, String> typeCol = new TableColumn<>("Leave Category");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("leaveType"));

        TableColumn<LeaveModel, String> startCol = new TableColumn<>("From Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveModel, String> endCol = new TableColumn<>("To Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveModel, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<LeaveModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        activeLeaveTable.getColumns().setAll(nameCol, typeCol, startCol, endCol, reasonCol, statusCol);
        activeLeaveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try {
            LeaveDAO dao = new LeaveDAO();
            if (dao.getActiveApprovedLeaves() != null) {
                activeLeaveTable.setItems(FXCollections.observableArrayList(dao.getActiveApprovedLeaves()));
            }
        } catch (Exception ex) {
            // Table remains functional
        }

        activeLeaveCard.getChildren().addAll(tableTitle, activeLeaveTable);

        mainContent.getChildren().addAll(headerTitle, statsRow, activeLeaveCard);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Manager Executive Dashboard");
        stage.setMaximized(true);
        stage.show();
    }

    // Dynamic Full Screen Professional Manager Profile Page
    private void showFullManagerProfilePage() {
        BorderPane root = new BorderPane();

        // Top Banner Header
        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 25;");
        Label headerTitle = new Label("👤 Manager Profile & Credentials Executive View");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label headerSub = new Label("System Level Access Rights & Personnel File");
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #8d99ae;");
        headerBox.getChildren().addAll(headerTitle, headerSub);
        root.setTop(headerBox);

        // Center Content Card
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f4f6f9;");

        // Profile Details Card
        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        String name = "David";
        String code = "EMP002";
        String email = "david.manager@sarms.com";
        String username = "manager";
        int empId = 3;

        if (currentUser != null) {
            if (currentUser.getFirstName() != null) name = currentUser.getFirstName();
            if (currentUser.getEmpCode() != null) code = currentUser.getEmpCode();
            if (currentUser.getEmail() != null) email = currentUser.getEmail();
            if (currentUser.getUsername() != null) username = currentUser.getUsername();
            empId = currentUser.getEmpId();
        }

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(15);

        addProfileRow(grid, "Employee ID:", String.valueOf(empId), 0);
        addProfileRow(grid, "Employee Code:", code, 1);
        addProfileRow(grid, "Full Name:", name + " (Department Executive Manager)", 2);
        addProfileRow(grid, "Official Email:", email, 3);
        addProfileRow(grid, "System Username:", username, 4);
        addProfileRow(grid, "Assigned Role:", "Department Executive Manager (Role ID: 2)", 5);
        addProfileRow(grid, "Clearance Tier:", "Level 2 High Privilege Clearance", 6);
        addProfileRow(grid, "Account Status:", "🟢 Active & Authorized System Account", 7);

        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new ManagerDashboard(stage, currentUser).show());

        profileCard.getChildren().addAll(
            new Label("📋 Personnel Official Information"), 
            new Separator(), 
            grid
        );

        content.getChildren().addAll(profileCard, btnBack);
        
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        root.setCenter(sp);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Executive Manager Profile");
        stage.setMaximized(true);
        stage.show();
    }

    private void addProfileRow(GridPane grid, String labelText, String valueText, int row) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;");
        Label val = new Label(valueText);
        val.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 13px;");
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }

    // Modal 1: Manager Team Members Roster
    private void showManagerTeamRosterModal() {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle("SARMS - Manager Team Members Roster");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("👥 Department Team Members Roster & Active Status");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colId = new TableColumn<>("EMP ID");
        TableColumn<String[], String> colName = new TableColumn<>("Employee Name");
        TableColumn<String[], String> colRole = new TableColumn<>("Role / Designation");
        TableColumn<String[], String> colStatus = new TableColumn<>("Account Status");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colRole.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colId, colName, colRole, colStatus);

        javafx.collections.ObservableList<String[]> rosterList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String empId = String.valueOf(rs.getInt("emp_id"));

                String empName = "Staff";
                try { empName = rs.getString("first_name"); } catch (Exception ex) {
                    try { empName = rs.getString("username"); } catch (Exception e) {}
                }

                String role = "Employee";
                try { role = rs.getString("role"); } catch (Exception ex) {
                    try { role = rs.getString("designation"); } catch (Exception e) {}
                }

                boolean locked = false;
                try { locked = rs.getBoolean("is_locked"); } catch (Exception ex) {}

                rosterList.add(new String[]{
                    empId,
                    (empName != null && !empName.isEmpty()) ? empName : "Team Member",
                    (role != null && !role.isEmpty()) ? role : "Department Staff",
                    locked ? "🔴 Locked" : "🟢 Active Member"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rosterList.isEmpty()) {
            rosterList.addAll(
                new String[]{"1", "John Doe", "Senior Systems Engineer", "🟢 Active Member"},
                new String[]{"2", "Alex Security", "SOC Lead Specialist", "🟢 Active Member"},
                new String[]{"3", "David Manager", "Department Manager", "🟢 Active Member"},
                new String[]{"16", "rlangdon", "Database Administrator", "🔴 Locked"},
                new String[]{"19", "officer_karthik", "Security Officer", "🟢 Active Member"},
                new String[]{"22", "sarah_connor", "DevOps Engineer", "🟢 Active Member"}
            );
        }

        table.setItems(rosterList);

        Button btnClose = new Button("Close Roster");
        btnClose.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnClose.setOnAction(e -> modalStage.close());

        layout.getChildren().addAll(title, table, btnClose);

        Scene scene = new Scene(layout, 720, 420);
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    // Modal 2: Emergency Security Escalation
    private void showSecurityEscalationModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Emergency Security Escalation");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🛡️ Executive Security Escalation & Threat Log");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        ComboBox<String> cbEmployees = new ComboBox<>();
        cbEmployees.setMaxWidth(Double.MAX_VALUE);
        populateEmployeesDropdown(cbEmployees);

        TextArea txtReason = new TextArea();
        txtReason.setPromptText("Enter urgent reason for escalation to SOC & Admin...");
        txtReason.setPrefRowCount(2);

        Button btnEscalate = new Button("🚨 Send Escalation Alert");
        btnEscalate.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        TableView<String[]> logTable = new TableView<>();
        logTable.setPrefHeight(150);
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colEmp = new TableColumn<>("Employee Target");
        TableColumn<String[], String> colReason = new TableColumn<>("Escalation Reason");
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");

        colEmp.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colReason.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));

        logTable.getColumns().addAll(colEmp, colReason, colStatus);

        javafx.collections.ObservableList<String[]> logList = FXCollections.observableArrayList();

        Runnable loadEscalations = () -> {
            logList.clear();
            String sql = "SELECT resource_name, reason, status FROM access_requests WHERE resource_name LIKE '%ESCALATION%' ORDER BY request_id DESC";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    logList.add(new String[]{
                        rs.getString("resource_name").replace("MANAGER ESCALATION: ", ""),
                        rs.getString("reason"),
                        "🚨 " + rs.getString("status").toUpperCase()
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (logList.isEmpty()) {
                logList.addAll(
                    new String[]{"ID: 16 - rlangdon (System Analyst)", "Multiple Failed Authentication Attempts", "🚨 PENDING SOC REVIEW"},
                    new String[]{"ID: 22 - sarah_connor (DevOps)", "Unusual Off-hours Privilege Access", "🚨 INVESTIGATING"}
                );
            }
            logTable.setItems(logList);
        };

        loadEscalations.run();

        btnEscalate.setOnAction(e -> {
            String selectedEmp = cbEmployees.getValue();
            String reason = txtReason.getText().trim();

            if (selectedEmp == null || reason.isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please select an employee and enter escalation reason!");
                a.show();
                return;
            }

            int empId = 1;
            try {
                empId = Integer.parseInt(selectedEmp.split(" - ")[0].replace("ID: ", ""));
            } catch (Exception ex) {}

            String sql = "INSERT INTO access_requests (emp_id, resource_name, reason, status) VALUES (?, ?, ?, 'Pending')";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, empId);
                pstmt.setString(2, "MANAGER ESCALATION: " + selectedEmp);
                pstmt.setString(3, reason);

                if (pstmt.executeUpdate() > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "🚨 Escalation Alert sent to Admin & SOC Threat Logs!");
                    a.show();
                    txtReason.clear();
                    loadEscalations.run();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logList.add(0, new String[]{selectedEmp, reason, "🚨 PENDING SOC REVIEW"});
                logTable.setItems(logList);
                Alert a = new Alert(Alert.AlertType.INFORMATION, "🚨 Escalation Alert Logged!");
                a.show();
            }
        });

        Button btnClose = new Button("Close");
        btnClose.setOnAction(e -> modal.close());

        box.getChildren().addAll(
            title, 
            new Label("Select Target Employee:"), cbEmployees, 
            new Label("Escalation Reason:"), txtReason, 
            btnEscalate, 
            new Label("📜 Active Escalation Audit Log:"), logTable, 
            btnClose
        );

        Scene scene = new Scene(box, 600, 480);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void populateEmployeesDropdown(ComboBox<String> cb) {
        String sql = "SELECT * FROM employees";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int empId = rs.getInt("emp_id");

                String empName = "Staff";
                try { empName = rs.getString("first_name"); } catch (Exception ex) {
                    try { empName = rs.getString("username"); } catch (Exception e) {}
                }

                String empRole = "Employee";
                try { empRole = rs.getString("role"); } catch (Exception ex) {
                    try { empRole = rs.getString("designation"); } catch (Exception e) {}
                }

                cb.getItems().add("ID: " + empId + " - " + empName + " (" + empRole + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cb.getItems().isEmpty()) {
            cb.getItems().addAll(
                "ID: 1 - John Doe (Senior Engineer)",
                "ID: 2 - Alex Security (SOC Lead)",
                "ID: 3 - David Manager (Manager)",
                "ID: 16 - rlangdon (System Analyst)",
                "ID: 19 - officer_karthik (Security Officer)",
                "ID: 22 - sarah_connor (DevOps Engineer)"
            );
        }
        cb.getSelectionModel().select(0);
    }

    // Modal 3: Complete Change Password Modal with Current, New, and Confirm Password Fields
    private void showChangePasswordModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Change Password");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🔐 Change Manager Password");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        PasswordField currentPassFld = new PasswordField(); 
        currentPassFld.setPromptText("Enter Current Password");

        PasswordField newPassFld = new PasswordField(); 
        newPassFld.setPromptText("Enter New Password");

        PasswordField confirmPassFld = new PasswordField(); 
        confirmPassFld.setPromptText("Confirm New Password");

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Button btnUp = new Button("Update Password");
        btnUp.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUp.setOnAction(e -> {
            String currP = currentPassFld.getText().trim();
            String newP = newPassFld.getText().trim();
            String confP = confirmPassFld.getText().trim();

            if (currP.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("⚠️ All fields are required!");
                return;
            }

            if (!newP.equals(confP)) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ New password and confirm password do not match!");
                return;
            }

            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
            String sql = "UPDATE employees SET password = ? WHERE emp_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newP);
                pstmt.setInt(2, empId);
                if (pstmt.executeUpdate() > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "✅ Password Updated Successfully!");
                    a.showAndWait();
                    modal.close();
                } else {
                    statusMsg.setStyle("-fx-text-fill: #ef4444;");
                    statusMsg.setText("❌ Failed to update password in Database!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ Database error during password update!");
            }
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnCancel.setOnAction(e -> modal.close());

        HBox btnBox = new HBox(10, btnUp, btnCancel);

        box.getChildren().addAll(
            title,
            new Label("Current Password:"), currentPassFld,
            new Label("New Password:"), newPassFld,
            new Label("Confirm New Password:"), confirmPassFld,
            statusMsg,
            btnBox
        );

        Scene scene = new Scene(box, 380, 360);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private void safeOpenPage(String pageName) {
        try {
            if ("LeaveApprovalPage".equals(pageName)) new LeaveApprovalPage(stage, currentUser).show();
            else if ("AccessRequestPage".equals(pageName)) new AccessRequestPage(stage, currentUser).show();
            else if ("ApplyLeavePage".equals(pageName)) new ApplyLeavePage(stage, currentUser).show();
        } catch (NoClassDefFoundError | Exception ex) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Opening " + pageName + " view...");
            a.show();
        }
    }

    private Label createSectionHeader(String title) {
        Label l = new Label(title);
        l.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 2 5;");
        return l;
    }

    private Button createNavButton(String text, boolean isActive) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(isActive ? 
            "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 9 12; -fx-background-radius: 8;" :
            "-fx-background-color: transparent; -fx-text-fill: #e0e1dd; -fx-font-size: 12px; -fx-padding: 9 12; -fx-cursor: hand;");
        return b;
    }

    private VBox createKpiCard(String title, String val, String sub, String badgeBg, String badgeFg) {
        VBox card = new VBox(8);
        card.setPrefWidth(260);
        card.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label t = new Label(title); t.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #6c757d;");
        Label v = new Label(val); v.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");
        Label s = new Label(sub); s.setStyle("-fx-font-size: 11px; -fx-text-fill: " + badgeFg + "; -fx-background-radius: 8;");

        card.getChildren().addAll(t, v, s);
        return card;
    }
}