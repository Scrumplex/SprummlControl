package net.scrumplex.sprummlbot.manager.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import net.scrumplex.sprummlbot.manager.R;
import net.scrumplex.sprummlbot.manager.fragments.ClientsList;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientActionDialog extends DialogFragment {

    private JSONObject client;
    private ClientsList clientsList;
    private String apiKey;
    private String baseUrl;

    public ClientActionDialog() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            builder.setTitle(client.getString("client_nickname"))
                    .setItems(R.array.client_actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    ClientMessageDialog msg = new ClientMessageDialog();
                                    msg.setData(client, apiKey, baseUrl);
                                    msg.show(getFragmentManager(), "client_message");
                                    break;
                                case 1:
                                    ClientPokeDialog poke = new ClientPokeDialog();
                                    poke.setData(client, apiKey, baseUrl);
                                    poke.show(getFragmentManager(), "client_poke");
                                    break;
                                case 2:
                                    ClientKickDialog kick = new ClientKickDialog();
                                    kick.setData(client, clientsList, apiKey, baseUrl);
                                    kick.show(getFragmentManager(), "client_kick");
                                    break;
                                case 3:
                                    ClientBanDialog ban = new ClientBanDialog();
                                    ban.setData(client, clientsList, apiKey, baseUrl);
                                    ban.show(getFragmentManager(), "client_ban");
                                    break;
                            }
                            dialog.dismiss();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.create();
    }
}
