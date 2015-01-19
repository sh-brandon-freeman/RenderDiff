package org.priorityhealth.stab.pdiff.controller.parent;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.priorityhealth.stab.pdiff.controller.ControllerFactory;
import org.priorityhealth.stab.pdiff.controller.stacked.AbstractStackedController;
import org.priorityhealth.stab.pdiff.controller.stacked.StackedControllerInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.RepositoryFactory;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.io.IOException;
import java.util.HashMap;

abstract public class AbstractParentController implements Initializable {

    protected ControllerFactory controllerFactory;

    protected HashMap<String, Node> screens = new HashMap<String, Node>();

    public AbstractParentController(ControllerFactory controllerFactory) {
        this.controllerFactory = controllerFactory;
    }

    public boolean loadScreen(String name, AbstractStackedController stackedController) {
        String className = stackedController.getClass().getName();
        String viewName = "/views/" + className.substring(
                className.lastIndexOf('.') + 1,
                className.lastIndexOf("Controller")
        ).toLowerCase() + ".fxml";
        stackedController.setParentController(this);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(viewName));
            fxmlLoader.setControllerFactory(controllerFactory);
            //fxmlLoader.setController(stackedController);
            Parent loadScreen = fxmlLoader.load();
            screens.put(name, loadScreen);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean unloadScreen(String name) {
        if(screens.remove(name) == null) {
            LogService.Info(this, "Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }

    abstract public boolean setScreen(final String name);
}
