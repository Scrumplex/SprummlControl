package net.scrumplex.sprummlbot.manager.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.scrumplex.sprummlbot.manager.R;
import net.scrumplex.sprummlbot.manager.api.APICallback;
import net.scrumplex.sprummlbot.manager.api.APIData;
import net.scrumplex.sprummlbot.manager.api.APIRequest;
import net.scrumplex.sprummlbot.manager.fragments.ClientsList;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClientBanDialog extends DialogFragment {

    private JSONObject client;
    private ClientsList clientsList;
    private String apiKey;
    private String baseUrl;

    public ClientBanDialog() {

    }

    public void setData(JSONObject client, ClientsList clientsList, String apiKey, String baseUrl) {
        this.client = client;
        this.clientsList = clientsList;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final Context c = getContext();
        final View dialogView = inflater.inflate(R.layout.dialog_ban, null);
        final EditText message = (EditText) dialogView.findViewById(R.id.banReason);
        final EditText time = (EditText) dialogView.findViewById(R.id.banTime);
        final Spinner timeFormat = (Spinner) dialogView.findViewById(R.id.banTimeFormat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(c, R.array.ban_times, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeFormat.setAdapter(adapter);
        timeFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4)
                    time.setEnabled(false);
                else
                    time.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });
        try {
            builder.setTitle(client.getString("client_nickname")).setView(dialogView).setPositiveButton("Ban", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String msg = message.getText().toString();
                    int t = Integer.parseInt(time.getText().toString());
                    final int tfPos = timeFormat.getSelectedItemPosition();
                    switch (tfPos) {
                        case 0:
                            break;

                        case 1:
                            t = t * 60;
                            break;

                        case 2:
                            t = t * 60 * 60;
                            break;

                        case 3:
                            t = t * 60 * 60 * 24;
                            break;

                        case 4:
                            t = 0;
                            break;
                    }
                    try {
                        String request = baseUrl + "/clients/" + client.getString("clid") + "/ban";
                        APIData data = new APIData(apiKey, new URL(request));
                        Map<String, String> post = new HashMap<>();
                        post.put("reason", msg);
                        post.put("duration", String.valueOf(t));
                        data.setPostDataMap(post);
                        new APIRequest(new APICallback() {
                            @Override
                            public void handle(JSONObject response) throws JSONException {
                                Log.d("sprummlbot_api", response.toString());
                                if (response.getString("msg").equalsIgnoreCase("success"))
                                    Toast.makeText(c, "Client was banned!", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(c, "Client could not be banned!", Toast.LENGTH_SHORT).show();
                                try {
                                    clientsList.gatherInformation();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
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
