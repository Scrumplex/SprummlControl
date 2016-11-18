package net.scrumplex.sprummlbot.manager.api;

import org.json.JSONException;
import org.json.JSONObject;

public interface APICallback {
    void handle(JSONObject response) throws JSONException;
}