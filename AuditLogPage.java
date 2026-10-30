import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class AuditLogPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessLog> table = new TableView<>();

    public AuditLogPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";

        // Left Sidebar Navigation
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(role.toUpperCase() + " AUDIT CONTROL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(15, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Main Portal Dashboard", false);
        Button btnLogs = createNavButton("📊 Security Audit Logs", true);

        btnHome.setOnAction(e -> navigateBack());
        navMenu.getChildren().addAll(btnHome, btnLogs);

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

        // Center Content Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("🛡 SYSTEM SECURITY AUDIT LOGS");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Table Columns Setup
        TableColumn<AccessLog, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));

        TableColumn<AccessLog, Integer> empIdCol = new TableColumn<>("Emp ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<AccessLog, String> loginCol = new TableColumn<>("Login Time");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("loginTime"));

        TableColumn<AccessLog, String> logoutCol = new TableColumn<>("Logout Time");
        logoutCol.setCellValueFactory(new PropertyValueFactory<>("logoutTime"));

        TableColumn<AccessLog, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().setAll(logIdCol, empIdCol, loginCol, logoutCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTableData();

        // Control Panel Bar
        HBox controlBox = new HBox(15);
        Button btnRefresh = new Button("🔄 Refresh Logs");
        btnRefresh.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> refreshTableData());

        Button btnBack = new Button("← Back to Dashboard");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> navigateBack());

        controlBox.getChildren().addAll(btnRefresh, btnBack);

        mainContent.getChildren().addAll(headerTitle, table, controlBox);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Security Audit Log Panel");
        stage.setMaximized(true);
        stage.show();
    }

    private void refreshTableData() {
        AccessLogDAO logDao = new AccessLogDAO();
        List<AccessLog> logs;

        if (currentUser != null && "Employee".equalsIgnoreCase(currentUser.getRole())) {
            logs = logDao.getLogsByEmpId(currentUser.getEmpId());
        } else {
            // Fix: Directly calls getAllLogs() from AccessLogDAO
            logs = logDao.getAllLogs();
        }

        if (logs != null) {
            table.setItems(FXCollections.observableArrayList(logs));
        }
    }

    private void navigateBack() {
        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            new AdminDashboard(stage, currentUser).show();
        } else if (currentUser != null && "Manager".equalsIgnoreCase(currentUser.getRole())) {
            new ManagerDashboard(stage, currentUser).show();
        } else {
            new EmployeeDashboard(stage, currentUser).show();
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