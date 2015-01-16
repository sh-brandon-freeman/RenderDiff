package org.priorityhealth.stab.pdiff.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import org.priorityhealth.stab.pdiff.controller.stacked.AbstractStackedController;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class TestController extends AbstractStackedController implements Initializable {

    public static final String CONTROLLER_NAME = "TEST";

    protected AssetRepositoryInterface assetRepository;
    protected ProfileRepositoryInterface profileRepository;
    protected StateRepositoryInterface stateRepository;

    @FXML
    private Button btnCompare;

    @FXML
    private ComboBox<Asset> cbAsset1;

    @FXML
    private ComboBox<Asset> cbAsset2;

    @FXML
    private ComboBox<Profile> cbProfile1;

    @FXML
    private ComboBox<Profile> cbProfile2;

    @FXML
    private ListView<State> lvStates1;

    @FXML
    private ListView<State> lvStates2;

    @FXML
    private ImageView ivCurrent;

    @FXML
    private ImageView ivKnown;

    @FXML
    private ImageView ivDiff;

    public TestController(
            AssetRepositoryInterface assetRepository,
            ProfileRepositoryInterface profileRepository,
            StateRepositoryInterface stateRepository
    ) {
        this.assetRepository = assetRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        cbAsset1.setCellFactory(new AssetCellFactory());
        cbAsset2.setCellFactory(new AssetCellFactory());
        cbAsset1.setConverter(new AssetStringConverter());
        cbAsset2.setConverter(new AssetStringConverter());

    }
}
