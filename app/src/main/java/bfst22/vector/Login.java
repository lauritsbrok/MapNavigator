package bfst22.vector;

import java.io.IOException;
import bfst22.vector.gui.AlertPopUp;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Login {
    public Login(Stage primaryStage) {
        try {
            primaryStage.show();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Login.fxml"));
            primaryStage.setScene(loader.load());
            primaryStage.setResizable(false);
            LoginController controller = loader.getController();
            controller.setStage(primaryStage);
            primaryStage.setTitle("Start");
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            AlertPopUp.newAlert("Couldn't find login.fxml",
                    "Couldn't find login.fxml, please check it's located under resources.");
        }
    }

}
