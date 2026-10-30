import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ReportIncidentPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<IncidentModel> table = new TableView<>();

    public ReportIncidentPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🚨 Report & Track Security Incidents");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        // Form Card
        VBox formCard = new VBox(12);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Suspicious Login Attempt", "Lost Hardware / Key", "Unauthorized Access", "Phishing Attempt");
        typeCombo.setValue("Suspicious Login Attempt");

        TextField descFld = new TextField();
        descFld.setPromptText("Enter brief description of security issue...");
        HBox.setHgrow(descFld, Priority.ALWAYS);

        Button btnSubmit = new Button("Submit Incident Report");
        btnSubmit.setStyle("-fx-background-color: #e63946; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            if (descFld.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please enter incident description!");
                a.show();
                return;
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Incident report logged successfully and dispatched to SOC / Admin!");
            a.show();
            descFld.clear();
        });

        HBox formRow = new HBox(10, new Label("Category:"), typeCombo, descFld, btnSubmit);
        formCard.getChildren().addAll(new Label("Report Security Threat / Anomaly"), formRow);

        // Incident History Table
        Label tableTitle = new Label("📜 Submitted Security Incidents History");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TableColumn<IncidentModel, String> colType = new TableColumn<>("Threat Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<IncidentModel, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));

        TableColumn<IncidentModel, String> colDate = new TableColumn<>("Reported Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<IncidentModel, String> colStatus = new TableColumn<>("SOC Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().setAll(colType, colDesc, colDate, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button backBtn = new Button("← Back to Portal");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        main.getChildren().addAll(title, formCard, tableTitle, table, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Incident Escalation");
        stage.setMaximized(true);
        stage.show();
    }

    public static class IncidentModel {
        private String type, desc, date, status;
        public IncidentModel(String type, String desc, String date, String status) {
            this.type = type; this.desc = desc; this.date = date; this.status = status;
        }
        public String getType() { return type; }
        public String getDesc() { return desc; }
        public String getDate() { return date; }
        public String getStatus() { return status; }
    }
}