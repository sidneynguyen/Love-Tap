package com.sidneynguyendev.lovetap;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentInteractionListener,
        MainFragment.OnMainFragmentInteractionListener,
        SelectFragment.OnSelectFragmentInteractionListener {

    private LoginFragment mLoginFragment;
    private MainFragment mMainFragment;
    private SelectFragment mSelectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginFragment = new LoginFragment();
        mMainFragment = new MainFragment();
        mSelectFragment = new SelectFragment();

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_main_fragmentcontainer, mLoginFragment).commit();
        } else {
            onLoginFragmentSuccess(token);
        }
    }

    @Override
    public void onLoginFragmentSuccess(final AccessToken token) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:3000/auth/facebook/token?access_token=" + token.getToken());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            /*if (key.equals("organization_url")) { // Check if desired key
                                // Fetch the value as a String
                                String value = jsonReader.nextString();

                                // Do something with the value
                                // ...

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }*/
                            Log.d("HERE", key + ": " + jsonReader.nextString());
                        }
                        jsonReader.close();
                        connection.disconnect();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
                    } else {
                        Log.e("ERROR", "" + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    Log.e("ERROR", "ERROR", e);
                } catch (IOException e) {
                    Log.e("ERROR", "ERROR", e);
                }
            }
        });
    }

    @Override
    public void onMainFragmentSelectCrush() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main_fragmentcontainer, mSelectFragment).commit();
    }

    @Override
    public void onMainFragmentLogOut() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main_fragmentcontainer, mLoginFragment).commit();
        mMainFragment = new MainFragment();
        mSelectFragment = new SelectFragment();
    }

    @Override
    public void onSelectFragmentCrush(final String crushId, final String crushName) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AccessToken token = AccessToken.getCurrentAccessToken();
                    String uid = Profile.getCurrentProfile().getId();
                    URL url = new URL("http://10.0.2.2:3000/api/crush");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
                    connection.setRequestMethod("POST");
                    JSONObject object = new JSONObject();
                    try {
                        object.put("facebookId", uid);
                        object.put("accessToken", token.getToken());
                        object.put("crushId", crushId);
                        object.put("crushName", crushName);
                    } catch (JSONException e) {

                    }
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(object.toString());
                    wr.flush();
                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            /*if (key.equals("organization_url")) { // Check if desired key
                                // Fetch the value as a String
                                String value = jsonReader.nextString();

                                // Do something with the value
                                // ...

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }*/
                            Log.d("HERE", key + ": " + jsonReader.nextString());
                        }
                        jsonReader.close();
                        connection.disconnect();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
                    } else {
                        Log.e("ERROR", "" + connection.getResponseCode());
                    }
                } catch (MalformedURLException e) {
                    Log.e("ERROR", "ERROR", e);
                } catch (IOException e) {
                    Log.e("ERROR", "ERROR", e);
                }
            }
        });
    }
}
