package com.sidneynguyendev.lovetap;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity
        implements LoginFragment.OnLoginFragmentInteractionListener,
        MainFragment.OnMainFragmentInteractionListener,
        SelectFragment.OnSelectFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.framelayout_test_fragmentcontainer, mainFragment).commit();

        Button loginButton = (Button) findViewById(R.id.button_test_login);
        Button mainButton = (Button) findViewById(R.id.button_test_main);
        Button selectButton = (Button) findViewById(R.id.button_test_select);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_test_fragmentcontainer, loginFragment).commit();
            }
        });

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment mainFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_test_fragmentcontainer, mainFragment).commit();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectFragment selectFragment = new SelectFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.framelayout_test_fragmentcontainer, selectFragment).commit();
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
