import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class AddResourcePage {

    private Stage stage;
    private Employee currentUser;
    private TableView<String[]> table = new TableView<>();

    public AddResourcePage(Stage stage, Employee currentUser) {
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
        Label brandSub = new Label("ADMIN CONTROL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        brandTitleBox.getChildren().addAll(brandTitle, brandSub);
        brandBox.getChildren().addAll(brandLogo, brandTitleBox);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnOverview = createNavButton("📊 Command Overview", false);
        Button btnEmployees = createNavButton("👥 Manage Employees", false);
        Button btnResources = createNavButton("📦 Manage Resources", true);
        Button btnRequests = createNavButton("📜 All Access Requests", false);
        Button btnPolicies = createNavButton("⚙ Security Policies", false);

        btnOverview.setOnAction(e -> new AdminDashboard(stage, currentUser).show());
        btnEmployees.setOnAction(e -> new EmployeePage(stage, currentUser).show());
        btnResources.setOnAction(e -> new AddResourcePage(stage, currentUser).show());
        btnRequests.setOnAction(e -> new AccessRequestPage(stage, currentUser).show());
        btnPolicies.setOnAction(e -> new SecurityPoliciesPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnOverview, btnEmployees, btnResources, btnRequests, btnPolicies);

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

        // 2. MAIN WORKSPACE (RESOURCE INVENTORY & FORM)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(25));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("📦 Protected Resources Inventory & Management");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Top Form Card
        VBox formCard = new VBox(12);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 8, 0, 0, 3);");

        Label formTitle = new Label("➕ Register New Protected Resource");
        formTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #3a86ff;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        TextField txtName = new TextField(); txtName.setPromptText("Resource Name");
        ComboBox<String> cbType = new ComboBox<>(); cbType.getItems().addAll("Physical", "Digital", "Database", "Network"); cbType.getSelectionModel().select(0);
        TextField txtLocation = new TextField(); txtLocation.setPromptText("Location / AWS / Server Room");
        ComboBox<String> cbStatus = new ComboBox<>(); cbStatus.getItems().addAll("Available", "Maintenance", "Restricted"); cbStatus.getSelectionModel().select(0);

        grid.add(new Label("Name:"), 0, 0); grid.add(txtName, 1, 0);
        grid.add(new Label("Type:"), 2, 0); grid.add(cbType, 3, 0);
        grid.add(new Label("Location:"), 0, 1); grid.add(txtLocation, 1, 1);
        grid.add(new Label("Status:"), 2, 1); grid.add(cbStatus, 3, 1);

        Button btnSave = new Button("💾 Save Resource");
        btnSave.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSave.setOnAction(e -> {
            String name = txtName.getText().trim();
            String type = cbType.getValue();
            String loc = txtLocation.getText().trim();
            String status = cbStatus.getValue();

            StringBuilder errors = new StringBuilder();

            // 1. Empty Field Validation Check
            if (name.isEmpty() || loc.isEmpty()) {
                errors.append("• Resource Name and Location are required!\n");
            }

            // 2. Resource Name Validation (Minimum 4 characters, no random 1-2 letter junk)
            if (!name.isEmpty() && !name.matches("^[a-zA-Z0-9\\s\\-_&]{4,50}$")) {
                errors.append("• Resource Name must be at least 4 valid characters long! (e.g., Server Room B3)\n");
            }

            // 3. Location Validation (Minimum 4 characters)
            if (!loc.isEmpty() && !loc.matches("^[a-zA-Z0-9\\s\\-_/]{4,50}$")) {
                errors.append("• Location must be at least 4 valid characters long! (e.g., HQ Basement or AWS East)\n");
            }

            // IF VALIDATION FAILS: BLOCK SUBMISSION & DISPLAY ERROR ALERT
            if (errors.length() > 0) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Resource Validation Error");
                a.setHeaderText("Invalid Resource Data Blocked!");
                a.setContentText(errors.toString());
                a.showAndWait();
                return; // Stop Database Insertion
            }

            if (saveResourceToDB(name, type, loc, status)) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Resource Registered Successfully!");
                a.show();
                txtName.clear(); txtLocation.clear();
                loadResourceData(); // Live table refresh
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to save resource to Database!");
                a.show();
            }
        });

        formCard.getChildren().addAll(formTitle, grid, btnSave);

        // Bottom Table View (Shows All Resources & Registration Timestamp)
        TableColumn<String[], String> colId = new TableColumn<>("ID");
        TableColumn<String[], String> colName = new TableColumn<>("Resource Name");
        TableColumn<String[], String> colType = new TableColumn<>("Type");
        TableColumn<String[], String> colLoc = new TableColumn<>("Location");
        TableColumn<String[], String> colStatus = new TableColumn<>("Status");
        TableColumn<String[], String> colDate = new TableColumn<>("Registered Date & Time");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colType.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colLoc.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        colDate.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));

        table.getColumns().addAll(colId, colName, colType, colLoc, colStatus, colDate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadResourceData();

        Button btnBack = new Button("← Back to Admin Console");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        mainContent.getChildren().addAll(headerTitle, formCard, table, btnBack);
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS Admin - Resource Management");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadResourceData() {
        table.getItems().clear();
        String sql = "SELECT * FROM resources ORDER BY resource_id DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("resource_id"));
                String name = rs.getString("resource_name");
                String type = rs.getString("type") != null ? rs.getString("type") : "N/A";
                String loc = rs.getString("location") != null ? rs.getString("location") : "N/A";
                String status = rs.getString("status") != null ? rs.getString("status") : "Available";
                
                String date = "N/A";
                try {
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        date = ts.toString();
                    }
                } catch (Exception e) {
                    date = "Recent";
                }

                table.getItems().add(new String[]{id, name, type, loc, status, date});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Schema-Safe Save Method (Uses exact columns available in your MySQL DB)
    private boolean saveResourceToDB(String name, String type, String location, String status) {
        String sql = "INSERT INTO resources (resource_name, type, location, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setString(3, location);
            pstmt.setString(4, status);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
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