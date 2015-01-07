package renderdiff;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import renderdiff.domain.entity.crawl.Crawl;
import renderdiff.persistence.repository.crawl.CrawlRepository;
import renderdiff.persistence.repository.crawl.RunRepository;
import renderdiff.persistence.repository.node.NodeRepository;
import renderdiff.persistence.repository.node.ResultRepository;
import renderdiff.service.comparator.CrawlService;
import renderdiff.service.comparator.PageQueueInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application implements PageQueueInterface {

    protected List<String> urls = new ArrayList<String>();
    protected int current = 0;
    protected CrawlService crawlService;

    double prefWidth = 1000;
    double prefHeight = 8000;

    protected CrawlRepository crawlRepository;
    protected NodeRepository nodeRepository;
    protected ResultRepository resultRepository;
    protected RunRepository runRepository;

    /**
     *
     * @param stage Stage
     */
    @Override
    public void start(final Stage stage) {
        initDb();

        stage.setTitle("Browser");
        stage.setWidth(1024);
        stage.setHeight(768);
        Scene scene = new Scene(new Group());
        VBox root = new VBox();

        //urls.add("http://localhost:3000/#/splash");
        urls.add("http://localhost:3150/#redirect=true");

        final WebView webView = new WebView();
        webView.setPrefSize(1000, 700);

        ScrollPane webViewScroll = new ScrollPane();
        webViewScroll.setContent(webView);
        webViewScroll.setPrefSize(800, 500);

        Crawl crawl = null;
        try {
            crawl = this.crawlRepository.getById(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        crawlService = new CrawlService(
                webView,
                this,
                crawl,
                crawlRepository,
                nodeRepository,
                resultRepository,
                runRepository
        );
        Button btnStart = new Button("Start");
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                crawlService.begin();
            }
        });

        root.getChildren().addAll(btnStart, webViewScroll);
        scene.setRoot(root);

        stage.setScene(scene);
        stage.show();
    }

    protected void initDb() {
        try {
            Class.forName("org.sqlite.JDBC");
            ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:sample.db");

            crawlRepository = new CrawlRepository(connectionSource);
            nodeRepository = new NodeRepository(connectionSource);
            //nodeRepository.createTable();
            resultRepository = new ResultRepository(connectionSource);
            //resultRepository.createTable();
            runRepository = new RunRepository(connectionSource);
            //runRepository.createTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    protected void setViewSize(WebView webView, double prefWidth, double prefHeight) {
        if (prefWidth < 100) {
            prefWidth = 100;
        }

        if (prefWidth > 2000) {
            prefWidth = 2000;
        }

        if (prefHeight < 100) {
            prefHeight = 100;
        }

        if (prefHeight > 16000) {
            prefHeight = 16000;
        }

        webView.setPrefWidth(prefWidth);
        webView.setPrefHeight(prefHeight);
    }

    /**
     * Add url to list.
     *
     * @param url URL to add
     */
    @Override
    public void addUrl(String url) {
        System.out.print("Attempting to add: " + url);
        if (urls.indexOf(url) == -1) {
            urls.add(url);
            System.out.println(" (Success)");
        } else {
            System.out.println(" (Duplicate)");
        }
    }

    /**
     * Iterate to next url
     */
    @Override
    public void iterate() {
        current++;
        if (urls.size() > current) {
            System.out.println("Iterating to next url.");
            crawlService.loadDocument(urls.get(current));
        } else {
            System.out.println("Url list exhausted.");
        }
    }


    private ScrollPane makeScrollable(final ImageView imageView) {
        final ScrollPane scroll = new ScrollPane();
        final StackPane centeredImageView = new StackPane();

        centeredImageView.getChildren().add(imageView);
        scroll.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) {
                centeredImageView.setPrefSize(
                        Math.max(imageView.prefWidth(bounds.getHeight()), bounds.getWidth()),
                        Math.max(imageView.prefHeight(bounds.getWidth()), bounds.getHeight())
                );
            }
        });
        scroll.setContent(centeredImageView);

        return scroll;
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
