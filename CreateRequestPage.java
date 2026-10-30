import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class CreateRequestPage {

    private Stage stage;
    private Employee currentUser;

    public CreateRequestPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        
        String userRole = "User";
        if (currentUser != null && currentUser.getRole() != null) {
            userRole = currentUser.getRole();
        }

        // Left Sidebar Navigation
        VBox sidebar = new VBox(12);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(userRole.toUpperCase() + " REQUEST PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(6);
        navMenu.setPadding(new Insets(10, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Main Workspace", false);
        Button btnNewReq = createNavButton("➕ New Access Request", true);

        btnHome.setOnAction(e -> navigateBack());
        btnNewReq.setOnAction(e -> new CreateRequestPage(stage, currentUser).show());

        navMenu.getChildren().addAll(btnHome, btnNewReq);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 12 0 0 0;");
        
        String firstName = "Officer";
        if (currentUser != null && currentUser.getFirstName() != null) {
            firstName = currentUser.getFirstName();
        }
        
        Label userName = new Label(firstName);
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        // Center Work Area
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("➕ Create New Security Access Request");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        ComboBox<String> cbResource = new ComboBox<>();
        // Fetch ALL registered resources from DB (without filtering status out)
        populateResourcesComboBox(cbResource);

        TextArea txtReason = new TextArea();
        txtReason.setPromptText("Enter purpose/reason for access request...");
        txtReason.setPrefRowCount(4);

        grid.add(new Label("Select Target Resource:"), 0, 0); grid.add(cbResource, 1, 0);
        grid.add(new Label("Business Purpose / Reason:"), 0, 1); grid.add(txtReason, 1, 1);

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnSubmit = new Button("Submit Clearance Request");
        btnSubmit.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            String resource = cbResource.getValue();
            String reason = txtReason.getText().trim();

            if (resource == null || resource.isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("⚠️ Please select a target resource!");
                return;
            }

            if (reason.isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("⚠️ Please provide a valid reason for access request!");
                return;
            }

            int empId = 1;
            if (currentUser != null) {
                empId = currentUser.getEmpId();
            }

            AccessRequestDAO dao = new AccessRequestDAO();
            boolean success = dao.createRequest(empId, resource, reason);

            if (success) {
                statusMsg.setStyle("-fx-text-fill: #10b981;");
                statusMsg.setText("✓ Access request for '" + resource + "' submitted successfully!");
                txtReason.clear();
            } else {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("❌ Failed to submit request in Database!");
            }
        });

        card.getChildren().addAll(grid, statusMsg, btnSubmit);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        main.getChildren().addAll(title, card, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - New Access Request");
        stage.setMaximized(true);
        stage.show();
    }

    // Fetches ALL resources regardless of status (Available, Restricted, etc.)
    private void populateResourcesComboBox(ComboBox<String> cbResource) {
        String sql = "SELECT resource_name FROM resources";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cbResource.getItems().add(rs.getString("resource_name"));
            }

            if (!cbResource.getItems().isEmpty()) {
                cbResource.getSelectionModel().select(0);
            } else {
                cbResource.setPromptText("No resources found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            cbResource.getItems().addAll("Server Room B3", "Finance API Gateway", "SAP HR & Payroll System", "Production Data Vault");
            cbResource.getSelectionModel().select(0);
        }
    }

    private void navigateBack() {
        if (currentUser != null && currentUser.getRole() != null) {
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