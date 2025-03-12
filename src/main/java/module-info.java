module it.unipi.guitarteacher {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.google.gson;
    requires java.sql;
    requires org.mybatis;
    requires org.apache.logging.log4j;
    
    
    opens it.unipi.guitarteacher to javafx.fxml;
    opens it.unipi.guitarteacher.controllers to javafx.fxml;
    exports it.unipi.guitarteacher;
    
}
