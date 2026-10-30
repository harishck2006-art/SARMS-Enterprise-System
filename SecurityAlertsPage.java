import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class SecurityAlertsPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<String[]> table = new TableView<>();

    public SecurityAlertsPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // 1. LEFT SIDEBAR (ADMIN CONTROL NAVIGATION)
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
        Button btnAlerts = createNavButton("🚨 Security Alerts", true);
        Button btnPolicies = createNavButton("⚙ Security Policies", false);

        btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
        btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
        btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());
        btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
        btnAlerts.setOnAction(e -> new SecurityAlertsPage(stage, currentUser).show());
        btnPolicies.setOnAction(e -> new SecurityPoliciesPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnOverview, btnEmployees, btnResources, btnRequests, btnAlerts, btnPolicies);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() + " (Admin)" : "System Administrator");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);

        root.setLeft(sidebar);

        // 2. MAIN WORKSPACE (SECURITY ALERTS OVERVIEW)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(25));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("🚨 Admin Security Incident & Account Overrides");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        HBox topBar = new HBox(12);
        
        Button btnUnlock = new Button("🔓 Unlock Selected Account");
        btnUnlock.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        
        Button btnRefresh = new Button("🔄 Refresh Alerts");
        btnRefresh.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");

        btnUnlock.setOnAction(e -> handleAccountUnlock());
        btnRefresh.setOnAction(e -> loadSecurityAlerts());

        topBar.getChildren().addAll(btnUnlock, btnRefresh);

        // Table Setup
        TableColumn<String[], String> colId = new TableColumn<>("EMP ID");
        TableColumn<String[], String> colUser = new TableColumn<>("Username / Target");
        TableColumn<String[], String> colAttempts = new TableColumn<>("Failed Attempts");
        TableColumn<String[], String> colType = new TableColumn<>("Alert Type");
        TableColumn<String[], String> colSeverity = new TableColumn<>("Status / Threat Level");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colUser.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colAttempts.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colSeverity.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));

        table.getColumns().addAll(colId, colUser, colAttempts, colType, colSeverity);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadSecurityAlerts();

        Button btnBack = new Button("← Back to Admin Console");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        mainContent.getChildren().addAll(headerTitle, topBar, table, btnBack);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS Admin - Security Alerts & Incident Response");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadSecurityAlerts() {
        table.getItems().clear();
        String sql = "SELECT * FROM employees WHERE is_locked = TRUE OR failed_attempts > 0";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("emp_id"));
                String username = rs.getString("username");
                String attempts = String.valueOf(rs.getInt("failed_attempts"));
                boolean locked = rs.getBoolean("is_locked");

                String alertType = locked ? "ACCOUNT LOCKED OUT" : "FAILED LOGIN THRESHOLD";
                String severity = locked ? "HIGH (🔒 Locked)" : "MEDIUM (⚠️ Warning)";

                table.getItems().add(new String[]{id, username, attempts, alertType, severity});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAccountUnlock() {
        String[] selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an account row from the table to unlock!");
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
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account '" + uname + "' (ID: " + empId + ") has been successfully unlocked!");
                alert.show();
                loadSecurityAlerts(); // Refresh table
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update unlock status in database!");
            alert.show();
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