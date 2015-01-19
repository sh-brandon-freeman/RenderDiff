package org.priorityhealth.stab.pdiff.service;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogService {
    public static void Info(Object caller, String message) {
        Logger.getLogger(caller.getClass().getName()).log(Level.INFO, message);
    }

    public static void Error(Object caller, String message) {
        Logger.getLogger(caller.getClass().getName()).log(Level.ERROR, message);
    }

    public static void Warn(Object caller, String message) {
        Logger.getLogger(caller.getClass().getName()).log(Level.WARN, message);
    }
}
