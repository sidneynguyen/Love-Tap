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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
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

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mCrushCardView = (CardView) view.findViewById(R.id.cardview_main_crush);
        mCrushProfilePicView = (ProfilePictureView) view.findViewById(R.id.profilepicview_main_crush);
        mCrushTextView = (TextView) view.findViewById(R.id.textview_main_crushname);
        mCrushDecisionTextView = (TextView) view.findViewById(R.id.textview_main_crushdecision);
        mTimeTextView = (TextView) view.findViewById(R.id.textview_main_time);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCrushCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestRequester requester = new RestRequester();
                AccessToken token = AccessToken.getCurrentAccessToken();
                String uid = token.getUserId();
                String url = "http://10.0.2.2:3000/api/me/time";
                JSONObject body = new JSONObject();
                try {
                    body.put("facebookId", uid);
                    body.put("accessToken", token.getToken());
                    requester.post(url, body, new RestRequester.OnJsonListener() {
                        @Override
                        public void onJson(Exception e, JsonReader jsonReader) {
                            try {
                                boolean canUpdate = false;
                                while (jsonReader.hasNext()) {
                                    String key = jsonReader.nextName();
                                    if (key.equals("canUpdate")) {
                                        canUpdate = jsonReader.nextBoolean();
                                        break;
                                    } else {
                                            jsonReader.skipValue();
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
                                Log.e(TAG, "POST to http://10.0.2.2:3000/api/me/time", eIO);
                            }
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "POST to http://10.0.2.2:3000/api/me/time", e);
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
            String uid = token.getUserId();
            JSONObject body = new JSONObject();
            try {
                body.put("facebookId", uid);
                body.put("accessToken", token.getToken());
                String url = "http://10.0.2.2:3000/api/me/crush";
                RestRequester requester = new RestRequester();
                requester.post(url, body, new RestRequester.OnJsonListener() {
                    @Override
                    public void onJson(Exception e, JsonReader jsonReader) {
                        if (e != null) {
                            Log.e(TAG, "POST to http://10.0.2.2:3000/api/me/crush", e);
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
                                            crushId = jsonReader.nextString();
                                            break;
                                        case "crushName":
                                            crushName = jsonReader.nextString();
                                            break;
                                        case "me":
                                            crushMe = jsonReader.nextBoolean();
                                            break;
                                        case "timeLeft":
                                            timeLeft = jsonReader.nextLong();
                                            break;
                                        default:
                                            jsonReader.skipValue();
                                            break;
                                    }
                                }
                                updateUI(crushId, crushName, crushMe, timeLeft);
                            } catch (IOException eIO) {
                                Log.e(TAG, "POST to http://10.0.2.2:3000/api/me/crush", eIO);
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "POST to http://10.0.2.2:3000/api/me/crush", e);
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mTimeTextView.setText(TimeParser.parseMillis(timeLeft));
                if (id != null && name != null) {
                    mCrushProfilePicView.setProfileId(id);
                    mCrushTextView.setText(name);
                    if (me) {
                        mCrushDecisionTextView.setText(name.toUpperCase() + " HAS A CRUSH ON YOU TOO!!!");
                    } else {
                        mCrushDecisionTextView.setText(name + " has not chosen you yet ;)");
                    }
                } else {
                    mCrushProfilePicView.setProfileId(null);
                    mCrushTextView.setText("Select a crush!!!");
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
}
