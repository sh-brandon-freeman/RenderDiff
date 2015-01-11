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
import renderdiff.domain.entity.asset.Asset;
import renderdiff.domain.entity.profile.Profile;
import renderdiff.domain.entity.asset.Node;
import renderdiff.domain.entity.profile.State;
import renderdiff.domain.repository.asset.AssetRepositoryInterface;
import renderdiff.domain.repository.profile.ProfileRepositoryInterface;
import renderdiff.domain.repository.asset.NodeRepositoryInterface;
import renderdiff.domain.repository.profile.StateRepositoryInterface;
import renderdiff.service.event.BrowserStateChangeListener;
import renderdiff.service.event.BrowserStateChangeHandlerInterface;
import renderdiff.service.general.DigestService;
import renderdiff.service.general.DateTimeService;
import renderdiff.service.general.ImageService;
import renderdiff.service.general.TextFileService;
import renderdiff.service.html.ParserService;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class ProfilerService implements BrowserStateChangeHandlerInterface {

    public static final int waitTime = 5000;
    protected final WebView webView;
    protected final WebEngine webEngine;
    protected String profileTimestamp;
    protected String currentUrl;
    protected String urlHash;

    protected AssetRepositoryInterface assetRepository;
    protected NodeRepositoryInterface nodeRepository;
    protected StateRepositoryInterface stateRepository;
    protected ProfileRepositoryInterface profileRepository;

    protected String storePath;

    protected Asset asset;
    protected Node currentNode;
    protected Profile profile;

    protected List<Node> queue;
    protected int queueIndex;

    /**
     *
     * @param webView
     * @param asset
     * @param storePath
     * @param assetRepository
     * @param nodeRepository
     * @param stateRepository
     * @param profileRepository
     */
    public ProfilerService(
            WebView webView,
            Asset asset,
            String storePath,
            AssetRepositoryInterface assetRepository,
            NodeRepositoryInterface nodeRepository,
            StateRepositoryInterface stateRepository,
            ProfileRepositoryInterface profileRepository
    ) {
        this.webView = webView;
        this.asset = asset;
        this.storePath = storePath;

        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
        this.stateRepository = stateRepository;
        this.profileRepository = profileRepository;

        this.webEngine = webView.getEngine();

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
        if (asset == null) {
            System.out.println("There was no asset to profile.");
            return;
        }

        this.profile = new Profile();
        this.profile.setAsset(asset).setCreated(new Date());

        queue = new ArrayList<Node>();
        queue.addAll(asset.getNodes());

        System.out.println("Run ID: " + profile.getId());

        if (asset.getLoginNodeUrl() != null && asset.getLoginNodeUrl().length() > 0) {
            loadDocument(asset.getLoginNodeUrl());
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

        if (newHeight > 6000) {
            newHeight = 6000d;
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
        if (asset.getLoadCompleteScript() != null && asset.getLoadCompleteScript().length() > 0) {
            try {
                System.out.print("Has the page finished? ");
                isFinished = (Boolean) webEngine.executeScript(asset.getLoadCompleteScript());
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

            String url = currentNode.getUrl();
            if (!url.startsWith(asset.getDomain()) && !url.startsWith("http")) {
                url = asset.getDomain() + url;
            }

            loadDocument(url);
        } else {
            onProfileComplete();
        }
    }

    protected void onProfileComplete() {
        System.out.println("Persisting profile");

        try {
            profileRepository.create(profile);
            for (State state : profile.getStates()) {
                stateRepository.create(state);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    private void recordVisualState() {
        if (currentNode == null) {
            System.out.println("No asset to compare.");
            return;
        }

        System.out.println("Recording visual state of node: " + currentNode.getUrl());

        String imageStorePath = storePath + File.separator + profile.getId() + File.separator + urlHash + ".png";

        BufferedImage nodeImage = ImageService.getBufferedImageFromWebView(webView);
        ImageService.saveImageFromBufferedImage(nodeImage, imageStorePath);

        State nodeState = new State();
        nodeState
                .setNode(currentNode)
                .setProfile(profile)
                .setImagePath(imageStorePath)
                .setCreated(new Date());

        profile.addState(nodeState);
    }

    public void addUrlToCrawl(String url) {
        if (url.startsWith(asset.getDomain())) {
            url = url.substring(asset.getDomain().length());
        }

        System.out.print("Checking queue for: " + url + " ... ");

        boolean found = false;
        for (Node node : asset.getNodes()) {
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
                    .setAsset(asset)
                    .setCreated(new Date());
            asset.addNode(newNode);
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
        recordVisualState();
        loadNextNode();
    }
}
