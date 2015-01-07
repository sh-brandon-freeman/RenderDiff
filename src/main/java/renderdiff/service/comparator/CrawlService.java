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
import renderdiff.domain.entity.crawl.Crawl;
import renderdiff.domain.entity.crawl.Run;
import renderdiff.domain.entity.node.Node;
import renderdiff.domain.entity.node.Result;
import renderdiff.domain.repository.crawl.CrawlRepositoryInterface;
import renderdiff.domain.repository.crawl.RunRepositoryInterface;
import renderdiff.domain.repository.node.NodeRepositoryInterface;
import renderdiff.domain.repository.node.ResultRepositoryInterface;
import renderdiff.persistence.repository.node.ResultRepository;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class CrawlService implements BrowserStateChangeHandlerInterface {

    public static final int waitTime = 5000;
    public static final String knownState = "known_state";
    protected final WebView webView;
    protected final WebEngine webEngine;
    protected String crawlTimestamp;
    protected String currentUrl;
    protected String urlHash;
    protected PageQueueInterface pageQueue;
    protected String autoloadJs = "";
    protected String basePath = System.getProperty("user.home") + File.separator + "pdiff" + File.separator;

    protected CrawlRepositoryInterface crawlRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected ResultRepositoryInterface resultRepository;
    protected RunRepositoryInterface runRepository;

    protected Crawl crawl;
    protected Node currentNode;
    protected Run run;

    protected List<Node> queue;
    protected int queueIndex;

    /**
     * Constructor
     *
     * @param webView WebView
     * @param pageQueue Supplier of pages.
     */
    public CrawlService(
            WebView webView,
            PageQueueInterface pageQueue,
            Crawl crawl,
            CrawlRepositoryInterface crawlRepository,
            NodeRepositoryInterface nodeRepository,
            ResultRepositoryInterface resultRepository,
            RunRepositoryInterface runRepository
    ) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
        this.pageQueue = pageQueue;

        this.crawlRepository = crawlRepository;
        this.nodeRepository = nodeRepository;
        this.resultRepository = resultRepository;
        this.runRepository = runRepository;

        this.crawl = crawl;

        queue = new ArrayList<Node>();
        queue.addAll(crawl.getNodes());

        autoloadJs = TextFileService.readTextResource(this, "/autoload.js");
        crawlTimestamp = DateTimeService.getTimestamp("yyyy_MM_dd_HH_mm_ss");

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

    public void begin() {
        this.run = new Run();
        this.run.setCrawl(crawl).setCreated(new Date());

        try {
            runRepository.create(run);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        System.out.println("Run ID: " + run.getId());

        if (crawl.getLoginNodeUrl() != null && crawl.getLoginNodeUrl().length() > 0) {
            loadDocument(crawl.getLoginNodeUrl());
        } else {
            loadNextNode();
        }
    }

    private double setDocumentHeight() {
        String heightText = webView.getEngine().executeScript(
                "window.getComputedStyle(document.body, null).getPropertyValue('height')"
        ).toString();
        Double newHeight = Math.ceil(Double.valueOf(heightText.replace("px", "")));

        System.out.println("New page height: " + newHeight);

        if (newHeight < 100) {
            newHeight = 100d;
        }

        if (newHeight > 16000) {
            newHeight = 16000d;
        }

        webView.setPrefHeight(newHeight);

        return newHeight;
    }

    /**
     *
     */
    @Override
    public void onPageLoadSuccess() {

        boolean isFinished = false;
        if (crawl.getLoadCompleteScript() != null && crawl.getLoadCompleteScript().length() > 0) {
            try {
                System.out.print("Has the page finished? ");
                isFinished = (Boolean) webEngine.executeScript(crawl.getLoadCompleteScript());
            } catch (JSException ex) {
                System.out.println("JSException: " + ex.getMessage());
            }
        } else {
            System.out.println("There was no completion script.");
            isFinished = true;
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

            setDocumentHeight();

            // Wait a tick before you process the document to allow it to render after resize.
            PauseTransition pause = new PauseTransition(Duration.millis(500));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    processDocument();
                }
            });
            pause.play();
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

    public void loadNextNode()
    {
        System.out.println("Loading Next Node");
        if (queue.size() > queueIndex) {
            currentNode = queue.get(queueIndex);
            queueIndex++;
            loadDocument(currentNode.getUrl());
        } else {
            closeCrawl();
        }
    }

    protected void closeCrawl() {
        System.out.println("Closing crawl");
//        try {
//            crawlRepository.update(crawl);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }

    /**
     *
     */
    private void compareVisualState() {
        if (currentNode == null) {
            System.out.println("No node to compare.");
            return;
        }

        String currentStoredPath = crawlTimestamp + File.separator + "store" + File.separator + urlHash + ".png";
        String knownStoredPath = knownState + File.separator + urlHash + ".png";

        String currentImagePath = crawlTimestamp + File.separator + urlHash + "-now.png";
        String knownImagePath = crawlTimestamp + File.separator + urlHash + "-known.png";
        String diffImagePath = crawlTimestamp + File.separator + urlHash + "-diff.png";

        File knownFile = new File(basePath + knownStoredPath);
        if (knownFile.exists() && !knownFile.isDirectory()) {

            try {
                Path tempDiff = Files.createTempFile("diff-temp", ".png");
                Path tempKnown = Files.createTempFile("diff-known", ".png");
                Path tempCurrent = Files.createTempFile("diff-now", ".png");

                BufferedImage imageCurrent = ImageService.getBufferedImageFromWebView(webView);
                BufferedImage imageKnown = ImageService.loadBufferedImageFromFile(basePath + knownStoredPath);

                // Save unmodified copy to store.
                ImageService.saveImageFromBufferedImage(imageCurrent, basePath + currentStoredPath);

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

                Result nodeResult = new Result();
                nodeResult
                        .setNode(currentNode)
                        .setRun(run)
                        .setCurrentImagePath(currentStoredPath)
                        .setKnownImagePath(knownStoredPath)
                        .setCreated(new Date());

                if (isDifferent) {
                    Files.move(tempDiff, Paths.get(basePath + diffImagePath), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(tempKnown, Paths.get(basePath + knownImagePath), StandardCopyOption.REPLACE_EXISTING);
                    Files.copy(tempCurrent, Paths.get(basePath + currentImagePath), StandardCopyOption.REPLACE_EXISTING);

                    nodeResult.setDiffImagePath(diffImagePath);
                } else {
                    // Don't rely on others to do your dirty work ...
                    Files.delete(tempDiff);
                    Files.delete(tempKnown);
                    Files.delete(tempCurrent);
                }
                resultRepository.create(nodeResult);
            } catch (IOException ex) {
                System.out.println("IOException" + ex.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("No known state file found");
        }
    }

    public void addUrlToCrawl(String url) {
        System.out.print("Checking queue for: " + url + " ... ");
        boolean found = false;
        for (Node node : crawl.getNodes()) {
            if (node.getUrl().equals(url)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Not Found ... adding.");
            Node newNode = new Node();
            newNode
                    .setUrl(url)
                    .setCrawl(crawl)
                    .setCreated(new Date());
            crawl.addNode(newNode);
            queue.add(newNode);
        } else {
            System.out.println("Found");
        }
    }

    /**
     *
     */
    public void processDocument() {
        List<String> urlList = ParserService.getLinks(webEngine);
        for (String url : urlList) {
            addUrlToCrawl(url);
        }
        compareVisualState();
        loadNextNode();
    }
}
