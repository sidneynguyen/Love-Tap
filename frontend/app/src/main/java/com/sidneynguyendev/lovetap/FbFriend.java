package com.sidneynguyendev.lovetap;

/**
 * File Name: FBFriend.java
 * Authors: Sidney Nguyen
 * Date Created: 3/19/17
 */

class FbFriend {
    private String mId;
    private String mName;

    FbFriend(String id, String name) {
        mId = id;
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
