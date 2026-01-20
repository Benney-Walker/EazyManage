module com.bbquantum.eazymanage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.base;
    requires java.desktop;

    exports com.bbquantum.eazymanage;
    exports com.bbquantum.eazymanage.CacheClasses;
    exports com.bbquantum.eazymanage.Controller;


    opens com.bbquantum.eazymanage to javafx.fxml;
    opens com.bbquantum.eazymanage.Controller to javafx.fxml;

    opens com.bbquantum.eazymanage.CacheClasses to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Academics;
    opens com.bbquantum.eazymanage.DTO.Academics to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Finance;
    opens com.bbquantum.eazymanage.DTO.Finance to com.google.gson;
    exports com.bbquantum.eazymanage.DTO.Institution;
    opens com.bbquantum.eazymanage.DTO.Institution to com.google.gson;
}