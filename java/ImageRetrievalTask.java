package com.example.reactive_android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ImageRetrievalTask implements Callable<List<Bitmap>> {

    private final Activity displayActivity;
    private String data;
    private final RemoteUtilities remoteUtilities;

    public ImageRetrievalTask(Activity displayActivity) {
        remoteUtilities = RemoteUtilities.getInstance(displayActivity);
        this.displayActivity = displayActivity;
        this.data = null;
    }


    @Override
    public List<Bitmap> call() throws Exception {

        List<Bitmap> images = null;
        List<String> endpoints = getEndpoint(this.data);

        if(endpoints == null) {
            displayActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(displayActivity,"No Images Found",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            images = getImageFromUrl(endpoints);
            /* Make Thread Sleep To Display The Progress Bar */
            try { Thread.sleep(3000); }
            catch (Exception ignored) { }
        }
        return images;
    }


    private List<String> getEndpoint(String data) {

        List<String> imageUrls = null;
        try
        {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            Log.d("NUMBER OF PICTURES LOADED:", String.valueOf(jHits.length()));

            if(jHits.length() > 0) {
                imageUrls = new ArrayList<>();
                for(int i = 0; i < jHits.length(); i++){
                    JSONObject jHitsItem = jHits.getJSONObject(i);
                    String imageUrl = jHitsItem.getString("previewURL");
                    //Log.d("IMAGE URL:", imageUrl);
                    imageUrls.add(imageUrl);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }


    private List<Bitmap> getImageFromUrl(List<String> imageUrls) {

        List<Bitmap> images = new ArrayList<>();

        for(int i = 0; i < imageUrls.size(); i++) {
            Uri.Builder url = Uri.parse(imageUrls.get(i)).buildUpon();
            String urlString = url.build().toString();
            HttpURLConnection connection = remoteUtilities.openConnection(urlString);

            if(connection != null) {
                if(remoteUtilities.isConnectionOkay(connection)==true) {
                    images.add(getBitmapFromConnection(connection));
                    connection.disconnect();
                }
            }
        }
        return images;
    }


    public Bitmap getBitmapFromConnection(HttpURLConnection conn) {

        Bitmap data = null;
        try
        {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }


    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }


    public void setData(String data) {
        this.data = data;
    }


















    /* MY PROJECT */
    /*private Activity displayActivity;
    private List<String> data;
    private RemoteUtilities remoteUtilities;

    public ImageRetrievalTask(Activity displayActivity) {
        remoteUtilities = RemoteUtilities.getInstance(displayActivity);
        this.displayActivity = displayActivity;
        this.data = null;
    }

    @Override
    public List<Bitmap> call() throws Exception {

        List<Bitmap> images = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            Bitmap image = null;
            String endpoint = getEndpoint(this.data.get(i));
            if(endpoint == null) {
                displayActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(displayActivity,"No image found",Toast.LENGTH_LONG).show();
                        // Ignore For Now
                    }
                });
            }
            else {
                image = getImageFromUrl(endpoint);
                images.add(image);
            }
        }
        return images;
    }

    private String getEndpoint(String data) {
        String imageUrl = null;
        try {
            JSONObject jBase = new JSONObject(data);
            JSONArray jHits = jBase.getJSONArray("hits");
            if(jHits.length()>0){
                JSONObject jHitsItem = jHits.getJSONObject(0);
                imageUrl = jHitsItem.getString("largeImageURL");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    private Bitmap getImageFromUrl(String imageUrl) {
        Bitmap image = null;
        Uri.Builder url = Uri.parse(imageUrl).buildUpon();
        String urlString = url.build().toString();
        HttpURLConnection connection = remoteUtilities.openConnection(urlString);
        if(connection!=null){
            if(remoteUtilities.isConnectionOkay(connection)==true){
                image = getBitmapFromConnection(connection);
                connection.disconnect();
            }
        }
        return image;
    }

    public Bitmap getBitmapFromConnection(HttpURLConnection conn) {
        Bitmap data = null;
        try {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData,0,byteData.length);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public void setData(List<String> data) {
        this.data = data;
    }*/

}
