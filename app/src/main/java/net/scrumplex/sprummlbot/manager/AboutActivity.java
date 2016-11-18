package net.scrumplex.sprummlbot.manager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ListView listView = (ListView) findViewById(R.id.about_list);
        assert listView != null;
        listView.setEmptyView(findViewById(android.R.id.empty));
        List<Map.Entry<String, String>> aboutList = new ArrayList<>();
        aboutList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "SprummlControl";
            }

            @Override
            public String getValue() {
                return "by Scrumplex";
            }

            @Override
            public String setValue(String object) {
                return null;
            }
        });
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        final String version = pInfo.versionName;
        aboutList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "Version " + version;
            }

            @Override
            public String getValue() {
                return "BETA";
            }

            @Override
            public String setValue(String object) {
                return null;
            }
        });
        aboutList.add(new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "Open Source Licenses";
            }

            @Override
            public String getValue() {
                return "Click to view";
            }

            @Override
            public String setValue(String object) {
                return null;
            }
        });
        listView.setAdapter(new AboutAdapter(getApplicationContext(), aboutList));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

class AboutAdapter extends ArrayAdapter<Map.Entry<String, String>> {

    private Context context;

    public AboutAdapter(Context context, List<Map.Entry<String, String>> entries) {
        super(context, 0, entries);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Map.Entry<String, String> entry = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_client, parent, false);

        TextView username = (TextView) convertView.findViewById(R.id.username);
        TextView information = (TextView) convertView.findViewById(R.id.information);
        username.setText(entry.getKey());
        information.setText(entry.getValue());
        if (entry.getKey().equalsIgnoreCase("Open Source Licenses")) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            });
        }
        return convertView;
    }
}
