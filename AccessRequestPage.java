import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class AccessRequestPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<AccessRequestModel> requestsTable = new TableView<>();
    private AccessRequestDAO dao = new AccessRequestDAO();

    public AccessRequestPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";
        boolean isPrivilegedUser = "Manager".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role);

        // Left Sidebar Navigation Panel
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(role.toUpperCase() + " APPROVAL PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");

        Button btnHome = createNavButton("Main Portal", false);
        Button btnRequests = createNavButton(isPrivilegedUser ? "Access Approvals" : "My Access Requests", true);
        Button btnCreateNew = createNavButton("➕ Create New Request", false);

        btnHome.setOnAction(e -> navigateBack());
        btnRequests.setOnAction(e -> show());
        btnCreateNew.setOnAction(e -> showNewRequestForm(root));

        sidebar.getChildren().addAll(new VBox(2, brandTitle, brandSub), btnHome, btnRequests, btnCreateNew);
        root.setLeft(sidebar);

        // Center Work Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label(isPrivilegedUser ? "Access Clearance Requests & Approvals" : "My Access Requests & Security Clearances");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox tableCard = new VBox(15);
        tableCard.setStyle("-fx-background-color: white; -fx-padding: 22; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 3);");

        Label tableTitle = new Label(isPrivilegedUser ? "Pending & Processed Request Decision Log" : "My Submitted Access Requests Log");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        setupRequestsTable();

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        HBox btnBox = new HBox(12);

        // Security Check: Show Approve/Reject ONLY for Manager/Admin
        if (isPrivilegedUser) {
            Button btnApprove = new Button("✓ Approve Access");
            btnApprove.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
            btnApprove.setOnAction(e -> handleAction("Approved", statusMsg));

            Button btnReject = new Button("✕ Reject Request");
            btnReject.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
            btnReject.setOnAction(e -> handleAction("Rejected", statusMsg));

            btnBox.getChildren().addAll(btnApprove, btnReject);
        }

        Button backBtn = new Button("Back to " + role + " Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        btnBox.getChildren().add(backBtn);

        tableCard.getChildren().addAll(tableTitle, requestsTable, statusMsg, btnBox);

        refreshData();

        mainContent.getChildren().addAll(headerTitle, tableCard);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Access Requests");
        stage.setMaximized(true);
        stage.show();
    }

    // Integrated Form to Create New Access Request with Strict Gibberish Validation
    private void showNewRequestForm(BorderPane root) {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("➕ Create New Security Access Request");
        headerTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        ComboBox<String> cbResource = new ComboBox<>();
        cbResource.getItems().addAll("Production Data Vault", "Financial Records DB", "Source Code Repository", "Admin Privilege Escalation");
        cbResource.getSelectionModel().select(0);

        TextArea txtPurpose = new TextArea();
        txtPurpose.setPromptText("Enter official business purpose (Minimum 12 characters and meaningful sentences required)...");
        txtPurpose.setPrefRowCount(3);

        Label lblMsg = new Label();
        lblMsg.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnSubmit = new Button("Submit Clearance Request");
        btnSubmit.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            String resource = cbResource.getValue();
            String purpose = txtPurpose.getText().trim();

            // Strict Purpose & Gibberish Validation
            if (purpose.length() < 12 || !purpose.contains(" ")) {
                lblMsg.setText("❌ Validation Error: Invalid/Gibberish reason! Please enter complete meaningful sentences (Min 12 characters).");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
                return;
            }

            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
            String sql = "INSERT INTO access_requests (emp_id, resource_name, reason, status) VALUES (?, ?, ?, 'Pending')";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, empId);
                pstmt.setString(2, resource);
                pstmt.setString(3, purpose);

                if (pstmt.executeUpdate() > 0) {
                    lblMsg.setText("✓ Access request for '" + resource + "' submitted successfully!");
                    lblMsg.setStyle("-fx-text-fill: #10b981; -fx-font-size: 12px; -fx-font-weight: bold;");
                    txtPurpose.clear();
                    refreshData();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                lblMsg.setText("❌ Database Submission Error!");
                lblMsg.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px; -fx-font-weight: bold;");
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        grid.add(new Label("Select Target Resource:"), 0, 0);
        grid.add(cbResource, 1, 0);
        grid.add(new Label("Business Purpose / Reason:"), 0, 1);
        grid.add(txtPurpose, 1, 1);

        formCard.getChildren().addAll(grid, lblMsg, btnSubmit);

        Button btnBack = new Button("← Back to Request List");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> show());

        mainContent.getChildren().addAll(headerTitle, formCard, btnBack);
        root.setCenter(mainContent);
    }

    private void refreshData() {
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";
        boolean isPrivilegedUser = "Manager".equalsIgnoreCase(role) || "Admin".equalsIgnoreCase(role);

        List<AccessRequestModel> list;

        // Privileged Users (Manager/Admin) view ALL requests. Standard Employees view ONLY THEIR OWN requests.
        if (isPrivilegedUser) {
            list = dao.getAllRequestsHistory();
        } else {
            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
            list = dao.getRequestsByEmpId(empId); 
        }

        if (list != null) {
            requestsTable.setItems(FXCollections.observableArrayList(list));
        }
    }

    private void handleAction(String newStatus, Label statusMsg) {
        AccessRequestModel selected = requestsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusMsg.setStyle("-fx-text-fill: #ef4444;");
            statusMsg.setText("⚠️ Please select a request row from table first!");
            return;
        }

        String currentStatus = selected.getStatus();

        // Strict Validation: Block modifications on already processed requests
        if ("Approved".equalsIgnoreCase(currentStatus) || "Rejected".equalsIgnoreCase(currentStatus)) {
            statusMsg.setStyle("-fx-text-fill: #f59e0b;");
            statusMsg.setText("⚠️ Request ID " + selected.getRequestId() + " is ALREADY " + currentStatus.toUpperCase() + "! Decision cannot be re-processed.");

            Alert alert = new Alert(Alert.AlertType.WARNING, "Request ID " + selected.getRequestId() + " has already been " + currentStatus.toLowerCase() + ".\nFurther decision modifications are blocked!");
            alert.setHeaderText("Action Blocked");
            alert.show();
            return;
        }

        boolean success = dao.updateStatus(selected.getRequestId(), newStatus);
        if (success) {
            if ("Approved".equalsIgnoreCase(newStatus)) {
                statusMsg.setStyle("-fx-text-fill: #10b981;");
                statusMsg.setText("✓ Request ID " + selected.getRequestId() + " has been APPROVED!");
            } else {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("✕ Request ID " + selected.getRequestId() + " has been REJECTED!");
            }
            refreshData(); // Live Table Reload
        } else {
            statusMsg.setStyle("-fx-text-fill: #ef4444;");
            statusMsg.setText("❌ Failed to update status in Database!");
        }
    }

    private void setupRequestsTable() {
        TableColumn<AccessRequestModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));

        TableColumn<AccessRequestModel, String> nameCol = new TableColumn<>("Applicant");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<AccessRequestModel, String> resCol = new TableColumn<>("Resource");
        resCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<AccessRequestModel, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<AccessRequestModel, String> reqDateCol = new TableColumn<>("Requested Date");
        reqDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

        TableColumn<AccessRequestModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<AccessRequestModel, String> procDateCol = new TableColumn<>("Decision Date");
        procDateCol.setCellValueFactory(new PropertyValueFactory<>("processedDate"));

        requestsTable.getColumns().setAll(idCol, nameCol, resCol, reasonCol, reqDateCol, statusCol, procDateCol);
        requestsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        requestsTable.setStyle("-fx-background-radius: 8; -fx-border-color: #e2e8f0;");
    }

    private void navigateBack() {
        if (currentUser != null && "Manager".equalsIgnoreCase(currentUser.getRole())) {
            new ManagerDashboard(stage, currentUser).show();
        } else if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            new AdminDashboard(stage, currentUser).show();
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