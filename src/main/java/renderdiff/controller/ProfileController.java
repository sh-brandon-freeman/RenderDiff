package renderdiff.controller;

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
import renderdiff.controller.stacked.AbstractStackedController;
import renderdiff.domain.entity.asset.Asset;
import renderdiff.persistence.repository.asset.AssetRepository;
import renderdiff.persistence.repository.asset.NodeRepository;
import renderdiff.persistence.repository.profile.ProfileRepository;
import renderdiff.persistence.repository.profile.StateRepository;
import renderdiff.persistence.repository.test.TestRepository;
import renderdiff.persistence.repository.test.TypeRepository;
import renderdiff.service.comparator.ProfilerService;
import renderdiff.service.general.DateTimeService;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController extends AbstractStackedController implements Initializable {

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

    protected AssetRepository assetRepository;
    protected NodeRepository nodeRepository;
    protected StateRepository stateRepository;
    protected ProfileRepository profileRepository;
    protected TestRepository testRepository;
    protected TypeRepository typeRepository;

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        initDb();
        //resetTables();

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

        cbAsset1.setCellFactory(new Callback<ListView<Asset>, ListCell<Asset>>() {
            @Override
            public ListCell<Asset> call(ListView<Asset> p) {
                return new ListCell<Asset>() {
                    @Override
                    protected void updateItem(Asset asset, boolean bln) {
                        super.updateItem(asset, bln);
                        if (asset != null) {
                            setText(asset.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        cbAsset2.setCellFactory(new Callback<ListView<Asset>, ListCell<Asset>>() {
            @Override
            public ListCell<Asset> call(ListView<Asset> p) {
                return new ListCell<Asset>() {
                    @Override
                    protected void updateItem(Asset asset, boolean bln) {
                        super.updateItem(asset, bln);
                        if (asset != null) {
                            setText(asset.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        cbAsset1.setConverter(new StringConverter<Asset>() {
            @Override
            public String toString(Asset asset) {
                if (asset == null){
                    return null;
                } else {
                    return asset.getName();
                }
            }

            @Override
            public Asset fromString(String name) {
                return null;
            }
        });

        cbAsset2.setConverter(new StringConverter<Asset>() {
            @Override
            public String toString(Asset asset) {
                if (asset == null){
                    return null;
                } else {
                    return asset.getName();
                }
            }

            @Override
            public Asset fromString(String name) {
                return null;
            }
        });

        List<Asset> assets = null;
        try {
            assets = assetRepository.getAll();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (assets != null) {
            cbAsset1.setItems(FXCollections.observableArrayList(assets));
            cbAsset2.setItems(FXCollections.observableArrayList(assets));
        }
    }

    protected void initDb() {
        try {
            Class.forName("org.sqlite.JDBC");
            ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:sample.db");

            assetRepository = new AssetRepository(connectionSource);
            nodeRepository = new NodeRepository(connectionSource);
            stateRepository = new StateRepository(connectionSource);
            profileRepository = new ProfileRepository(connectionSource);
            testRepository = new TestRepository(connectionSource);
            typeRepository = new TypeRepository(connectionSource);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    protected void resetTables() {
        try {
            assetRepository.createTable();
            nodeRepository.createTable();
            stateRepository.createTable();
            profileRepository.createTable();
            testRepository.createTable();
            typeRepository.createTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
