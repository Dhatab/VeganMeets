package com.veganmeets.Matches;

/**
 * Created by User on 7/28/2018.
 */

public class MatchesReference {
    private String userID, userName, userProfilePic;

    public MatchesReference(String userID, String userName, String userProfilePic) {
        this.userID = userID;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
}
