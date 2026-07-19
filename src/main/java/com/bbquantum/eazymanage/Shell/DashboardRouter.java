package com.bbquantum.eazymanage.Shell;

import com.bbquantum.eazymanage.Controller.Dashboard.AccountantDashboardController;
import com.bbquantum.eazymanage.Controller.Dashboard.AdminDashboardController;
import com.bbquantum.eazymanage.Controller.Dashboard.PrincipalDashboardController;
import com.bbquantum.eazymanage.Controller.Dashboard.StaffDashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class DashboardRouter {

    private DashboardRouter() {}

    public static Parent load(String role, String staffId, String fullName,
                              String organizationName, Consumer<String> navigateToTab) {
        String fxml = switch (role) {
            case "PRINCIPAL" -> "/com/bbquantum/eazymanage/Shell/Dashboard/PrincipalDashboard.fxml";
            case "ADMINISTRATOR" -> "/com/bbquantum/eazymanage/Shell/Dashboard/AdminDashboard.fxml";
            case "ACCOUNTANT" -> "/com/bbquantum/eazymanage/Shell/Dashboard/AccountantDashboard.fxml";
            case "TEACHING_STAFF" -> "/com/bbquantum/eazymanage/Shell/Dashboard/StaffDashboard.fxml";
            default -> null;
        };

        if (fxml == null) return fallback(role, fullName);

        try {
            FXMLLoader loader = new FXMLLoader(DashboardRouter.class.getResource(fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            // Each role's controller exposes a similarly-shaped attach method.
            if (controller instanceof PrincipalDashboardController c)
                c.attach(staffId, fullName, organizationName);

            if (controller instanceof AdminDashboardController c)
                c.attach(staffId, fullName, organizationName);

            if (controller instanceof AccountantDashboardController c)
                c.attach(staffId, fullName, organizationName);

            if (controller instanceof StaffDashboardController c)
                c.attach(staffId, fullName, organizationName);
            return root;
        } catch (Exception ex) {
            ex.printStackTrace();
            return fallback(role, fullName);
        }
    }

    private static Parent fallback(String role, String fullName) {
        VBox box = new VBox(8);
        box.setStyle("-fx-padding: 40; -fx-alignment: center;");
        Label t = new Label("Welcome" + (fullName == null ? "" : ", " + fullName));
        t.getStyleClass().add("page-title");
        Label d = new Label("No dashboard configured for role " + role + ".");
        d.getStyleClass().add("page-subtitle");
        box.getChildren().addAll(t, d);
        return box;
    }
}
