import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AccessLogPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessRequestModel> table = new TableView<>();
    private AccessRequestDAO dao = new AccessRequestDAO();

    public AccessLogPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        String userRole = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";
        boolean isAdmin = "Admin".equalsIgnoreCase(userRole);
        boolean isManager = "Manager".equalsIgnoreCase(userRole);
        boolean isSecurity = "Security".equalsIgnoreCase(userRole) || "Security Officer".equalsIgnoreCase(userRole) || "SOC".equalsIgnoreCase(userRole);

        // Left Sidebar Navigation Panel
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(userRole.toUpperCase() + " PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(6);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        if (isAdmin) {
            Label cat1 = new Label("ADMIN SYSTEM CONTROL");
            cat1.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 2 5;");

            Button btnOverview = createNavButton("📊 Command Overview", false);
            Button btnEmployees = createNavButton("👥 Manage Employees", false);
            Button btnRequests = createNavButton("🔑 All Access Requests", false);
            Button btnLogs = createNavButton("📊 System Access Logs", true);

            btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
            btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
            btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
            btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

            navMenu.getChildren().addAll(cat1, btnOverview, btnEmployees, btnRequests, btnLogs);
        } else if (isManager) {
            Label cat1 = new Label("TEAM MANAGEMENT");
            cat1.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 2 5;");

            Button btnOverview = createNavButton("📊 Department Workspace", false);
            Button btnTeamLeaves = createNavButton("📥 Team Leave Requests", false);
            Button btnRequests = createNavButton("🔑 Access Request Approvals", false);
            Button btnLogs = createNavButton("📊 System Audit Logs", true);

            btnOverview.setOnAction(e -> new ManagerDashboard(stage, currentUser).show());
            btnTeamLeaves.setOnAction(e -> new LeaveApprovalPage(stage, currentUser).show());
            btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
            btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

            navMenu.getChildren().addAll(cat1, btnOverview, btnTeamLeaves, btnRequests, btnLogs);
        } else if (isSecurity) {
            Label cat1 = new Label("SOC OPERATIONS");
            cat1.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 2 5;");

            Button btnCommand = createNavButton("📊 Command Center", false);
            Button btnPermissions = createNavButton("🔑 Grant & Revoke Permissions", false);
            Button btnThreats = createNavButton("🚨 Threat Incident Escalations", false);
            Button btnLogs = createNavButton("📊 System Audit Trail", true);

            btnCommand.setOnAction(e -> new SecurityDashboard(stage, currentUser).show());
            btnPermissions.setOnAction(e -> new PermissionPage(stage, currentUser).show());
            btnThreats.setOnAction(e -> new SecurityAlertsPage(stage, currentUser).show());
            btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

            navMenu.getChildren().addAll(cat1, btnCommand, btnPermissions, btnThreats, btnLogs);
        } else {
            Label cat1 = new Label("MY SELF-SERVICE");
            cat1.setStyle("-fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 10 0 2 5;");

            Button btnHome = createNavButton("🏠 Employee Home", false);
            Button btnRequest = createNavButton("➕ New Access Request", false);
            Button btnStatus = createNavButton("📜 My Access Requests", false);
            Button btnKeys = createNavButton("🔑 My Keys & Active Clearances", false);
            Button btnLeave = createNavButton("📅 Apply Leave (To Manager)", false);
            Button btnLogs = createNavButton("📊 View My Logs & Sessions", true);

            btnHome.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());
            btnRequest.setOnAction(e -> new CreateRequestPage(stage, currentUser).show());
            btnStatus.setOnAction(e -> new MyAccessRequestsPage(stage, currentUser).show());
            btnKeys.setOnAction(e -> new MyKeysPage(stage, currentUser).show());
            btnLeave.setOnAction(e -> new ApplyLeavePage(stage, currentUser).show());
            btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

            navMenu.getChildren().addAll(cat1, btnHome, btnRequest, btnStatus, btnKeys, btnLeave, btnLogs);
        }

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "User");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        // Work Center Panel
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        String pageTitleText = (isAdmin || isManager || isSecurity) ? "📊 Enterprise System Access Audit Logs" : "💻 My Session Activity & Portal History";
        Label title = new Label(pageTitleText);
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TableColumn<AccessRequestModel, Integer> colId = new TableColumn<>("Log ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<AccessRequestModel, String> colUser = new TableColumn<>("User Account");
        colUser.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<AccessRequestModel, String> colRes = new TableColumn<>("Resource / Module");
        colRes.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<AccessRequestModel, String> colTime = new TableColumn<>("Timestamp");
        colTime.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<AccessRequestModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AccessRequestModel, String> colProcessed = new TableColumn<>("Audit Date");
        colProcessed.setCellValueFactory(new PropertyValueFactory<>("processedDate"));

        table.getColumns().setAll(colId, colUser, colRes, colTime, colStatus, colProcessed);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadLogs(isAdmin || isManager || isSecurity);

        card.getChildren().add(table);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if (isAdmin) {
                new AdminDashboard(stage, currentUser).show();
            } else if (isManager) {
                new ManagerDashboard(stage, currentUser).show();
            } else if (isSecurity) {
                new SecurityDashboard(stage, currentUser).show();
            } else {
                new EmployeeDashboard(stage, currentUser).show();
            }
        });

        main.getChildren().addAll(title, card, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - System Access Logs");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadLogs(boolean isElevatedUser) {
        if (isElevatedUser) {
            var logs = dao.getAllRequestsHistory();
            if (logs != null) table.setItems(FXCollections.observableArrayList(logs));
        } else {
            int empId = (currentUser != null && currentUser.getEmpId() > 0) ? currentUser.getEmpId() : 4;
            var personalLogs = dao.getEmployeeRequestsHistory(empId);
            if (personalLogs != null) table.setItems(FXCollections.observableArrayList(personalLogs));
        }
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
}