package org.priorityhealth.stab.pdiff.domain.service.comparator;

import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Node;
import org.priorityhealth.stab.pdiff.domain.entity.profile.IgnoredArea;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.profile.State;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.domain.entity.test.Test;
import org.priorityhealth.stab.pdiff.domain.repository.profile.StateRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.ResultRepositoryInterface;
import org.priorityhealth.stab.pdiff.domain.repository.test.TestRepositoryInterface;
import org.priorityhealth.stab.pdiff.persistence.repository.test.ResultRepository;
import org.priorityhealth.stab.pdiff.service.DigestService;
import org.priorityhealth.stab.pdiff.service.ImageService;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class StateCompareService {

    protected Profile profile1;
    protected Profile profile2;
    protected String storePath;
    protected StateResultInterface stateResult;
    protected List<Result> queue = new ArrayList<Result>();
    protected Test test;

    protected ResultRepositoryInterface resultRepository;
    protected TestRepositoryInterface testRepository;

    public StateCompareService(
            Profile profile1,
            Profile profile2,
            String storePath,
            StateResultInterface stateResult,
            ResultRepositoryInterface resultRepository,
            TestRepositoryInterface testRepository
    ) {
        this.profile1 = profile1;
        this.profile2 = profile2;
        this.storePath = storePath;
        this.stateResult = stateResult;
        this.resultRepository = resultRepository;
        this.testRepository = testRepository;

        test = new Test();
        test.setKnown(profile1);
        test.setCurrent(profile2);
        test.setCreated(new Date());

        try {
            testRepository.create(test);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        buildQueue();
        createFolder();
    }

    protected void createFolder() {
        File storeDir = new File(storePath);
        if (!storeDir.exists()) {
            System.out.println("creating directory: " + storeDir);
            boolean result = false;

            try{
                result = storeDir.mkdir();
            } catch(SecurityException ex){
                ex.printStackTrace();
            }
            if(result) {
                System.out.println("DIR created");
            }
        }
    }

    protected void buildQueue() {
        for (State knownState : profile1.getStates()) {
            Node knownNode = knownState.getNode();
            if (knownNode == null || knownNode.getUrl() == null) {
                continue;
            }

            for (State currentState : profile2.getStates()) {
                Node currentNode = currentState.getNode();
                if (currentNode == null || currentNode.getUrl() == null) {
                    continue;
                }

                if (knownNode.getUrl().equals(currentNode.getUrl())) {
                    Result result = new Result();
                    result.setCurrent(currentState);
                    result.setKnown(knownState);
                    result.setCreated(new Date());
                    result.setTest(test);
                    queue.add(result);
                    LogService.Info(this, "Adding url: " + knownNode.getUrl());
                }
            }
        }

        LogService.Info(this, "Queue of size: " + queue.size());
    }

    public void run() {
        if (queue.size() > 0) {
            for (Result result : queue) {
                executeTest(result);
                try {
                    resultRepository.create(result);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            LogService.Info(this, "There was no queue to iterate.");
        }

        stateResult.onQueueComplete();
    }

    public void executeTest(Result result) {
        try {
            Path tempDiffPath = Files.createTempFile("diff-now", ".png");
            Path tempKnownPath = Files.createTempFile("diff-known", ".png");
            Path tempCurrentPath = Files.createTempFile("diff-now", ".png");

            if (tempDiffPath != null) {
                String currentImagePath = result.getCurrent().getImagePath();
                String knownImagePath = result.getKnown().getImagePath();

                BufferedImage imageCurrent = ImageService.loadBufferedImageFromFile(currentImagePath);
                BufferedImage imageKnown = ImageService.loadBufferedImageFromFile(knownImagePath);

                Rectangle dimensions = ImageService.calculateMaxDimensions(imageKnown, imageCurrent);

                imageKnown = ImageService.resizeImageCanvas(imageKnown, dimensions);
                imageCurrent = ImageService.resizeImageCanvas(imageCurrent, dimensions);

                blockOffIgnored(result.getKnown(), imageKnown);
                blockOffIgnored(result.getCurrent(), imageKnown);
                blockOffIgnored(result.getKnown(), imageCurrent);
                blockOffIgnored(result.getCurrent(), imageCurrent);

                ImageService.saveImageFromBufferedImage(imageKnown, tempKnownPath.toString());
                ImageService.saveImageFromBufferedImage(imageCurrent, tempCurrentPath.toString());

                boolean isDifferent = this.pdiff(
                        tempKnownPath.toString(),
                        tempCurrentPath.toString(),
                        tempDiffPath.toString()
                );
                if (isDifferent) {
                    BufferedImage imageDiff = ImageService.loadBufferedImageFromFile(tempDiffPath.toString());
                    if (imageDiff != null) {
                        if (ImageService.hasRedPixels(imageDiff, true)) {
                            String diffImageSavePath = storePath + File.separator + UUID.randomUUID().toString() + "-diff.png";
                            Files.copy(tempDiffPath, Paths.get(diffImageSavePath), StandardCopyOption.REPLACE_EXISTING);
                            result.setDiffImagePath(diffImageSavePath);
                        }
                    }
                }
                Files.delete(tempDiffPath);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stateResult.onCompareComplete(result);
    }

    protected void blockOffIgnored(State state, BufferedImage bufferedImage) {
        if (state == null || bufferedImage == null) {
            return;
        }

        for (IgnoredArea ignoredArea : state.getIgnoredAreas()) {
            ImageService.drawRectangle(bufferedImage, ignoredArea.getX1(), ignoredArea.getY1(), ignoredArea.getX2(), ignoredArea.getY2(), Color.BLACK);
        }
    }

    /**
     *
     * @param source1 Source1 image path
     * @param source2 Source2 image path
     * @param output Diff image path
     * @return Difference?
     */
    public boolean pdiff(String source1, String source2, String output) {
        LogService.Info(this, "Running pDiff ...");
        LogService.Info(this, "Source1: " + source1);
        LogService.Info(this, "Source2: " + source2);
        LogService.Info(this, "Output: " + output);

        ImageCommand compare=new ImageCommand();
        compare.setCommand("compare");
        IMOperation op = new IMOperation();
        op.fuzz(25d, true);
        op.metric("RMSE");
        op.highlightColor("Red");
        op.compose("src");
        op.addImage(source1);
        op.addImage(source2);
        op.addImage(output);

        try {
            compare.run(op);
        } catch (InterruptedException ex) {
            LogService.Info(StateCompareService.class, "InterruptedException" + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            LogService.Info(StateCompareService.class, "IOException" + ex.getMessage());
            ex.printStackTrace();
        } catch (IM4JavaException ex) {
            // Exception if the images are different.
            return true;
        }
        return false;
    }
}
