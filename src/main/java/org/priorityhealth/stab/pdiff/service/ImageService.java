package org.priorityhealth.stab.pdiff.service;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.web.WebView;

import javax.imageio.ImageIO;
import java.awt.*;
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
            LogService.Info(TextFileService.class.getClass(), "Saved image to: " + file.getAbsolutePath());
        } catch (IOException ex) {
            LogService.Info(TextFileService.class.getClass(), "IOException: " + ex.getMessage());
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
            LogService.Info(TextFileService.class.getClass(), "Successfully loaded: " + file.getAbsolutePath());
        } catch (IOException ex) {
            LogService.Info(TextFileService.class.getClass(), "IOException: " + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public static Rectangle calculateMaxDimensions(BufferedImage... images) {
        Rectangle newDimensions = new Rectangle();

        for (BufferedImage image : images) {
            if (image != null) {
                if (image.getWidth() > newDimensions.getWidth()) {
                    newDimensions.width = image.getWidth();
                }

                if (image.getHeight() > newDimensions.getHeight()) {
                    newDimensions.height = image.getHeight();
                }
            }
        }

        LogService.Info(TextFileService.class.getClass(), "Max Dimensions: " + newDimensions.toString());
        return newDimensions;
    }

    public static BufferedImage resizeImageCanvas(BufferedImage originalImage, Rectangle dimensions) {
        if (originalImage.getWidth() == dimensions.getWidth() && originalImage.getHeight() == dimensions.getHeight()) {
            return originalImage;
        }

        BufferedImage resizedImage = new BufferedImage(
                (int) dimensions.getWidth(),
                (int) dimensions.getHeight(),
                originalImage.getType()
        );

        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
        graphics.dispose();

        LogService.Info(TextFileService.class.getClass(),
                originalImage.getWidth() + "x" + originalImage.getHeight() + " -> " +
                        resizedImage.getWidth() + "x" + resizedImage.getHeight()
        );

        return resizedImage;
    }

    public static WritableImage loadWritableImageFromFile(String path) {
        BufferedImage bufferedImage = loadBufferedImageFromFile(path);

        WritableImage writableImage = null;
        if (bufferedImage != null) {
            writableImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pw = writableImage.getPixelWriter();
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        return writableImage;
    }

    public static void drawRectangle(BufferedImage bufferedImage, int x1, int y1, int x2, int y2, Color color) {
        if (bufferedImage == null) {
            return;
        }

        int x = x1 < x2 ? x1 : x2;
        int y = y1 < y2 ? y1 : y2;
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(color);
        graphics.fill(new Rectangle(x, y, width, height));
        graphics.dispose();
    }

    public static boolean hasRedPixels(BufferedImage bufferedImage) {
        return hasRedPixels(bufferedImage, false);
    }

    public static boolean hasRedPixels(BufferedImage bufferedImage, boolean strict) {
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int pixel = bufferedImage.getRGB(j, i);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                if (alpha == 255) {
                    if (!strict && red > 0) {
                        return true;
                    } else if (strict && red == 255) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
