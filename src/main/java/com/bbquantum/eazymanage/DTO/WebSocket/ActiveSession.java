package com.bbquantum.eazymanage.DTO.WebSocket;

public class ActiveSession {

    private String staffId;
    private String displayName;
    /** One of "PRINCIPAL" / "ADMINISTRATOR" / "ACCOUNTANT" / "TEACHING_STAFF". */
    private String role;
    /** ISO 8601 timestamp when the session connected. */
    private String connectedAt;
    /** ISO 8601 timestamp of the last ping the server received. */
    private String lastSeenAt;

    public ActiveSession() {}

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(String connectedAt) {
        this.connectedAt = connectedAt;
    }

    public String getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(String lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
