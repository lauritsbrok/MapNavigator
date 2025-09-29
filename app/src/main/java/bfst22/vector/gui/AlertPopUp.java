package bfst22.vector.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertPopUp {
    /**
     * A simple dialog we can use to warn user in case of exceptions.
     * 
     * @param title       the title of the window
     * @param Information the actual message to the user
     * @return
     */
    public static void newAlert(String title, String Information) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(Information);
        alert.showAndWait();
    }
}
