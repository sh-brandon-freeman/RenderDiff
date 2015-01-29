package org.priorityhealth.stab.pdiff.controller;

import com.sun.deploy.util.StringUtils;
import javafx.beans.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.web.WebView;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.domain.entity.test.Test;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.ResultRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.TestRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.service.comparator.ComparatorService;
import org.priorityhealth.stab.pdiff.domain.service.comparator.StateCompareListenerInterface;
import org.priorityhealth.stab.pdiff.domain.service.comparator.factory.ComparatorServiceFactory;
import org.priorityhealth.stab.pdiff.service.ImageService;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.converter.ProfileStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.ProfileCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.ResultCellFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Observable;

public class ComparatorController implements Initializable, StateCompareListenerInterface {

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
    private Button btnAllResults;

    @FXML
    private Button btnDifferentResults;

    @FXML
    private Button btnSameResults;

    @FXML
    protected WebView wvProfile;

    @FXML
    private ListView<Result> lvResults;

    @FXML
    private ImageView ivCurrent;

    @FXML
    private ImageView ivKnown;

    @FXML
    private ImageView ivDiff;

    @FXML
    private ScrollPane spCurrent;

    @FXML
    private ScrollPane spKnown;

    @FXML
    private ScrollPane spDiff;

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected ProfileRepositoryInterface profileRepository;
    protected ResultRepositoryInterface resultRepository;
    protected StateRepositoryInterface stateRepository;
    protected TestRepositoryInterface testRepository;
    protected ComparatorService comparatorService;

    protected ObservableList<Result> resultsList;
    protected ObservableList<Result> allResultsList;

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

        resultsList = FXCollections.observableArrayList();
        allResultsList = FXCollections.observableArrayList();
    }

    protected void execute() {
        LogService.Info(this, "Executing.");
        Asset asset1 = cbAsset1.getValue();
        Asset asset2 = cbAsset2.getValue();
        Profile profile1 = chkNewProfile1.isSelected() ? null : cbProfile1.getValue();
        Profile profile2 = chkNewProfile2.isSelected() ? null : cbProfile2.getValue();

        if (comparatorService != null) {
            comparatorService.init(asset1, profile1, asset2, profile2, chkNodes.isSelected(), this);
            comparatorService.run();
        } else {
            LogService.Error(this, "There was no comparator service.");
        }
    }

    protected void clearUi() {
        resultsList.clear();
        allResultsList.clear();
        ivCurrent.setImage(null);
        ivDiff.setImage(null);
        ivKnown.setImage(null);
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
        lvResults.setCellFactory(new ResultCellFactory());

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

        lvResults.setItems(resultsList);

        registerEvents();
    }

    @Override
    public void onCompareComplete(Result result) {
        if (result == null) {
            return;
        }
        allResultsList.add(result);
    }

    @Override
    public void onQueueComplete(Test test) {
        resultsList.addAll(allResultsList);
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
                clearUi();
                if (validate()) {
                    execute();
                }
            }
        });

        lvResults.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Result result = lvResults.getSelectionModel().getSelectedItem();
                if (result != null) {
                    loadResult(result);
                }
            }
        });

        final DoubleProperty knownZoomProperty = new SimpleDoubleProperty(200);
        knownZoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable arg0) {
                ivKnown.setFitWidth(knownZoomProperty.get() * 4);
                ivKnown.setFitHeight(knownZoomProperty.get() * 3);
            }
        });

        spKnown.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    knownZoomProperty.set(knownZoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    knownZoomProperty.set(knownZoomProperty.get() / 1.1);
                }
            }
        });

        final DoubleProperty currentZoomProperty = new SimpleDoubleProperty(200);
        currentZoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable arg0) {
                ivCurrent.setFitWidth(currentZoomProperty.get() * 4);
                ivCurrent.setFitHeight(currentZoomProperty.get() * 3);
            }
        });

        spCurrent.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    currentZoomProperty.set(currentZoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    currentZoomProperty.set(currentZoomProperty.get() / 1.1);
                }
            }
        });

        final DoubleProperty diffZoomProperty = new SimpleDoubleProperty(200);
        diffZoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable arg0) {
                ivDiff.setFitWidth(diffZoomProperty.get() * 4);
                ivDiff.setFitHeight(diffZoomProperty.get() * 3);
            }
        });

        spDiff.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    diffZoomProperty.set(diffZoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    diffZoomProperty.set(diffZoomProperty.get() / 1.1);
                }
            }
        });

        btnAllResults.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resultsList.clear();
                resultsList.addAll(allResultsList);
            }
        });

        btnSameResults.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resultsList.clear();
                for (Result result : allResultsList) {
                    if (result.getDiffImagePath() == null || result.getDiffImagePath().length() == 0) {
                        resultsList.add(result);
                    }
                }
            }
        });

        btnDifferentResults.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                resultsList.clear();
                for (Result result : allResultsList) {
                    if (result.getDiffImagePath() != null && result.getDiffImagePath().length() > 0) {
                        resultsList.add(result);
                    }
                }
            }
        });
    }

    public void loadResult(Result result) {
        String currentPath = result.getCurrent().getImagePath();
        LogService.Info(this, "Result Current: " + currentPath);
        WritableImage currentImage = ImageService.loadWritableImageFromFile(currentPath);
        if (currentImage != null) {
            ivCurrent.setImage(currentImage);
        }

        String knownPath = result.getKnown().getImagePath();
        LogService.Info(this, "Result Known: " + knownPath);
        WritableImage knownImage = ImageService.loadWritableImageFromFile(knownPath);
        if (knownImage != null) {
            ivKnown.setImage(knownImage);
        }

        String diffPath = result.getDiffImagePath();
        if (diffPath != null && diffPath.length() > 0) {
            LogService.Info(this, "Result Diff: " + diffPath);

            WritableImage diffImage = ImageService.loadWritableImageFromFile(diffPath);
            if (diffImage != null) {
                ivDiff.setImage(diffImage);
            }
        } else {
            ivDiff.setImage(null);
        }
    }
}
