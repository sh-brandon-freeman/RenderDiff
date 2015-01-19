package org.priorityhealth.stab.pdiff.view.web;

import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class UrlMonitoringHttpsHandler extends Handler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {

        UrlMonitoringHttpsConnection urlConnection = new UrlMonitoringHttpsConnection(url);

        urlConnection.setConnectTimeout(60000);
        urlConnection.setReadTimeout(60000);

        return urlConnection;
    }


}
