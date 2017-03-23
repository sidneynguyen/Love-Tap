package com.sidneynguyendev.lovetap;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;

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
                    .add(R.id.framelayout_main_fragmentcontainer, mLoginFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
        }
    }

    @Override
    public void onLoginFragmentSuccess(AccessToken token) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_main_fragmentcontainer, mMainFragment).commit();
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
