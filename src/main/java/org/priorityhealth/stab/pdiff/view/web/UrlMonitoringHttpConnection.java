package org.priorityhealth.stab.pdiff.view.web;

import org.priorityhealth.stab.pdiff.service.LogService;
import sun.net.www.protocol.http.Handler;
import sun.net.www.protocol.http.HttpURLConnection;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

public class UrlMonitoringHttpConnection extends HttpURLConnection {

    protected boolean connected = false;

    public UrlMonitoringHttpConnection(URL url, Handler handler) throws IOException {
        super(url, handler);
    }

    public UrlMonitoringHttpConnection(URL url, String s, int i) {
        super(url, s, i);
    }

    public UrlMonitoringHttpConnection(URL url, Proxy proxy) {
        super(url, proxy);
    }

    public UrlMonitoringHttpConnection(URL url, Proxy proxy, Handler handler) {
        super(url, proxy, handler);
    }

    @Override
    public void connect() throws IOException {
        this.setConnectTimeout(60000);
        this.setReadTimeout(60000);
        super.connect();
    }

    public boolean isConnected() {
        return connected;
    }
}
