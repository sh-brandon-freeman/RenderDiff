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
import org.w3c.dom.Document;
import renderdiff.service.event.BrowserStateChangeListener;
import renderdiff.service.event.BrowserStateChangeHandlerInterface;
import renderdiff.service.general.DigestService;
import renderdiff.service.general.DateTimeService;
import renderdiff.service.general.ImageService;
import renderdiff.service.general.TextFileService;
import renderdiff.service.html.ParserService;

import java.awt.*;
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

    private double setDocumentHeight() {
        String heightText = webView.getEngine().executeScript(
                "window.getComputedStyle(document.body, null).getPropertyValue('height')"
        ).toString();
        Double newHeight = Math.ceil(Double.valueOf(heightText.replace("px", "")));

        System.out.println("New page height: " + newHeight);

        webView.setPrefHeight(newHeight);

        return newHeight;
    }

    /**
     *
     */
    @Override
    public void onPageLoadSuccess() {
        boolean isFinished = false;
        try {
            System.out.print("Has the page finished? ");
            isFinished = (Boolean) webEngine.executeScript("window.isLoadComplete();");
        } catch (JSException ex) {
            System.out.println("JSException: " + ex.getMessage());
        }
        if (!isFinished) {
            System.out.println("Nope. Waiting " + (waitTime / 1000) + "s ...");
            PauseTransition pause = new PauseTransition(Duration.millis(waitTime));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    onPageLoadSuccess();
                }
            });
            pause.play();
        } else {
            System.out.println("Yes!");
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
        String currentStoredPath = currentPath + File.separator + "store" + File.separator + urlHash + ".png";
        String knownStoredPath = basePath + knownState + File.separator + urlHash + ".png";

        String currentImagePath = currentPath + File.separator + urlHash + "-now.png";
        String knownImagePath = currentPath + File.separator + urlHash + "-known.png";
        String diffImagePath = currentPath + File.separator + urlHash + "-diff.png";

        File knownFile = new File(knownStoredPath);
        if (knownFile.exists() && !knownFile.isDirectory()) {

            try {
                Path tempDiff = Files.createTempFile("diff-temp", ".png");
                Path tempKnown = Files.createTempFile("diff-known", ".png");
                Path tempCurrent = Files.createTempFile("diff-now", ".png");

                BufferedImage imageCurrent = ImageService.getBufferedImageFromWebView(webView);
                BufferedImage imageKnown = ImageService.loadBufferedImageFromFile(knownStoredPath);

                // Save unmodified copy to store.
                ImageService.saveImageFromBufferedImage(imageCurrent, currentStoredPath);

                Rectangle dimensions = ImageService.calculateMaxDimensions(imageKnown, imageCurrent);

                imageKnown = ImageService.resizeImageCanvas(imageKnown, dimensions);
                imageCurrent = ImageService.resizeImageCanvas(imageCurrent, dimensions);

                ImageService.saveImageFromBufferedImage(imageKnown, tempKnown.toString());
                ImageService.saveImageFromBufferedImage(imageCurrent, tempCurrent.toString());

                boolean isDifferent = ImageCompareService.pdiff(
                        tempKnown.toString(),
                        tempCurrent.toString(),
                        tempDiff.toString()
                );

                if (isDifferent) {
                    Files.move(tempDiff, Paths.get(diffImagePath), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(tempKnown, Paths.get(knownImagePath), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(tempCurrent, Paths.get(currentImagePath), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    // Don't rely on others to do your dirty work ...
                    Files.delete(tempDiff);
                    Files.delete(tempKnown);
                    Files.delete(tempCurrent);
                }
            } catch (IOException ex) {
                System.out.println("IOException" + ex.getMessage());
            }
        }
    }

    /**
     *
     */
    public void processDocument() {
        setDocumentHeight();
        List<String> urlList = ParserService.getLinks(webEngine);
        for (String url : urlList) {
            pageQueue.addUrl(url);
        }
        compareVisualState();
        pageQueue.iterate();
    }
}
