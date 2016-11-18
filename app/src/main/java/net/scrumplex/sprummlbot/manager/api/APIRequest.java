package net.scrumplex.sprummlbot.manager.api;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

public class APIRequest extends AsyncTask<APIData, Void, JSONObject> {

    private final APICallback callback;
    private final UUID requestID;

    public APIRequest(APICallback callback) {
        this.callback = callback;
        this.requestID = UUID.randomUUID();
    }

    @Override
    protected JSONObject doInBackground(APIData... params) {
        try {
            APIData data = params[0];
            Log.d("sprummlbot_api", "Sprummlbot API Request " + requestID + ": " + data.toString());
            URL url = data.getRequestURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Authorization", "key=" + data.getApiKey());
            if(data.isPostDataAvailable()) {
                conn.setDoInput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                String post = "";
                for (String key : data.getPostData().keySet()) {
                    post += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(data.getPostData().get(key), "UTF-8");
                }
                try {
                    post = post.substring(1);
                } catch (Exception ignored) {}
                out.writeBytes(post);
            }
            conn.connect();
            conn.getResponseCode();
            InputStream stream = conn.getErrorStream();
            if (stream == null) {
                stream = conn.getInputStream();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String response = "";
            String line;
            while ((line = in.readLine()) != null) {
                response += line + "\n";
            }
            return new JSONObject(response);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.d("sprummlbot_api", "Sprummlbot API Response " + requestID + ": " + jsonObject.toString());
        try {
            callback.handle(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}