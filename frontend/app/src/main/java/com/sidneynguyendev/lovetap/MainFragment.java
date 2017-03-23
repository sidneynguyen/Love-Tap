package com.sidneynguyendev.lovetap;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnMainFragmentInteractionListener mListener;

    private CardView mCrushCardView;
    private Button mLogoutButton;

    private ProfilePictureView mCrushProfilePicView;
    private TextView mCrushTextView;

    public MainFragment() {}

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mCrushCardView = (CardView) view.findViewById(R.id.cardview_main_crush);
        mLogoutButton = (Button) view.findViewById(R.id.button_main_logout);
        mCrushProfilePicView = (ProfilePictureView) view.findViewById(R.id.profilepicview_main_crush);
        mCrushTextView = (TextView) view.findViewById(R.id.textview_main_crushname);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCrushCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMainFragmentSelectCrush();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                mListener.onMainFragmentLogOut();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AccessToken token = AccessToken.getCurrentAccessToken();
                    String uid = Profile.getCurrentProfile().getId();
                    URL url = new URL("http://10.0.2.2:3000/api/me/crush");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
                    connection.setRequestMethod("POST");
                    JSONObject object = new JSONObject();
                    try {
                        object.put("facebookId", uid);
                        object.put("accessToken", token.getToken());
                    } catch (JSONException e) {

                    }
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(object.toString());
                    wr.flush();
                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);
                        jsonReader.beginObject();
                        String crushId = null;
                        String crushName = null;
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
                            if (key.equals("crushId")) {
                                crushId = jsonReader.nextString();
                            } else if (key.equals("crushName")) {
                                crushName = jsonReader.nextString();
                            } else {
                                Log.d("HERE", key + ": " + jsonReader.nextString());
                            }
                            if (crushId != null && crushName != null) {
                                updateCrush(crushId, crushName);
                            }
                        }
                        jsonReader.close();
                        connection.disconnect();
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

    private void updateCrush(final String id, final String name) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCrushProfilePicView.setProfileId(id);
                mCrushTextView.setText(name);
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

    public interface OnMainFragmentInteractionListener {
        void onMainFragmentSelectCrush();
        void onMainFragmentLogOut();
    }
}
