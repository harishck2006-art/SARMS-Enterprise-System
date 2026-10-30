import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LeaveApprovalPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<LeaveModel> table = new TableView<>();

    public LeaveApprovalPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("📥 Leave Applications Approval Center");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableColumn<LeaveModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("leaveId"));

        TableColumn<LeaveModel, String> nameCol = new TableColumn<>("Applicant Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<LeaveModel, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("leaveType"));

        TableColumn<LeaveModel, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveModel, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveModel, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        // Status Column Added for Visual Verification
        TableColumn<LeaveModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().setAll(idCol, nameCol, typeCol, startCol, endCol, reasonCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshTableData();

        // Approval Action Controls
        HBox actionBox = new HBox(12);
        TextField remarkFld = new TextField();
        remarkFld.setPromptText("Enter Approval/Rejection remarks for Applicant...");
        remarkFld.setPrefWidth(350);

        Button btnApprove = new Button("✓ Approve");
        btnApprove.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnApprove.setOnAction(e -> handleAction("Approved", remarkFld.getText()));

        Button btnReject = new Button("✕ Reject");
        btnReject.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");
        btnReject.setOnAction(e -> handleAction("Rejected", remarkFld.getText()));

        actionBox.getChildren().addAll(remarkFld, btnApprove, btnReject);

        Button backBtn = new Button("← Back to Workspace");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            if ("Manager".equalsIgnoreCase(currentUser.getRole())) {
                new ManagerDashboard(stage, currentUser).show();
            } else {
                new AdminDashboard(stage, currentUser).show();
            }
        });

        main.getChildren().addAll(title, table, actionBox, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Leave Approvals Center");
        stage.setMaximized(true);
        stage.show();
    }

    private void refreshTableData() {
        LeaveDAO dao = new LeaveDAO();
        table.setItems(FXCollections.observableArrayList(dao.getAllPendingLeaves()));
    }

    private void handleAction(String targetStatus, String remarks) {
        LeaveModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a leave request row from the table first!");
            alert.show();
            return;
        }

        // 1. Double Approval / Double Rejection Protection Check
        if (selected.getStatus() != null && selected.getStatus().equalsIgnoreCase(targetStatus)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Action Blocked");
            alert.setHeaderText(null);
            alert.setContentText("This leave request is ALREADY " + targetStatus.toUpperCase() + "!");
            alert.showAndWait();
            return;
        }

        // 2. Perform DB Update
        LeaveDAO dao = new LeaveDAO();
        boolean success = dao.updateLeaveStatus(selected.getLeaveId(), targetStatus, remarks);
        if (success) {
            refreshTableData();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Leave " + targetStatus + " successfully! Notification sent to applicant.");
            alert.show();
        }
    }
}