package renderdiff.service.comparator;

import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

public class HtmlComparatorService {

    public static String diff(String s1, String s2) {

        try {
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

            TransformerHandler result = tf.newTransformerHandler();
            StringWriter resultWriter = new StringWriter();
            result.setResult(new StreamResult(resultWriter));

            result.startDocument();
            String tag = Long.toString(System.currentTimeMillis(), 3);
            result.startElement("", tag, tag, new AttributesImpl());
            DaisyDiff.diffTag(s1, s2, result);

            result.endDocument();
            result.endElement("", tag, tag);
            return resultWriter.toString();
            //String string = resultWriter.toString();
            //return string.substring(string.indexOf("<"+tag+">")+tag.length()+2, string.indexOf("</"+tag+">"));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static String diff(InputStream html1, InputStream html2) {
        try {
            SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();

            TransformerHandler result = tf.newTransformerHandler();
            StringWriter resultWriter = new StringWriter();
            result.setResult(new StreamResult(resultWriter));

            Locale locale = Locale.getDefault();
            String prefix = "diff";

            HtmlCleaner cleaner = new HtmlCleaner();

            InputSource oldSource = new InputSource(html1);
            InputSource newSource = new InputSource(html2);

            DomTreeBuilder oldHandler = new DomTreeBuilder();
            cleaner.cleanAndParse(oldSource, oldHandler);
            TextNodeComparator leftComparator = new TextNodeComparator(oldHandler, locale);

            DomTreeBuilder newHandler = new DomTreeBuilder();
            cleaner.cleanAndParse(newSource, newHandler);
            TextNodeComparator rightComparator = new TextNodeComparator(newHandler, locale);

            result.startDocument();
            result.startElement("", "html", "html", new AttributesImpl());
            result.startElement("", "head", "head", new AttributesImpl());

            AttributesImpl cssLinkAttrs = new AttributesImpl();
            cssLinkAttrs.addAttribute("", "href", "href", "", "css/diff.css");
            cssLinkAttrs.addAttribute("", "type", "type", "", "text/css");
            cssLinkAttrs.addAttribute("", "rel", "rel", "", "stylesheet");
            result.startElement("", "link", "link", cssLinkAttrs);

            result.endElement("", "head", "head");
            result.startElement("", "body", "body", new AttributesImpl());
            HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(result, prefix);

            HTMLDiffer differ = new HTMLDiffer(output);
            differ.diff(leftComparator, rightComparator);

            result.endElement("", "body", "body");
            result.endElement("", "html", "html");
            result.endDocument();
            return resultWriter.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
