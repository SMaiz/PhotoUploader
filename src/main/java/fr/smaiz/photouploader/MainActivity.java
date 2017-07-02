package fr.smaiz.photouploader;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;//*/

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends Activity {
    public static final int SELECT_PHOTO = 100;

    private ImageButton sendBtn = null;
    private Button photoBtn = null;
    private Button stopBtn = null;
    private Uri imgSelect;
    private Bitmap img = null;
    private ProgressBar progUp = null;
    private Upload uploadTask = null;
    private TextView txtView = null;
    private String imgName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendBtn = (ImageButton) findViewById(R.id.sendBtn);
        photoBtn = (Button) findViewById(R.id.photoBtn);
        progUp = (ProgressBar) findViewById(R.id.progressUpload);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        txtView = (TextView) findViewById(R.id.txtView);

        this.loading(false);
        this.showText("Test");

        photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                startActivityForResult(photoPickIntent, MainActivity.SELECT_PHOTO);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img == null) {
                    Toast.makeText(view.getContext(), R.string.not_chosed, Toast.LENGTH_LONG).show();
                }
                else {
                    uploadTask = new Upload(MainActivity.this);
                    uploadTask.execute(img, imgName);
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploadTask != null)
                    uploadTask.cancel(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.options) {
            startActivity(new Intent(this, PrefsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void loading(boolean show) {
        if (show) {
            progUp.setVisibility(View.VISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
            sendBtn.setVisibility(View.GONE);
            photoBtn.setVisibility(View.GONE);
        }
        else {
            progUp.setVisibility(View.GONE);
            stopBtn.setVisibility(View.GONE);
            sendBtn.setVisibility(View.VISIBLE);
            photoBtn.setVisibility(View.VISIBLE);
        }
    }

    public void showText(String txt) {
        txtView.setText(txt);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent imgRetIntent) {
        super.onActivityResult(reqCode, resCode, imgRetIntent);

        switch (reqCode) {
        case MainActivity.SELECT_PHOTO:
            if (resCode == RESULT_OK) {
                imgSelect = imgRetIntent.getData();
                try {
                    Cursor cursor = getContentResolver().query(imgSelect, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        imgName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), imgSelect);
                    this.showText(imgName);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }
}