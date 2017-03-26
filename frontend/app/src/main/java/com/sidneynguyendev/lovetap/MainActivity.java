package com.sidneynguyendev.lovetap;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * File Name: MainActivity.java
 * Authors: Sidney Nguyen
 * Date Created: 3/26/17
 */

public class MainActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentInteractionListener,
        MainFragment.OnMainFragmentInteractionListener,
        SelectFragment.OnSelectFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    private static final int FRAG_LOGIN = 1;
    private static final int FRAG_MAIN = 2;
    private static final int FRAG_SEL = 3;

    private LoginFragment mLoginFragment;
    private MainFragment mMainFragment;
    private SelectFragment mSelectFragment;
    private int mCurrFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    private void setFragment(int fragment) {
        mCurrFragment = fragment;
        switch (fragment) {
            case FRAG_LOGIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mLoginFragment).commit();
                break;
            case FRAG_MAIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
                break;
            case FRAG_SEL:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mSelectFragment).commit();
                break;
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onLoginFragmentSuccess(AccessToken token) {
        RestRequester requester = new RestRequester();
        String url = "http://10.0.2.2:3000/auth/facebook/token";
        JSONObject body = new JSONObject();
        try {
            body.put("access_token", token.getToken());
            requester.post(url, body, new RestRequester.OnJsonListener() {
                @Override
                public void onJson(Exception e, JsonReader jsonReader) {
                    if (e != null) {
                        Log.e(TAG, "POST to http://10.0.2.2:3000/auth/facebook/token", e);
                    } else {
                        setFragment(FRAG_MAIN);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/auth/facebook/token", e);
        }
    }

    @Override
    public void onMainFragmentSelectCrush() {
        setFragment(FRAG_SEL);
    }

    @Override
    public void onMainFragmentLogOut() {
        setFragment(FRAG_LOGIN);
        mMainFragment = new MainFragment();
        mSelectFragment = new SelectFragment();
    }

    @Override
    public void onSelectFragmentCrush(final String crushId, final String crushName) {
        AccessToken token = AccessToken.getCurrentAccessToken();
        String uid = token.getUserId();
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
                            setFragment(FRAG_MAIN);
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
        setFragment(FRAG_MAIN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        switch (mCurrFragment) {
            case FRAG_LOGIN:
                menu.findItem(R.id.menu_logout).setVisible(false);
                menu.findItem(R.id.menu_cancel).setVisible(false);
                menu.findItem(R.id.menu_clearcrush).setVisible(false);
                break;
            case FRAG_MAIN:
                menu.findItem(R.id.menu_logout).setVisible(true);
                menu.findItem(R.id.menu_cancel).setVisible(false);
                menu.findItem(R.id.menu_clearcrush).setVisible(true);
                break;
            case FRAG_SEL:
                menu.findItem(R.id.menu_logout).setVisible(true);
                menu.findItem(R.id.menu_cancel).setVisible(true);
                menu.findItem(R.id.menu_clearcrush).setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout:
                LoginManager.getInstance().logOut();
                setFragment(FRAG_LOGIN);
                mMainFragment = new MainFragment();
                mSelectFragment = new SelectFragment();
                return true;
            case R.id.menu_cancel:
                setFragment(FRAG_MAIN);
                return true;
            case R.id.menu_clearcrush:
                clearCrush();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearCrush() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        String uid = token.getUserId();
        JSONObject body = new JSONObject();
        try {
            body.put("facebookId", uid);
            body.put("accessToken", token.getToken());
            RestRequester requester = new RestRequester();
            String url = "http://10.0.2.2:3000/api/clear/crush";
            requester.post(url, body, new RestRequester.OnJsonListener() {
                @Override
                public void onJson(Exception e, JsonReader jsonReader) {
                    if (e != null) {
                        Log.e(TAG, "POST to http://10.0.2.2:3000/api/crush", e);
                    } else {
                        getSupportFragmentManager().beginTransaction().detach(mMainFragment).attach(mMainFragment).commit();
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/api/crush", e);
        }
    }
}
