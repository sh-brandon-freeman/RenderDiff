package renderdiff.service.comparator;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import renderdiff.service.event.BrowserStateChangeListener;
import renderdiff.service.event.BrowserStateChangeHandlerInterface;
import renderdiff.service.general.CryptoService;
import renderdiff.service.general.DateTimeService;
import renderdiff.service.general.ImageService;
import renderdiff.service.general.TextFileService;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

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
    protected String basePath = "C:\\save\\";


    public PageAnalyzerService(
        WebView webView,
        PageQueueInterface pageQueue
    ) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
        this.pageQueue = pageQueue;

        //autoloadJs = TextFileService.readTextResource(this, "autoload.js");
        currentTimestamp = DateTimeService.getTimestamp("yyyy_MM_dd_HH_mm_ss");
        currentPath = basePath + currentTimestamp + "\\";

        webEngine.getLoadWorker().stateProperty().addListener(
            new BrowserStateChangeListener(webEngine, this)
        );
    }

    @Override
    public void onChangeSuccess() {
        System.out.println("Waiting " + waitTime / 1000 + "s");
        PauseTransition pause = new PauseTransition(Duration.millis(waitTime));
        pause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                processDocument();
            }
        });
        pause.play();
    }

    @Override
    public void handleStall() {
        System.out.println("Reloading: " + currentUrl);
        webEngine.reload();
    }

    public void loadDocument(final String url) {
        this.currentUrl = url;
        this.urlHash = CryptoService.getSHA1(currentUrl.getBytes());
        System.out.println("Loading: " + currentUrl);
        webEngine.load(url);
    }

    private void injectJavascript() {
//        try {
//            webEngine.executeScript(autoloadJs);
//            boolean isFinished = (Boolean) webEngine.executeScript("isLoadComplete();");
//            System.out.println(isFinished);
//        } catch (JSException ex) {
//            System.out.println("JS error: " + ex.getMessage());
//        }
    }

    private void getLinks() {
        URI uri;
        try {
            uri = new URI(webEngine.getLocation());
        } catch (URISyntaxException ex) {
            return;
        }

        String originalUrl = webEngine.getLocation();
        boolean isSecure = originalUrl.toLowerCase().contains("https://");

        int port = uri.getPort();
        String host = port == 80 ? uri.getHost() : uri.getHost() + ":" + port;

        System.out.println("Getting links within: " + currentUrl);
        org.jsoup.nodes.Document doc = Jsoup.parse(getDocumentData());
        Elements elements = doc.select("a[href]");

        for (Element element : elements) {
            String url = element.attr("href");

            if (url.length() == 0 || url.equals("?") || url.equals("#") || url.equals("/")) {
                continue;
            }

            if (url.contains("phone:") || url.contains("mailto:")) {
                continue;
            }

            try {
                uri = new URI(url);
            } catch (URISyntaxException ex) {
                continue;
            }

            if (uri.isAbsolute() && uri.getHost() != null && !uri.getHost().equals(host)) {
                continue;
            }

            if (url.substring(0, 1).equals("#")) {
                url = "/" + url;
            }

            pageQueue.addUrl(uri.isAbsolute() ? url : (isSecure ? "https://" : "http://") + host + url);
        }
    }

    private void compareVisualState() {
        TextFileService.saveStringToFile(getDocumentData(), currentPath + File.separator + urlHash + ".html");
        BufferedImage currentView = ImageService.getBufferedImageFromWebView(webView);
        BufferedImage knownView =
            ImageService.loadBufferedImageFromFile(basePath + knownState + File.separator + urlHash + ".png");

        if (knownView == null) {
            System.out.println("No known view to compare.");
        }

        if (currentView == null) {
            System.out.println("No current view to compare.");
        }

        if (currentView == null || knownView == null) {
            return;
        }

        ImageCompareService imageCompare = new ImageCompareService(8, 6, 0);
        imageCompare.setDebugMode(2);
        boolean matchesKnownState = imageCompare.compare(knownView, currentView);

        System.out.println("Matches known state: " + matchesKnownState);

        if (!matchesKnownState) {
            ImageService.saveImageFromBufferedImage(
                imageCompare.getChangeIndicator(),
                currentPath + File.separator + urlHash + "-delta.png"
            );

            ImageService.saveImageFromBufferedImage(
                knownView,
                currentPath + File.separator + urlHash + "-known.png"
            );

            ImageService.saveImageFromBufferedImage(
                currentView,
                currentPath + File.separator + urlHash + "-current.png"
            );
        }
    }

    private void processDocument() {
        injectJavascript();
        //getLinks();

        compareVisualState();

        pageQueue.iterate();
    }

    private String getDocumentData() {
        String result = null;
        Document doc = webEngine.getDocument();
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(stringWriter));
            result = stringWriter.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
