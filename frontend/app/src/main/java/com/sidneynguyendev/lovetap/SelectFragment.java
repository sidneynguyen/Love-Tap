package com.sidneynguyendev.lovetap;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * File Name: SelectFragment.java
 * Authors: Sidney Nguyen
 * Date Created: 3/26/17
 */

public class SelectFragment extends Fragment implements FriendListAdapter.OnFriendClickListener {
    private static final String TAG = "SelectFragment";

    private OnSelectFragmentInteractionListener mListener;

    private FriendListAdapter mFriendListAdapter;

    private ArrayList<FbFriend> mFriendList;

    public SelectFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriendList = new ArrayList<>();
        mFriendListAdapter = new FriendListAdapter(mFriendList);
        mFriendListAdapter.setOnFriendClickListener(SelectFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);
        RecyclerView friendListView = (RecyclerView) view.findViewById(R.id.recyclerview_select_friendlist);
        friendListView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendListView.setAdapter(mFriendListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null) {
            Log.d(TAG, "User is not logged in");
            mListener.onSelectFragmentCancel();
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
                            Log.e(TAG, "My friends request", e);
                            showErrorOnUIThread("Could not get friends list. Please try again.");

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

    interface OnSelectFragmentInteractionListener {
        void onSelectFragmentCrush(String crushId, String crushName);
        void onSelectFragmentCancel();
    }

    private void showErrorOnUIThread(final String err) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
            }
        });
    }

}
