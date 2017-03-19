package com.sidneynguyendev.lovetap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.util.ArrayList;

/**
 * Created by sidney on 3/19/17.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendViewHolder> {

    private ArrayList<FbFriend> mFriendList;

    public FriendListAdapter(ArrayList<FbFriend> friendList) {
        this.mFriendList = friendList;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_select_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.setProfilePic(mFriendList.get(position).getId());
        holder.setNameText(mFriendList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        private ProfilePictureView mFriendProfilePic;
        private TextView mFriendNameText;

        public FriendViewHolder(View itemView) {
            super(itemView);
            mFriendProfilePic = (ProfilePictureView) itemView.findViewById(R.id.profilepicview_select_friend);
            mFriendNameText = (TextView) itemView.findViewById(R.id.textview_select_friendname);
        }

        public void setProfilePic(String id) {
            mFriendProfilePic.setProfileId(id);
        }

        public void setNameText(String name) {
            mFriendNameText.setText(name);
        }
    }
}
