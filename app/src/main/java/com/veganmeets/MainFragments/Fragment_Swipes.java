package com.veganmeets.MainFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.veganmeets.CardsArray.bottom_card_arrayAdapter;
import com.veganmeets.CardsArray.bottom_view_reference;
import com.veganmeets.CardsArray.card_arrayAdapter;
import com.veganmeets.CardsArray.cards_reference;
import com.veganmeets.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 8/5/2018.
 */

public class Fragment_Swipes extends Fragment implements View.OnClickListener {
    private card_arrayAdapter arrayAdapter;
    private String userSex,oppositeSex,currentUID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView.Adapter bottomAdapter;
    private View bottomSheet;
    private List<cards_reference> cardItems;
    private RecyclerView recyclerView;
    private View background;
    private ArrayList<bottom_view_reference> bottomCardItem = new ArrayList<bottom_view_reference>();
    private List<bottom_view_reference> getBio() {
        return bottomCardItem;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.frag_swipes, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        bottomSheet = v.findViewById(R.id.bottom_sheet);
        background = (View) v.findViewById(R.id.background);

        recyclerView = (RecyclerView) v.findViewById(R.id.bottomRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bottomAdapter = new bottom_card_arrayAdapter(getBio(), getContext());
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheet.setOnClickListener(this);
        background.setOnClickListener(this);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    v.findViewById(R.id.background).setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                v.findViewById(R.id.background).setVisibility(View.VISIBLE);
                v.findViewById(R.id.background).setAlpha(slideOffset);
            }
        });

        checkUserSex();
        onBackPressCloseBottom(v);


        cardItems = new ArrayList<>();
        arrayAdapter = new card_arrayAdapter(getContext(), R.layout.item, cardItems);
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) v.findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        recyclerView.setAdapter(bottomAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardItems.remove(0);
                bottomCardItem.remove(0);
                arrayAdapter.notifyDataSetChanged();
                bottomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject

                //This will save USER ID to Firebase DB on LEFT swipe
                cards_reference object = (cards_reference) dataObject;
                String userID = object.getUserID();
                databaseReference.child(userID).child("swipes").child("no").child(currentUID).setValue(true);
                Toast.makeText(getContext(),"Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //This will save USER ID to Firebase DB on Right swipe
                cards_reference object = (cards_reference) dataObject;
                String userID = object.getUserID();
                databaseReference.child(userID).child("swipes").child("yes").child(currentUID).setValue(true);
                usersMatched(userID);
                Toast.makeText(getContext(),"Right!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getContext(),"Click!",Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    private void usersMatched(String userID){
        DatabaseReference currentUserMatches = databaseReference.child(currentUID).child("swipes").child("yes").child(userID);
        currentUserMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getContext(),"New Match!",Toast.LENGTH_LONG).show();
                    String key = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();

                    databaseReference.child(dataSnapshot.getKey()).child("swipes").child("matches").child(currentUID).child("ChatID").setValue(key);
                    databaseReference.child(currentUID).child("swipes").child("matches").child(dataSnapshot.getKey()).child("ChatID").setValue(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = databaseReference.child(user.getUid()).child("myProfile");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("userSex").getValue() != null){
                        userSex = dataSnapshot.child("userSex").getValue().toString();
                        switch (userSex){
                            case "Male":
                                oppositeSex = "Female";
                                break;
                            case "Female":
                                oppositeSex = "Male";
                                break;
                        }
                        getOppositeSex();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSex(){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("myProfile").child("userSex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("swipes").child("no").hasChild(currentUID) && !dataSnapshot.child("swipes").child("yes").hasChild(currentUID) && dataSnapshot.child("myProfile").child("userSex").getValue().toString().equals(oppositeSex)) {
                        String profilePicURL = "default";
                        if (!dataSnapshot.child("myProfile").child("profilePicURL").getValue().equals("default")) {
                            profilePicURL = dataSnapshot.child("myProfile").child("profilePicURL").getValue().toString();
                        }
                        cards_reference item = new cards_reference(dataSnapshot.getKey(), dataSnapshot.child("myProfile").child("name").getValue().toString(), profilePicURL);
                        bottom_view_reference bottom_item = new bottom_view_reference(dataSnapshot.getKey(),dataSnapshot.child("myProfile").child("name").getValue().toString());
                        cardItems.add(item);
                        bottomCardItem.add(bottom_item);

                        arrayAdapter.notifyDataSetChanged();
                        bottomAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bottom_sheet :
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.background :
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            }
        }

        public void onBackPressCloseBottom (View v){
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        return true;
                    }
                    return false;
                }
            });
        }
}
