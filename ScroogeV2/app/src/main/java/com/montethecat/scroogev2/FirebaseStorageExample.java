package com.montethecat.scroogev2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

public class FirebaseStorageExample extends AppCompatActivity {


    FirebaseStorage storage;
    FirebaseFirestore database;
    private Button uploadButt, chooseButt;
    Uri csvUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_storage_example);

        uploadButt = (Button) findViewById(R.id.uploadButt);
        chooseButt = (Button) findViewById(R.id.chooseBut);

        storage = FirebaseStorage.getInstance();
        database = FirebaseFirestore.getInstance();

        chooseButt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(FirebaseStorageExample.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        selectCSV();
                }else{
                    ActivityCompat.requestPermissions(FirebaseStorageExample.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }

            }
        });

        uploadButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(csvUri!=null){
                    uploadFile(csvUri);
                }else{
                    Toast.makeText(FirebaseStorageExample.this, "Select a file", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void uploadFile(Uri csvUri){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();


        String fileName = System.currentTimeMillis() + "";
        final StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads").child(fileName).putFile(csvUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String url = storageReference.getDownloadUrl().toString();
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("CSV",url);
                DocumentReference doc = database.collection("users/" + userID + "/csvData").document();
                doc.set(temp).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(FirebaseStorageExample.this, "File Successfully uploaded", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(FirebaseStorageExample.this, "File not uplaoded", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FirebaseStorageExample.this, "File not uplaoded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectCSV();
        }else{
            Toast.makeText(FirebaseStorageExample.this, "please provide permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCSV(){
        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==86 && resultCode == RESULT_OK && data != null){
            csvUri = data.getData();
        }else{
            Toast.makeText(FirebaseStorageExample.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }
}
