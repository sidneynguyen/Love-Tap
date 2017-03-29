package com.sidneynguyen.lovetap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sidneynguyen.lovetap.R;

/**
 * File Name: LoginFragment.java
 * Authors: Sidney Nguyen
 * Date Created: 3/26/17
 */

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    private OnLoginFragmentInteractionListener mListener;

    private CallbackManager mCallbackManager;
    private LoginButton mLoginButton;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mLoginButton = (LoginButton) view.findViewById(R.id.loginbutton_login_fb);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoginButton.setReadPermissions("user_friends");
        mLoginButton.setFragment(this);
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mLoginButton.setVisibility(View.GONE);
                mListener.onLoginFragmentSuccess(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FB login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "FB login error", error);
                Toast.makeText(getContext(), "An error has occurred when trying to log in. Please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    interface OnLoginFragmentInteractionListener {
        void onLoginFragmentSuccess(AccessToken token);
    }
}
