package com.sidneynguyendev.lovetap;

/**
 * Created by sidney on 3/19/17.
 */

public class FbFriend {
    private String mId;
    private String mName;

    public FbFriend(String id, String name) {
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
