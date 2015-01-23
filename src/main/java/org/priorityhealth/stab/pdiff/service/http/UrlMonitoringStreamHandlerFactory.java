package org.priorityhealth.stab.pdiff.service.http;

import org.priorityhealth.stab.pdiff.service.LogService;

import java.net.*;

public class UrlMonitoringStreamHandlerFactory implements URLStreamHandlerFactory {

    public URLStreamHandler createURLStreamHandler(String protocol) {
        LogService.Info(this, "Conecting with: " + protocol);

        if (protocol.equalsIgnoreCase("http")) {
            return new UrlMonitoringHttpHandler();
        } else if (protocol.equalsIgnoreCase("http")) {
            return new UrlMonitoringHttpsHandler();
        }
        return null;
    }
}
