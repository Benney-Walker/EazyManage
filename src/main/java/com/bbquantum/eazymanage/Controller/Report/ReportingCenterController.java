package com.bbquantum.eazymanage.Controller.Report;

import com.bbquantum.eazymanage.Constants.ReportStatus;
import com.bbquantum.eazymanage.Records.ReportDefinition;
import com.bbquantum.eazymanage.Reports.ReportCatalogue;
import com.bbquantum.eazymanage.Reports.ReportViewerController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ReportingCenterController {

    @FXML private FlowPane reportCardGrid;
    @FXML private Label subtitleLabel;
    @FXML private Label    emptyStateLabel;

    private String staffId;
    private String role;
    private StackPane host;
    private Parent indexRoot;

    /** Captured during FXML load so we can return to it after viewing a report. */
    @FXML
    private void initialize() {

    }

    public void setContext(String staffId, String role, StackPane host, Parent indexRoot) {
        this.staffId   = staffId;
        this.role      = role;
        this.host      = host;
        this.indexRoot = indexRoot;
        subtitleLabel.setText("Choose a report to view. PDFs are generated on demand by the server.");
        renderCards();
    }

    /** Swap the index screen back into the host. Used by viewer back buttons. */
    public void goBackToIndex() {
        if (host == null || indexRoot == null) return;
        host.getChildren().setAll(indexRoot);
    }

    private void renderCards() {
        reportCardGrid.getChildren().clear();
        var visible = ReportCatalogue.reportsFor(role);
        if (visible.isEmpty()) {
            emptyStateLabel.setVisible(true);
            emptyStateLabel.setManaged(true);
            return;
        }
        emptyStateLabel.setVisible(false);
        emptyStateLabel.setManaged(false);

        for (ReportDefinition r : visible) {
            reportCardGrid.getChildren().add(buildCard(r));
        }
    }

    private VBox buildCard(ReportDefinition report) {
        Label icon = new Label(report.icon());
        icon.getStyleClass().add("report-card-icon");

        Label title = new Label(report.title());
        title.getStyleClass().add("report-card-title");

        Label body = new Label(report.description());
        body.getStyleClass().add("report-card-body");
        body.setWrapText(true);
        body.setMaxWidth(280);

        VBox card = new VBox(4, icon, title, body);
        card.getStyleClass().add("report-card");

        if (report.status() == ReportStatus.AVAILABLE) {
            card.setOnMouseClicked(e -> openViewer(report));
        } else {
            card.getStyleClass().add("report-card-disabled");
            Region spacer = new Region();
            spacer.setMinHeight(4);
            Label coming = new Label("Coming in " + report.phase());
            coming.getStyleClass().add("report-card-coming");
            card.getChildren().addAll(spacer, coming);
            card.setAlignment(Pos.TOP_LEFT);
        }
        return card;
    }

    private void openViewer(ReportDefinition report) {
        if (host == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(report.viewerFxml()));
            Parent viewer = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ReportViewerController rv) {
                rv.attach(staffId, role, this::goBackToIndex);
            }
            host.getChildren().setAll(viewer);
        } catch (Exception e) {
            e.printStackTrace();
            // Stay on the index; show a simple banner via the subtitle.
            subtitleLabel.setText("Could not open " + report.title() + ": " + safeMsg(e));
        }
    }

    private static String safeMsg(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
