package bfst22.vector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import bfst22.vector.gui.AlertPopUp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The login controller, controls the login screen, where the user chooses the
 * file they want to run.
 */
public class LoginController extends Controller {
        Model model;
        double parsingProgress;
        double loadingProgress;
        String actionString;
        Stage stage;
        boolean modelComplete;
        @FXML
        ProgressBar osmProgressBar;
        @FXML
        Text action;

        @FXML
        private void loadCustomOSM(ActionEvent event)
                        throws IOException, SAXException, ClassNotFoundException, InterruptedException {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter(
                                "Supported files (*.osm, *.obj, *.osm.zip, *.zip)",
                                "*.osm", "*.obj", "*.osm.zip", "*.zip");
                FileChooser.ExtensionFilter osmFilter = new FileChooser.ExtensionFilter("OSM files (*.osm)",
                                "*.osm");
                FileChooser.ExtensionFilter objFilter = new FileChooser.ExtensionFilter("Object files (*.obj)",
                                "*.obj");
                FileChooser.ExtensionFilter zipFilter = new FileChooser.ExtensionFilter(
                                "Zipped OSM files (*.osm.zip, *.zip)", "*.osm.zip", "*.zip");
                fileChooser.getExtensionFilters().add(allFilter);
                fileChooser.getExtensionFilters().add(osmFilter);
                fileChooser.getExtensionFilters().add(objFilter);
                fileChooser.getExtensionFilters().add(zipFilter);
                Node source = (Node) event.getSource();
                Stage stage = (Stage) source.getScene().getWindow();
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                        makeModel(file.getAbsolutePath());
                }
        }

        public void makeModel(String file) {
                Thread t = new Thread(() -> {
                        try {
                                model = new Model(file, this);
                        } catch (ClassNotFoundException e1) {
                                Platform.runLater(() -> AlertPopUp.newAlert("Something went wrong",
                                                "An unexpected error occurred, try again"));
                        } catch (SAXException e1) {
                                Platform.runLater(() -> AlertPopUp.newAlert("Osmfile not working",
                                                "Not a Valid or broken OSM file"));
                        } catch (IOException e1) {
                                Platform.runLater(() -> AlertPopUp.newAlert("Can't find OSM file!",
                                                "Please choose a custom file."));
                        } catch (OutOfMemoryError e1) {
                                Platform.runLater(() -> AlertPopUp.newAlert("Not Enough Memory!",
                                                "Allow the program to use more memory"));
                        }
                        Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                        if (model != null) {
                                                osmProgressBar.setProgress(-1);
                                                try {
                                                        new View(model, new Stage());
                                                } catch (IOException e) {
                                                        AlertPopUp.newAlert("Osmfile not working",
                                                                        "Not a Valid or broken OSM file");
                                                } catch (URISyntaxException e) {
                                                        e.printStackTrace();
                                                }
                                                if (stage != null) {
                                                        stage.close();
                                                }
                                        } else {
                                                osmProgressBar.setProgress(parsingProgress);
                                                action.setText(actionString);
                                        }
                                }
                        });
                });
                t.start();

        }

        public void setActionString(String newAction) {
                this.actionString = newAction;
                action.setText(actionString);
        }

        public String getActionString() {
                return actionString;
        }

        public void setParsingProgress(double newValue) {
                this.parsingProgress = newValue;
                osmProgressBar.setProgress(parsingProgress);
        }

        public double getParsingProgress() {
                return this.parsingProgress;
        }

        public void setStage(Stage primaryStage) {
                this.stage = primaryStage;
        }
}
