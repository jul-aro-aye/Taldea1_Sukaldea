module Sukaldea {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    requires transitive java.sql;
    requires mysql.connector.j;

    exports Main;
    exports Util;
    exports DatuBasea;
    exports kontrola;
    exports model;

    opens Main to javafx.fxml;
    opens kontrola to javafx.fxml;
    opens Util to javafx.fxml;
    opens DatuBasea to javafx.fxml;
    opens model to javafx.fxml;
}

