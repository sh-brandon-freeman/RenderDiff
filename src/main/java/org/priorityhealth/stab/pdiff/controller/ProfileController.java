package org.priorityhealth.stab.pdiff.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ProfilerService;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    public static final String CONTROLLER_NAME = "PROFILE";

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected StateRepositoryInterface stateRepository;
    protected ProfileRepositoryInterface profileRepository;
    protected ProfilerService profilerService;

    @FXML
    private Button btnProfile;

    @FXML
    private WebView wvProfile;

    @FXML
    private ComboBox<Asset> cbAssetProfile;

    @FXML
    private ComboBox<Asset> cbAssetNodes;

    @FXML
    private CheckBox chkCrawl;

    public ProfileController (
            AssetRepositoryInterface assetRepository,
            NodeRepositoryInterface nodeRepository,
            ProfileRepositoryInterface profileRepository,
            StateRepositoryInterface stateRepository,
            ProfilerService profilerService
    ) {
        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
        this.profilerService = profilerService;
    }

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        profilerService.setWebView(wvProfile);

        btnProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAssetProfile.getValue();

                Asset nodeListAsset = cbAssetNodes.getValue();

                LogService.Info(this, "Beginning Profile for: " + asset.getName());
                profilerService.init(asset);

                if (nodeListAsset != null) {
                    LogService.Info(this, "Using alternate node list from " + nodeListAsset.getName());
                    profilerService.setAlternateNodeList(nodeListAsset.getNodes());
                }

                if (!chkCrawl.isSelected()) {
                    profilerService.setCrawlNewNodes(false);
                }

                profilerService.run();
            }
        });

        wvProfile.setPrefWidth(1000);

        cbAssetProfile.setCellFactory(new AssetCellFactory());
        cbAssetNodes.setCellFactory(new AssetCellFactory());
        cbAssetProfile.setConverter(new AssetStringConverter());
        cbAssetNodes.setConverter(new AssetStringConverter());

        List<Asset> assets = null;
        try {
            assets = assetRepository.getAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (assets != null) {
            cbAssetProfile.setItems(FXCollections.observableArrayList(assets));
            cbAssetNodes.setItems(FXCollections.observableArrayList(assets));
        } else {
            LogService.Info(this, "There were no assets");
        }

        cbAssetNodes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAssetNodes.getValue();
                if (asset != null) {
                    chkCrawl.setSelected(false);
                    chkCrawl.setDisable(true);
                } else {
                    chkCrawl.setSelected(true);
                    chkCrawl.setDisable(false);
                }

            }
        });
    }
}
