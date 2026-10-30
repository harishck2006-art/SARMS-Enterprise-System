import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ResourcePage {

    private Stage stage;
    private Employee currentUser;

    public ResourcePage(Stage stage, Employee currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        TableView<Resource> table = new TableView<>();

        TableColumn<Resource, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("resourceId"));

        TableColumn<Resource, String> nameCol = new TableColumn<>("Resource Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("resourceName"));

        TableColumn<Resource, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("resourceType"));

        TableColumn<Resource, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Resource, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(idCol, nameCol, typeCol, locCol, statusCol);
        table.setItems(FXCollections.observableArrayList(new ResourceDAO().getAllResources()));

        Button addBtn = new Button("Add New Resource");
        Button backBtn = new Button("Back to Dashboard");

        addBtn.setOnAction(e -> new AddResourcePage(stage, currentUser).show());

        backBtn.setOnAction(e -> {
            String role = currentUser.getRoleName();
            if (role.equalsIgnoreCase("Admin")) {
                new AdminDashboard(stage, currentUser).show();
            } else if (role.equalsIgnoreCase("Manager")) {
                new ManagerDashboard(stage, currentUser).show();
            } else if (role.equalsIgnoreCase("SecurityOfficer")) {
                new SecurityDashboard(stage, currentUser).show();
            } else {
                new EmployeeDashboard(stage, currentUser).show();
            }
        });

        // ===== ROLE BASED BUTTONS =====
        // Only Admin can Add New Resource. Others can only VIEW.
        HBox buttons = new HBox(15);
        String role = currentUser.getRoleName();

        if (role.equalsIgnoreCase("Admin")) {
            buttons.getChildren().addAll(addBtn, backBtn);
        } else {
            // Employee, Manager, SecurityOfficer → only Back button
            buttons.getChildren().add(backBtn);
        }

        VBox root = new VBox(15, new Label("Resource Management"), table, buttons);
        root.setPadding(new Insets(20));

        stage.setScene(new Scene(root, 900, 550));
        stage.setTitle("Resources");
        stage.show();
    }
}