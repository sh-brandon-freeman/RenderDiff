package renderdiff.service.comparator;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSException;
import renderdiff.service.event.BrowserStateChangeListener;
import renderdiff.service.event.BrowserStateChangeHandlerInterface;
import renderdiff.service.general.DigestService;
import renderdiff.service.general.DateTimeService;
import renderdiff.service.general.ImageService;
import renderdiff.service.general.TextFileService;
import renderdiff.service.html.ParserService;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 *
 */
public class PageAnalyzerService implements BrowserStateChangeHandlerInterface {

    public static final int waitTime = 5000;
    public static final String knownState = "known_state";
    protected final WebView webView;
    protected final WebEngine webEngine;
    protected String currentPath;
    protected String currentTimestamp;
    protected String currentUrl;
    protected String urlHash;
    protected PageQueueInterface pageQueue;
    protected String autoloadJs = "";
    protected String basePath = System.getProperty("user.home") + File.separator + "pdiff" + File.separator;

    /**
     * Constructor
     *
     * @param webView WebView
     * @param pageQueue Supplier of pages.
     */
    public PageAnalyzerService(
        WebView webView,
        PageQueueInterface pageQueue
    ) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
        this.pageQueue = pageQueue;

        autoloadJs = TextFileService.readTextResource(this, "/autoload.js");
        currentTimestamp = DateTimeService.getTimestamp("yyyy_MM_dd_HH_mm_ss");
        currentPath = basePath + currentTimestamp + File.separator;

        webEngine.getLoadWorker().stateProperty().addListener(
            new BrowserStateChangeListener(webEngine, this)
        );

        webEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override
            public void changed(ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) {
                System.out.println("Received exception: " + t1.getMessage());
            }
        });
    }

    /**
     *
     */
    @Override
    public void onPageLoadSuccess() {
        System.out.println("Executing on Load Success.");

        boolean isFinished = false;
        try {
            isFinished = (Boolean) webEngine.executeScript("window.isLoadComplete();");
        } catch (JSException ex) {
            System.out.println("JSException: " + ex.getMessage());
        }
        if (!isFinished) {
            System.out.println("Page is not loaded.");
            System.out.println("Waiting " + waitTime / 1000 + "s");
            PauseTransition pause = new PauseTransition(Duration.millis(waitTime));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Once Again.");
                    onPageLoadSuccess();
                }
            });
            pause.play();
        } else {
            processDocument();
        }
    }

    /**
     *
     */
    @Override
    public void handleStall() {
        System.out.println("Reloading: " + currentUrl);
        webEngine.reload();
    }

    /**
     *
     * @param url Source
     */
    public void loadDocument(final String url) {
        this.currentUrl = url;
        this.urlHash = DigestService.getSHA1(currentUrl.getBytes());
        System.out.println("Loading: " + currentUrl);
        webEngine.load(url);
    }

    /**
     *
     */
    private void compareVisualState() {
        String storedPath = currentPath + File.separator + "store" + File.separator + urlHash + ".png";
        String knownStoredPath = basePath + knownState + File.separator + urlHash + ".png";

        String currentImagePath = currentPath + File.separator + urlHash + "-now.png";
        String knownImagePath = currentPath + File.separator + urlHash + "-known.png";
        String diffImagePath = currentPath + File.separator + urlHash + "-diff.png";

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile("tempfiles", ".png");
        } catch (IOException ex) {
            System.out.println("IOException" + ex.getMessage());
        }

        if (tempFile != null) {
            BufferedImage currentView = ImageService.getBufferedImageFromWebView(webView);
            ImageService.saveImageFromBufferedImage(currentView, storedPath);

            File knownFile = new File(knownImagePath);
            if (knownFile.exists() && !knownFile.isDirectory()) {
                boolean isDifferent = ImageCompareService.pdiff(knownStoredPath, storedPath, tempFile.toString());

                if (isDifferent) {
                    try {
                        Files.move(tempFile, Paths.get(diffImagePath), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(Paths.get(knownStoredPath), Paths.get(knownImagePath), StandardCopyOption.REPLACE_EXISTING);
                        Files.copy(Paths.get(storedPath), Paths.get(currentImagePath), StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        System.out.println("IOException" + ex.getMessage());
                    }
                }
            } else {
                System.out.println("No known state.");
            }
        }
    }

    /**
     *
     */
    public void processDocument() {
        List<String> urlList = ParserService.getLinks(webEngine);
        for (String url : urlList) {
            pageQueue.addUrl(url);
        }
        compareVisualState();
        pageQueue.iterate();
    }
}
