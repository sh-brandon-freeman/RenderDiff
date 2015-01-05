package renderdiff.service.general;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class ImageService {

    /**
     *
     * @param webView WebView
     * @return BufferedImage
     */
    public static BufferedImage getBufferedImageFromWebView(WebView webView) {
        WritableImage image = webView.snapshot(new SnapshotParameters(), null);
        return SwingFXUtils.fromFXImage(image, null);
    }

    /**
     *
     * @param img Source image
     * @param path Destination path
     */
    public static void saveImageFromBufferedImage(BufferedImage img, String path) {
        File file = new File(path);
        try {
            File parent = file.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            ImageIO.write(img, "png", file);
            System.out.println("Saved image to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param path Source path
     * @return BufferedImage
     */
    public static BufferedImage loadBufferedImageFromFile(String path) {
        File file = new File(path);
        BufferedImage result = null;
        try {
            if(file.exists()) {
                result = ImageIO.read(file);
            }
            System.out.println("Successfully loaded: " + file.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }
}
