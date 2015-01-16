package org.priorityhealth.stab.pdiff.controller;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.priorityhealth.stab.pdiff.controller.stacked.AbstractStackedController;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.asset.AssetRepository;
import org.priorityhealth.stab.pdiff.persistence.repository.asset.NodeRepository;
import org.priorityhealth.stab.pdiff.persistence.repository.profile.ProfileRepository;
import org.priorityhealth.stab.pdiff.persistence.repository.profile.StateRepository;
import org.priorityhealth.stab.pdiff.persistence.repository.test.TypeRepository;
import org.priorityhealth.stab.pdiff.domain.service.general.DateTimeService;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.persistence.repository.test.TestRepository;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ProfilerService;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController extends AbstractStackedController implements Initializable {

    public static final String CONTROLLER_NAME = "PROFILE";

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected StateRepositoryInterface stateRepository;
    protected ProfileRepositoryInterface profileRepository;

    @FXML
    private Button btnProfile;

    @FXML
    private WebView wvProfile1;

    @FXML
    private WebView wvProfile2;

    @FXML
    private ComboBox<Asset> cbAsset1;

    @FXML
    private ComboBox<Asset> cbAsset2;

    public ProfileController (
            AssetRepositoryInterface assetRepository,
            NodeRepositoryInterface nodeRepository,
            ProfileRepositoryInterface profileRepository,
            StateRepositoryInterface stateRepository
    ) {
        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        btnProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAsset1.getValue();
                if (asset != null) {
                    final ProfilerService profilerService = new ProfilerService(
                            wvProfile1,
                            asset,
                            System.getProperty("user.home") + File.separator + "pdiff" +
                                    File.separator + DateTimeService.getTimestamp("yyyy_MM_dd_HH_mm_ss"),
                            assetRepository,
                            nodeRepository,
                            stateRepository,
                            profileRepository
                    );

                    profilerService.begin();
                }
            }
        });

        wvProfile1.setPrefWidth(1000);

        cbAsset1.setCellFactory(new AssetCellFactory());
        cbAsset2.setCellFactory(new AssetCellFactory());
        cbAsset1.setConverter(new AssetStringConverter());
        cbAsset2.setConverter(new AssetStringConverter());

        List<Asset> assets = null;
        try {
            assets = assetRepository.getAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (assets != null) {
            cbAsset1.setItems(FXCollections.observableArrayList(assets));
            cbAsset2.setItems(FXCollections.observableArrayList(assets));
        } else {
            LogService.Info("There were no assets");
        }
    }
}
