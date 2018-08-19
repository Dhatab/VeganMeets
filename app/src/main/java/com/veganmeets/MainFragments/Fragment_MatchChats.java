package com.veganmeets.MainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.veganmeets.Matches.MatchesAdapter;
import com.veganmeets.Matches.MatchesReference;
import com.veganmeets.Matches.RecyclerViewChatAdapter;
import com.veganmeets.Matches.RecyclerViewChatReference;
import com.veganmeets.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/5/2018.
 */

public class Fragment_MatchChats extends Fragment {
    private RecyclerView mRecyclerView, mRecyclerViewChat;
    private RecyclerView.Adapter mMatchesAdapter, mChatAdapter;
    private String currentUserID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_match_chat, container, false);


    currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
    LinearLayoutManager layoutManagerChat = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
    mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
    mRecyclerViewChat = (RecyclerView) v.findViewById(R.id.recyclerViewChat);

    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLayoutManager(layoutManager);
    mRecyclerViewChat.setHasFixedSize(true);
    mRecyclerViewChat.setLayoutManager(layoutManagerChat);

    mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
    mChatAdapter = new RecyclerViewChatAdapter(getDataSetChat(), getContext());

    //mRecyclerView.setAdapter(mMatchesAdapter);
    //mRecyclerViewChat.setAdapter(mMatchesAdapter);

    getUserMatchId();
    return v;
    }

    //this method will get the user ID in the database that you matched with. It will run through the matches child and get all the user IDs
    private void getUserMatchId() {
        DatabaseReference matchDB = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("swipes").child("matches");
        matchDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInfo(match.getKey());
                        CheckChatID(match.child("ChatID").getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void FetchChatID(String key){
        DatabaseReference userChatID = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("swipes").child("matches")
                .child(key).child("ChatID");
        userChatID.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String chat_id = dataSnapshot.getKey();
                CheckChatID(chat_id);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void CheckChatID(String chat) {
        DatabaseReference ChatDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(chat);
        ChatDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mRecyclerViewChat.setAdapter(mChatAdapter);
                    mChatAdapter.notifyDataSetChanged();
                } else{
                    mRecyclerView.setAdapter(mMatchesAdapter);
                    mMatchesAdapter.notifyDataSetChanged();
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
                    RecyclerViewChatReference chat_obj = new RecyclerViewChatReference(matched_userID, matches_userName, matches_userProPic);
                    resultsMatches.add(object);
                    mMatchesAdapter.notifyDataSetChanged();

                    resultsChats.add(chat_obj);
                    mChatAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MatchesReference> resultsMatches = new ArrayList<MatchesReference>();
    private ArrayList<RecyclerViewChatReference> resultsChats = new ArrayList<RecyclerViewChatReference>();

    private List<MatchesReference> getDataSetMatches() {
        return resultsMatches;
    }
    private List<RecyclerViewChatReference> getDataSetChat() {
        return resultsChats;
    }
}
