module com.bbquantum.eazymanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;
    requires java.prefs;
    requires java.sql;
    requires spring.messaging;

    requires spring.websocket;
    requires spring.core;
    requires spring.context;
    requires com.fasterxml.jackson.databind;
    requires org.xerial.sqlitejdbc;

    exports com.bbquantum.eazymanage;
    exports com.bbquantum.eazymanage.CacheClasses;
    exports com.bbquantum.eazymanage.Controller;
    exports com.bbquantum.eazymanage.Shell;
    exports com.bbquantum.eazymanage.Shell.Navigation;
    exports com.bbquantum.eazymanage.Shell.Notification;

    exports com.bbquantum.eazymanage.Services;

    opens com.bbquantum.eazymanage to javafx.fxml;
    opens com.bbquantum.eazymanage.Controller to javafx.fxml;
    opens com.bbquantum.eazymanage.Shell to javafx.fxml;

    opens com.bbquantum.eazymanage.CacheClasses to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Academics;
    opens com.bbquantum.eazymanage.DTO.Academics to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Finance;
    opens com.bbquantum.eazymanage.DTO.Finance to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Institution;
    opens com.bbquantum.eazymanage.DTO.Institution to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Auth;
    opens com.bbquantum.eazymanage.DTO.Auth to com.google.gson;
    exports com.bbquantum.eazymanage.Controller.Report;
    opens com.bbquantum.eazymanage.Controller.Report to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.WebSocket;
    opens com.bbquantum.eazymanage.Controller.WebSocket to javafx.fxml;
    opens com.bbquantum.eazymanage.Shell.Notification to javafx.fxml;
    opens com.bbquantum.eazymanage.Shell.Navigation to javafx.fxml;
    opens com.bbquantum.eazymanage.Shell.Settings to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Settings;
    opens com.bbquantum.eazymanage.Controller.Settings to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Results;
    opens com.bbquantum.eazymanage.Controller.Results to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Students;
    opens com.bbquantum.eazymanage.Controller.Students to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Staff;
    opens com.bbquantum.eazymanage.Controller.Staff to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Academics;
    opens com.bbquantum.eazymanage.Controller.Academics to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.AuditLogs;
    opens com.bbquantum.eazymanage.Controller.AuditLogs to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Attendance;
    opens com.bbquantum.eazymanage.Controller.Attendance to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Finance;
    opens com.bbquantum.eazymanage.Controller.Finance to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Dashboard;
    opens com.bbquantum.eazymanage.Controller.Dashboard to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Conduct;
    opens com.bbquantum.eazymanage.Controller.Conduct to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Auth;
    opens com.bbquantum.eazymanage.Controller.Auth to javafx.fxml;
    exports com.bbquantum.eazymanage.Controller.Broadcasts;
    opens com.bbquantum.eazymanage.Controller.Broadcasts to javafx.fxml;
}