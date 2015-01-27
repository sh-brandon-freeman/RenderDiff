package org.priorityhealth.stab.pdiff.service;

import java.io.File;

public class StorageService {

    public static String getStoragePath() {
        return System.getProperty("user.home") + File.separator + "pdiff";
    }
}
