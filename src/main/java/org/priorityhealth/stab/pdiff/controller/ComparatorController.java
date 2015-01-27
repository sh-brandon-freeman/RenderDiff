package org.priorityhealth.stab.pdiff.controller;

import com.sun.deploy.util.StringUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.ResultRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.TestRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ComparatorService;
import org.priorityhealth.stab.pdiff.domain.service.comparator.factory.ComparatorServiceFactory;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.converter.ProfileStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.ProfileCellFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ComparatorController implements Initializable {

    public static final String CONTROLLER_NAME = "COMPARATOR";

    @FXML
    protected ComboBox<Asset> cbAsset1;

    @FXML
    protected ComboBox<Asset> cbAsset2;

    @FXML
    protected ComboBox<Profile> cbProfile1;

    @FXML
    protected ComboBox<Profile> cbProfile2;

    @FXML
    protected CheckBox chkNewProfile1;

    @FXML
    protected CheckBox chkNewProfile2;

    @FXML
    protected CheckBox chkNodes;

    @FXML
    protected Button btnRun;

    @FXML
    protected WebView wvProfile;

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected ProfileRepositoryInterface profileRepository;
    protected ResultRepositoryInterface resultRepository;
    protected StateRepositoryInterface stateRepository;
    protected TestRepositoryInterface testRepository;
    protected ComparatorService comparatorService;

    public ComparatorController(
            AssetRepositoryInterface assetRepository,
            NodeRepositoryInterface nodeRepository,
            ProfileRepositoryInterface profileRepository,
            ResultRepositoryInterface resultRepository,
            StateRepositoryInterface stateRepository,
            TestRepositoryInterface testRepository,
            ComparatorService comparatorService
    ) {
        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
        this.profileRepository = profileRepository;
        this.resultRepository = resultRepository;
        this.stateRepository = stateRepository;
        this.testRepository = testRepository;
        this.comparatorService = comparatorService;
    }

    protected void execute() {
        LogService.Info(this, "Executing.");
        Asset asset1 = cbAsset1.getValue();
        Asset asset2 = cbAsset2.getValue();
        Profile profile1 = chkNewProfile1.isSelected() ? null : cbProfile1.getValue();
        Profile profile2 = chkNewProfile2.isSelected() ? null : cbProfile2.getValue();

        if (comparatorService != null) {
            comparatorService.init(asset1, profile1, asset2, profile2, chkNodes.isSelected());
            comparatorService.run();
        } else {
            LogService.Error(this, "There was no comparator service.");
        }
    }

    protected boolean validate() {
        List<String> fields = new ArrayList<String>();
        if (cbAsset1.getValue() == null) {
            fields.add("Asset1");
        }

        if (cbAsset2.getValue() == null) {
            fields.add("Asset2");
        }

        if (cbProfile1.getValue() == null && !chkNewProfile1.isSelected()) {
            fields.add("Profile1");
        }

        if (cbProfile2.getValue() == null && !chkNewProfile2.isSelected()) {
            fields.add("Profile2");
        }

        if (fields.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete");
            alert.setHeaderText("There is more information required.");
            alert.setContentText("Missing fields: " + fields);
            alert.showAndWait();
        }

        return fields.size() == 0;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        wvProfile.setPrefWidth(1000);

        cbAsset1.setCellFactory(new AssetCellFactory());
        cbAsset2.setCellFactory(new AssetCellFactory());
        cbAsset1.setConverter(new AssetStringConverter());
        cbAsset2.setConverter(new AssetStringConverter());

        java.util.List<Asset> assets = null;
        try {
            assets = assetRepository.getAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (assets != null && assets.size() > 0) {
            cbAsset1.setItems(FXCollections.observableArrayList(assets));
            cbAsset2.setItems(FXCollections.observableArrayList(assets));
        } else {
            LogService.Info(this, "There were no assets");
        }

        cbProfile1.setCellFactory(new ProfileCellFactory());
        cbProfile2.setCellFactory(new ProfileCellFactory());
        cbProfile1.setConverter(new ProfileStringConverter());
        cbProfile2.setConverter(new ProfileStringConverter());

        comparatorService.setWebView(wvProfile);

        registerEvents();
    }

    protected void registerEvents() {
        cbAsset1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAsset1.getValue();

                LogService.Info(this, "Selected Asset1: " + asset.getName());

                cbProfile1.setItems(null);

                List<Profile> profiles = null;
                try {
                    profiles = profileRepository.getByAssetId(asset.getId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (profiles != null) {
                    cbProfile1.setItems(FXCollections.observableArrayList(profiles));
                } else {
                    LogService.Info(this, "There were no profiles");
                }
            }
        });

        cbAsset2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAsset2.getValue();

                LogService.Info(this, "Selected Asset2: " + asset.getName());

                cbProfile2.setItems(null);

                List<Profile> profiles = null;
                try {
                    profiles = profileRepository.getByAssetId(asset.getId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (profiles != null) {
                    cbProfile2.setItems(FXCollections.observableArrayList(profiles));
                } else {
                    LogService.Info(this, "There were no profiles");
                }
            }
        });

        chkNewProfile1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                cbProfile1.setDisable(chkNewProfile1.isSelected());
            }
        });

        chkNewProfile2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                cbProfile2.setDisable(chkNewProfile2.isSelected());
            }
        });

        btnRun.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (validate()) {
                    execute();
                }
            }
        });
    }
}
