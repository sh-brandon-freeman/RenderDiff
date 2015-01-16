package org.priorityhealth.stab.pdiff.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogService {
    public static void Info(String message) {
        Logger.getLogger(LogService.class.getName()).log(Level.INFO, message);
    }

    public static void Error(String message) {
        Logger.getLogger(LogService.class.getName()).log(Level.ERROR, message);
    }

    public static void Warn(String message) {
        Logger.getLogger(LogService.class.getName()).log(Level.WARN, message);
    }
}
