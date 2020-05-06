module com.rubbertranslator {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.rubbertranslator to javafx.fxml;
    exports com.rubbertranslator;
}