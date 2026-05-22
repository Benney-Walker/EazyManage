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

    exports com.bbquantum.eazymanage;
    exports com.bbquantum.eazymanage.CacheClasses;
    exports com.bbquantum.eazymanage.Controller;
    exports com.bbquantum.eazymanage.Shell;
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
}