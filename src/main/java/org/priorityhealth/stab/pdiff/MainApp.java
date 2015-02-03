package org.priorityhealth.stab.pdiff;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.priorityhealth.stab.pdiff.controller.AbstractController;
import org.priorityhealth.stab.pdiff.controller.factory.ControllerFactory;
import org.priorityhealth.stab.pdiff.domain.service.comparator.factory.ComparatorServiceFactory;
import org.priorityhealth.stab.pdiff.persistence.repository.factory.RepositoryFactory;
import org.priorityhealth.stab.pdiff.service.ParameterService;
import org.priorityhealth.stab.pdiff.service.http.UrlMonitoringStreamHandlerFactory;
import org.priorityhealth.stab.pdiff.view.headless.HeadlessAppLauncher;

import java.net.URL;
import java.util.List;

public class MainApp extends Application {

    /**
     *
     * @param stage Stage
     */
    @Override
    public void start(final Stage stage) throws Exception {

        ParameterService.setParameters(getParameters().getRaw());

        URL.setURLStreamHandlerFactory(new UrlMonitoringStreamHandlerFactory());

        Class.forName("org.sqlite.JDBC");
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:sample.db");

        RepositoryFactory repositoryFactory = new RepositoryFactory(connectionSource);
        ControllerFactory controllerFactory = new ControllerFactory(repositoryFactory);
        ComparatorServiceFactory.setRepositoryFactory(repositoryFactory);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/view/main.fxml"));
        fxmlLoader.setControllerFactory(controllerFactory);

        Parent root = fxmlLoader.load();
        stage.setTitle("pDiff");
        stage.setScene(new Scene(root, 1024, 768));
        stage.show();
    }

    /**
     * Main
     *
     * @param appArgs Arguments
     */
    public static void main(String[] appArgs) {
        new HeadlessAppLauncher().launch(MainApp.class, appArgs);
    }
}
