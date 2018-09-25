package com.veganmeets.MainFragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.veganmeets.App.MyProfile;
import com.veganmeets.App.SettingsActivity;
import com.veganmeets.R;
import com.veganmeets.SignUp.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 8/5/2018.
 */

public class Fragment_Account extends Fragment {

    private CardView edit_profile_button, edit_profile_settings;
    private Button logout;
    private CircleImageView settingsProPic;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView settingsAge, settingsName;

    private String userID, name, age, profile_pic_url, userSex;
    private Uri mUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_account, container, false);
        populateUI(v);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("myProfile");
        getUserInfo();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(),LoginActivity.class));
            }
        });

        edit_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MyProfile.class));
            }
        });

        edit_profile_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),SettingsActivity.class));
            }
        });

        return v;
    }

    private void getUserInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        settingsName.setText(name);
                    }
                    if (map.get("age") != null) {
                        age = map.get("age").toString();
                        settingsAge.setText(age);
                    }
                    if (map.get("userSex") != null) {
                        userSex = map.get("userSex").toString();
                    }
                    Glide.clear(settingsProPic);
                    if (map.get("profilePicURL") != null) {
                        profile_pic_url = map.get("profilePicURL").toString();
                        switch (profile_pic_url) {
                            case "default":
                                break;
                            default:
                                Glide.with(getContext()).load(profile_pic_url).into(settingsProPic);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            mUri = imageUri;
            settingsProPic.setImageURI(mUri);
        }
    }

    private void populateUI(View v) {
        edit_profile_button = (CardView) v.findViewById(R.id.setting_edit_profile);
        edit_profile_settings = (CardView) v.findViewById(R.id.setting_edit_settings);
        settingsAge = (TextView) v.findViewById(R.id.setting_user_age);
        settingsName = (TextView) v.findViewById(R.id.setting_user_name);
        settingsProPic = (CircleImageView) v.findViewById(R.id.main_profile_image);
        logout = (Button) v.findViewById(R.id.logout_btn);
    }
}
