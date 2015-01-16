package org.priorityhealth.stab.pdiff;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.priorityhealth.stab.pdiff.controller.ControllerFactory;
import org.priorityhealth.stab.pdiff.controller.MainController;
import org.priorityhealth.stab.pdiff.persistence.repository.RepositoryFactory;

public class MainApp extends Application {

    /**
     *
     * @param stage Stage
     */
    @Override
    public void start(final Stage stage) throws Exception {
        Class.forName("org.sqlite.JDBC");
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:sample.db");

        RepositoryFactory repositoryFactory = new RepositoryFactory(connectionSource);
        ControllerFactory controllerFactory = new ControllerFactory(repositoryFactory);

        MainController mainController = new MainController(controllerFactory);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
        fxmlLoader.setControllerFactory(controllerFactory);

        Parent root = fxmlLoader.load();
        stage.setTitle("pDiff");
        stage.setScene(new Scene(root, 1024, 768));
        stage.show();
    }

    /**
     * Main
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
