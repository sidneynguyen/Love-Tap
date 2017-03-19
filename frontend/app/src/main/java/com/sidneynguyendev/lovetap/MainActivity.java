package com.sidneynguyendev.lovetap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            // LoginFragment
        } else {
            // MainFragment
        }
    }
}
