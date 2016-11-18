package net.scrumplex.sprummlbot.manager.api;

import java.net.URL;
import java.util.Map;

public class APIData {
    private final String apiKey;
    private final URL requestUrl;
    private Map<String, String> postData = null;

    public APIData(String apiKey, URL requestUrl) {
        this.apiKey = apiKey;
        this.requestUrl = requestUrl;
    }

    public void setPostDataMap(Map<String, String> postData) {
        this.postData = postData;
    }

    public boolean isPostDataAvailable() {
        return this.postData != null;
    }

    public Map<String, String> getPostData() {
        return this.postData;
    }

    public String getApiKey() {
        return apiKey;
    }

    public URL getRequestURL() {
        return requestUrl;
    }

    @Override
    public String toString() {
        return apiKey + "@" + requestUrl.toString() + ", " + (isPostDataAvailable() ? getPostData() : "");
    }
}