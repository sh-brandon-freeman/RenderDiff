package org.priorityhealth.stab.pdiff.controller;

import org.priorityhealth.stab.pdiff.view.headless.HeadlessAppLauncher;

public class AbstractController {

    protected static boolean isHeadless() {
        String headlessProperty = System.getProperty(HeadlessAppLauncher.PROPERTY_TESTFX_HEADLESS);
        return headlessProperty != null && headlessProperty.equalsIgnoreCase("true");
    }
}
