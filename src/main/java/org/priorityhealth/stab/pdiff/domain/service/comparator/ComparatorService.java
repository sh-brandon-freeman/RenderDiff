package org.priorityhealth.stab.pdiff.domain.service.comparator;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.priorityhealth.stab.pdiff.domain.entity.asset.Asset;
import org.priorityhealth.stab.pdiff.domain.entity.profile.Profile;
import org.priorityhealth.stab.pdiff.domain.entity.test.Result;
import org.priorityhealth.stab.pdiff.domain.entity.test.Test;
import org.priorityhealth.stab.pdiff.service.LogService;

public class ComparatorService implements ProfilerListenerInterface, StateCompareListenerInterface {

    protected ProfilerService profilerService;
    protected StateCompareService stateCompareService;

    protected WebView webView;
    protected WebEngine webEngine;

    protected Asset[] assets = new Asset[2];
    protected Profile[] profiles = new Profile[2];
    protected boolean useAsset1Nodes;

    protected int profiledIndex = 0;

    public ComparatorService(ProfilerService profilerService, StateCompareService stateCompareService) {
        this.profilerService = profilerService;
        this.stateCompareService = stateCompareService;
    }

    public void init(Asset asset1, Profile profile1, Asset asset2, Profile profile2, boolean useAsset1Nodes) {
        LogService.Info(this, "Init ...");
        if (webEngine == null) {
            LogService.Info(this, "No webengine specified.");
            return;
        }

        if (asset1 == null) {
            LogService.Info(this, "Asset1 missing.");
        } else {
            LogService.Info(this, "Asset1: " + asset1.getName());
        }

        if (asset2 == null) {
            LogService.Info(this, "Asset2 missing.");
        } else {
            LogService.Info(this, "Asset2: " + asset2.getName());
        }

        this.assets[0] = asset1;
        this.assets[1] = asset2;
        this.profiles[0] = profile1;
        this.profiles[1] = profile2;
        this.useAsset1Nodes = useAsset1Nodes;

        LogService.Info(this, assets.toString());
    }

    public void run() {
        LogService.Info(this, "Running ...");
        verifyMaterials();
    }

    protected void verifyMaterials() {
        LogService.Info(this, "Verifying Materials ...");
        for (Asset asset : assets) {
            if (asset == null) {
                LogService.Info(this, "Asset Missing.");
                return;
            } else {
                LogService.Info(this, "Asset Found.");
            }
        }

        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i] == null && assets[i] != null) {
                LogService.Info(this, "Profile Missing.");
                getProfile(assets[i], i);
                return;
            } else {
                LogService.Info(this, "Profile Found.");
            }
        }

        beginStateCompare();
    }

    protected void getProfile(Asset asset, int profiledIndex) {
        LogService.Info(this, "Getting profile for slot: " + profiledIndex);
        this.profiledIndex = profiledIndex;
        profilerService.init(asset);
        if (useAsset1Nodes) {
            profilerService.setCrawlNewNodes(false);
            profilerService.setAlternateNodeList(assets[0].getNodes());
        }

        profilerService.setProfilerListener(this);
        profilerService.run();
    }

    @Override
    public void onProfileComplete(Profile profile) {
        LogService.Info(this, "Received profile for slot: " + profiledIndex);
        profiles[profiledIndex] = profile;
        verifyMaterials();
    }

    protected void beginStateCompare() {
        LogService.Info(this, "Beginning state compare.");
        stateCompareService.init(
                profiles[0],
                profiles[1]
        );
        stateCompareService.setStateCompareListener(this);
        stateCompareService.run();
    }

    @Override
    public void onCompareComplete(Result result) {
        // Nobody cares
    }

    @Override
    public void onQueueComplete(Test test) {

    }

    public void setWebView(WebView webView) {
        this.webView = webView;
        this.webEngine = webView.getEngine();
        profilerService.setWebView(webView);
    }
}
