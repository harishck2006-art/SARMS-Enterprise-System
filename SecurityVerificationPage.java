import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SecurityVerificationPage {

    private Stage stage;
    private Employee currentUser;

    public SecurityVerificationPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        Label title = new Label("EMPLOYEE BACKGROUND & ELIGIBILITY VERIFICATION");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e65100;");

        TableView<SecurityVerification> table = new TableView<>();

        TableColumn<SecurityVerification, Integer> empIdCol = new TableColumn<>("Emp ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));

        TableColumn<SecurityVerification, String> nameCol = new TableColumn<>("Employee Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("empName"));

        TableColumn<SecurityVerification, String> degreeCol = new TableColumn<>("Degree Check");
        degreeCol.setCellValueFactory(new PropertyValueFactory<>("degreeStatus"));

        TableColumn<SecurityVerification, String> bgCol = new TableColumn<>("Background Check");
        bgCol.setCellValueFactory(new PropertyValueFactory<>("backgroundStatus"));

        TableColumn<SecurityVerification, String> trainCol = new TableColumn<>("Training Status");
        trainCol.setCellValueFactory(new PropertyValueFactory<>("trainingStatus"));

        TableColumn<SecurityVerification, String> statusCol = new TableColumn<>("Eligibility");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("overallEligibility"));

        table.getColumns().addAll(empIdCol, nameCol, degreeCol, bgCol, trainCol, statusCol);

        SecurityVerificationDAO dao = new SecurityVerificationDAO();
        table.setItems(FXCollections.observableArrayList(dao.getAllVerifications()));

        Button btnApprove = new Button("Mark Verified & Eligible");
        Button btnReject = new Button("Mark Ineligible");
        Button btnBack = new Button("Back to Dashboard");

        btnApprove.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; -fx-font-weight: bold;");
        btnReject.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-font-weight: bold;");
        btnBack.setStyle("-fx-background-color: #37474f; -fx-text-fill: white;");

        btnApprove.setOnAction(e -> {
            SecurityVerification sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                dao.updateStatus(sel.getEmpId(), "overall_eligibility", "Eligible");
                dao.updateStatus(sel.getEmpId(), "background_status", "Cleared");
                table.setItems(FXCollections.observableArrayList(dao.getAllVerifications()));
                new Alert(Alert.AlertType.INFORMATION, "Employee verified & updated to Eligible!").showAndWait();
            } else {
                new Alert(Alert.AlertType.WARNING, "Select an employee first!").showAndWait();
            }
        });

        btnReject.setOnAction(e -> {
            SecurityVerification sel = table.getSelectionModel().getSelectedItem();
            if (sel != null) {
                dao.updateStatus(sel.getEmpId(), "overall_eligibility", "Rejected / Ineligible");
                table.setItems(FXCollections.observableArrayList(dao.getAllVerifications()));
                new Alert(Alert.AlertType.WARNING, "Employee marked as Ineligible!").showAndWait();
            } else {
                new Alert(Alert.AlertType.WARNING, "Select an employee first!").showAndWait();
            }
        });

        btnBack.setOnAction(e -> new SecurityDashboard(stage, currentUser).show());

        HBox actions = new HBox(12, btnApprove, btnReject, btnBack);
        VBox root = new VBox(15, title, table, actions);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 800, 500));
        stage.setTitle("Real-time Security Verification Portal");
        stage.show();
    }
}