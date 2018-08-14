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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private FirebaseAuth auth;
    private Button mBtnSignup;
    protected DocumentReference mDocRef;
    protected Map<String, Object> dataToSave;


    private RelativeLayout rellay1;

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
        setContentView(R.layout.activity_signup);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        handler.postDelayed(runnable, 1000); //2000 is the timeout for the splash


        // Initialise widgets
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPw = findViewById(R.id.editTextPassword);
        mBtnSignup = findViewById(R.id.buttonSignup);


        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();


        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                // hide keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Signup.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Signup.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if password is alphanumeric
                if(!password.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z])[a-zA-Z0-9]+$")){
                    Toast.makeText(Signup.this, "Password must contain one upper and lower case letter and one number", Toast.LENGTH_LONG).show();
                    return;
                }

                // Set your own additional constraints

                // Create a new user
                auth.createUserWithEmailAndPassword(email, password).
                        addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {

                                    // get unique id of user to store data of the user
                                    FirebaseUser user = auth.getCurrentUser();
                                    String userID = user.getUid();

                                    mDocRef = FirebaseFirestore.getInstance().document("users/" + userID);

                                    dataToSave = new HashMap<String, Object>();
                                    dataToSave.put("email", email);
                                    mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Email", "Document has been saved!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Email", "Document was not saved!", e);
                                        }
                                    });

                                    // brings new user to onboarding process
                                    Intent intent=new Intent(Signup.this, SignUp1.class);
                                    intent.putExtra("From","not Reset");
                                    startActivity(intent);

                                    finish();
                                }
                            }
                        });
            }
        });



    }
}
