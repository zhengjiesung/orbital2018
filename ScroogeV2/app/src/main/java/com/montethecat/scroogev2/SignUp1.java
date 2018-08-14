package com.montethecat.scroogev2;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignUp1 extends AppCompatActivity {

    private Button continueButton;
    private EditText mEditTextFullName;
    private EditText mEditTextNumber;
    private RelativeLayout rellay1;
    private FirebaseAuth auth;

    protected DocumentReference mDocRef;
    protected Map<String, Object> dataToSave;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        continueButton = findViewById(R.id.buttonContinue);
        mEditTextFullName = findViewById(R.id.editTextFullName);
        mEditTextNumber = findViewById(R.id.editTextNumber);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        handler.postDelayed(runnable, 1000); //2000 is the timeout for the splash*

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = mEditTextFullName.getText().toString();
                String hpNumber = mEditTextNumber.getText().toString();

                // hide keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Check if name is empty
                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(SignUp1.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(hpNumber)) {
                    Toast.makeText(SignUp1.this, "Enter Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                dataToSave = new HashMap<String, Object>();
                dataToSave.put("name", fullName);
                dataToSave.put("hpNumber",hpNumber);


                // get unique id of user to store data of the user
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    finish();
                    startActivity(new Intent(SignUp1.this, Login.class));
                }
                String userID = user.getUid();
                dataToSave.put("userID",userID);
                mDocRef = FirebaseFirestore.getInstance().document("users/" + userID);

                mDocRef.set(dataToSave, SetOptions.merge());

                /*mDocRef.update(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Full Name", "Document has been saved!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Full Name", "Document was not saved!", e);
                    }
                });
                */
                String whereFrom=getIntent().getStringExtra("From");
                if (whereFrom.equals("Reset"))
                {
                    Intent intent = new Intent(SignUp1.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    // brings user to part 2 of signup
                    Intent intent = new Intent(SignUp1.this, GetPhoto.class);
                    startActivity(intent);
                }
            }
        });
    }
}
