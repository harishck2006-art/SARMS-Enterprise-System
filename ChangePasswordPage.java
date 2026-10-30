import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChangePasswordPage {

    private Stage stage;
    private Employee currentUser;

    public ChangePasswordPage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "Employee";
        boolean isManager = "Manager".equalsIgnoreCase(role);
        boolean isAdmin = "Admin".equalsIgnoreCase(role);

        // Left Sidebar Navigation
        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #0d1b2a; -fx-padding: 20;");

        Label brandTitle = new Label("SARMS");
        brandTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
        Label brandSub = new Label(role.toUpperCase() + " PORTAL");
        brandSub.setStyle("-fx-text-fill: #8d99ae; -fx-font-size: 10px;");
        VBox brandBox = new VBox(2, brandTitle, brandSub);

        VBox navMenu = new VBox(8);
        navMenu.setPadding(new Insets(15, 0, 0, 0));

        Button btnHome = createNavButton("🏠 Home Dashboard", false);
        Button btnPassword = createNavButton("🔐 Change Password", true);

        // Dynamic Role-Aware Home Navigation
        btnHome.setOnAction(e -> navigateBack());
        navMenu.getChildren().addAll(btnHome, btnPassword);

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

        // Main Password Reset Card Section
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("🔐 Change Account Password");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox card = new VBox(15);
        card.setMaxWidth(480);
        card.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        PasswordField oldPassFld = new PasswordField();
        oldPassFld.setPromptText("Enter current password");

        PasswordField newPassFld = new PasswordField();
        newPassFld.setPromptText("Enter new password");

        PasswordField confirmPassFld = new PasswordField();
        confirmPassFld.setPromptText("Confirm new password");

        Label statusMsg = new Label();
        statusMsg.setStyle("-fx-font-weight: bold;");

        Button btnUpdate = new Button("Update Password");
        btnUpdate.setStyle("-fx-background-color: #3a86ff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");

        btnUpdate.setOnAction(e -> {
            String oldP = oldPassFld.getText().trim();
            String newP = newPassFld.getText().trim();
            String confirmP = confirmPassFld.getText().trim();

            if (oldP.isEmpty() || newP.isEmpty() || confirmP.isEmpty()) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("Please fill all password fields!");
                return;
            }

            if (!newP.equals(confirmP)) {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("New password & Confirm password do not match!");
                return;
            }

            int empId = (currentUser != null && currentUser.getEmpId() > 0) ? currentUser.getEmpId() : 1;
            EmployeeDAO dao = new EmployeeDAO();
            boolean success = dao.updatePassword(empId, oldP, newP);

            if (success) {
                statusMsg.setStyle("-fx-text-fill: #10b981;");
                statusMsg.setText("✓ Password updated successfully in Database!");
                oldPassFld.clear();
                newPassFld.clear();
                confirmPassFld.clear();
            } else {
                statusMsg.setStyle("-fx-text-fill: #ef4444;");
                statusMsg.setText("Current password incorrect or DB Error!");
            }
        });

        card.getChildren().addAll(
            new Label("Current Password:"), oldPassFld,
            new Label("New Password:"), newPassFld,
            new Label("Confirm New Password:"), confirmPassFld,
            statusMsg, btnUpdate
        );

        // Dynamic Back Button
        Button backBtn = new Button("← Back to " + role + " Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        main.getChildren().addAll(title, card, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - Change Password");
        stage.setMaximized(true);
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