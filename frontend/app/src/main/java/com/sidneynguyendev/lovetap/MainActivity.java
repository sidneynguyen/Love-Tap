package com.sidneynguyendev.lovetap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
            setFragment(FRAG_LOGIN);
        } else {
            onLoginFragmentSuccess(token);
        }
    }

    private void setFragment(int fragment) {
        mCurrFragment = fragment;
        switch (fragment) {
            case FRAG_LOGIN:
                mLoginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mLoginFragment).commit();
                invalidateOptionsMenu();
                break;
            case FRAG_MAIN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
                invalidateOptionsMenu();
                break;
            case FRAG_SEL:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_main_fragmentcontainer, mSelectFragment).commit();
                invalidateOptionsMenu();
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
                        showErrorOnUIThread("An error has occurred when trying to log in. Please try again.");
                        LoginManager.getInstance().logOut();
                        setFragment(FRAG_LOGIN);
                    } else {
                        setFragment(FRAG_MAIN);
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/auth/facebook/token", e);
            showErrorOnUIThread("An error has occurred when trying to log in. Please try again.");
            LoginManager.getInstance().logOut();
            setFragment(FRAG_LOGIN);
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
            String url = "http://10.0.2.2:3000/api/select/crush";
            requester.post(url, body, new RestRequester.OnJsonListener() {
                @Override
                public void onJson(Exception e, JsonReader jsonReader) {
                    if (e != null) {
                        Log.e(TAG, "POST to http://10.0.2.2:3000/api/select/crush", e);
                        showErrorOnUIThread("Could not connect to the server. Please try again.");
                    } else {
                        try {
                            while (jsonReader.hasNext()) {
                                String key = jsonReader.nextName();
                                if (key.equals("err")) {
                                    int error = jsonReader.nextInt();
                                    handleError(error);
                                    return;
                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            setFragment(FRAG_MAIN);
                        } catch (IOException eIO) {
                            Log.e(TAG, "POST to http://10.0.2.2:3000/api/select/crush", eIO);
                            showErrorOnUIThread("Could not connect to the server. Please try again.");
                        }
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/api/select/crush", e);
            showErrorOnUIThread("Could not connect to the server. Please try again.");
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
                        Log.e(TAG, "POST to http://10.0.2.2:3000/api/clear/crush", e);
                        showErrorOnUIThread("Could not connect to the server. Please try again.");
                    } else {
                        try {
                            while (jsonReader.hasNext()) {
                                String key = jsonReader.nextName();
                                if (key.equals("err")) {
                                    int error = jsonReader.nextInt();
                                    handleError(error);
                                    return;
                                } else {
                                    jsonReader.skipValue();
                                }
                            }
                            getSupportFragmentManager().beginTransaction().detach(mMainFragment).attach(mMainFragment).commit();
                        } catch (IOException eIO) {
                            Log.e(TAG, "POST to http://10.0.2.2:3000/api/clear/crush", e);
                            showErrorOnUIThread("Could not connect to the server. Please try again.");
                        }
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "POST to http://10.0.2.2:3000/api/clear/crush", e);
            showErrorOnUIThread("Could not connect to the server. Please try again.");
        }
    }

    private void handleError(int error) {
        switch (error) {
            case ErrorCodes.ERR_DATABASE:
                Log.e(TAG, "ERROR DATABASE");
                break;
            case ErrorCodes.ERR_24_HOURS_NOT_PASSED:
                Log.e(TAG, "ERROR 24 HOURS NOT PASSED");
                showErrorOnUIThread("You can only select a new crush once every 24 hours");
                break;
            case ErrorCodes.ERR_USER_NOT_FOUND:
                Log.e(TAG, "ERROR USER NOT FOUND");
                showErrorOnUIThread("An error has occurred. You have been logged out.");
                LoginManager.getInstance().logOut();
                setFragment(FRAG_LOGIN);
                mMainFragment = new MainFragment();
                mSelectFragment = new SelectFragment();
                break;
            default:
                Log.e(TAG, "ERROR UNKNOWN: " + error);
                break;
        }
    }

    private void showErrorOnUIThread(final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, err, Toast.LENGTH_LONG).show();
            }
        });
    }
}
