package renderdiff.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import renderdiff.controller.parent.AbstractParentController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractParentController {

    @FXML
    private StackPane stackPane;

    @FXML
    private MenuItem menuProfile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadScreen("profile", "/views/profile.fxml");

        menuProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setScreen("profile");
            }
        });
    }

    @Override
    public boolean setScreen(final String name) {
        if (screens.get(name) != null) { //screen loaded
            if (!stackPane.getChildren().isEmpty()) {
                stackPane.getChildren().remove(0);
                stackPane.getChildren().add(0, screens.get(name));
            } else {
                stackPane.getChildren().add(screens.get(name));
            }
            return true;
        } else {
            System.out.println("screen hasn't been loaded!\n");
            return false;
        }
    }
}
