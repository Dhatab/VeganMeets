package com.veganmeets.App;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.veganmeets.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private EditText settingsName, settingsAge;
    private Button settingsBack, settingsConfirm;
    private ImageView settingsProPic;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private String userID, name, age, profile_pic_url, userSex;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        populateUI();

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();

        settingsProPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        settingsConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });
        settingsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        name = map.get("name").toString();
                        settingsName.setText(name);
                    }
                    if(map.get("age") != null){
                        age = map.get("age").toString();
                        settingsAge.setText(age);
                    }
                    if(map.get("userSex") != null){
                        userSex = map.get("userSex").toString();
                    }
                    Glide.clear(settingsProPic);
                    if(map.get("profilePicURL")!= null){
                        profile_pic_url = map.get("profilePicURL").toString();
                        switch (profile_pic_url) {
                            case "default":
                                Glide.with(getApplication()).load(R.mipmap.ic_default_profile).into(settingsProPic);
                                break;
                            default:
                                Glide.with(getApplication()).load(profile_pic_url).into(settingsProPic);
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

    private void saveUserInfo() {
        name = settingsName.getText().toString();
        age = settingsAge.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("age", age);
        databaseReference.updateChildren(userInfo);
        if(mUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profilePicURL").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), mUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map userInfo = new HashMap();
                    userInfo.put("profilePicURL", downloadUrl.toString());
                    databaseReference.updateChildren(userInfo);

                    finish();
                    return;
                }
            });
        }else{
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            mUri = imageUri;
            settingsProPic.setImageURI(mUri);
        }
    }

    private void populateUI(){
        settingsName = (EditText) findViewById(R.id.settings_name);
        settingsAge = (EditText) findViewById(R.id.settings_age);
        settingsBack = (Button) findViewById(R.id.settings_back);
        settingsConfirm = (Button) findViewById(R.id.settings_confirm);
        settingsProPic = (ImageView) findViewById(R.id.settings_profile_pic);
    }
}
