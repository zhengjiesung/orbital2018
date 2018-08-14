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

import com.google.android.gms.common.oob.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;

public class Login extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText mEditTextEmail;
    private EditText mEditTextPw;
    private Button mButtonLogin;
    private Button mButtonSignup;
    private Button mButtonForgotPassword;


    private RelativeLayout rellay1;
    private RelativeLayout rellay2;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MetaData.userinfo=new Users();

        MetaData.recentTransactionItemList = new ArrayList<RecentTransactionItem>();
        MetaData.recentIncomeItemList = new ArrayList<RecentTransactionItem>();

        MetaData.recentTransactionItemListFull = new ArrayList<RecentTransactionItem>();
        MetaData.recentIncomeItemListFull = new ArrayList<RecentTransactionItem>();
        //used in create wallet
        //For user search
        MetaData.searchedUsersItemListFull=new ArrayList<Users>();
        MetaData.selectedUsersList=new ArrayList<Users>();
        MetaData.selectedUsersListForEdit=new ArrayList<Users>();
        MetaData.hereForMembersEdit=false;
        MetaData.walletForEditName=new Wallet();
        MetaData.profileImageDownloadURL= "";
        MetaData.myWallets=new ArrayList<Wallet>();

        //added this for wallet
        MetaData.inwallet=false;
        MetaData.walletName="";
        //For Editing
        MetaData.hereForEdit=false;
        MetaData.recentTransactionItemForEdit= new RecentTransactionItem();

        MetaData.catTotal= new HashMap<String, Double>();
        MetaData.catTotal1=new HashMap<String, Double>();
        MetaData.incTotal= new HashMap<String, Double>();
        MetaData.incTotal1= new HashMap<String, Double>();
        MetaData.date= "";

        MetaData.colorExpense= new ArrayList<Integer>();
        MetaData.colorExpense1=new ArrayList<Integer>();
        MetaData.colorIncome=new ArrayList<Integer>();
        MetaData.colorIncome1=new ArrayList<Integer>();

        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 1000); //2000 is the timeout for the splash

        // Firebase Auth Instance
        auth = FirebaseAuth.getInstance();

        // Initialise widgets
        mButtonLogin = findViewById(R.id.buttonLogin);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextPw = findViewById(R.id.editTextPassword);
        mButtonSignup = findViewById(R.id.buttonSignup);
        mButtonForgotPassword = findViewById(R.id.buttonForgotPassword);

        mButtonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("clicks", "CLICKED");

                // brings user to ForgetPassword page
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
            }
        });

        mButtonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // brings user to SignUp Page
                Intent intent = new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEditTextEmail.getText().toString().trim();
                String password = mEditTextPw.getText().toString().trim();

                // hide keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // Check if email is empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }


                // check if password is empty
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Set your own additional constraints

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // error occurred
                                    Toast.makeText(Login.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        // check whether user has come from the Forget Password Page
        String tracker = getIntent().getStringExtra("ForgetPassword");
        if (tracker == null){
            // nothing happens, user did not come from Forget Password Page

        } else {
            // check whether the tracker is from Forget Password Page
            if (tracker.equals("True")) {
                // notification(alert) appears on login page
                Alerts.alertBox("Reset password link sent", "Please check your email and reset your password before the link expires in 24 hours.", Login.this);

            }

        }

    }

}
