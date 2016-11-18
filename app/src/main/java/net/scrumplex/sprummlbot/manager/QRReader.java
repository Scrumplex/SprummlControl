package net.scrumplex.sprummlbot.manager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import net.scrumplex.sprummlbot.manager.dialogs.AuthKeyDialog;

public class QRReader extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView myDecoderView;
    private Intent returnIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrreader);
        returnIntent = new Intent();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        myDecoderView = (QRCodeReaderView) findViewById(R.id.qr_decoder_view);
        assert myDecoderView != null;
        myDecoderView.setOnQRCodeReadListener(this);
        myDecoderView.setQRDecodingEnabled(false);
        myDecoderView.setAutofocusInterval(1000L);
        myDecoderView.setQRDecodingEnabled(true);
        myDecoderView.setBackCamera();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final QRReader me = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthKeyDialog dialog = new AuthKeyDialog();
                dialog.setActivity(me);
                dialog.show(getSupportFragmentManager(), "authkey");
            }
        });
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (text.startsWith("sprummlbot://")) {
            String part = text.substring("sprummlbot://".length());
            if (part.contains("@")) {
                String[] parts = part.split("@", 2);
                String apiKey = parts[0];
                String ipPort = parts[1];
                returnIntent.putExtra("apiKey", apiKey);
                returnIntent.putExtra("ipPort", ipPort);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDecoderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myDecoderView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission denied to access the camera!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
