package org.priorityhealth.stab.pdiff.domain.service.comparator;

import com.j256.ormlite.dao.ForeignCollection;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSException;
import org.apache.commons.validator.routines.UrlValidator;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.repository.asset.AssetRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.asset.NodeRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.profile.ProfileRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.service.event.BrowserStateChangeHandlerInterface;
import org.priorityhealth.stab.pdiff.domain.service.event.BrowserStateChangeListener;
import org.priorityhealth.stab.pdiff.domain.service.login.ul.Credentials;
import org.priorityhealth.stab.pdiff.domain.service.login.ul.Response;
import org.priorityhealth.stab.pdiff.domain.service.login.ul.UniversalLoginService;
import org.priorityhealth.stab.pdiff.service.DigestService;
import org.priorityhealth.stab.pdiff.domain.service.html.ParserService;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.service.ImageService;
import org.priorityhealth.stab.pdiff.service.LogService;

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
    protected WebView webView;
    protected WebEngine webEngine;
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

    protected boolean usingAlternateNodeList = false;
    protected boolean crawlNewNodes = true;

    protected ProfilerListenerInterface profilerListener;

    public ProfilerService(
            AssetRepositoryInterface assetRepository,
            NodeRepositoryInterface nodeRepository,
            StateRepositoryInterface stateRepository,
            ProfileRepositoryInterface profileRepository,
            String storePath
    ) {
        this.assetRepository = assetRepository;
        this.nodeRepository = nodeRepository;
        this.stateRepository = stateRepository;
        this.profileRepository = profileRepository;
        this.storePath = storePath;
    }

    public void init(Asset asset) {
        this.asset = asset;
        attachEvents();
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
    }

    public void setProfilerListener(ProfilerListenerInterface profilerListener) {
        this.profilerListener = profilerListener;
    }

    public void attachEvents() {
        webEngine.getLoadWorker().stateProperty().addListener(
                new BrowserStateChangeListener(webEngine, this)
        );

        webEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {
            @Override
            public void changed(ObservableValue<? extends Throwable> ov, Throwable t, Throwable t1) {
                LogService.Info(this, "Received exception: " + t1.getMessage());
            }
        });
    }

    public void run() {
        if (asset == null) {
            LogService.Info(this, "There was no asset to profile.");
            return;
        }

        if (webView == null) {
            LogService.Info(this, "There was no webview available.");
            return;
        }

        profile = new Profile();
        profile.setAsset(asset).setCreated(new Date());
        try {
            profileRepository.create(profile);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return;
        }

        if (!usingAlternateNodeList) {
            queue = new ArrayList<Node>();
            queue.addAll(asset.getNodes());
        } else {
            crawlNewNodes = false;
        }

        LogService.Info(this, "Run ID: " + profile.getId());

        // Logging in Asset if necessary
        if (asset.getLoginServer() != null && asset.getLoginServer().length() > 0) {
            Response response = UniversalLoginService.getAuthToken(
                    asset.getLoginServer(),
                    new Credentials(asset.getUsername(), asset.getPassword())
            );

            if (response.isSuccess()) {
                String token = response.getMessage();
                LogService.Info(this, "Token: " + token);
                if (asset.getLoginNodeUrl() != null && asset.getLoginNodeUrl().length() > 0) {
                    String loginNode = asset.getLoginNodeUrl().replace("<<token>>", token);
                    LogService.Info(this, "Using login node: " + loginNode);
                    loadDocument(loginNode, false);
                    return;
                } else {
                    LogService.Info(this, "There was no login node.");
                }
            } else {
                LogService.Info(this, "Login failed: " + response.getMessage());
            }
            LogService.Info(this, "Login has failed.");
        } else {
            loadNextNode();
        }
    }

    public void setAlternateNodeList(ForeignCollection<Node> nodes) {
        queue = new ArrayList<Node>();
        queue.addAll(nodes);
        usingAlternateNodeList = true;
    }

    public void setCrawlNewNodes(boolean crawl) {
        crawlNewNodes = crawl;
    }

    private double setDocumentHeight() {
        String heightText = webEngine.executeScript(
                "window.getComputedStyle(document.body, null).getPropertyValue('height')"
        ).toString();
        Double newHeight = Math.ceil(Double.valueOf(heightText.replace("px", "")));

        LogService.Info(this, "New page height: " + newHeight);

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
                LogService.Info(this, "Has the page finished? ");
                isFinished = (Boolean) webEngine.executeScript(asset.getLoadCompleteScript());
            } catch (JSException ex) {
                LogService.Info(this, "JSException: " + ex.getMessage());
            }
        } else {
            LogService.Info(this, "There was no completion script.");
            isFinished = true;
        }

        if (!isFinished) {
            LogService.Info(this, "Nope. Waiting " + (waitTime / 1000) + "s ...");
            PauseTransition pause = new PauseTransition(Duration.millis(waitTime));
            pause.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    onPageLoadSuccess();
                }
            });
            pause.play();
        } else {
            LogService.Info(this, "Yes!");

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
        LogService.Info(this, "Reloading: " + currentUrl);
        webEngine.reload();
    }

    protected void loadDocument(String url) {
        loadDocument(url, true);
    }

    /**
     *
     * @param url Source
     */
    protected void loadDocument(final String url, boolean continueOnError) {
        String previousUrl = currentUrl;
        currentUrl = formatNodeUrl(url);

        urlHash = DigestService.getSHA1(currentUrl.getBytes());
        LogService.Info(this, "Loading: " + currentUrl);

        UrlValidator urlValidator = new UrlValidator();
        if (currentUrl.startsWith("http://localhost") || urlValidator.isValid(currentUrl)) {
            LogService.Info(this, "Url valid.");

            webEngine.load(currentUrl);

            // If the page doesn't need to reload, no event state will be triggered.
            // Wait half a second and see if any asynchronous items are being loaded.
            if (previousUrl != null) {
                String documentUrlCurrent = getDocumentUrl(currentUrl);
                String documentUrlPrevious = getDocumentUrl(previousUrl);
                LogService.Info(this, "Match? " + documentUrlPrevious + " == " + documentUrlCurrent);
                if (documentUrlPrevious.equals(documentUrlCurrent)) {
                    PauseTransition pause = new PauseTransition(Duration.millis(500));
                    pause.setOnFinished(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            onPageLoadSuccess();
                        }
                    });
                    pause.play();
                }
            }
        } else {
            LogService.Info(this, "Url invalid.");
            if (continueOnError) {
                loadNextNode();
            } else {
                LogService.Info(this, "Load halted.");
            }
        }
    }

    protected void loadNextNode()
    {
        LogService.Info(this, "Loading Next Node");
        if (queue.size() > queueIndex) {
            currentNode = queue.get(queueIndex);
            queueIndex++;

            loadDocument(currentNode.getUrl());
        } else {
            onProfileComplete();
        }
    }

    protected String formatNodeUrl(String url) {
        if (!url.startsWith(asset.getDomain()) && !url.startsWith("http")) {
            return asset.getDomain() + url;
        }
        return url;
    }

    protected String getDocumentUrl(String url) {
        if (url.contains("#")) {
            String[] parts = url.split("#");
            if (parts.length > 0) {
                url = parts[0];
            }
        }
        return url;
    }

    protected void onProfileComplete() {
        LogService.Info(this, "Persisting profile");
        this.profile.setComplete(true);
        try {
            profileRepository.update(this.profile);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        if (profilerListener != null) {
            profilerListener.onProfileComplete(profile);
        }
    }

    /**
     *
     */
    private void recordVisualState() {
        if (currentNode == null) {
            LogService.Info(this, "No node to record.");
            return;
        }

        LogService.Info(this, "Recording visual state of node: " + currentNode.getUrl());

        String imageStorePath = storePath + File.separator + profile.getId() + File.separator + urlHash + ".png";

        BufferedImage nodeImage = ImageService.getBufferedImageFromWebView(webView);
        ImageService.saveImageFromBufferedImage(nodeImage, imageStorePath);

        State nodeState = new State();
        nodeState
                .setNode(currentNode)
                .setProfile(profile)
                .setImagePath(imageStorePath)
                .setCreated(new Date());

        try {
            stateRepository.create(nodeState);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addUrlToCrawl(String url) {
        if (url.startsWith(asset.getDomain())) {
            url = url.substring(asset.getDomain().length());
        }

        LogService.Info(this, "Checking queue for: " + url + " ... ");

        boolean found = false;
        for (Node node : asset.getNodes()) {
            if (node.getUrl().equals(url)) {
                found = true;
                break;
            }
        }
        if (!found) {
            LogService.Info(this, "Not Found ... adding.");
            Node newNode = new Node();
            newNode
                    .setUrl(url)
                    .setAsset(asset)
                    .setCreated(new Date());
            asset.addNode(newNode);
            queue.add(newNode);
        } else {
            LogService.Info(this, "Found");
        }
    }

    /**
     *
     */
    public void processDocument() {
        if (crawlNewNodes) {
            List<String> urlList = ParserService.getLinks(webEngine);
            for (String url : urlList) {
                addUrlToCrawl(url);
            }
        }
        recordVisualState();
        loadNextNode();
    }
}
