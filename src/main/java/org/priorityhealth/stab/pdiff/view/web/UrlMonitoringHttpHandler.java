package org.priorityhealth.stab.pdiff.view.web;

import org.priorityhealth.stab.pdiff.view.service.UrlMonitoringService;
import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UrlMonitoringHttpHandler extends Handler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {

        UrlMonitoringHttpConnection urlConnection = new UrlMonitoringHttpConnection(url, this);

        return urlConnection;
    }


}
