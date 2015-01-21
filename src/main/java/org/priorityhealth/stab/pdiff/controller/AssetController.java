package org.priorityhealth.stab.pdiff.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.*;
import org.priorityhealth.stab.pdiff.controller.stacked.AbstractStackedController;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.NodeCellFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class AssetController extends AbstractStackedController implements Initializable {

    public static final String CONTROLLER_NAME = "ASSET";

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;

    protected Asset selectedAsset;
    protected ObservableList<Asset> assets;

    @FXML
    private ComboBox<Asset> cbAssets;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtDomain;

    @FXML
    private TextField txtScript;

    @FXML
    private TextField txtLogin;

    @FXML
    private TextField txtAdd;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAddAsset;

    @FXML
    private Button btnCloneAsset;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private ListView<Node> lvNodes;

    public AssetController(AssetRepositoryInterface assetRepository, NodeRepositoryInterface nodeRepository) {
        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbAssets.setCellFactory(new AssetCellFactory());
        cbAssets.setConverter(new AssetStringConverter());
        lvNodes.setCellFactory(new NodeCellFactory());

        try {
            assets = FXCollections.observableArrayList(assetRepository.getAll());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (assets != null) {
            cbAssets.setItems(assets);
        } else {
            LogService.Info(this, "There were no assets");
        }

        cbAssets.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                selectedAsset = cbAssets.getValue();

                loadSelectedAsset();

                btnSave.setDisable(true);
                btnCloneAsset.setDisable(false);
            }
        });

        ChangeListener<String> assetChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                btnSave.setDisable(false);
            }
        };

        txtName.textProperty().addListener(assetChangeListener);
        txtDomain.textProperty().addListener(assetChangeListener);
        txtLogin.textProperty().addListener(assetChangeListener);
        txtLogin.textProperty().addListener(assetChangeListener);

        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedAsset.setName(txtName.getText());
                selectedAsset.setDomain(txtDomain.getText());
                selectedAsset.setLoginNodeUrl(txtLogin.getText());
                selectedAsset.setLoadCompleteScript(txtScript.getText());
                try {
                    if (selectedAsset.getId() > 0) {
                        assetRepository.update(selectedAsset);
                    } else {
                        assetRepository.create(selectedAsset);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                btnSave.setDisable(true);
            }
        });

        btnAddAsset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectedAsset = new Asset();
                selectedAsset.setName("New Asset");
                try {
                    assetRepository.create(selectedAsset);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                assets.add(selectedAsset);
                loadSelectedAsset();
            }
        });

        btnCloneAsset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cloneSelectedAsset();
            }
        });

        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Delete");
                alert.setHeaderText("Delete Asset?");
                alert.setContentText("Are you want to delete: " + selectedAsset.getName() + "?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    deleteSelectedAsset();
                }
            }
        });
    }

    protected void cloneSelectedAsset() {
//        Asset newAsset = null;
//        try {
//            newAsset = (Asset) selectedAsset.clone();
//            newAsset.setId(0);
//            newAsset.setName(newAsset.getName() + " (Clone)");
//            newAsset.getNodes().clear();
//
//            assetRepository.create(newAsset);
//
//            for (Node node : selectedAsset.getNodes()) {
//                Node newNode = (Node) node.clone();
//                newNode.setId(0);
//                newNode.setAsset(newAsset);
//                nodeRepository.create(newNode);
//                newAsset.addNode(newNode);
//            }
//
//        } catch (CloneNotSupportedException ex) {
//            ex.printStackTrace();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//
//        if (newAsset != null) {
//            selectedAsset = newAsset;
//            assets.add(newAsset);
//            loadSelectedAsset();
//            cbAssets.setValue(newAsset);
//        } else {
//            LogService.Info(this, "Asset could not be cloned.");
//        }
    }

    protected void deleteSelectedAsset() {
        if (selectedAsset == null) {
            return;
        }

        for (Node node : selectedAsset.getNodes()) {
            if (node != null && node.getId() > 0) {
                try {
                    nodeRepository.delete(node);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        assets.remove(selectedAsset);

        try {
            assetRepository.delete(selectedAsset);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    protected void loadSelectedAsset() {
        txtDomain.setText(selectedAsset.getDomain());
        txtLogin.setText(selectedAsset.getLoginNodeUrl());
        txtName.setText(selectedAsset.getName());
        txtScript.setText(selectedAsset.getLoadCompleteScript());
        if (selectedAsset.getNodes() != null) {
            lvNodes.setItems(FXCollections.observableArrayList(selectedAsset.getNodes()));
        }
    }

}
