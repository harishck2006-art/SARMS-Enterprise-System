import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CompanyPolicyPage {

    private Stage stage;
    private Employee currentUser;

    public CompanyPolicyPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("📑 Official Enterprise Security & Compliance Policy");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        TextArea policyBox = new TextArea();
        policyBox.setEditable(false);
        policyBox.setPrefRowCount(15);
        policyBox.setStyle("-fx-font-size: 13px; -fx-font-family: monospace;");
        policyBox.setText(
            "========================================================================\n" +
            "            SARMS ENTERPRISE ACCESS CONTROL POLICY GUIDELINES           \n" +
            "========================================================================\n\n" +
            "1. PRINCIPLE OF LEAST PRIVILEGE (PoLP):\n" +
            "   Employees are only granted minimum resource privileges required to perform\n" +
            "   their designated official job responsibilities.\n\n" +
            "2. ABSOLUTE ACCESS BLOCK DURING APPROVED LEAVE:\n" +
            "   System security policy automatically prohibits any access request submissions\n" +
            "   while an employee is on approved active leave status.\n\n" +
            "3. MANDATORY SESSION LOGGING & AUDIT TRAIL:\n" +
            "   All user login timestamps and system interactions are monitored and recorded\n" +
            "   in strict compliance with ISO/IEC 27001 audit controls.\n\n" +
            "4. CREDENTIAL SHARING PROHIBITION:\n" +
            "   Sharing login credentials, tokens, or active sessions is strictly forbidden\n" +
            "   and subject to immediate disciplinary escalation."
        );

        Button backBtn = new Button("← Back to Employee Home");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        main.getChildren().addAll(title, policyBox, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Company Policy");
        stage.setMaximized(true);
        stage.show();
    }
}