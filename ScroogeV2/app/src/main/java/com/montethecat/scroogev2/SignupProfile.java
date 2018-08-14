package com.montethecat.scroogev2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupProfile extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private EditText editTextName;
    private Button buttonSaveInformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_profile);
        //getting user information
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser user=firebaseAuth.getCurrentUser();

        //
        databaseReference= FirebaseDatabase.getInstance().getReference();
        editTextName=(EditText)findViewById(R.id.editTextName);
        buttonSaveInformation=(Button)findViewById(R.id.buttonSaveInformation);


        buttonSaveInformation.setOnClickListener(this);
    }

    private void saveUserInformation(){
        String name=editTextName.getText().toString().trim();

        Users userInformation=new Users();
        // get unique id of user to store data of the user
        FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation);
        Toast.makeText(this, "Information Saved", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View view) {
    if (view==buttonSaveInformation){
        saveUserInformation();
        startActivity(new Intent(SignupProfile.this, GetPhoto.class));
        finish();
    }
    }
}
