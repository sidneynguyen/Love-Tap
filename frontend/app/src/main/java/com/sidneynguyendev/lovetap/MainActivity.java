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

    private static final String TAG = "MainActivity";

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
    public void onLoginFragmentSuccess(AccessToken token) {
        RestRequester requester = new RestRequester();
        String url = "http://10.0.2.2:3000/auth/facebook/token?access_token=" + token.getToken();
        requester.post(url, null, new RestRequester.OnJsonListener() {
            @Override
            public void onJson(Exception e, JsonReader jsonReader) {
                if (e != null) {
                    Log.e(TAG, "POST to http://10.0.2.2:3000/auth/facebook/token?access_token=", e);
                } else {
                    try {
                        while (jsonReader.hasNext()) {
                            String key = jsonReader.nextName();
                            Log.d(TAG, key + ": " + jsonReader.nextString());
                        }
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
                    } catch (IOException eIO) {
                        Log.e(TAG, "POST to http://10.0.2.2:3000/auth/facebook/token?access_token=", eIO);
                    }
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
        AccessToken token = AccessToken.getCurrentAccessToken();
        String uid = Profile.getCurrentProfile().getId();
        JSONObject body = new JSONObject();
        try {
            body.put("facebookId", uid);
            body.put("accessToken", token.getToken());
            body.put("crushId", crushId);
            body.put("crushName", crushName);
            RestRequester requester = new RestRequester();
            String url = "http://10.0.2.2:3000/api/crush";
            requester.post(url, body, new RestRequester.OnJsonListener() {
                @Override
                public void onJson(Exception e, JsonReader jsonReader) {
                    if (e != null) {
                        Log.e(TAG, "POST to http://10.0.2.2:3000/api/crush", e);
                    } else {
                        try {
                            while (jsonReader.hasNext()) {
                                String key = jsonReader.nextName();
                                Log.d(TAG, key + ": " + jsonReader.nextString());
                            }
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment)
                                    .commit();
                        } catch (IOException eIO) {
                            Log.e(TAG, "POST to http://10.0.2.2:3000/api/crush", eIO);
                        }
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/api/crush", e);
        }
    }

    @Override
    public void onSelectFragmentCancel() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
    }
}
