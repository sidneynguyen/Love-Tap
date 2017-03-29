package com.sidneynguyendev.lovetap;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * File Name: MainFragment.java
 * Authors: Sidney Nguyen
 * Date Created: 3/26/17
 */

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragment";

    private OnMainFragmentInteractionListener mListener;

    private CardView mCrushCardView;

    private ProfilePictureView mCrushProfilePicView;
    private TextView mCrushTextView;
    private TextView mCrushDecisionTextView;
    private TextView mTimeTextView;

    private CountDownTimer mTimer;

    private ProgressBar mProgressBar;

    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mCrushCardView = (CardView) view.findViewById(R.id.cardview_main_crush);
        mCrushProfilePicView = (ProfilePictureView) view.findViewById(R.id.profilepicview_main_crush);
        mCrushTextView = (TextView) view.findViewById(R.id.textview_main_crushname);
        mCrushDecisionTextView = (TextView) view.findViewById(R.id.textview_main_crushdecision);
        mTimeTextView = (TextView) view.findViewById(R.id.textview_main_time);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_mainfrag);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCrushCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarVisibility(View.VISIBLE);
                RestRequester requester = new RestRequester();
                AccessToken token = AccessToken.getCurrentAccessToken();
                String uid = token.getUserId();
                String url = "http://10.0.2.2:3000/api/get/time";
                JSONObject body = new JSONObject();
                try {
                    body.put("facebookId", uid);
                    body.put("accessToken", token.getToken());
                    requester.post(url, body, new RestRequester.OnJsonListener() {
                        @Override
                        public void onJson(Exception e, JsonReader jsonReader) {
                            setProgressBarVisibility(View.GONE);
                            if (e != null) {
                                Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/time", e);
                                showErrorOnUIThread("Could not connect to the server. Please try again.");
                            } else {
                                try {
                                    boolean canUpdate = false;
                                    label:
                                    while (jsonReader.hasNext()) {
                                        String key = jsonReader.nextName();
                                        switch (key) {
                                            case "canUpdate":
                                                canUpdate = jsonReader.nextBoolean();
                                                break label;
                                            case "err":
                                                int error = jsonReader.nextInt();
                                                showErrorOnUIThread("Could not connect to the server. Please try again.");
                                                handleError(error);
                                                return;
                                            default:
                                                jsonReader.skipValue();
                                                break;
                                        }
                                    }
                                    if (canUpdate) {
                                        mListener.onMainFragmentSelectCrush();
                                    } else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),
                                                        "You can only select a new crush once every 24 hours",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (IOException eIO) {
                                    Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/time", eIO);
                                    showErrorOnUIThread("Could not connect to the server. Please try again.");
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/time", e);
                    showErrorOnUIThread("Could not connect to the server. Please try again.");
                    setProgressBarVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            Log.d(TAG, "User not logged in");
            mListener.onMainFragmentLogOut();
        } else {
            setProgressBarVisibility(View.VISIBLE);
            String uid = token.getUserId();
            JSONObject body = new JSONObject();
            try {
                body.put("facebookId", uid);
                body.put("accessToken", token.getToken());
                String url = "http://10.0.2.2:3000/api/get/crush";
                RestRequester requester = new RestRequester();
                requester.post(url, body, new RestRequester.OnJsonListener() {
                    @Override
                    public void onJson(Exception e, JsonReader jsonReader) {
                        setProgressBarVisibility(View.GONE);
                        if (e != null) {
                            Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/crush", e);
                            showErrorOnUIThread("Could not connect to the server. Please try again.");
                        } else {
                            String crushId = null;
                            String crushName = null;
                            boolean crushMe = false;
                            long timeLeft = 0;
                            try {
                                while (jsonReader.hasNext()) {
                                    String key = jsonReader.nextName();
                                    switch (key) {
                                        case "crushId":
                                            try {
                                                crushId = jsonReader.nextString();
                                            } catch (IllegalStateException eISE) {
                                                jsonReader.skipValue();
                                                crushId = null;
                                            }
                                            break;
                                        case "crushName":
                                            try {
                                                crushName = jsonReader.nextString();
                                            } catch (IllegalStateException eISE) {
                                                jsonReader.skipValue();
                                                crushName = null;
                                            }
                                            break;
                                        case "me":
                                            crushMe = jsonReader.nextBoolean();
                                            break;
                                        case "timeLeft":
                                            timeLeft = jsonReader.nextLong();
                                            break;
                                        case "err":
                                            int error = jsonReader.nextInt();
                                            showErrorOnUIThread("Could not connect to the server. Please try again.");
                                            handleError(error);
                                            return;
                                        default:
                                            jsonReader.skipValue();
                                            break;
                                    }
                                }
                                updateUI(crushId, crushName, crushMe, timeLeft);
                            } catch (IOException eIO) {
                                Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/crush", eIO);
                                showErrorOnUIThread("Could not connect to the server. Please try again.");
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "POST to http://10.0.2.2:3000/api/get/crush", e);
                showErrorOnUIThread("Could not connect to the server. Please try again.");
                setProgressBarVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void updateUI(final String id, final String name, final boolean me, final long timeLeft) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mTimeTextView.setText(TimeParser.parseMillis(timeLeft));
                    if (id != null && name != null) {
                        mCrushProfilePicView.setProfileId(id);
                        mCrushTextView.setText(name);
                        if (me) {
                            mCrushDecisionTextView.setText(getResources().getString(R.string.string_main_crushtoo, name.toUpperCase()));
                        } else {
                            mCrushDecisionTextView.setText(getResources().getString(R.string.string_main_crushyet, name));
                        }
                    } else {
                        mCrushProfilePicView.setProfileId(null);
                        mCrushTextView.setText(R.string.string_crush_select);
                        mCrushDecisionTextView.setText("");
                    }
                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    mTimer = new CountDownTimer(timeLeft, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mTimeTextView.setText(TimeParser.parseMillis(millisUntilFinished));
                        }

                        @Override
                        public void onFinish() {
                            mTimeTextView.setText(TimeParser.parseMillis(0));
                            mTimer = null;
                        }
                    };
                    mTimer.start();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
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

    interface OnMainFragmentInteractionListener {
        void onMainFragmentSelectCrush();
        void onMainFragmentLogOut();
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
                mListener.onMainFragmentLogOut();
                break;
            default:
                Log.e(TAG, "ERROR UNKNOWN: " + error);
                break;
        }
    }

    private void showErrorOnUIThread(final String err) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setProgressBarVisibility(final int visibility) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(visibility);
                }
            });
        }
    }
}
