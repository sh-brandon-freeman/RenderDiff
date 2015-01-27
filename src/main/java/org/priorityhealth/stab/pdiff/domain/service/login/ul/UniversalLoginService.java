package org.priorityhealth.stab.pdiff.domain.service.login.ul;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.priorityhealth.stab.pdiff.service.LogService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class UniversalLoginService {

    public static Response getAuthToken(String url, Credentials credentials) {
        Gson gson = new Gson();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        StringEntity postingString = null;
        try {
            LogService.Info("UniversalLoginService", "Using creds: " + credentials);
            postingString = new StringEntity(gson.toJson(credentials));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }

        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = null;
        HttpEntity httpEntity = null;

        try {
            response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                httpEntity = response.getEntity();
            } else {
                LogService.Info("UniversalLoginService", "Failed: " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        LogService.Info("UniversalLoginService", httpEntity.getContentType().toString());
        try {
            String source = EntityUtils.toString(httpEntity);
            LogService.Info("UniversalLoginService", "Source: " + source);
            return gson.fromJson(source, Response.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
