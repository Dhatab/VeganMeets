package com.veganmeets.App;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {
    private CircleImageView myProPic1;
    private EditText myBioEditText, myJobEditText, myEducationEditText;
    private RadioGroup mySexRadioGroup, myDietRadioGroup;
    private Button mySaveButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String myUID,myBio, myJob,myEducation,mySex, myDiet, myProPicURL;
    private Uri myUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        populateUI();
        firebaseAuth = FirebaseAuth.getInstance();
        myUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(myUID).child("myProfile");

        getUserInfo();

        myProPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mySaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
                Toast.makeText(MyProfile.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("myBio") != null){
                        myBio = map.get("myBio").toString();
                        myBioEditText.setText(myBio);
                    }
                    if(map.get("myJob") != null){
                        myJob = map.get("myJob").toString();
                        myJobEditText.setText(myJob);
                    }
                    if(map.get("myEducation") != null){
                        myEducation = map.get("myEducation").toString();
                        myEducationEditText.setText(myEducation);
                    }
                    if(map.get("userSex") != null) {
                        mySex = map.get("userSex").toString();
                        final RadioButton radioButton1 = (RadioButton) findViewById(R.id.radio_sex1);
                        final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio_sex2);
                        if (radioButton1.getText().equals(mySex)) {
                            radioButton1.setChecked(true);
                        } else if (radioButton2.getText().equals(mySex)) {
                            radioButton2.setChecked(true);
                        }
                    }
                    if(map.get("myDiet") != null){
                        myDiet = map.get("myDiet").toString();
                        final RadioButton radioButton1 = (RadioButton) findViewById(R.id.radio_1);
                        final RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio_2);
                        final RadioButton radioButton3 = (RadioButton) findViewById(R.id.radio_3);
                        if(radioButton1.getText().equals(myDiet)){
                            radioButton1.setChecked(true);
                        } else if(radioButton2.getText().equals(myDiet)){
                            radioButton2.setChecked(true);
                        } else if (radioButton3.getText().equals(myDiet)){
                            radioButton3.setChecked(true);
                        }
                    }

                    Glide.clear(myProPic1);
                    if(map.get("profilePicURL")!= null){
                        myProPicURL = map.get("profilePicURL").toString();
                        switch (myProPicURL) {
                            case "default":
                                break;
                            default:
                                Glide.with(getApplication()).load(myProPicURL).into(myProPic1);
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
        myBio = myBioEditText.getText().toString();
        myJob = myJobEditText.getText().toString();
        myEducation = myEducationEditText.getText().toString();
        int userSexID = mySexRadioGroup.getCheckedRadioButtonId();
        int selectedID = myDietRadioGroup.getCheckedRadioButtonId();
        final RadioButton mySexRadioButton = (RadioButton) findViewById(userSexID);
        final RadioButton myRadioButton = (RadioButton) findViewById(selectedID);
        mySex = mySexRadioButton.getText().toString();
        myDiet = myRadioButton.getText().toString();
        if(myDiet == null){
            myDiet = "Vegan";
        }

        Map userInfo = new HashMap();
        userInfo.put("myBio", myBio);
        userInfo.put("myJob", myJob);
        userInfo.put("myEducation", myEducation);
        userInfo.put("userSex", mySex);
        userInfo.put("myDiet", myDiet);
        databaseReference.updateChildren(userInfo);

        if(myUri != null){
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profilePicURL").child(myUID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), myUri);
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

                    return;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            myUri = imageUri;
            myProPic1.setImageURI(myUri);
        }
    }

    private void populateUI(){
        myProPic1 = (CircleImageView) findViewById(R.id.myProPic1);
        myBioEditText = (EditText) findViewById(R.id.my_bio);
        myJobEditText = (EditText) findViewById(R.id.my_job);
        myEducationEditText = (EditText) findViewById(R.id.my_education);
        mySexRadioGroup = (RadioGroup) findViewById(R.id.my_sex);
        myDietRadioGroup = (RadioGroup) findViewById(R.id.my_diet);
        mySaveButton = (Button) findViewById(R.id.my_save_btn);
    }
}
