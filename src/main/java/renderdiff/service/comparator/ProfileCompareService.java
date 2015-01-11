package renderdiff.service.comparator;

import javafx.scene.web.WebView;
import renderdiff.domain.entity.asset.Node;
import renderdiff.domain.entity.profile.Profile;
import renderdiff.domain.entity.profile.State;
import renderdiff.domain.repository.profile.StateRepositoryInterface;
import renderdiff.service.general.ImageService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by bra50311 on 1/8/15.
 */
public class ProfileCompareService {

//    protected WebView webView;
//    protected String urlHash;
//    protected Node currentNode;
//    protected String knownState;
//    protected Profile profile;
//    protected StateRepositoryInterface resultRepository;
//
//    private void compareVisualState(String crawlTimestamp, String basePath) {
//        if (currentNode == null) {
//            System.out.println("No asset to compare.");
//            return;
//        }
//
//        String currentStoredPath = crawlTimestamp + File.separator + "store" + File.separator + urlHash + ".png";
//        String knownStoredPath = knownState + File.separator + urlHash + ".png";
//
//        String currentImagePath = crawlTimestamp + File.separator + urlHash + "-now.png";
//        String knownImagePath = crawlTimestamp + File.separator + urlHash + "-known.png";
//        String diffImagePath = crawlTimestamp + File.separator + urlHash + "-diff.png";
//
//        File knownFile = new File(basePath + knownStoredPath);
//        if (knownFile.exists() && !knownFile.isDirectory()) {
//
//            try {
//                Path tempDiff = Files.createTempFile("diff-temp", ".png");
//                Path tempKnown = Files.createTempFile("diff-known", ".png");
//                Path tempCurrent = Files.createTempFile("diff-now", ".png");
//
//                BufferedImage imageCurrent = ImageService.getBufferedImageFromWebView(webView);
//                BufferedImage imageKnown = ImageService.loadBufferedImageFromFile(basePath + knownStoredPath);
//
//                // Save unmodified copy to store.
//                ImageService.saveImageFromBufferedImage(imageCurrent, basePath + currentStoredPath);
//
//                Rectangle dimensions = ImageService.calculateMaxDimensions(imageKnown, imageCurrent);
//
//                imageKnown = ImageService.resizeImageCanvas(imageKnown, dimensions);
//                imageCurrent = ImageService.resizeImageCanvas(imageCurrent, dimensions);
//
//                ImageService.saveImageFromBufferedImage(imageKnown, tempKnown.toString());
//                ImageService.saveImageFromBufferedImage(imageCurrent, tempCurrent.toString());
//
//                boolean isDifferent = ImageCompareService.pdiff(
//                        tempKnown.toString(),
//                        tempCurrent.toString(),
//                        tempDiff.toString()
//                );
//
//                State nodeState = new State();
//                nodeState
//                        .setNode(currentNode)
//                        .setProfile(profile)
//                        .setCurrentImagePath(currentStoredPath)
//                        .setImagePath(knownStoredPath)
//                        .setCreated(new Date());
//
//                if (isDifferent) {
//                    Files.move(tempDiff, Paths.get(basePath + diffImagePath), StandardCopyOption.REPLACE_EXISTING);
//                    Files.copy(tempKnown, Paths.get(basePath + knownImagePath), StandardCopyOption.REPLACE_EXISTING);
//                    Files.copy(tempCurrent, Paths.get(basePath + currentImagePath), StandardCopyOption.REPLACE_EXISTING);
//
//                    nodeState.setDiffImagePath(diffImagePath);
//                } else {
//                    // Don't rely on others to do your dirty work ...
//                    Files.delete(tempDiff);
//                    Files.delete(tempKnown);
//                    Files.delete(tempCurrent);
//                }
//                resultRepository.create(nodeState);
//            } catch (IOException ex) {
//                System.out.println("IOException" + ex.getMessage());
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            System.out.println("No known state file found");
//        }
//    }
}
