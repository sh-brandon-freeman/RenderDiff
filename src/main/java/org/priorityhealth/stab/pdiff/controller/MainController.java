package org.priorityhealth.stab.pdiff.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import org.priorityhealth.stab.pdiff.controller.factory.ControllerFactory;
import org.priorityhealth.stab.pdiff.controller.parent.AbstractParentController;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends AbstractParentController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private MenuItem menuProfile;

    @FXML
    private MenuItem menuTest;

    @FXML
    private MenuItem menuAsset;

    public MainController(ControllerFactory controllerFactory) {
        super(controllerFactory);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadScreen(ProfileController.CONTROLLER_NAME, controllerFactory.getProfileController());
        this.loadScreen(TestController.CONTROLLER_NAME, controllerFactory.getTestController());
        this.loadScreen(AssetController.CONTROLLER_NAME, controllerFactory.getAssetController());

        menuAsset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setScreen(AssetController.CONTROLLER_NAME);
            }
        });
        menuProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setScreen(ProfileController.CONTROLLER_NAME);
            }
        });
        menuTest.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setScreen(TestController.CONTROLLER_NAME);
            }
        });
    }

    @Override
    public boolean setScreen(final String name) {
        if (screens.get(name) != null) {
            Node screen = screens.get(name);
            AnchorPane.setLeftAnchor(screen, 0d);
            AnchorPane.setRightAnchor(screen, 0d);
            AnchorPane.setTopAnchor(screen, 0d);
            AnchorPane.setBottomAnchor(screen, 0d);
            if (!anchorPane.getChildren().isEmpty()) {
                anchorPane.getChildren().remove(0);
                anchorPane.getChildren().add(0, screen);
            } else {
                anchorPane.getChildren().add(screen);
            }
            return true;
        } else {
            LogService.Info(this, "screen hasn't been loaded!\n");
            return false;
        }
    }
}
