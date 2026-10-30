import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class MyKeysPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessRequestModel> table = new TableView<>();
    private AccessRequestDAO dao = new AccessRequestDAO();

    public MyKeysPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        String userRole = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";

        // Left Sidebar Navigation
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(userRole.toUpperCase() + " CLEARANCES");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(6);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Main Workspace", false);
        Button btnKeys = createNavButton("🔑 My Keys & Clearances", true);

        btnHome.setOnAction(e -> navigateBack());
        btnKeys.setOnAction(e -> new MyKeysPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnHome, btnKeys);

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

        // Center Content Workspace
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🔑 Active Access Keys & Security Clearances");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 22; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TableColumn<AccessRequestModel, Integer> idCol = new TableColumn<>("Request ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<AccessRequestModel, String> resCol = new TableColumn<>("Resource / Key");
        resCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<AccessRequestModel, String> reasonCol = new TableColumn<>("Purpose");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<AccessRequestModel, String> statusCol = new TableColumn<>("Clearance Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AccessRequestModel, String> dateCol = new TableColumn<>("Issued / Requested Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        table.getColumns().setAll(idCol, resCol, reasonCol, statusCol, dateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-background-radius: 8; -fx-border-color: #e2e8f0;");

        loadKeysData();

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        card.getChildren().addAll(table, backBtn);
        main.getChildren().addAll(title, card);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - My Security Keys");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadKeysData() {
        List<AccessRequestModel> data = dao.getAllRequestsHistory();
        if (data != null) {
            table.setItems(FXCollections.observableArrayList(data));
        }
    }

    private void navigateBack() {
        if (currentUser != null) {
            String role = currentUser.getRole();
            if ("Admin".equalsIgnoreCase(role)) {
                new AdminDashboard(stage, currentUser).show();
            } else if ("Manager".equalsIgnoreCase(role)) {
                new ManagerDashboard(stage, currentUser).show();
            } else if ("Security Officer".equalsIgnoreCase(role) || "Security".equalsIgnoreCase(role) || "SOC".equalsIgnoreCase(role)) {
                new SecurityDashboard(stage, currentUser).show();
            } else {
                new EmployeeDashboard(stage, currentUser).show();
            }
        } else {
            new LoginPage(stage).show();
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