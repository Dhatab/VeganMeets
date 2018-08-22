package com.veganmeets.Matches;

/**
 * Created by User on 7/28/2018.
 */

public class RecyclerViewChatReference {
    private String userID, userName, userProfilePic,chatID;

    public RecyclerViewChatReference(String userID, String userName, String userProfilePic, String chatID) {
        this.userID = userID;
        this.userName = userName;
        this.userProfilePic = userProfilePic;
        this.chatID = chatID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
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
