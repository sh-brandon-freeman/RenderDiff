package renderdiff.service.comparator;

/**
 * Original nabbed from: http://mindmeat.blogspot.com/2008/07/java-image-comparison.html
 * Modified to conform to a service and it's domain.
 */

import java.awt.*;
import java.awt.image.*;

public class ImageCompareService {

    protected BufferedImage imgc = null;
    protected int compareX = 10;
    protected int compareY = 10;
    protected int maxTolerance = 0;
    protected int debugMode = 0; // 1: textual indication of change, 2: difference of factors

    public ImageCompareService(int x, int y) {
        this.compareX = x;
        this.compareY = y;
    }

    public ImageCompareService(int x, int y, int maxTolerance) {
        this.compareX = x;
        this.compareY = y;
        this.maxTolerance = maxTolerance;
    }

    public ImageCompareService(int x, int y, int maxTolerance, int debugMode) {
        this.compareX = x;
        this.compareY = y;
        this.maxTolerance = maxTolerance;
        this.debugMode = debugMode;
    }

    // want to see some stuff in the console as the comparison is happening?
    public ImageCompareService setDebugMode(int m) {
        this.debugMode = m;
        return this;
    }

    public ImageCompareService setcompareX(int compareX) {
        this.compareX = compareX;
        return this;
    }

    public ImageCompareService setcompareY(int compareY) {
        this.compareY = compareY;
        return this;
    }

    public boolean compare(BufferedImage source1, BufferedImage source2) {
        // Convert to grayscale.
        BufferedImage img1 = grayscaleBufferedImage(source1);
        BufferedImage img2 = grayscaleBufferedImage(source2);
        imgc = grayscaleBufferedImage(source2);

        Graphics2D gc = imgc.createGraphics();
        gc.setColor(Color.RED);
        // how big are each section
        int blocksX = img1.getWidth() / compareX;
        int blocksY = img1.getHeight() / compareY;
        // set to a match by default, if a change is found then flag non-match
        boolean match = true;
        // loop through whole image and compare individual blocks of images
        for (int y = 0; y < compareY; y++) {
            if (debugMode > 0) System.out.print("|");
            for (int x = 0; x < compareX; x++) {
                int b1 = getAverageBrightness(img1.getSubimage(x * blocksX, y * blocksY, blocksX - 1, blocksY - 1));
                int b2 = getAverageBrightness(img2.getSubimage(x * blocksX, y * blocksY, blocksX - 1, blocksY - 1));
                int diff = Math.abs(b1 - b2);
                if (diff > maxTolerance) { // the difference in a certain region has passed the threshold value of maxTolerance
                    // draw an indicator on the change image to show where change was detected.
                    gc.drawRect(x * blocksX, y * blocksY, blocksX - 1, blocksY - 1);
                    match = false;
                }
                switch (debugMode) {
                    case 0:
                        break;
                    case 1:
                        System.out.print((diff > maxTolerance ? "X" : " "));
                        break;
                    default:
                    case 2:
                        System.out.print(diff + (x < compareX - 1 ? "," : ""));
                        break;
                }
            }
            if (debugMode > 0) System.out.println("|");
        }
        return match;
    }

    public BufferedImage grayscaleBufferedImage(BufferedImage source) {
        BufferedImage result = null;
        try
        {
            result = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = result.createGraphics();
            g.drawImage(source, 0, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // return the image that indicates the regions where changes where detected.
    public BufferedImage getChangeIndicator() {
        return imgc;
    }

    // returns a value specifying some kind of average brightness in the image.
    protected int getAverageBrightness(BufferedImage img) {
        Raster r = img.getData();
        int total = 0;
        for (int y = 0; y < r.getHeight(); y++) {
            for (int x = 0; x < r.getWidth(); x++) {
                total += r.getSample(r.getMinX() + x, r.getMinY() + y, 0);
            }
        }
        return Math.round(total / ((r.getWidth()) * (r.getHeight())));
    }
}
