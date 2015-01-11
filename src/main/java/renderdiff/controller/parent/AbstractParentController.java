package renderdiff.controller.parent;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import renderdiff.controller.stacked.StackedControllerInterface;

import java.io.IOException;
import java.util.HashMap;

abstract public class AbstractParentController implements ParentControllerInterface, Initializable {

    protected HashMap<String, Node> screens = new HashMap<String, Node>();

    public void addScreen(String name, Node node) {
        screens.put(name, node);
    }

    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = myLoader.load();
            StackedControllerInterface stackedController = myLoader.getController();
            stackedController.setParentController(this);
            addScreen(name, loadScreen);
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean unloadScreen(String name) {
        if(screens.remove(name) == null) {
            System.out.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }

    abstract public boolean setScreen(final String name);
}
