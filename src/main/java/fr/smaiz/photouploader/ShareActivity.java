package fr.smaiz.photouploader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Toast;

import java.util.ArrayList;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                sendImg(uri);
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                ArrayList<Uri> imgUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                for (Uri uri : imgUris) {
                    sendImg(uri);
                }
            }
        }
        finish();
    }

    protected void sendImg(Uri uri) {
        Bitmap img;
        String imgName = "defaultName";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            imgName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            cursor.close();
        }
        try {
            img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to get image.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }
        Toast.makeText(this, "Sending " + imgName, Toast.LENGTH_LONG).show();
        new Upload(this).execute(img, imgName);
    }
}
