package com.veganmeets.MainFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.veganmeets.Chats.ChatObject;
import com.veganmeets.Matches.MatchesAdapter;
import com.veganmeets.Matches.MatchesReference;
import com.veganmeets.Matches.RecyclerViewChatAdapter;
import com.veganmeets.Matches.RecyclerViewChatReference;
import com.veganmeets.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/5/2018.
 */

public class Fragment_MatchChats extends Fragment {
    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.Adapter mMatchesAdapter, mMatchChatAdapter;
    private String currentUserID;
    private DatabaseReference mDatabaseChat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_match_chat, container, false);

    currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    LinearLayoutManager layoutManagerChat = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    mRecyclerViewChat = (RecyclerView) v.findViewById(R.id.recyclerViewChat);

    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerViewChat.setHasFixedSize(true);
    mRecyclerViewChat.setLayoutManager(layoutManagerChat);

    mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
    mMatchChatAdapter = new RecyclerViewChatAdapter(getDataSetChat(), getContext());



    return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        resultsMatches.clear();
        mResultsChats.clear();
        getUserMatchId();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //this method will get the user ID in the database that you matched with. It will run through the matches child and get all the user IDs
    private void getUserMatchId() {
        DatabaseReference matchDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("swipes").child("matches");
        matchDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        CheckChatID(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CheckChatID(final String chat) {
        DatabaseReference ChatDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("swipes").child("matches")
                .child(chat).child("ChatID");
        ChatDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String ChatID = dataSnapshot.getValue().toString();
                    ChatIDExist(ChatID, chat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ChatIDExist(final String chatID, final String oppUserID) {
        final DatabaseReference ChatDB = mDatabaseChat.child(chatID);
        ChatDB.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    getLastChatMessages(oppUserID,chatID);
                } else {
                    FetchMatchInfo(oppUserID);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getLastChatMessages(final String oppUser, String chatID) {
        final DatabaseReference LastChatDB = mDatabaseChat.child("lastMessage").child(chatID);
        LastChatDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String lastText = "";

                    if (dataSnapshot.child("lastText").getValue() != null){
                        lastText = dataSnapshot.child("lastText").getValue().toString();
                        FetchChatInfo(oppUser,lastText);
                    }

                } else{
                    FetchChatInfo(oppUser,"");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //This method will get the user you matched with in the database. It will get there information, such as name, age etc...
    private void FetchMatchInfo(String key) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String matched_userID = dataSnapshot.getKey();
                    String matches_userName = "";
                    String matches_userProPic = "";

                    if(dataSnapshot.child("name").getValue() != null){
                        matches_userName = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("profilePicURL").getValue() != null){
                        matches_userProPic = dataSnapshot.child("profilePicURL").getValue().toString();
                    }
                    MatchesReference object = new MatchesReference(matched_userID, matches_userName, matches_userProPic);
                    resultsMatches.add(object);
                    mRecyclerView.setAdapter(mMatchesAdapter);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchChatInfo(String key, final String lastText) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String matched_userID = dataSnapshot.getKey();
                    String matches_userName = "";
                    String matches_userProPic = "";
                    String match_CHATID = lastText;



                    if(dataSnapshot.child("name").getValue() != null){
                        matches_userName = dataSnapshot.child("name").getValue().toString();
                    }
                    if(dataSnapshot.child("profilePicURL").getValue() != null){
                        matches_userProPic = dataSnapshot.child("profilePicURL").getValue().toString();
                    }
                    RecyclerViewChatReference chat_obj = new RecyclerViewChatReference(matched_userID, matches_userName, matches_userProPic, match_CHATID);
                    mResultsChats.add(chat_obj);
                    mRecyclerViewChat.setAdapter(mMatchChatAdapter);
                    mMatchChatAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private ArrayList<MatchesReference> resultsMatches = new ArrayList<MatchesReference>();
    private ArrayList<RecyclerViewChatReference> mResultsChats = new ArrayList<RecyclerViewChatReference>();

    private List<MatchesReference> getDataSetMatches() {
        return resultsMatches;
    }
    private List<RecyclerViewChatReference> getDataSetChat() {
        return mResultsChats;
    }
}

