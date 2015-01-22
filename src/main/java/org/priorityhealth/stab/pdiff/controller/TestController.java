package org.priorityhealth.stab.pdiff.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.priorityhealth.stab.pdiff.controller.stacked.AbstractStackedController;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.ResultRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.TestRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.service.comparator.StateCompareService;
import org.priorityhealth.stab.pdiff.domain.service.comparator.StateResultInterface;
import org.priorityhealth.stab.pdiff.service.DateTimeService;
import org.priorityhealth.stab.pdiff.service.ImageService;
import org.priorityhealth.stab.pdiff.service.LogService;
import org.priorityhealth.stab.pdiff.view.converter.AssetStringConverter;
import org.priorityhealth.stab.pdiff.view.converter.ProfileStringConverter;
import org.priorityhealth.stab.pdiff.view.factory.AssetCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.ProfileCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.ResultCellFactory;
import org.priorityhealth.stab.pdiff.view.factory.StateCellFactory;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class TestController extends AbstractStackedController implements Initializable, StateResultInterface {

    public static final String CONTROLLER_NAME = "TEST";

    protected AssetRepositoryInterface assetRepository;
    protected ProfileRepositoryInterface profileRepository;
    protected StateRepositoryInterface stateRepository;
    protected ResultRepositoryInterface resultRepository;
    protected TestRepositoryInterface testRepository;

    protected ObservableList<Result> resultsList;
    protected ObservableList<Result> allResultsList;

    @FXML
    private Button btnCompare;

    @FXML
    private Button btnAllResults;

    @FXML
    private Button btnDifferentResults;

    @FXML
    private Button btnSameResults;

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

    @FXML
    private HBox hBox;

    public TestController(
            AssetRepositoryInterface assetRepository,
            ProfileRepositoryInterface profileRepository,
            StateRepositoryInterface stateRepository,
            ResultRepositoryInterface resultRepository,
            TestRepositoryInterface testRepository
    ) {
        this.assetRepository = assetRepository;
        this.profileRepository = profileRepository;
        this.stateRepository = stateRepository;
        this.resultRepository = resultRepository;
        this.testRepository = testRepository;

        resultsList = FXCollections.observableArrayList();
        allResultsList = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initControls();
    }

    protected void initControls() {
        AnchorPane.setLeftAnchor(hBox, 0d);
        AnchorPane.setRightAnchor(hBox, 0d);
        AnchorPane.setTopAnchor(hBox, 0d);
        AnchorPane.setBottomAnchor(hBox, 0d);

        cbAsset1.setCellFactory(new AssetCellFactory());
        cbAsset2.setCellFactory(new AssetCellFactory());
        cbAsset1.setConverter(new AssetStringConverter());
        cbAsset2.setConverter(new AssetStringConverter());

        cbProfile1.setCellFactory(new ProfileCellFactory());
        cbProfile2.setCellFactory(new ProfileCellFactory());
        cbProfile1.setConverter(new ProfileStringConverter());
        cbProfile2.setConverter(new ProfileStringConverter());

        lvStates1.setCellFactory(new StateCellFactory());
        lvStates2.setCellFactory(new StateCellFactory());
        lvResults.setCellFactory(new ResultCellFactory());

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
            LogService.Info(this, "There were no assets");
        }

        cbAsset1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Asset asset = cbAsset1.getValue();

                LogService.Info(this, "Selected Asset1: " + asset.getName());

                cbProfile1.setItems(null);
                lvStates1.setItems(null);

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
                lvStates2.setItems(null);

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

        cbProfile1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Profile profile = cbProfile1.getValue();

                LogService.Info(this, "Selected Profile1: " + profile.getCreated());

                lvStates1.setItems(null);

                List<State> states = null;
                try {
                    states = stateRepository.getByProfileId(profile.getId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (states != null) {
                    lvStates1.setItems(FXCollections.observableArrayList(states));
                } else {
                    LogService.Info(this, "There were no states");
                }
            }
        });

        cbProfile2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Profile profile = cbProfile2.getValue();

                LogService.Info(this, "Selected Profile2: " + profile.getCreated());

                lvStates2.setItems(null);

                List<State> states = null;
                try {
                    states = stateRepository.getByProfileId(profile.getId());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if (states != null) {
                    lvStates2.setItems(FXCollections.observableArrayList(states));
                } else {
                    LogService.Info(this, "There were no states");
                }
            }
        });

        final StateResultInterface stateResult = this;
        btnCompare.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Profile profile1 = cbProfile1.getValue();
                Profile profile2 = cbProfile2.getValue();

                if (profile1 != null && profile2 != null) {
                    resultsList.clear();
                    allResultsList.clear();
                    StateCompareService stateCompareService = new StateCompareService(
                            profile1,
                            profile2,
                            System.getProperty("user.home") + File.separator + "pdiff" +
                                    File.separator + DateTimeService.getTimestamp("yyyy_MM_dd_HH_mm_ss"),
                            stateResult,
                            resultRepository,
                            testRepository
                    );
                    stateCompareService.run();
                }
            }
        });

        lvResults.setItems(resultsList);

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
            public void invalidated(Observable arg0) {
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
            public void invalidated(Observable arg0) {
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
            public void invalidated(Observable arg0) {
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

    @Override
    public void onCompareComplete(Result result) {
        if (result == null) {
            return;
        }
        allResultsList.add(result);
    }

    @Override
    public void onQueueComplete() {
        resultsList.addAll(allResultsList);
    }
}
