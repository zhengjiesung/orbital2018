package com.montethecat.scroogev2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button resetButton;
    private EditText editTextEmail;

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
        setContentView(R.layout.activity_forgot_password);

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        handler.postDelayed(runnable, 1000); //2000 is the timeout for the splash


        resetButton = findViewById(R.id.resetButton);
        editTextEmail = findViewById(R.id.editTextEmail);

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(ForgetPassword.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // hide keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // firebase authentication
                FirebaseAuth.getInstance().sendPasswordResetEmail(editTextEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Forget Password", "Email sent.");

                                    // brings user to login page
                                    Intent intent = new Intent(ForgetPassword.this, Login.class);
                                    intent.putExtra("ForgetPassword", "True"); // tracker validation
                                    startActivity(intent);

                                } else {
                                    Toast toast = Toast.makeText(ForgetPassword.this, "Unable to find user with email " + email, Toast.LENGTH_SHORT);
                                    TextView v = toast.getView().findViewById(android.R.id.message);
                                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                    if( v != null) v.setGravity(Gravity.CENTER);
                                    toast.show();
                                }
                            }
                        });

            }
        });




    }

}
