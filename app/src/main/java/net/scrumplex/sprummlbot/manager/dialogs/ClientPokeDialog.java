package net.scrumplex.sprummlbot.manager.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.scrumplex.sprummlbot.manager.R;
import net.scrumplex.sprummlbot.manager.api.APICallback;
import net.scrumplex.sprummlbot.manager.api.APIData;
import net.scrumplex.sprummlbot.manager.api.APIRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClientPokeDialog extends DialogFragment {

    private JSONObject client;
    private String apiKey;
    private String baseUrl;

    public ClientPokeDialog() {

    }

    public void setData(JSONObject client, String apiKey, String baseUrl) {
        this.client = client;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_message, null);
        final EditText message = (EditText) dialogView.findViewById(R.id.message);
        final Context c = getContext();
        try {
            builder.setTitle(client.getString("client_nickname")).setView(dialogView).setPositiveButton("Poke", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String msg = message.getText().toString();
                    try {
                        String request = baseUrl + "/clients/" + client.getString("clid") + "/poke";
                        APIData data = new APIData(apiKey, new URL(request));
                        Map<String,String> post = new HashMap<>();
                        post.put("message", msg);
                        data.setPostDataMap(post);
                        new APIRequest(new APICallback() {
                            @Override
                            public void handle(JSONObject response) throws JSONException {
                                if (response.getString("msg").equalsIgnoreCase("success")) {
                                    Toast.makeText(c, "Client was poked!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(c, "Client could not be poked!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute(data);
                    } catch (MalformedURLException | JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.create();
    }
}
