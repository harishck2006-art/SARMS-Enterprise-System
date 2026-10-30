import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MyAccessRequestsPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessRequestModel> table = new TableView<>();
    private AccessRequestDAO dao = new AccessRequestDAO();

    public MyAccessRequestsPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Left Sidebar Navigation
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label("EMPLOYEE PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(15, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Employee Home", false);
        Button btnRequest = createNavButton("➕ Create Access Request", false);
        Button btnStatus = createNavButton("📜 My Access Requests", true);
        Button btnLeave = createNavButton("📅 Apply Leave", false);

        btnHome.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());
        btnRequest.setOnAction(e -> new CreateRequestPage(stage, currentUser).show());
        btnStatus.setOnAction(e -> new MyAccessRequestsPage(stage, currentUser).show());
        btnLeave.setOnAction(e -> new ApplyLeavePage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnHome, btnRequest, btnStatus, btnLeave);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Employee");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        // Workspace
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("📜 My Access Requests & Approval Status");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TableColumn<AccessRequestModel, Integer> idCol = new TableColumn<>("Req ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<AccessRequestModel, String> resCol = new TableColumn<>("Requested Resource");
        resCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<AccessRequestModel, String> reasonCol = new TableColumn<>("Justification Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<AccessRequestModel, String> reqDateCol = new TableColumn<>("Requested Date");
        reqDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<AccessRequestModel, String> statusCol = new TableColumn<>("Approval Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AccessRequestModel, String> decDateCol = new TableColumn<>("Decision Date / Remarks");
        decDateCol.setCellValueFactory(new PropertyValueFactory<>("processedDate"));

        table.getColumns().setAll(idCol, resCol, reasonCol, reqDateCol, statusCol, decDateCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshData();

        card.getChildren().add(table);

        Button backBtn = new Button("← Back to Employee Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        main.getChildren().addAll(title, card, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - My Personal Access Requests");
        stage.setMaximized(true);
        stage.show();
    }

    private void refreshData() {
        int currentEmpId = (currentUser != null && currentUser.getEmpId() > 0) ? currentUser.getEmpId() : 4;
        var list = dao.getEmployeeRequestsHistory(currentEmpId);
        if (list != null) {
            table.setItems(FXCollections.observableArrayList(list));
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