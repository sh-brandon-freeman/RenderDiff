package org.priorityhealth.stab.pdiff.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.priorityhealth.stab.pdiff.controller.factory.ControllerFactory;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.headless.HeadlessAppLauncher;

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

    @FXML
    private MenuItem menuSchedule;

    public MainController(ControllerFactory controllerFactory) {
        super(controllerFactory);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
        menuSchedule.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                setScreen(ComparatorController.CONTROLLER_NAME);
            }
        });

        if (isHeadless()) {
            LogService.Info(this, "HEADLESS!!");
            setScreen(ComparatorController.CONTROLLER_NAME);
        }
    }

    @Override
    protected Pane getContentPane() {
        return anchorPane;
    }
}
