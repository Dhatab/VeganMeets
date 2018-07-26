package com.veganmeets;

/**
 * Created by User on 7/25/2018.
 */

public class cards_reference {
    private String userID,name;

    public cards_reference(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
