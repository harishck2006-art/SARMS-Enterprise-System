import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

public class GeneratePermissionPage {

    private Stage stage;
    private Employee currentUser;
    private AccessRequest request;

    public GeneratePermissionPage(Stage stage, Employee currentUser, AccessRequest request) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.request = request;
    }

    public void show() {
        Label title = new Label("Generate Permission");

        Label empLabel = new Label("Employee: " + request.getEmpName());
        Label resLabel = new Label("Resource: " + request.getResourceName());

        DatePicker startDate = new DatePicker(LocalDate.now());
        DatePicker expiryDate = new DatePicker(LocalDate.now().plusMonths(1));

        Button generateBtn = new Button("Generate Permission");
        Button backBtn = new Button("Back");

        Label msg = new Label();

        generateBtn.setOnAction(e -> {
            if (startDate.getValue() == null || expiryDate.getValue() == null) {
                msg.setText("Please select dates");
                return;
            }

            Permission p = new Permission();
            p.setRequestId(request.getRequestId());
            p.setEmpId(request.getEmpId());
            p.setResourceId(request.getResourceId());
            p.setStartDate(startDate.getValue().toString());
            p.setExpiryDate(expiryDate.getValue().toString());

            PermissionDAO dao = new PermissionDAO();
            if (dao.generatePermission(p)) {
                msg.setText("Permission Generated Successfully!");
            } else {
                msg.setText("Failed to generate permission");
            }
        });

        backBtn.setOnAction(e -> {
            AccessRequestPage page = new AccessRequestPage(stage, currentUser);
            page.show();
        });

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.add(new Label("Start Date:"), 0, 0);
        grid.add(startDate, 1, 0);
        grid.add(new Label("Expiry Date:"), 0, 1);
        grid.add(expiryDate, 1, 1);

        VBox root = new VBox(20, title, empLabel, resLabel, grid, generateBtn, backBtn, msg);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        Scene scene = new Scene(root, 500, 400);
        stage.setTitle("Generate Permission");
        stage.setScene(scene);
        stage.show();
    }
}