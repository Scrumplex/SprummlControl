package net.scrumplex.sprummlbot.manager.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import net.scrumplex.sprummlbot.manager.R;

import org.json.JSONException;
import org.json.JSONObject;

public class QueryActionDialog extends DialogFragment {

    private JSONObject client;

    public QueryActionDialog() {

    }

    public void setData(JSONObject client) {
        this.client = client;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        try {
            builder.setTitle(client.getString("client_nickname"))
                    .setItems(R.array.query_actions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    //Shutdown
                                    break;
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.create();
    }
}
