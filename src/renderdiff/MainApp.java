package renderdiff;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import renderdiff.service.comparator.PageAnalyzerService;
import renderdiff.service.comparator.PageQueueInterface;

import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application implements PageQueueInterface {

    protected List<String> urls = new ArrayList<String>();
    protected int current = 0;
    protected PageAnalyzerService pageAnalyzerService;

    @Override
    public void start(final Stage stage) {
        stage.setTitle("Browser");
        stage.setWidth(1024);
        stage.setHeight(768);
        Scene scene = new Scene(new Group());
        VBox root = new VBox();

        urls.add("http://localhost:3000/#/splash");

        final WebView webView = new WebView();

        pageAnalyzerService = new PageAnalyzerService(webView, this);
        Button btnStart = new Button("Start");
        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                pageAnalyzerService.loadDocument(urls.get(0));
            }
        });

        root.getChildren().addAll(btnStart, webView);
        scene.setRoot(root);

        stage.setScene(scene);
        stage.show();
    }

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

    @Override
    public void iterate() {
        current++;
        if (urls.size() > current) {
            System.out.println("Iterating to next url.");
            pageAnalyzerService.loadDocument(urls.get(current));
        } else {
            System.out.println("Url list exhausted.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
