package org.priorityhealth.stab.pdiff.view.web;

import sun.net.www.protocol.https.HttpsURLConnectionImpl;

import java.io.IOException;
import java.net.URL;

public class UrlMonitoringHttpsConnection extends HttpsURLConnectionImpl {

    public UrlMonitoringHttpsConnection(URL url) throws IOException {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        this.setConnectTimeout(10000);
        this.setReadTimeout(10000);
        super.connect();
    }
}
