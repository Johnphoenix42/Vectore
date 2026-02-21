module com.qualibits.vectore {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.logging;
    requires org.joml;

    opens com.qualibits.vectore to javafx.graphics, javafx.fxml;
    exports com.qualibits.vectore;
}