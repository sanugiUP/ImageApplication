package com.example.reactive_android;

import android.app.Activity;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RemoteUtilities {

    public static RemoteUtilities remoteUtilities = null;
    private Activity displayActivity;


    public RemoteUtilities(Activity displayActivity) {
        this.displayActivity = displayActivity;
    }


    public void setUIActivity(Activity displayActivity){
        this.displayActivity = displayActivity;
    }


    public static RemoteUtilities getInstance(Activity displayActivity) {
        if(remoteUtilities == null) {
            remoteUtilities = new RemoteUtilities(displayActivity);
        }
        remoteUtilities.setUIActivity(displayActivity);
        return remoteUtilities;
    }


    public HttpURLConnection openConnection(String urlString) {

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(connection == null) {
            displayActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(displayActivity,"Check Internet Connection",Toast.LENGTH_LONG).show();
                }
            });
        }

        return connection;
    }


    public boolean isConnectionOkay(HttpURLConnection conn) {
        try
        {
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK) {
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            displayActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(displayActivity,"Problem with API Endpoint",Toast.LENGTH_LONG).show();
                }
            });
        }
        return false;
    }


    public String getResponseString(HttpURLConnection conn) {

        String data = null;
        try
        {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = IOUtils.toByteArray(inputStream);
            data = new String(byteData, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
