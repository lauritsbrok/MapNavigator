package bfst22.vector;

import java.io.IOException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class is responsible for opening the canvas.
 */

public class View {
    public View(Model model, Stage primaryStage) throws IOException, URISyntaxException {
        primaryStage.show();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/View.fxml"));
        primaryStage.setScene(loader.load());
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);
        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controller.windowResized();

            }
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                try {
                    model.savePointOfInterest(model.filename);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        primaryStage.setTitle("Danmarks Kort");
    }

}
