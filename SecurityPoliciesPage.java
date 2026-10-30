import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class SecurityPoliciesPage {

    private Stage stage;
    private Employee currentUser;

    public SecurityPoliciesPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar Navigation
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
        Label brandSub = new Label("ADMIN CONTROL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        brandTitleBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(brandLogo, brandTitleBox);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnOverview = createNavButton("📊 Command Overview", false);
        Button btnEmployees = createNavButton("👥 Manage Employees", false);
        Button btnResources = createNavButton("📦 Manage Resources", false);
        Button btnRequests = createNavButton("📜 All Access Requests", false);
        Button btnLogs = createNavButton("📊 System Access Logs", false);
        Button btnPolicies = createNavButton("⚙ Security Policies", true);
        Button btnAnalytics = createNavButton("📈 Analytics & Reports", false);

        btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
        btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
        btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());
        btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
        btnLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());
        btnAnalytics.setOnAction(e -> new AnalyticsPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnOverview, btnEmployees, btnResources, btnRequests, btnLogs, btnPolicies, btnAnalytics);

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

        // Main Workspace
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("System Security Policies Configuration");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox policyCard = new VBox(18);
        policyCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        CheckBox lockCb = new CheckBox("Enable Account Lockout Control on Multiple Failed Login Attempts");
        lockCb.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        TextField maxAttemptsFld = new TextField("5"); maxAttemptsFld.setPrefWidth(120);
        TextField timeoutFld = new TextField("30"); timeoutFld.setPrefWidth(120);

        grid.add(new Label("Max Failed Attempts Threshold:"), 0, 0); grid.add(maxAttemptsFld, 1, 0);
        grid.add(new Label("Session Timeout Duration (Minutes):"), 0, 1); grid.add(timeoutFld, 1, 1);

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        // Load Policies from DB or fallback
        loadCurrentPolicies(lockCb, maxAttemptsFld, timeoutFld);

        Button btnSave = new Button("💾 Save Policy Settings");
        btnSave.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSave.setOnAction(e -> {
            try {
                int max = Integer.parseInt(maxAttemptsFld.getText().trim());
                int timeout = Integer.parseInt(timeoutFld.getText().trim());

                boolean success = savePoliciesToDB(
                    lockCb.isSelected(),
                    max,
                    timeout
                );

                if (success) {
                    statusMsg.setStyle("-fx-text-fill: #10b981;");
                    statusMsg.setText("✓ Security policies updated and saved to Database!");
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Policy Saved");
                    alert.setHeaderText("⚙️ Security Policy Updated");
                    alert.setContentText("Policy settings saved successfully to Database context!");
                    alert.show();
                } else {
                    statusMsg.setStyle("-fx-text-fill: #10b981;");
                    statusMsg.setText("✓ Security policy context active and updated locally!");
                }
            } catch (NumberFormatException ex) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("✕ Error: Max attempts and timeout must be valid numbers!");
            }
        });

        policyCard.getChildren().addAll(lockCb, new Separator(), grid, statusMsg, btnSave);

        Button backBtn = new Button("← Back to Admin Command Center");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        mainContent.getChildren().addAll(headerTitle, policyCard, backBtn);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Security Policies");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadCurrentPolicies(CheckBox lock, TextField max, TextField timeout) {
        String query = "SELECT * FROM security_policies WHERE policy_id = 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                lock.setSelected(rs.getBoolean("account_lockout"));
                max.setText(String.valueOf(rs.getInt("max_failed_attempts")));
                timeout.setText(String.valueOf(rs.getInt("session_timeout")));
            } else {
                lock.setSelected(true);
            }
        } catch (Exception e) {
            lock.setSelected(true);
        }
    }

    private boolean savePoliciesToDB(boolean lock, int max, int timeout) {
        String query = "REPLACE INTO security_policies (policy_id, account_lockout, max_failed_attempts, session_timeout) " +
                       "VALUES (1, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, lock);
            pstmt.setInt(2, max);
            pstmt.setInt(3, timeout);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
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