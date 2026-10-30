import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddEmployeePage {

    private Stage stage;
    private Employee currentUser;

    public AddEmployeePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        Label title = new Label("Add New Employee");

        TextField codeField = new TextField();
        codeField.setPromptText("Employee Code");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Admin", "Manager", "SecurityOfficer", "Employee");
        roleBox.setValue("Employee");

        Button saveBtn = new Button("Save Employee");
        Button backBtn = new Button("Back");

        Label msg = new Label();

        saveBtn.setOnAction(e -> {
            if (codeField.getText().isEmpty() || firstNameField.getText().isEmpty() || 
                usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                msg.setText("Please fill required fields");
                return;
            }

            int roleId = 4;
            String role = roleBox.getValue();
            if (role.equals("Admin")) roleId = 1;
            else if (role.equals("Manager")) roleId = 2;
            else if (role.equals("SecurityOfficer")) roleId = 3;

            String sql = "INSERT INTO employee (emp_code, first_name, last_name, email, phone, username, password, dept_id, role_id, joining_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, CURDATE(), 'Active')";

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, codeField.getText());
                pst.setString(2, firstNameField.getText());
                pst.setString(3, lastNameField.getText());
                pst.setString(4, emailField.getText());
                pst.setString(5, phoneField.getText());
                pst.setString(6, usernameField.getText());
                pst.setString(7, passwordField.getText());
                pst.setInt(8, roleId);

                if (pst.executeUpdate() > 0) {
                    msg.setText("Employee Added Successfully!");
                    codeField.clear();
                    firstNameField.clear();
                    lastNameField.clear();
                    emailField.clear();
                    phoneField.clear();
                    usernameField.clear();
                    passwordField.clear();
                } else {
                    msg.setText("Failed to add employee");
                }
            } catch (Exception ex) {
                msg.setText("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        backBtn.setOnAction(e -> {
            EmployeePage page = new EmployeePage(stage, currentUser);
            page.show();
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Emp Code:"), 0, 0);
        grid.add(codeField, 1, 0);
        grid.add(new Label("First Name:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Last Name:"), 0, 2);
        grid.add(lastNameField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);
        grid.add(new Label("Username:"), 0, 5);
        grid.add(usernameField, 1, 5);
        grid.add(new Label("Password:"), 0, 6);
        grid.add(passwordField, 1, 6);
        grid.add(new Label("Role:"), 0, 7);
        grid.add(roleBox, 1, 7);

        VBox root = new VBox(20, title, grid, saveBtn, backBtn, msg);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 500, 550);
        stage.setTitle("Add Employee");
        stage.setScene(scene);
        stage.show();
    }
}