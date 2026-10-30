import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.*;

public class EmployeePage {

    private Stage stage;
    private Employee currentUser;
    private TableView<String[]> table = new TableView<>();

    public EmployeePage(Stage stage, Employee currentUser) {
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
        Button btnEmployees = createNavButton("👥 Manage Employees", true);
        Button btnResources = createNavButton("📦 Manage Resources", false);
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

        // 2. MAIN WORKSPACE (ADMIN USER MANAGEMENT)
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("👥 System User Management");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Action Buttons Row
        HBox topBar = new HBox(12);
        Button btnAddUser = new Button("➕ Add New User");
        btnAddUser.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnAddUser.setOnAction(e -> showAddUserModal());

        Button btnRefresh = new Button("🔄 Refresh Data");
        btnRefresh.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 18; -fx-background-radius: 8; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> loadEmployeeData());

        topBar.getChildren().addAll(btnAddUser, btnRefresh);

        // Table Setup
        TableColumn<String[], String> colId = new TableColumn<>("EMP ID");
        TableColumn<String[], String> colCode = new TableColumn<>("Code");
        TableColumn<String[], String> colName = new TableColumn<>("Full Name");
        TableColumn<String[], String> colEmail = new TableColumn<>("Email");
        TableColumn<String[], String> colUsername = new TableColumn<>("Username");
        TableColumn<String[], String> colRole = new TableColumn<>("Role ID");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colCode.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colEmail.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colUsername.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));
        colRole.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[5]));

        table.getColumns().addAll(colId, colCode, colName, colEmail, colUsername, colRole);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadEmployeeData();

        Button btnBack = new Button("← Back to Admin Console");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        mainContent.getChildren().addAll(headerTitle, topBar, table, btnBack);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS Admin - Manage Employees");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadEmployeeData() {
        table.getItems().clear();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("emp_id"));
                String code = rs.getString("emp_code") != null ? rs.getString("emp_code") : "N/A";
                String name = rs.getString("first_name") != null ? rs.getString("first_name") : "";
                String email = rs.getString("email") != null ? rs.getString("email") : "N/A";
                String username = rs.getString("username");
                String roleId = String.valueOf(rs.getInt("role_id"));

                table.getItems().add(new String[]{id, code, name, email, username, roleId});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Modal to Add New Employee WITH STRICT INPUT VALIDATION RULES
    private void showAddUserModal() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Create New User Account");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));

        Label lblTitle = new Label("➕ Create Employee Account");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField txtCode = new TextField(); txtCode.setPromptText("EMP Code (e.g. EMP011)");
        TextField txtName = new TextField(); txtName.setPromptText("First Name");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email Address");
        TextField txtUsername = new TextField(); txtUsername.setPromptText("Username");
        PasswordField txtPassword = new PasswordField(); txtPassword.setPromptText("Password");
        
        ComboBox<String> cbRole = new ComboBox<>();
        populateRolesComboBox(cbRole); // Dynamically loads all active roles directly from Database

        Button btnSubmit = new Button("Create User");
        btnSubmit.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String uname = txtUsername.getText().trim();
            String pass = txtPassword.getText().trim();
            
            StringBuilder errors = new StringBuilder();

            // 1. Check for empty fields
            if (code.isEmpty() || name.isEmpty() || email.isEmpty() || uname.isEmpty() || pass.isEmpty()) {
                errors.append("• All fields (Code, Name, Email, Username, Password) are mandatory!\n");
            }

            // 2. EMP Code validation: Must start with EMP followed by digits
            if (!code.isEmpty() && !code.matches("^EMP_?[A-Za-z0-9]+$")) {
                errors.append("• EMP Code format is invalid! (e.g., EMP011 or EMP_SOC_01)\n");
            }

            // 3. First Name validation: Alphabets only
            if (!name.isEmpty() && !name.matches("^[a-zA-Z\\s]{2,30}$")) {
                errors.append("• First Name must contain letters only (minimum 2 characters)!\n");
            }

            // 4. Strict Email validation regex check
            if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                errors.append("• Invalid Email address! (e.g., user@sarms.com)\n");
            }

            // 5. Username length check
            if (!uname.isEmpty() && uname.length() < 3) {
                errors.append("• Username must be at least 3 characters long!\n");
            }

            // 6. Password complexity/length check
            if (!pass.isEmpty() && pass.length() < 4) {
                errors.append("• Password must be at least 4 characters long!\n");
            }

            // 7. Role selection check
            if (cbRole.getValue() == null) {
                errors.append("• Please select a valid Role from the dropdown!\n");
            }

            // IF VALIDATION FAILS: DISPLAY ERROR DIALOG AND BLOCK SUBMISSION
            if (errors.length() > 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Invalid Form Submission!");
                alert.setContentText(errors.toString());
                alert.showAndWait();
                return; // Stop Database Insertion
            }

            int roleId = Integer.parseInt(cbRole.getValue().split(" - ")[0]);

            String sql = "INSERT INTO employees (emp_code, first_name, email, username, password, role_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, code);
                pstmt.setString(2, name);
                pstmt.setString(3, email);
                pstmt.setString(4, uname);
                pstmt.setString(5, pass);
                pstmt.setInt(6, roleId);

                if (pstmt.executeUpdate() > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "User created successfully!");
                    a.setHeaderText(null);
                    a.showAndWait();
                    modal.close();
                    loadEmployeeData();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to create user in DB: " + ex.getMessage());
                a.show();
            }
        });

        layout.getChildren().addAll(
            lblTitle, 
            new Label("Emp Code:"), txtCode,
            new Label("First Name:"), txtName,
            new Label("Email:"), txtEmail,
            new Label("Username:"), txtUsername,
            new Label("Password:"), txtPassword,
            new Label("Role:"), cbRole,
            btnSubmit
        );

        Scene scene = new Scene(layout, 380, 520);
        modal.setScene(scene);
        modal.showAndWait();
    }

    // Dynamic Helper Method: Fetches Roles from DB Table
    private void populateRolesComboBox(ComboBox<String> cbRole) {
        String sql = "SELECT * FROM roles";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int rId = rs.getInt("role_id");
                String rName = rs.getString("role_name");
                cbRole.getItems().add(rId + " - " + rName);
            }
            if (!cbRole.getItems().isEmpty()) {
                cbRole.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            // Fallback if DB fetch encounters an issue
            cbRole.getItems().addAll("1 - Admin", "2 - Manager", "3 - Security Officer", "4 - Employee", "5 - Auditor");
            cbRole.getSelectionModel().select(0);
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