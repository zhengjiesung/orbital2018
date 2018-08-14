package com.montethecat.scroogev2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfilePageFragment extends Fragment {

    TextView textViewName;
    TextView textViewEmail;
    TextView textViewWallet;
    TextView textViewNumber;
    TextView textViewID;
    ImageView emailView;
    ImageView phoneView;
    ImageView walletView;
    ImageView imageViewInProfilePage;
    DocumentReference docRef;
    CollectionReference docRef2;
    FirebaseAuth auth;
    Button logoutButt;
    public static TextView cashAmt;
    public static TextView cardAmt;
    public static TextView bankAmt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Profile Page");

        return inflater.inflate(R.layout.activity_profile_page, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewName = getView().findViewById(R.id.textViewName);
        textViewEmail = getView().findViewById(R.id.textViewEmail);
        textViewWallet = getView().findViewById(R.id.textViewWallet);
        textViewNumber = getView().findViewById(R.id.textViewNumber);
        textViewID = getView().findViewById(R.id.textViewID);
        imageViewInProfilePage = getView().findViewById(R.id.imageViewProfilePage);
        logoutButt = getView().findViewById(R.id.logoutButt);
        cashAmt = getView().findViewById(R.id.cashAmt);
        cardAmt = getView().findViewById(R.id.cardAmt);
        bankAmt = getView().findViewById(R.id.bankAmt);
        emailView = getView().findViewById(R.id.emailView);
        phoneView = getView().findViewById(R.id.phoneView);
        walletView = getView().findViewById(R.id.walletView);

        emailView.setColorFilter(Color.rgb(1,170,113));
        phoneView.setColorFilter(Color.rgb(1,170,113));
        walletView.setColorFilter(Color.rgb(1,170,113));

        MainActivity.updateProfileBalance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            getActivity().finish();
            getActivity().startActivity(new Intent(getContext(), Login.class));
        }
        if(MetaData.profileImageDownloadURL!=null) {
            Glide.with(getView().getContext()).load(MetaData.profileImageDownloadURL).into(imageViewInProfilePage);
        }else {
            Glide.with(getView().getContext()).load(R.drawable.basicgroot).into(imageViewInProfilePage);
        }

        String uid = user.getUid();
        docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    String name = document.getString("name");
                    String email = document.getString("email");
                    String hpNumber = document.getString("hpNumber");
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    textViewName.setText(name);
                    textViewEmail.setText(email);
                    textViewNumber.setText(hpNumber);
                    textViewID.setText(userID.substring(0,8));
                    //textViewName.setTextColor(Color.rgb(1,170,113));
                    //textViewEmail.setTextColor(Color.rgb(1,170,113));
                    //textViewNumber.setTextColor(Color.rgb(1,170,113));


                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data");
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
        docRef2 = FirebaseFirestore.getInstance().collection("users/" + uid + "/wallet");
        Query query = docRef2.orderBy("walletName");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                    }
                    textViewWallet.setText(Integer.toString(count));
                    //textViewWallet.setTextColor(Color.rgb(1,170,113));
                } else {
                    Log.i( "Error getting document", ":(");
                }
            }
        });

        view.findViewById(R.id.textViewName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),SignUp1.class);
                intent.putExtra("From","Reset");
                startActivity(intent);



            }
        });


        view.findViewById(R.id.imageViewProfilePage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),GetPhoto.class);
                //intent.putExtra("From","Reset");
                startActivity(intent);

            }
        });

        view.findViewById(R.id.logoutButt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getActivity(),Login.class);
                //intent.putExtra("From","Reset");
                startActivity(intent);

            }
        });

    }

}
