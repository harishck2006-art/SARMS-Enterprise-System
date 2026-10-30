import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class AdminSecurityUnlockPage {

    private Stage stage;
    private Employee currentUser;
    private TableView<String[]> table = new TableView<>();

    public AdminSecurityUnlockPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();

        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🛡️ Admin Master Override - Security Officer Account Unlocks");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        Label subtitle = new Label("Only Super Admin can reset locked Security Officer (SOC) accounts.");
        subtitle.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        // Table Setup
        TableColumn<String[], String> colId = new TableColumn<>("EMP ID");
        TableColumn<String[], String> colCode = new TableColumn<>("EMP Code");
        TableColumn<String[], String> colName = new TableColumn<>("Officer Name");
        TableColumn<String[], String> colAttempts = new TableColumn<>("Failed Attempts");
        TableColumn<String[], String> colStatus = new TableColumn<>("Lock Status");

        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[0]));
        colCode.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[1]));
        colName.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[2]));
        colAttempts.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[3]));
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue()[4]));

        table.getColumns().addAll(colId, colCode, colName, colAttempts, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadLockedSecurityOfficers();

        Button btnUnlockSelected = new Button("🔓 Master Unlock Security Officer Account");
        btnUnlockSelected.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");

        btnUnlockSelected.setOnAction(e -> {
            String[] selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert a = new Alert(Alert.AlertType.WARNING, "Select a Security Officer account to unlock!");
                a.show();
                return;
            }

            int empId = Integer.parseInt(selected[0]);
            boolean ok = unlockSecurityOfficer(empId);

            if (ok) {
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Security Officer Account ID " + empId + " Unlocked Successfully!");
                a.show();
                loadLockedSecurityOfficers();
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR, "Failed to unlock Security Officer account in Database!");
                a.show();
            }
        });

        Button btnBack = new Button("← Back to Admin Console");
        btnBack.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 6; -fx-cursor: hand;");
        btnBack.setOnAction(e -> new AdminDashboard(stage, currentUser).show());

        HBox btnBox = new HBox(15, btnUnlockSelected, btnBack);

        main.getChildren().addAll(title, subtitle, table, btnBox);
        root.setCenter(main);

        Scene scene = new Scene(root, 1100, 680);
        stage.setScene(scene);
        stage.setTitle("SARMS Admin - Security Officer Lockout Management");
        stage.setMaximized(true);
        stage.show();
    }

    private void loadLockedSecurityOfficers() {
        table.getItems().clear();
        // Schema-Exact Query: Fetches locked or warned officers directly by emp_id and status attributes
        String sql = "SELECT * FROM employees WHERE is_locked = 1 OR failed_attempts > 0";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("emp_id"));
                String code = rs.getString("emp_code");
                if (code == null) code = "EMP-" + id;
                
                String name = rs.getString("first_name");
                if (name == null || name.isEmpty()) {
                    name = rs.getString("username");
                }
                
                String attempts = String.valueOf(rs.getInt("failed_attempts"));
                boolean locked = rs.getBoolean("is_locked");

                table.getItems().add(new String[]{
                    id, code, name, attempts, locked ? "🔒 LOCKED" : "⚠️ WARNING"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean unlockSecurityOfficer(int empId) {
        String sql = "UPDATE employees SET is_locked = 0, failed_attempts = 0 WHERE emp_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empId);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}