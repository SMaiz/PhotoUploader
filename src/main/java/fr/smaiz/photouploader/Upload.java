package fr.smaiz.photouploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sidou on 01/07/17.
 */

public class Upload extends AsyncTask<Object, Void, String> {
    public final String BOUNDARY = "****___SMaiz@smaiz.fr___****";
    public final String CRLF = "\r\n";

    private MainActivity context = null;
    private boolean ok = false;

    public Upload(MainActivity context_p) {
        context = context_p;
    }

    public byte[] getImgbytes(Bitmap img) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public byte[] sendBytes(String name, byte[] obj, String filename, String type) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String header = "--" + BOUNDARY + CRLF +
            "Content-Disposition: form-data; name=\"" + "image" + "\";filename=\"" + filename + "\"" + CRLF +
            //"Content-Type: " + type + CRLF +
            CRLF;
        String end = CRLF + "--" + BOUNDARY + "--" + CRLF;
        try {
            out.write(header.getBytes());
            out.write(obj);
            out.write(end.getBytes());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    protected void onPreExecute() {
        context.loading(true);
    }

    protected void onPostExecute(String obj) {
        if (ok) {
            Toast.makeText(context, R.string.success, Toast.LENGTH_LONG).show();
            context.loading(false);
        }
        else {
            Toast.makeText(context, R.string.failure, Toast.LENGTH_LONG).show();
            context.loading(false);
        }
        context.showText(obj);
    }

    protected void onCancelled() {
        Toast.makeText(context, R.string.cancelled, Toast.LENGTH_LONG).show();
        context.loading(false);
    }

    protected String doInBackground(Object... args) {
        String uri = PreferenceManager.getDefaultSharedPreferences(context).getString("address", "euuh...");
        String name = PreferenceManager.getDefaultSharedPreferences(context).getString("filename", "fichier");
        Bitmap img = (Bitmap) args[0];
        String imgFileName = (String) args[1];

        URL url;
        HttpURLConnection hUC;
        try {
            url = new URL(uri);
            hUC = (HttpURLConnection) url.openConnection();
            hUC.setRequestMethod("POST");
            hUC.setUseCaches(false);
            hUC.setDoOutput(true);
        } catch (Exception e) {
            return "1" + e.toString();
        }
        hUC.setRequestProperty("Connection", "Keep-Alive");
        hUC.setRequestProperty("Cache-Control", "no-cache");
        hUC.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.BOUNDARY);

        try {
            OutputStream out = new DataOutputStream(hUC.getOutputStream());
            out.write(sendBytes(name, getImgbytes(img), imgFileName, "image/*"));
            out.flush();
            out.close();
        } catch (IOException e) {
            return "2" + e.toString();
        }

        try {
            if (hUC.getResponseCode() == HttpURLConnection.HTTP_OK)
                ok = true;
            InputStream in = hUC.getInputStream();
            BufferedReader reader = null;
            StringBuilder strBuilder = new StringBuilder();

            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    strBuilder.append(line);
                }
            } catch (IOException e) {
                return "3" + e.toString();
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            return strBuilder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return "4" + e.toString();
        }
    }

}
