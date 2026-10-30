import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NotificationsPage {

    private Stage stage;
    private Employee currentUser;

    public NotificationsPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🔔 Real-time Notifications & Approval Alerts");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox box1 = createNotificationTile("✓ Access Request Approved", "Your request for Financial Vault access was APPROVED by Manager.", "Today at 09:15 AM", "#dcfce7", "#16a34a");
        VBox box2 = createNotificationTile("📅 Leave Application Received", "Your Leave Request from Oct 12 to Oct 15 is pending review.", "Yesterday at 04:30 PM", "#fef3c7", "#d97706");
        VBox box3 = createNotificationTile("🔐 Password Policy Update", "Quarterly security compliance requires mandatory credential change.", "Oct 08, 2026", "#e0f2fe", "#0284c7");

        Button backBtn = new Button("← Back to Employee Home");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new EmployeeDashboard(stage, currentUser).show());

        main.getChildren().addAll(title, box1, box2, box3, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Notifications");
        stage.setMaximized(true);
        stage.show();
    }

    private VBox createNotificationTile(String heading, String message, String time, String bg, String fg) {
        VBox card = new VBox(6);
        card.setStyle("-fx-background-color: white; -fx-padding: 18; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        Label h = new Label(heading);
        h.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + fg + ";");

        Label m = new Label(message);
        m.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");

        Label t = new Label(time);
        t.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        card.getChildren().addAll(h, m, t);
        return card;
    }
}