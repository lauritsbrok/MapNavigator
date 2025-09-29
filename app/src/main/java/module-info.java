module helloFX {
    requires java.xml;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.junit.jupiter.api;
    requires transitive javafx.graphics;

    opens bfst22.vector to javafx.fxml;
    opens bfst22.vector.gui to javafx.fxml;
    opens bfst22.vector.Graph to javafx.fxml;
    opens bfst22.vector.OSMParsing to javafx.fxml;
    opens bfst22.vector.Util to javafx.fxml;

    exports bfst22.vector;
    exports bfst22.vector.gui;
    exports bfst22.vector.Graph;
    exports bfst22.vector.OSMParsing;
    exports bfst22.vector.Util;
}