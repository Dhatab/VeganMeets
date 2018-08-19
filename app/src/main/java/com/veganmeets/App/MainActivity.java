package com.veganmeets.App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import com.veganmeets.CardsArray.card_arrayAdapter;
import com.veganmeets.CardsArray.cards_reference;
import com.veganmeets.Matches.MatchesActivity;
import com.veganmeets.R;
import com.veganmeets.SignUp.LoginActivity;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private cards_reference cards_data[];
    private card_arrayAdapter arrayAdapter;

    private String userSex,oppositeSex,currentUID;
    private Button signout;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    ListView listView;
    List<cards_reference> cardItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        signout = (Button) findViewById(R.id.signout);

        checkUserSex();

        cardItems = new ArrayList<>();
        arrayAdapter = new card_arrayAdapter(this, R.layout.item, cardItems);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
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
                Toast.makeText(getApplicationContext(),"Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                //This will save USER ID to Firebase DB on Right swipe
                cards_reference object = (cards_reference) dataObject;
                String userID = object.getUserID();
                databaseReference.child(userID).child("swipes").child("yes").child(currentUID).setValue(true);
                usersMatched(userID);
                Toast.makeText(getApplicationContext(),"Right!",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"Click!",Toast.LENGTH_SHORT).show();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
    }
});
    }

    private void usersMatched(String userID){
        DatabaseReference currentUserMatches = databaseReference.child(currentUID).child("swipes").child("yes").child(userID);
        currentUserMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this,"New Match!",Toast.LENGTH_LONG).show();
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
        DatabaseReference userDB = databaseReference.child(user.getUid());
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
                if (dataSnapshot.child("userSex").getValue() != null) {
                    if (dataSnapshot.exists() && !dataSnapshot.child("swipes").child("no").hasChild(currentUID) && !dataSnapshot.child("swipes").child("yes").hasChild(currentUID) && dataSnapshot.child("userSex").getValue().toString().equals(oppositeSex)) {
                        String profilePicURL = "default";
                        if (!dataSnapshot.child("profilePicURL").getValue().equals("default")) {
                            profilePicURL = dataSnapshot.child("profilePicURL").getValue().toString();
                        }
                        cards_reference item = new cards_reference(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profilePicURL);
                        cardItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
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

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void goToMatches(View view) {
        startActivity(new Intent(MainActivity.this,MatchesActivity.class));
    }
}
