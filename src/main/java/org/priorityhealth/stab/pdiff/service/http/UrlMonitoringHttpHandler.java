package org.priorityhealth.stab.pdiff.service.http;

import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class UrlMonitoringHttpHandler extends Handler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {

        return new UrlMonitoringHttpConnection(url, this);

    }


}
