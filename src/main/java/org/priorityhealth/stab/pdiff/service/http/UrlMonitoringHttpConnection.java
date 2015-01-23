package org.priorityhealth.stab.pdiff.service.http;

import sun.net.www.protocol.http.Handler;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.net.URL;

public class UrlMonitoringHttpConnection extends HttpURLConnection {

    public UrlMonitoringHttpConnection(URL url, Handler handler) throws IOException {
        super(url, handler);
    }

    @Override
    public void connect() throws IOException {
        this.setConnectTimeout(60000);
        this.setReadTimeout(60000);
        super.connect();
    }

}
