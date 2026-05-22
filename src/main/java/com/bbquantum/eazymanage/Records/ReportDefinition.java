package com.bbquantum.eazymanage.Records;

import com.bbquantum.eazymanage.Constants.ReportStatus;

import java.util.Set;

public record ReportDefinition(String id, String title,  String description,  String icon,
                               Set<String> roles, ReportStatus status, String phase,
                               String viewerFxml) {

    public boolean availableFor(String role) {
        return roles.contains(role);
    }
}
