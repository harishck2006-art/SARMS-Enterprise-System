import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class LeavePage {

    private Stage stage;
    private Employee currentUser;

    public LeavePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        String role = (currentUser != null) ? 
            (currentUser.getRoleName() != null ? currentUser.getRoleName() : currentUser.getRole()) : "Employee";
        if (role == null) role = "Employee";

        boolean isAdminOrManager = role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Manager");

        // Left Sidebar Navigation
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(isAdminOrManager ? "MANAGEMENT PORTAL" : "EMPLOYEE PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(15, 0, 0, 0));

        if (isAdminOrManager) {
            Button btnOverview = createNavButton("📊 Command Overview", false);
            Button btnEmployees = createNavButton("👥 Manage Employees", false);
            Button btnResources = createNavButton("📦 Manage Resources", false);
            Button btnLeaves = createNavButton("📅 Leave Approvals", true);

            btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
            btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
            btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());

            navMenu.getChildren().addAll(btnOverview, btnEmployees, btnResources, btnLeaves);
        } else {
            Button btnHome = createNavButton("🏠 Employee Home", false);
            Button btnRequest = createNavButton("➕ Create Access Request", false);
            Button btnStatus = createNavButton("📜 My Access Requests", false);
            Button btnLeave = createNavButton("📅 Apply Leave", true);
            Button btnLogs = createNavButton("📊 View My Logs", false);

            btnHome.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());
            btnRequest.setOnAction(e -> new CreateRequestPage(stage, currentUser).show());
            btnStatus.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
            btnLeave.setOnAction(e -> new ApplyLeavePage(stage, currentUser).show());
            btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

            navMenu.getChildren().addAll(btnHome, btnRequest, btnStatus, btnLeave, btnLogs);
        }

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "User");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        // Main Table Workspace
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label(isAdminOrManager ? "Leave Requests Management" : "My Leave History & Status");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<LeaveRequest> table = new TableView<>();
        table.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        TableColumn<LeaveRequest, Integer> idCol = new TableColumn<>("Leave ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("leaveId"));

        TableColumn<LeaveRequest, Integer> empCol = new TableColumn<>("Emp ID");
        empCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<LeaveRequest, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("leaveType"));

        TableColumn<LeaveRequest, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveRequest, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveRequest, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<LeaveRequest, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, empCol, typeCol, startCol, endCol, reasonCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        LeaveRequestDAO dao = new LeaveRequestDAO();
        refreshTable(table, dao, role);

        Button applyBtn = new Button("➕ Apply Leave");
        applyBtn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Button approveBtn = new Button("✓ Approve");
        approveBtn.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Button rejectBtn = new Button("✕ Reject");
        rejectBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        applyBtn.setOnAction(e -> new ApplyLeavePage(stage, currentUser).show());

        final String currentRole = role;

        approveBtn.setOnAction(e -> {
            LeaveRequest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (dao.updateLeaveStatus(selected.getLeaveId(), "Approved")) {
                    NotificationDAO notifyDao = new NotificationDAO();
                    String msg = "Your leave request (" + selected.getLeaveType() + ") from " +
                            selected.getStartDate() + " to " + selected.getEndDate() +
                            " has been APPROVED by " + currentUser.getFirstName() + ".";
                    notifyDao.addNotification(selected.getEmpId(), msg, "Leave Approved");
                    refreshTable(table, dao, currentRole);
                    new Alert(Alert.AlertType.INFORMATION, "Leave Approved & Notification sent!").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select a leave request").showAndWait();
            }
        });

        rejectBtn.setOnAction(e -> {
            LeaveRequest selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (dao.updateLeaveStatus(selected.getLeaveId(), "Rejected")) {
                    NotificationDAO notifyDao = new NotificationDAO();
                    String msg = "Your leave request (" + selected.getLeaveType() + ") from " +
                            selected.getStartDate() + " to " + selected.getEndDate() +
                            " has been REJECTED by " + currentUser.getFirstName() + ".";
                    notifyDao.addNotification(selected.getEmpId(), msg, "Leave Rejected");
                    refreshTable(table, dao, currentRole);
                    new Alert(Alert.AlertType.INFORMATION, "Leave Rejected & Notification sent!").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select a leave request").showAndWait();
            }
        });

        backBtn.setOnAction(e -> {
            if (currentRole.equalsIgnoreCase("Admin")) {
                new AdminDashboard(stage, currentUser).show();
            } else if (currentRole.equalsIgnoreCase("Manager")) {
                new ManagerDashboard(stage, currentUser).show();
            } else {
                new EmployeeDashboard(stage, currentUser).show();
            }
        });

        HBox buttons = new HBox(12);

        if (role.equalsIgnoreCase("Employee")) {
            buttons.getChildren().addAll(applyBtn, backBtn);
        } else if (role.equalsIgnoreCase("Manager")) {
            buttons.getChildren().addAll(approveBtn, rejectBtn, backBtn);
        } else {
            buttons.getChildren().addAll(applyBtn, approveBtn, rejectBtn, backBtn);
        }

        main.getChildren().addAll(headerTitle, table, buttons);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Leave Requests");
        stage.setMaximized(true);
        stage.show();
    }

    private void refreshTable(TableView<LeaveRequest> table, LeaveRequestDAO dao, String role) {
        List<LeaveRequest> allLeaves = dao.getAllLeaveRequests();
        if (role.equalsIgnoreCase("Employee") && currentUser != null) {
            List<LeaveRequest> myLeaves = allLeaves.stream()
                    .filter(l -> l.getEmpId() == currentUser.getEmpId())
                    .collect(Collectors.toList());
            table.setItems(FXCollections.observableArrayList(myLeaves));
        } else {
            table.setItems(FXCollections.observableArrayList(allLeaves));
        }
    }

    private Button createNavButton(String text, boolean isActive) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle(isActive ? 
            "-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10 15; -fx-background-radius: 8;" :
            "-fx-background-color: transparent; -fx-text-fill: #e0e1dd; -fx-font-size: 13px; -fx-padding: 10 15; -fx-cursor: hand;");
        return b;
    }
}