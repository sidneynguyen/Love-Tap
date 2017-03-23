package com.sidneynguyendev.lovetap;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SelectFragment extends Fragment implements FriendListAdapter.OnFriendClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnSelectFragmentInteractionListener mListener;

    private RecyclerView mFriendListView;
    private FriendListAdapter mFriendListAdapter;

    private ArrayList<FbFriend> mFriendList;

    private Button mCancelButton;

    public SelectFragment() {
    }

    public static SelectFragment newInstance(String param1, String param2) {
        SelectFragment fragment = new SelectFragment();
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
        mFriendList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(mFriendList);
        mFriendListAdapter.setOnFriendClickListener(SelectFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        mFriendListView = (RecyclerView) view.findViewById(R.id.recyclerview_select_friendlist);
        mFriendListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendListView.setAdapter(mFriendListAdapter);
        mCancelButton = (Button) view.findViewById(R.id.button_select_cancel);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSelectFragmentCancel();
            }
        });

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            // not logged in
        } else {
            GraphRequest.newMyFriendsRequest(token, new GraphRequest.GraphJSONArrayCallback() {
                @Override
                public void onCompleted(JSONArray objects, GraphResponse response) {
                    if (objects != null) {
                        try {
                            for (int i = 0; i < objects.length(); i++) {
                                FbFriend friend = new FbFriend(
                                        objects.getJSONObject(i).getString("id"),
                                        objects.getJSONObject(i).getString("name")
                                );
                                mFriendList.add(friend);
                                mFriendListAdapter.notifyItemInserted(mFriendList.size() - 1);
                            }
                        } catch (JSONException e) {

                        }
                    }
                }
            }).executeAsync();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectFragmentInteractionListener) {
            mListener = (OnSelectFragmentInteractionListener) context;
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
    public void onFriendClick(View view, int position) {
        mListener.onSelectFragmentCrush(mFriendList.get(position).getId(), mFriendList.get(position).getName());
    }

    public interface OnSelectFragmentInteractionListener {
        void onSelectFragmentCrush(String crushId, String crushName);
        void onSelectFragmentCancel();
    }

}
