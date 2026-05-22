package com.bbquantum.eazymanage.Reports;

public interface ReportViewerController {

    void attach(String staffId, String role, Runnable onBack);
}
