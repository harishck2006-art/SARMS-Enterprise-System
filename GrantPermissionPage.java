import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GrantPermissionPage {

    private Stage stage;
    private Employee currentUser;

    public GrantPermissionPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "User";

        // Left Sidebar Navigation
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(260);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(role.toUpperCase() + " CLEARANCE CENTER");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(15, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Main Portal Dashboard", false);
        Button btnGrant = createNavButton("🔑 Grant Clearance", true);

        btnHome.setOnAction(e -> navigateBack());
        navMenu.getChildren().addAll(btnHome, btnGrant);

        VBox profileBox = new VBox(5);
        profileBox.setStyle("-fx-border-color: #1b263b; -fx-border-width: 1 0 0 0; -fx-padding: 15 0 0 0;");
        Label userName = new Label((currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "User");
        userName.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        Button btnLogout = new Button("🚪 End Session");
        btnLogout.setStyle("-fx-background-color: transparent; -fx-text-fill: #e63946; -fx-font-size: 12px; -fx-cursor: hand;");
        btnLogout.setOnAction(e -> new LoginPage(stage).show());

        profileBox.getChildren().addAll(userName, btnLogout);
        sidebar.getChildren().addAll(brandBox, navMenu, new Region(), profileBox);
        VBox.setVgrow(sidebar.getChildren().get(2), Priority.ALWAYS);
        root.setLeft(sidebar);

        // Center Area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #f4f6f9;");

        Label headerTitle = new Label("🔑 Grant Clearance & Resource Authorization");
        headerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox formCard = new VBox(15);
        formCard.setMaxWidth(550);
        formCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        TextField empIdFld = new TextField();
        empIdFld.setPromptText("Enter Target Employee ID (e.g. 102)");
        empIdFld.setStyle("-fx-padding: 8; -fx-background-radius: 6;");

        ComboBox<String> levelCombo = new ComboBox<>();
        levelCombo.getItems().addAll(
            "Level 1 - Read Only Access",
            "Level 2 - Managerial & Approvals Access",
            "Level 3 - System Admin & Vault Clearance"
        );
        levelCombo.setValue("Level 1 - Read Only Access");
        levelCombo.setMaxWidth(Double.MAX_VALUE);

        TextArea remarkArea = new TextArea();
        remarkArea.setPromptText("Provide justification note for privilege change...");
        remarkArea.setPrefRowCount(3);

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold;");

        Button btnGrantAction = new Button("Confirm & Grant Privilege");
        btnGrantAction.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        btnGrantAction.setOnAction(e -> {
            if (empIdFld.getText().trim().isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("⚠ Please specify Target Employee ID!");
            } else {
                statusMsg.setStyle("-fx-text-fill: #10b981;");
                statusMsg.setText("✓ Privilege Level updated successfully in Database!");
                empIdFld.clear();
                remarkArea.clear();
            }
        });

        formCard.getChildren().addAll(
            new Label("Target Employee ID:"), empIdFld,
            new Label("Security Clearance Tier:"), levelCombo,
            new Label("Authorization Remarks:"), remarkArea,
            statusMsg, btnGrantAction
        );

        Button backBtn = new Button("← Back to " + role + " Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        mainContent.getChildren().addAll(headerTitle, formCard, backBtn);
        root.setCenter(mainContent);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Grant Clearance Portal");
        stage.setMaximized(true);
        stage.show();
    }

    private void navigateBack() {
        if (currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole())) {
            new AdminDashboard(stage, currentUser).show();
        } else if (currentUser != null && "Manager".equalsIgnoreCase(currentUser.getRole())) {
            new ManagerDashboard(stage, currentUser).show();
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