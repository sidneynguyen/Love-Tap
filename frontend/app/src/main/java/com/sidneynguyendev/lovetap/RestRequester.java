package com.sidneynguyendev.lovetap;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * File Name: RestRequester.java
 * Authors: Sidney Nguyen
 * Date Created: 3/25/17
 */

class RestRequester {
    void post(final String urlString, final JSONObject body, final OnJsonListener listener) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Secrets.SERVER_URL + urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(20000);
                    conn.setReadTimeout(20000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json; charset=UTF-8");
                    conn.setRequestMethod("POST");
                    if (body != null) {
                        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                        writer.write(body.toString());
                        writer.flush();
                    }
                    if (conn.getResponseCode() == 200) {
                        InputStream responseBody = conn.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        listener.onJson(null, jsonReader);
                        jsonReader.close();
                        conn.disconnect();
                    } else {
                        listener.onJson(new Exception("Could not establish a connection."), null);
                    }
                } catch (IOException e) {
                    listener.onJson(e, null);
                }
            }
        });
    }

    interface OnJsonListener {
        void onJson(Exception e, JsonReader jsonReader);
    }
}
