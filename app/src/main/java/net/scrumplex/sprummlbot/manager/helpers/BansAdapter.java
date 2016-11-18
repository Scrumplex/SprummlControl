package net.scrumplex.sprummlbot.manager.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.scrumplex.sprummlbot.manager.R;
import net.scrumplex.sprummlbot.manager.api.APICallback;
import net.scrumplex.sprummlbot.manager.api.APIData;
import net.scrumplex.sprummlbot.manager.api.APIRequest;
import net.scrumplex.sprummlbot.manager.fragments.BansList;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BansAdapter extends ArrayAdapter<JSONObject> {

    private final BansList bans;
    private final String apiKey;
    private final String baseUrl;

    public BansAdapter(Context context, List<JSONObject> objects, BansList bans, String apiKey, String baseUrl) {
        super(context, 0, objects);
        this.bans = bans;
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
                showBanActions(json);
            }
        });
        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView information = (TextView) convertView.findViewById(R.id.information);
        try {
            String lastNickName = json.getString("lastnickname");
            String ip = json.getString("ip");
            String usernameText;
            String informationText = getContext().getString(R.string.banned_by) + " " + json.getString("invokername");
            if (json.getString("invokername").equals(""))
                informationText = "";
            if (lastNickName.equals("") && ip.equals(""))
                usernameText = json.getString("uid");
            else if (lastNickName.equals(""))
                usernameText = ip;
            else if (ip.equals(""))
                usernameText = lastNickName;
            else
                return null;

            username.setText(usernameText);
            information.setText(informationText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void showBanActions(final JSONObject client) {
        final Context c = getContext();
        try {
            final String lastNickName = client.getString("lastnickname");
            final String ip = client.getString("ip");
            final String usernameText;
            if (client.getString("invokername").equals(""))
                return;
            if (lastNickName.equals(""))
                usernameText = ip;
            else if (ip.equals(""))
                usernameText = lastNickName;
            else
                usernameText = "";
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(usernameText);
            builder.setMessage(getContext().getString(R.string.unban_question));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        String request = baseUrl + "/bans/" + client.getString("banid") + "/unban";
                        APIData data = new APIData(apiKey, new URL(request));
                        Map<String, String> post = new HashMap<>();
                        data.setPostDataMap(post);
                        new APIRequest(new APICallback() {
                            @Override
                            public void handle(JSONObject response) throws JSONException {
                                if (response.getString("msg").equalsIgnoreCase("success")) {
                                    Toast.makeText(c, "Ban was removed!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(c, "Could not remove ban!", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    bans.gatherInformation();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(data);
                        dialog.dismiss();
                    } catch (JSONException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
        }
    }
}
