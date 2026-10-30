import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class ApplyLeavePage {

    private Stage stage;
    private Employee currentUser;

    public ApplyLeavePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        // Top Header Section
        VBox headerBox = new VBox(5);
        headerBox.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");
        Label title = new Label("📅 Apply Leave & Track Approval Status");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        headerBox.getChildren().add(title);
        root.setTop(headerBox);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(25));
        contentBox.setStyle("-fx-background-color: #f4f6f9;");

        // Form Card Layout
        VBox formCard = new VBox(12);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label formTitle = new Label("New Leave Request Form (Approver: Manager/Admin)");
        formTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        HBox formInputs = new HBox(12);
        formInputs.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList("Casual Leave", "Medical Leave", "Sick Leave"));
        cbType.setValue("Casual Leave");

        DatePicker dpStart = new DatePicker(LocalDate.now());
        DatePicker dpEnd = new DatePicker(LocalDate.now());

        // Strictly Disable Past Dates in DatePicker UI
        dpStart.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        dpEnd.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        TextField txtReason = new TextField();
        txtReason.setPromptText("Enter reason for leave (Min 5 chars)...");
        txtReason.setPrefWidth(220);

        Button btnSubmit = new Button("Submit Application");
        btnSubmit.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        formInputs.getChildren().addAll(
            new Label("Type:"), cbType,
            new Label("Start:"), dpStart,
            new Label("End:"), dpEnd,
            txtReason, btnSubmit
        );

        formCard.getChildren().addAll(formTitle, formInputs);

        // Leave Table View Section
        VBox tableCard = new VBox(12);
        tableCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label tableTitle = new Label("🔔 My Leave Application History & Manager Remarks");
        tableTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableView<LeaveModel> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LeaveModel, String> colType = new TableColumn<>("Leave Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("leaveType"));

        TableColumn<LeaveModel, String> colStart = new TableColumn<>("Start Date");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));

        TableColumn<LeaveModel, String> colEnd = new TableColumn<>("End Date");
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));

        TableColumn<LeaveModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<LeaveModel, String> colRemarks = new TableColumn<>("Manager Remarks / Feedback");
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("reason"));

        table.getColumns().addAll(colType, colStart, colEnd, colStatus, colRemarks);

        Runnable refreshTable = () -> {
            try {
                LeaveDAO dao = new LeaveDAO();
                int empId = (currentUser != null) ? currentUser.getEmpId() : 1;
                table.setItems(FXCollections.observableArrayList(dao.getPersonalLeaves(empId)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        refreshTable.run();

        // Application Submission Handler with Strict Validation Checks
        btnSubmit.setOnAction(e -> {
            LocalDate start = dpStart.getValue();
            LocalDate end = dpEnd.getValue();
            String reason = txtReason.getText().trim();

            if (start == null || end == null) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Please select valid Start and End dates!");
                a.show();
                return;
            }

            if (start.isBefore(LocalDate.now())) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Cannot apply leave for past dates! Please select Today or a Future Date.");
                a.show();
                return;
            }

            if (end.isBefore(start)) {
                Alert a = new Alert(Alert.AlertType.ERROR, "End Date cannot be earlier than Start Date!");
                a.show();
                return;
            }

            if (reason.length() < 5) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Please provide a valid leave reason (Minimum 5 characters required)!");
                a.show();
                return;
            }

            int empId = (currentUser != null) ? currentUser.getEmpId() : 1;

            try {
                LeaveDAO dao = new LeaveDAO();
                boolean success = dao.submitLeave(empId, cbType.getValue(), start.toString(), end.toString(), reason);
                
                if (success) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Leave Application Submitted Successfully!");
                    a.show();
                    txtReason.clear();
                    refreshTable.run();
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR, "Failed to submit leave to database!");
                    a.show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        tableCard.getChildren().addAll(tableTitle, table);

        // Safe Navigation back based on role
        Button btnBack = new Button("← Back to Portal");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> navigateBack());

        contentBox.getChildren().addAll(formCard, tableCard, btnBack);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 1100, 680);
        stage.setScene(scene);
        stage.setTitle("SARMS - Leave Portal");
        stage.show();
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
}