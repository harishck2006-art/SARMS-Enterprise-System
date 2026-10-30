import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RequestDevicePage {

    private Stage stage;
    private Employee currentUser;

    public RequestDevicePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🛠️ Request Hardware / Security Device Access");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox formCard = new VBox(15);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        ComboBox<String> deviceCombo = new ComboBox<>();
        deviceCombo.getItems().addAll("YubiKey 5 Series (Hardware Key)", "Encrypted Workstation Laptop", "VPN Physical Token", "Biometric SmartCard");
        deviceCombo.setValue("YubiKey 5 Series (Hardware Key)");

        TextArea reasonArea = new TextArea();
        reasonArea.setPromptText("Enter business justification for physical hardware/device access...");
        reasonArea.setPrefRowCount(4);

        Button btnSubmit = new Button("Submit Device Requisition");
        btnSubmit.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnSubmit.setOnAction(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Please enter business reason!");
                a.show();
                return;
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Device requisition request submitted to Manager for approval!");
            a.show();
            reasonArea.clear();
        });

        formCard.getChildren().addAll(new Label("Select Required Security Hardware:"), deviceCombo, new Label("Business Reason:"), reasonArea, btnSubmit);

        Button backBtn = new Button("← Back to Portal");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        main.getChildren().addAll(title, formCard, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Hardware Access");
        stage.setMaximized(true);
        stage.show();
    }
}