package com.veganmeets.App;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.veganmeets.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference, userRef;
    private String userID,distance,minAge,maxAge,mySexPref,mySex;
    private CrystalRangeSeekbar ageSeekBar;
    private CrystalSeekbar distanceSeekbar;
    private RadioButton radioButton1,radioButton2,radioButton3;
    private RadioGroup SexPrefRadioGroup;
    private Button savePreferences;
    private TextView ageTextView, distanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        populateUI();

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("myPrefs");

        getOppSex();



        // set listener
        ageSeekBar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                ageTextView.setText(String.valueOf("Between " + minValue + " and " + maxValue + " years old."));
            }
        });

        distanceSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                final String mile;
                if (String.valueOf(value).equals("1")){
                    mile = "mile";
                }else {
                    mile = "miles";
                }
                distanceTextView.setText(String.valueOf("Max Distance: " + value + " " + mile));
            }
        });

        // set final value listener
        ageSeekBar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                minAge = String.valueOf(minValue);
                maxAge = String.valueOf(maxValue);
            }
        });

        distanceSeekbar.setOnSeekbarFinalValueListener(new OnSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number value) {
                distance = String.valueOf(value);
            }
        });

        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserPrefs();
                Toast.makeText(SettingsActivity.this, "Settings Saved!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getOppSex() {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("myProfile");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("userSex").getValue() != null){
                        mySex = dataSnapshot.child("userSex").getValue().toString();
                        switch (mySex){
                            case "Male":
                                radioButton2.setChecked(true);
                                break;
                            case "Female":
                                radioButton1.setChecked(true);
                                break;
                        }
                        getUserPrefs();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUserPrefs() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("sexPref") != null) {
                        mySexPref = map.get("sexPref").toString();
                        if (radioButton1.getText().equals(mySexPref)) {
                            radioButton1.setChecked(true);
                        } else if (radioButton2.getText().equals(mySexPref)) {
                            radioButton2.setChecked(true);
                        }else if (radioButton3.getText().equals(mySexPref)) {
                            radioButton3.setChecked(true);
                        }
                    }

                    if(map.get("agePrefMin") != null && map.get("agePrefMax") != null){
                        minAge = map.get("agePrefMin").toString();
                        maxAge = map.get("agePrefMax").toString();
                        ageSeekBar.setMinStartValue(Float.parseFloat(minAge)).apply();
                        ageSeekBar.setMaxStartValue(Float.parseFloat(maxAge)).apply();
                    }

                    if(map.get("distancePref") != null){
                        distance = map.get("distancePref").toString();
                        distanceSeekbar.setMinStartValue(Integer.parseInt(distance)).apply();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void saveUserPrefs() {
        int sexPref = SexPrefRadioGroup.getCheckedRadioButtonId();
        final RadioButton mySexPrefRadioButton = (RadioButton) findViewById(sexPref);
        if (mySexPrefRadioButton != null) {
            mySexPref = mySexPrefRadioButton.getText().toString();
        }
        Map userInfo = new HashMap();
        userInfo.put("sexPref", mySexPref);
        userInfo.put("agePrefMin", minAge);
        userInfo.put("agePrefMax", maxAge);
        userInfo.put("distancePref", distance);
        databaseReference.updateChildren(userInfo);
    }


    private void populateUI(){
        SexPrefRadioGroup = (RadioGroup) findViewById(R.id.my_sex_pref);
        ageSeekBar = (CrystalRangeSeekbar) findViewById(R.id.ageSeekbar);
        distanceSeekbar = (CrystalSeekbar) findViewById(R.id.distanceSeekbar);
        ageTextView = (TextView) findViewById(R.id.age_pref);
        distanceTextView = (TextView) findViewById(R.id.distance_pref);
        savePreferences = (Button) findViewById(R.id.settings_save_btn);
        radioButton1 = (RadioButton) findViewById(R.id.sex_pref1);
        radioButton2 = (RadioButton) findViewById(R.id.sex_pref2);
        radioButton3 = (RadioButton) findViewById(R.id.sex_pref3);
    }
}
