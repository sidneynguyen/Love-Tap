package com.sidneynguyendev.lovetap;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.facebook.AccessToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private String mServerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoginFragment = new LoginFragment();
        mMainFragment = new MainFragment();
        mSelectFragment = new SelectFragment();

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            onLoginFragmentSuccess(token);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
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
                            if (key.equals("_id")) {
                                String value = jsonReader.nextString();
                                mServerId = value;
                                Log.d("HERE", "mServerId: " + value);
                            } else {
                                Log.d("HERE", key + ": " + jsonReader.nextString());
                            }
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
    public void onSelectFragmentCrush(String uid) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
    }
}
