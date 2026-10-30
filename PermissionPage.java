import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class PermissionPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessRequestModel> table = new TableView<>();
    private AccessRequestDAO dao = new AccessRequestDAO();

    public PermissionPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox sidebar = new VBox(15);
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

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnCommand = createNavButton("📊 Command Center", false);
        Button btnPermissions = createNavButton("🔑 Grant & Revoke Permissions", true);
        Button btnThreats = createNavButton("🚨 Locked Accounts & Threats", false);
        Button btnAuditLogs = createNavButton("📊 System Audit Trail", false);

        btnCommand.setOnAction(e -> new SecurityDashboard(stage, currentUser).show());
        btnPermissions.setOnAction(e -> new PermissionPage(stage, currentUser).show());
        btnThreats.setOnAction(e -> new SecurityAlertsPage(stage, currentUser).show());
        btnAuditLogs.setOnAction(e -> new AccessLogPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnCommand, btnPermissions, btnThreats, btnAuditLogs);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Officer Alex");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("🔑 Grant & Revoke System Permissions");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 22; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TableColumn<AccessRequestModel, Integer> colId = new TableColumn<>("Req ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<AccessRequestModel, String> colEmp = new TableColumn<>("Applicant");
        colEmp.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<AccessRequestModel, String> colRes = new TableColumn<>("Resource");
        colRes.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<AccessRequestModel, String> colReason = new TableColumn<>("Reason");
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<AccessRequestModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().setAll(colId, colEmp, colRes, colReason, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadData();

        Button btnGrant = new Button("✓ Grant Permission");
        btnGrant.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnGrant.setOnAction(e -> updatePermission("Granted"));

        Button btnRevoke = new Button("✕ Revoke Permission");
        btnRevoke.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnRevoke.setOnAction(e -> updatePermission("Revoked"));

        Button btnBack = new Button("← Back to SOC Command Center");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new SecurityDashboard(stage, currentUser).show());

        HBox buttonBox = new HBox(12, btnGrant, btnRevoke, btnBack);
        card.getChildren().addAll(table, buttonBox);
        mainContent.getChildren().addAll(headerTitle, card);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Grant & Revoke Permissions");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadData() {
        var data = dao.getAllRequestsHistory();
        if (data != null) {
            ObservableList<AccessRequestModel> list = FXCollections.observableArrayList(data);
            table.setItems(list);
        }
    }

    private void updatePermission(String newStatus) {
        AccessRequestModel selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean success = dao.updateStatus(selected.getRequestId(), newStatus);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Permission updated to " + newStatus);
                loadData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update permission!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "Select a request from table first!");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
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