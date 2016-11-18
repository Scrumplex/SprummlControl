package net.scrumplex.sprummlbot.manager.helpers;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.scrumplex.sprummlbot.manager.R;
import net.scrumplex.sprummlbot.manager.dialogs.ClientActionDialog;
import net.scrumplex.sprummlbot.manager.dialogs.QueryActionDialog;
import net.scrumplex.sprummlbot.manager.fragments.ClientsList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ClientsAdapter extends ArrayAdapter<JSONObject> {


    private final FragmentManager fragmentManager;
    private final String apiKey;
    private final String baseUrl;
    private final ClientsList clientsList;

    public ClientsAdapter(ClientsList clientsList, List<JSONObject> objects, String apiKey, String baseUrl) {
        super(clientsList.getContext(), 0, objects);
        this.clientsList = clientsList;
        this.fragmentManager = clientsList.getFragmentManager();
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final JSONObject json = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_client, parent, false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClientActions(json);
            }
        });
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView information = (TextView) convertView.findViewById(R.id.information);
        try {
            username.setText(json.getString("client_nickname"));
            information.setText(json.getString("client_unique_identifier"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void showClientActions(JSONObject client) {
        try {
            if (client.getString("client_platform").equals("ServerQuery")) {
                QueryActionDialog dialog = new QueryActionDialog();
                dialog.setData(client);
                dialog.show(fragmentManager, "query_actions");
            } else {
                ClientActionDialog dialog = new ClientActionDialog();
                dialog.setData(client, clientsList, apiKey, baseUrl);
                dialog.show(fragmentManager, "client_actions");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
        }
    }
}
