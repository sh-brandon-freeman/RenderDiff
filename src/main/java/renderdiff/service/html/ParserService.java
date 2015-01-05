package renderdiff.service.html;

import javafx.scene.web.WebEngine;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser Service
 */
public class ParserService {
    /**
     *
     * @param webEngine WebEngine
     * @return Document HTML
     */
    public static String getDocumentData(WebEngine webEngine) {
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

    /**
     *
     * @param webEngine WebEngine
     * @return List of urls in document
     */
    public static List<String> getLinks(WebEngine webEngine) {
        URI uri;
        try {
            uri = new URI(webEngine.getLocation());
        } catch (URISyntaxException ex) {
            return null;
        }

        String originalUrl = webEngine.getLocation();
        boolean isSecure = originalUrl.toLowerCase().contains("https://");

        int port = uri.getPort();
        String host = port == 80 ? uri.getHost() : uri.getHost() + ":" + port;

        org.jsoup.nodes.Document doc = Jsoup.parse(getDocumentData(webEngine));
        Elements elements = doc.select("a[href]");

        List<String> urlList = new ArrayList<String>();

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

            String result = uri.isAbsolute() ? url : (isSecure ? "https://" : "http://") + host + url;

            if (urlList.indexOf(result) == -1) {
                urlList.add(result);
            }
        }

        return urlList;
    }
}
