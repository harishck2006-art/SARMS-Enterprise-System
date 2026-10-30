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

public class EmployeeDashboard {

    private Stage stage;
    private Employee currentUser;

    public EmployeeDashboard(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar Navigation
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label("EMPLOYEE PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        ScrollPane menuScroll = new ScrollPane();
        menuScroll.setFitToWidth(true);
        menuScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox navMenu = new VBox(6);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        // Category 1: My Self-Service
        Label cat1 = createSectionHeader("MY SELF-SERVICE");
        Button btnHome = createNavButton("🏠 Employee Home", true);
        Button btnNewReq = createNavButton("➕ New Access Request", false);
        Button btnMyReqs = createNavButton("📋 My Access Requests", false);
        Button btnKeys = createNavButton("🔑 My Keys & Active Clearances", false);
        Button btnLeave = createNavButton("📝 Apply Leave (To Manager)", false);
        Button btnLogs = createNavButton("📊 View My Logs & Sessions", false);

        // Category 2: Security Actions
        Label cat2 = createSectionHeader("SECURITY & HARDWARE ACTIONS");
        Button btnDevice = createNavButton("🛠️ Request Device Access", false);
        Button btnIncident = createNavButton("🚨 Report Security Incident", false);
        Button btnRevoke = createNavButton("🚨 Emergency Access Revoke", false);

        // Category 3: My Profile
        Label cat3 = createSectionHeader("MY PROFILE");
        Button btnProfile = createNavButton("👤 My Info & Profile", false);
        Button btnPassword = createNavButton("🔐 Change Password", false);

        // Action Handlers
        btnHome.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());
        btnNewReq.setOnAction(e -> showNewAccessRequestPage());
        btnMyReqs.setOnAction(e -> safeOpenPage("AccessRequestPage"));
        btnKeys.setOnAction(e -> showFullKeysAndClearancesPage());
        btnLeave.setOnAction(e -> safeOpenPage("ApplyLeavePage"));
        btnLogs.setOnAction(e -> showFullLogsAndSessionsPage());

        btnDevice.setOnAction(e -> showDeviceAccessModal());
        btnIncident.setOnAction(e -> showReportIncidentModal());
        btnRevoke.setOnAction(e -> showEmergencyRevokeModal());

        // Full Page View for Profile
        btnProfile.setOnAction(e -> showFullEmployeeProfilePage());
        btnPassword.setOnAction(e -> showChangePasswordModal());

        navMenu.getChildren().addAll(
            cat1, btnHome, btnNewReq, btnMyReqs, btnKeys, btnLeave, btnLogs,
            cat2, btnDevice, btnIncident, btnRevoke,
            cat3, btnProfile, btnPassword
        );
        menuScroll.setContent(navMenu);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Employee");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, menuScroll, profileBox);
        VBox.setVgrow(menuScroll, Priority.ALWAYS);
        root.setLeft(sidebar);

        // Center Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("Welcome back, " + userName.getText() + "!");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createKpiCard("MY ACTIVE PERMISSIONS", "2 Resources", "Granted", "#dcfce7", "#16a34a"),
            createKpiCard("MY PENDING REQUESTS", "0 Pending", "Up to Date", "#fef3c7", "#d97706"),
            createKpiCard("MY LEAVE STATUS", "Active Work", "No Active Leave", "#e0f2fe", "#0284c7")
        );

        VBox quickCard = new VBox(12);
        quickCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label quickTitle = new Label("⚡ Employee Quick Action Shortcuts");
        quickTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        Button btnActionNew = new Button("+ Create Access Request");
        btnActionNew.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnActionNew.setOnAction(e -> showNewAccessRequestPage());

        quickCard.getChildren().addAll(quickTitle, btnActionNew);

