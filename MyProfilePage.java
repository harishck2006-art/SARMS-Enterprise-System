import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MyProfilePage {

    private Stage stage;
    private Employee currentUser;

    public MyProfilePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        BorderPane root = new BorderPane();
        String role = (currentUser != null && currentUser.getRole() != null) ? currentUser.getRole() : "User";

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
        Button btnProfile = createNavButton("👤 My Profile", true);

        btnHome.setOnAction(e -> navigateBack());
        navMenu.getChildren().addAll(btnHome, btnProfile);

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

        // Main Center Area - Profile Card
        VBox main = new VBox(20);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: #f4f6f9;");

        Label title = new Label("👤 Profile & Compliance Overview");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #0d1b2a;");

        VBox profileCard = new VBox(15);
        profileCard.setMaxWidth(550);
        profileCard.setStyle("-fx-background-color: white; -fx-padding: 25; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);");

        String fName = (currentUser != null && currentUser.getFirstName() != null) ? currentUser.getFirstName() : "Manager";
        String lName = (currentUser != null && currentUser.getLastName() != null) ? currentUser.getLastName() : "User";
        String uname = (currentUser != null && currentUser.getUsername() != null) ? currentUser.getUsername() : "manager1";
        int empId = (currentUser != null && currentUser.getEmpId() > 0) ? currentUser.getEmpId() : 2;

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);

        grid.add(createLabel("Employee ID:", true), 0, 0);
        grid.add(createLabel("EMP-" + empId, false), 1, 0);

        grid.add(createLabel("Full Name:", true), 0, 1);
        grid.add(createLabel(fName + " " + lName, false), 1, 1);

        grid.add(createLabel("Username:", true), 0, 2);
        grid.add(createLabel(uname, false), 1, 2);

        grid.add(createLabel("Assigned Role:", true), 0, 3);
        grid.add(createLabel(role, false), 1, 3);

        grid.add(createLabel("Security Clearance:", true), 0, 4);
        grid.add(createLabel("Level 3 (Manager Privileges)", false), 1, 4);

        grid.add(createLabel("MFA Status:", true), 0, 5);
        grid.add(createLabel("✓ Enabled & Verified", false), 1, 5);

        profileCard.getChildren().addAll(new Label("User Identity Details"), new Separator(), grid);

        Button backBtn = new Button("← Back to " + role + " Dashboard");
        backBtn.setStyle("-fx-background-color: #0d1b2a; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 8; -fx-cursor: hand;");
        backBtn.setOnAction(e -> navigateBack());

        main.getChildren().addAll(title, profileCard, backBtn);
        root.setCenter(main);

        Scene scene = new Scene(root, 1200, 720);
        stage.setScene(scene);
        stage.setTitle("SARMS - User Profile");
        stage.setMaximized(true);
        stage.show();
    }

    private Label createLabel(String text, boolean isHeader) {
        Label l = new Label(text);
        l.setStyle(isHeader ? 
            "-fx-font-weight: bold; -fx-text-fill: #475569; -fx-font-size: 13px;" : 
            "-fx-text-fill: #0d1b2a; -fx-font-size: 13px;");
        return l;
    }

    private void navigateBack() {
        if (currentUser != null && "Manager".equalsIgnoreCase(currentUser.getRole())) {
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