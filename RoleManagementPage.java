import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

public class RoleManagementPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<String[]> table = new TableView<>();
    private RoleDAO roleDao = new RoleDAO();

    public RoleManagementPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);
        Label brandLogo = new Label("S");
        brandLogo.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 8 14; -fx-background-radius: 8;");
        
        VBox brandTitleBox = new VBox(2);
        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
        Label brandSub = new Label("ADMIN CONTROL CENTER");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        brandTitleBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(brandLogo, brandTitleBox);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnOverview = createNavButton("📊 Command Overview", false);
        Button btnEmployees = createNavButton("👥 Manage Employees", false);
        Button btnResources = createNavButton("📦 Manage Resources", false);
        Button btnRequests = createNavButton("📜 All Access Requests", false);
        Button btnLeaves = createNavButton("📅 Leave Approvals", false);
        Button btnRoles = createNavButton("🔐 Role Management", true);
        Button btnAlerts = createNavButton("🛡 Security Alerts", false);
        Button btnLogs = createNavButton("📊 System Access Logs", false);
        Button btnPolicies = createNavButton("⚙ Security Policies", false);
        Button btnAnalytics = createNavButton("📈 Analytics & Reports", false);

        btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
        btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
        btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());
        btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
        btnLeaves.setOnAction(e -> new AdminLeaveApprovalPage(stage, currentUser).show());
        btnAlerts.setOnAction(e -> new SecurityAlertsPage(stage, currentUser).show());
        btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());
        btnPolicies.setOnAction(e -> new SecurityPoliciesPage(stage, currentUser).show());
        btnAnalytics.setOnAction(e -> new AnalyticsPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnOverview, btnEmployees, btnResources, btnRequests, btnLeaves, btnRoles, btnAlerts, btnLogs, btnPolicies, btnAnalytics);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() + " (Admin)" : "Admin");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);

        root.setLeft(sidebar);

        // Main Content Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label headerTitle = new Label("System Roles & Privilege Controls");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action Toolbar
        HBox actionToolbar = new HBox(10);

        Button btnAddRole = new Button("➕ Add New Role");
        btnAddRole.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnAddRole.setOnAction(e -> openAddRoleDialog());

        Button btnEditRole = new Button("✏ Edit Role");
        btnEditRole.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnEditRole.setOnAction(e -> openEditRoleDialog());

        Button btnDeleteRole = new Button("🗑 Delete Role");
        btnDeleteRole.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnDeleteRole.setOnAction(e -> handleDeleteRole());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> refreshTableData());

        actionToolbar.getChildren().addAll(btnAddRole, btnEditRole, btnDeleteRole, btnRefresh);
        headerBox.getChildren().addAll(headerTitle, spacer, actionToolbar);

        table.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e2e8f0;");

        TableColumn<String[], String> colId = new TableColumn<>("Role ID");
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));

        TableColumn<String[], String> colName = new TableColumn<>("Role Name");
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));

        TableColumn<String[], String> colDesc = new TableColumn<>("Privilege Scope");
        colDesc.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));

        table.getColumns().setAll(colId, colName, colDesc);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTableData();

        Button backBtn = new Button("← Back to Admin Command Center");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        mainContent.getChildren().addAll(headerBox, table, backBtn);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Role Management");
        stage.setMaximized(true);
        stage.show();
    }

    private void refreshTableData() {
        List<String[]> roles = roleDao.getAllRoles();
        table.setItems(FXCollections.observableArrayList(roles));
    }

    private void openAddRoleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Create New System Role");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Add New Role Definition");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TextField roleNameFld = new TextField();
        roleNameFld.setPromptText("Enter Role Name (e.g., Auditor)");

        Label statusLbl = new Label();

        Button saveBtn = new Button("Save Role");
        saveBtn.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            String rName = roleNameFld.getText().trim();
            if (rName.isEmpty()) {
                statusLbl.setStyle("-fx-text-fill: red;");
                statusLbl.setText("Role Name cannot be empty!");
                return;
            }

            if (roleDao.addRole(rName, "Custom Privilege Role")) {
                refreshTableData();
                dialog.close();
            } else {
                statusLbl.setStyle("-fx-text-fill: red;");
                statusLbl.setText("Failed to create role. Name might exist!");
            }
        });

        layout.getChildren().addAll(title, new Label("Role Title:"), roleNameFld, statusLbl, saveBtn);
        Scene scene = new Scene(layout, 340, 220);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void openEditRoleDialog() {
        String[] selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select a role from the table first!");
            alert.show();
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit System Role");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("Edit Role: " + selected[1]);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TextField roleNameFld = new TextField(selected[1]);

        Button updateBtn = new Button("Update Role");
        updateBtn.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        updateBtn.setOnAction(e -> {
            int roleId = Integer.parseInt(selected[0]);
            if (roleDao.updateRole(roleId, roleNameFld.getText().trim())) {
                refreshTableData();
                dialog.close();
            }
        });

        layout.getChildren().addAll(title, new Label("New Role Name:"), roleNameFld, updateBtn);
        Scene scene = new Scene(layout, 340, 200);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void handleDeleteRole() {
        String[] selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Select a role from table to delete!");
            alert.show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete Role '" + selected[1] + "'?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                int roleId = Integer.parseInt(selected[0]);
                if (roleDao.deleteRole(roleId)) {
                    refreshTableData();
                } else {
                    Alert err = new Alert(Alert.AlertType.ERROR, "Cannot delete role assigned to active users!");
                    err.show();
                }
            }
        });
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