        mainContent.getChildren().addAll(headerTitle, statsRow, quickCard);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Employee Portal");
        stage.setMaximized(true);
        stage.show();
    }

    // Access Request Page
    private void showNewAccessRequestPage() {
        BorderPane root = new BorderPane();

        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");
        Label title = new Label("➕ Create New Security Access Request");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        headerBox.getChildren().add(title);
        root.setTop(headerBox);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: #f4f6f9;");

        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        ComboBox<String> cbResource = new ComboBox<>(FXCollections.observableArrayList(
            "Production Data Vault", "Financial Records DB", "Source Code Repository", "Admin Privilege Escalation"
        ));
        cbResource.getSelectionModel().select(0);

        TextArea txtPurpose = new TextArea();
        txtPurpose.setPromptText("Enter official business purpose / reason...");
        txtPurpose.setPrefRowCount(3);

        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnSubmit = new Button("Submit Clearance Request");
        btnSubmit.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            String purpose = txtPurpose.getText().trim();

            if (purpose.length() < 12 || !purpose.contains(" ")) {
                lblMsg.setText("❌ Validation Error: Please enter complete meaningful business reasons (Min 12 characters & words)!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
                return;
            }

            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
            String resource = cbResource.getValue();
            String sql = "INSERT INTO access_requests (emp_id, resource_name, reason, status) VALUES (?, ?, ?, 'Pending')";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, empId);
                pstmt.setString(2, resource);
                pstmt.setString(3, purpose);

                if (pstmt.executeUpdate() > 0) {
                    lblMsg.setText("✓ Access request for '" + resource + "' submitted successfully!");
                    lblMsg.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
                    txtPurpose.clear();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                lblMsg.setText("❌ Database Submission Error!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.add(new Label("Select Target Resource:"), 0, 0);
        grid.add(cbResource, 1, 0);
        grid.add(new Label("Business Purpose / Reason:"), 0, 1);
        grid.add(txtPurpose, 1, 1);

        formCard.getChildren().addAll(grid, lblMsg, btnSubmit);

        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        contentBox.getChildren().addAll(formCard, btnBack);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - New Access Request");
        stage.setMaximized(true);
        stage.show();
    }

    // Full Screen Professional Employee Profile View
    private void showFullEmployeeProfilePage() {
        BorderPane root = new BorderPane();

        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 25;");
        Label headerTitle = new Label("👤 Employee Personnel Profile & Credentials");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label headerSub = new Label("SARMS Security Personnel Record & Official Access Privileges");
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #8d99ae;");
        headerBox.getChildren().addAll(headerTitle, headerSub);
        root.setTop(headerBox);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f4f6f9;");

        VBox profileCard = new VBox(15);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        String name = "John";
        String code = "EMP001";
        String email = "john.doe@sarms.com";
        String username = "john";
        String dept = "Engineering & Systems Operations";
        int empId = 1;

        if (currentUser != null) {
            if (currentUser.getFirstName() != null) name = currentUser.getFirstName();
            if (currentUser.getEmpCode() != null) code = currentUser.getEmpCode();
            if (currentUser.getEmail() != null) email = currentUser.getEmail();
            if (currentUser.getUsername() != null) username = currentUser.getUsername();
            empId = currentUser.getEmpId();
        }

        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(16);

        addProfileRow(grid, "Employee ID:", String.valueOf(empId), 0);
        addProfileRow(grid, "Employee Code:", code, 1);
        addProfileRow(grid, "Full Name:", name, 2);
        addProfileRow(grid, "Official Email:", email, 3);
        addProfileRow(grid, "System Username:", username, 4);
        addProfileRow(grid, "Department:", dept, 5);
        addProfileRow(grid, "Designation / Role:", "Standard Employee (Role ID: 3)", 6);
        addProfileRow(grid, "Clearance Level:", "Level 2 Standard Operational Clearance", 7);
        addProfileRow(grid, "Account Status:", "🟢 Active & Verified Account", 8);

        Button btnBack = new Button("← Back to Workspace");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        profileCard.getChildren().addAll(
            new Label("📋 Official Security Record"), 
            new Separator(), 
            grid
        );

        content.getChildren().addAll(profileCard, btnBack);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        root.setCenter(sp);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Employee Profile Workspace");
        stage.setMaximized(true);
        stage.show();
    }

    // Full Workspace View for Keys & Active Clearances
    private void showFullKeysAndClearancesPage() {
        BorderPane root = new BorderPane();

        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 25;");
        Label headerTitle = new Label("🔑 My Active Keys & Security Clearances");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label headerSub = new Label("SARMS Verified Security Rights, Credentials & Access Tokens");
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #8d99ae;");
        headerBox.getChildren().addAll(headerTitle, headerSub);
        root.setTop(headerBox);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f4f6f9;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label cardTitle = new Label("🛡️ Active Granted Clearances & Privileges");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colRes = new TableColumn<>("Resource / Key Vault");
        TableColumn<String[], String> colLevel = new TableColumn<>("Access Level");
        TableColumn<String[], String> colDate = new TableColumn<>("Granted Date");
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");

        colRes.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colLevel.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        table.getColumns().addAll(colRes, colLevel, colDate, colStatus);

        table.setItems(FXCollections.observableArrayList(
            new String[]{"Production Data Vault", "Read & Query Access", "2026-01-15", "🟢 ACTIVE CLEARANCE"},
            new String[]{"Source Code Repository", "Read-Only Access", "2026-02-10", "🟢 ACTIVE CLEARANCE"},
            new String[]{"Financial DB Workspace", "Standard Access", "2026-03-01", "🟡 PENDING RENEWAL"}
        ));

        Button btnBack = new Button("← Back to Workspace");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        card.getChildren().addAll(cardTitle, new Separator(), table);
        content.getChildren().addAll(card, btnBack);

        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Active Clearances Workspace");
        stage.setMaximized(true);
        stage.show();
    }

    // Full Workspace View for Logs & Active Sessions Dashboard
    private void showFullLogsAndSessionsPage() {
        BorderPane root = new BorderPane();

        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 25;");
        Label headerTitle = new Label("📊 My Activity Logs & Active System Sessions");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label headerSub = new Label("SARMS Security Personnel Audit Log & Real-Time Login Tracking");
        headerSub.setStyle("-fx-font-size: 12px; -fx-text-fill: #8d99ae;");
        headerBox.getChildren().addAll(headerTitle, headerSub);
        root.setTop(headerBox);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #f4f6f9;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label cardTitle = new Label("📋 Recent User Session Audit Logs");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<String[]> logTable = new TableView<>();
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<String[], String> colEvent = new TableColumn<>("Event Description");
        TableColumn<String[], String> colIp = new TableColumn<>("IP Address / Host");
        TableColumn<String[], String> colTime = new TableColumn<>("Timestamp");
        TableColumn<String[], String> colStatus = new TableColumn<>("Security Status");

        colEvent.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colIp.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colTime.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));

        logTable.getColumns().addAll(colEvent, colIp, colTime, colStatus);

        logTable.setItems(FXCollections.observableArrayList(
            new String[]{"Logged into System Portal", "192.168.1.45 (Local Workstation)", "2026-10-24 10:24:12", "🟢 VERIFIED SESSION"},
            new String[]{"Submitted Access Clearance Request (Vault)", "192.168.1.45 (Local Workstation)", "2026-10-24 10:30:45", "🟢 SUCCESSFUL ACTION"},
            new String[]{"Profile Information Viewed", "192.168.1.45 (Local Workstation)", "2026-10-24 11:15:02", "🟢 NORMAL AUDIT"},
            new String[]{"Password Validation Check", "192.168.1.45 (Local Workstation)", "2026-10-24 12:00:10", "🟢 PASSED"}
        ));

        Button btnBack = new Button("← Back to Workspace");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 22; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        card.getChildren().addAll(cardTitle, new Separator(), logTable);
        content.getChildren().addAll(card, btnBack);

        root.setCenter(content);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Activity Audit Logs Workspace");
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

    // Modal Helpers with Red Alert Themes and Strict Purpose Validations
    private void showReportIncidentModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("🚨 Report Security Incident");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🚨 Report Security Incident / Breach");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #dc2626;");

        TextArea txtDesc = new TextArea();
        txtDesc.setPromptText("Enter detailed description of security incident (Min 10 characters required)...");
        txtDesc.setPrefRowCount(4);

        Label lblErr = new Label();
        lblErr.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        Button btnSend = new Button("🚨 SEND EMERGENCY ALERT");
        btnSend.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSend.setOnAction(e -> {
            String desc = txtDesc.getText().trim();
            if (desc.length() < 10) {
                lblErr.setText("❌ Minimum 10 characters required to report an incident!");
                lblErr.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to trigger a SECURITY INCIDENT ALERT to Administrators?", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Confirm Incident Report");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "🚨 Incident reported successfully! Security team has been notified.");
                    info.show();
                    modal.close();
                }
            });
        });

        box.getChildren().addAll(title, txtDesc, lblErr, btnSend);
        modal.setScene(new Scene(box, 420, 260));
        modal.showAndWait();
    }

    private void showEmergencyRevokeModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("🚨 Emergency Access Revoke");

        VBox box = new VBox(15);
        box.setPadding(new Insets(22));
        box.setStyle("-fx-background-color: #fff5f5; -fx-border-color: #fca5a5; -fx-border-width: 1; -fx-border-radius: 8;");

        Label title = new Label("🚨 EMERGENCY SELF-REVOKE CLEARANCES");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #991b1b;");

        Label warnMsg = new Label("⚠️ Warning: This will immediately freeze your account and revoke all granted active clearances!");
        warnMsg.setStyle("-fx-text-fill: #7f1d1d; -fx-font-size: 11px;");
        warnMsg.setWrapText(true);

        Button btnRevoke = new Button("🔥 IMMEDIATELY REVOKE ALL MY PERMISSIONS");
        btnRevoke.setStyle("-fx-background-color: #b91c1c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 6; -fx-cursor: hand;");

        btnRevoke.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.WARNING, "CRITICAL ACTION:\nAre you sure you want to revoke ALL your active clearances now?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Emergency Revocation");
            confirm.showAndWait().ifPresent(resp -> {
                if (resp == ButtonType.YES) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "🔒 All active clearances have been emergency revoked. Session ending.");
                    info.showAndWait();
                    modal.close();
                    new LoginPage(stage).show();
                }
            });
        });

        box.getChildren().addAll(title, warnMsg, btnRevoke);
        modal.setScene(new Scene(box, 420, 200));
        modal.showAndWait();
    }

    private void showDeviceAccessModal() {
        Stage modal = new Stage(); modal.initModality(Modality.APPLICATION_MODAL);
        VBox box = new VBox(10); box.setPadding(new Insets(20));
        box.getChildren().addAll(new Label("🛠️ Request Hardware Device Access"), new TextField(), new Button("Submit Request"));
        modal.setScene(new Scene(box, 350, 180)); modal.showAndWait();
    }

    // Updated Change Password Modal with 3 Password Fields & DB Checks
    private void showChangePasswordModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("🔐 Security Settings - Change Password");

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("🔐 Change Account Password");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #0d1b2a;");

        PasswordField pfOld = new PasswordField();
        pfOld.setPromptText("Enter Current Password");

        PasswordField pfNew = new PasswordField();
        pfNew.setPromptText("Enter New Password (Min 6 chars)");

        PasswordField pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirm New Password");

        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        Button btnUpdate = new Button("🔐 Update Security Password");
        btnUpdate.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUpdate.setOnAction(e -> {
            String oldPass = pfOld.getText().trim();
            String newPass = pfNew.getText().trim();
            String confirmPass = pfConfirm.getText().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                lblMsg.setText("❌ All password fields are required!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                return;
            }

            if (newPass.length() < 6) {
                lblMsg.setText("❌ New password must be at least 6 characters long!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                lblMsg.setText("❌ New Password and Confirm Password do not match!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                return;
            }

            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
            String checkSql = "SELECT password FROM employees WHERE emp_id = ?";
            String updateSql = "UPDATE employees SET password = ? WHERE emp_id = ?";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

                checkStmt.setInt(1, empId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    String currentDbPass = rs.getString("password");
                    if (!oldPass.equals(currentDbPass)) {
                        lblMsg.setText("❌ Incorrect Current Password!");
                        lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        return;
                    }
                }

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newPass);
                    updateStmt.setInt(2, empId);

                    if (updateStmt.executeUpdate() > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "✓ Password updated successfully!");
                        alert.showAndWait();
                        modal.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                lblMsg.setText("❌ Database Update Error!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(pfOld, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(pfNew, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(pfConfirm, 1, 2);

        box.getChildren().addAll(title, grid, lblMsg, btnUpdate);
        modal.setScene(new Scene(box, 380, 260));
        modal.showAndWait();
    }

    private void safeOpenPage(String pageName) {
        try {
            if ("AccessRequestPage".equals(pageName)) new AccessRequestPage(stage, currentUser).show();
            else if ("ApplyLeavePage".equals(pageName)) new ApplyLeavePage(stage, currentUser).show();
        } catch (Exception ex) {
            ex.printStackTrace();
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