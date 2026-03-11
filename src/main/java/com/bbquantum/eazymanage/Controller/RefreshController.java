package com.bbquantum.eazymanage.Controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class RefreshController {

    @FXML private Label pageTitleLabel;
    @FXML private Button refreshBtn;

    public RefreshController() {}

    private Runnable onRefresh;

    public void setTitle(String title) {
        pageTitleLabel.setText(title);
    }

    public void setOnRefresh(Runnable action) {
        this.onRefresh = action;
    }

    @FXML
    public void refreshDashboard() {
        onRefresh.run();
    }
}
