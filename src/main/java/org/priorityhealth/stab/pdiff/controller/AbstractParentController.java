package org.priorityhealth.stab.pdiff.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.priorityhealth.stab.pdiff.controller.factory.ControllerFactory;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

abstract public class AbstractParentController extends AbstractController implements Initializable {

    protected ControllerFactory controllerFactory;

    public AbstractParentController(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public boolean setScreen(final String name) {
        String viewName = "/views/view/" + name.toLowerCase() + ".fxml";

        Pane contentPane = getContentPane();
        if (contentPane == null) {
            LogService.Info(this, "There was no content pane in which to load content!");
            return false;
        }

        Parent screen;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
            fxmlLoader.setControllerFactory(controllerFactory);
            screen = fxmlLoader.load();
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }

        String styleName = "/views/css/" + name.toLowerCase() + ".css";
        try {
            URL cssUrl = getClass().getResource(styleName);
            if (cssUrl != null) {
                File cssFile = new File(cssUrl.toURI());
                if (cssFile.exists() && !cssFile.isDirectory()) {
                    screen.getStylesheets().add(styleName);
                } else {
                    LogService.Info(this, "Css file '" + cssFile + "' doesn't exist!");
                }
            }
        } catch (URISyntaxException ex) {
            LogService.Info(this, "Css file not a valid resource!");
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

        if (screen == null) {
            LogService.Info(this, "There was no content in which to load!");
            return false;
        }

        if (contentPane instanceof AnchorPane) {
            AnchorPane.setLeftAnchor(screen, 0d);
            AnchorPane.setRightAnchor(screen, 0d);
            AnchorPane.setTopAnchor(screen, 0d);
            AnchorPane.setBottomAnchor(screen, 0d);
        }

        if (!contentPane.getChildren().isEmpty()) {
            contentPane.getChildren().clear();
        }
        contentPane.getChildren().add(screen);

        return true;
    }

    abstract protected Pane getContentPane();
}
