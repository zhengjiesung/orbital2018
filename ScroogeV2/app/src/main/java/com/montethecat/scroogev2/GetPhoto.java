package com.montethecat.scroogev2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class GetPhoto extends AppCompatActivity {
    String mode;
    //mode is set when button is clicked it tells
    // getPhoto() which Intent to carry out and also
    // onActivityResult() which requestcode
    ImageView imageView,imageView2;
    ImageButton camera, photo, restart,startNow;
    Intent intent = new Intent();
    TextView setPhotoText,editPhotoText;

    FirebaseStorage storage;
    FirebaseAuth auth;
    DocumentReference mDocRef;
    String downloadurl;
    Uri selectedImage;
    ProgressDialog progressDialog;
    Boolean selectedImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getphoto);
        camera = (ImageButton) findViewById(R.id.camera);
        photo = (ImageButton) findViewById(R.id.photo);
        restart = (ImageButton) findViewById(R.id.restart);
        startNow=(ImageButton) findViewById(R.id.startNow);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        editPhotoText=(TextView)findViewById(R.id.editPhotoText);
        editPhotoText.setText("Get Photo");
        setPhotoText=(TextView)findViewById(R.id.setPhotoText);
        setPhotoText.setText("Set");
        startNow.setVisibility(View.GONE);
        imageView = findViewById(R.id.imageView);
        selectedImg=false;

        if (getIntent().getStringExtra("getNewImage") == "get it") {
            Log.i("Entered", "here");
            CropImage.activity(selectedImage)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(GetPhoto.this);
        }


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = "camera";
                camera.animate().alpha(0).setDuration(200);
                camera.setImageDrawable(getResources().getDrawable(R.drawable.edit));
                editPhotoText.setText("Edit");
                camera.animate().alpha(1).setDuration(200);
                //startActivityForResult(getPickImageChooserIntent(), 200);
                CropImage.activity(selectedImage)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
                        .start(GetPhoto.this);

            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetPhoto.this, GetPhoto.class);
                intent.putExtra("getNewImage", "get it");
                startActivity(intent);

            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedImg==true) {
                    uploadFile(selectedImage);
                }else {

                    setPhotoText.setText("START");
                    startNow.setVisibility(View.VISIBLE);
                    photo.setVisibility(View.GONE);
                }




            }
        });



        startNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GetPhoto.this, OnBoard.class));
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mode == "camera") {

            if (/*requestCode == 0 &&*/ resultCode == RESULT_OK) {
                selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
                selectedImg=true;

                Log.i("Barnabas check here2", "entered here yo");
                //bitmap = (Bitmap) data.getExtras().get("data");

            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    Log.i("Barnabas check here", "entered here yo");
                    imageView.setImageURI(resultUri);
                    selectedImg=true;
                    selectedImage = resultUri;

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        }
    }
    private void uploadFile(Uri imageUri){
        progressDialog = new ProgressDialog(GetPhoto.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Setting Image...");
        progressDialog.setProgress(0);
        progressDialog.show();
        auth=FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(GetPhoto.this, Login.class));
        }
        String userID = user.getUid();
        mDocRef = FirebaseFirestore.getInstance().document("users/" + userID);
        final String fileName=System.currentTimeMillis()+"";
        final StorageReference storageReference=storage.getInstance().getReference().child("ProfileImages").child(fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(GetPhoto.this, "UploadDone", Toast.LENGTH_SHORT).show();

                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadurl=uri.toString();
                        Log.i("downloadurl",downloadurl);
                        Glide.with(getApplicationContext()).load(downloadurl).into(imageView);
                        progressDialog.cancel();
                        //Chsnge button
                        setPhotoText.setText("START");
                        startNow.setVisibility(View.VISIBLE);
                        photo.setVisibility(View.GONE);
                        HashMap<String, String>dataToSave = new HashMap<String, String>();
                        dataToSave.put("image", downloadurl);
                        mDocRef.set(dataToSave, SetOptions.merge());

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                //String downloadurl=storageReference.getDownloadUrl().toString();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // track the progress of our upload
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);

            }
        });
    }
}