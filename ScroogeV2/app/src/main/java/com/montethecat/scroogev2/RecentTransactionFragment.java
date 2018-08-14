package com.montethecat.scroogev2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecentTransactionFragment extends Fragment{
    View view;
    private RecyclerView recyclerView;
    public static RecentTransactionAdapter recentTransactionAdapter;
    DocumentReference docRef;
    private static FirebaseAuth auth;
    private static CollectionReference collectionRef;

    TextView recentTransactionTitle2;

   /* public RecentTransactionFragment(){

    }*/

    public static List<RecentTransactionItem> recentTransactionItemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recenttransactions, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRecentTransaction);
        recentTransactionAdapter = new RecentTransactionAdapter(getContext(), MetaData.recentTransactionItemListFull);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recentTransactionTitle2=(TextView)view.findViewById(R.id.recentTransactionTitle2);
        recentTransactionTitle2.setText("Last 5 record "+MetaData.setMonth(MainActivity.monthCurrently)+" "+MainActivity.yearCurrently);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(recentTransactionAdapter);
        //start here
        /*auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            getActivity().finish();
            startActivity(new Intent(getContext(), Login.class));
        }
        String userID = user.getUid();
        docRef = FirebaseFirestore.getInstance().document("users/" + userID + "/transactionData/data");
       *//* docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){


                    }

                }
            }
        });*//*
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Document snapshot in an object that represents your document
                //it contain id,meta data, underlying data
                if (documentSnapshot.exists()) {
                    //Using documentSnapshot.getString to get Quote output
                    //String quoteText=documentSnapshot.getString(QUOTE);
                    //String authorText=documentSnapshot.getString(NAME);
                    // mQuoteTextView.setText("\""+quoteText+"\"--"+authorText);


                    //Map<String, Object> myData=documentSnapshot.getData();
                    //can get data in a string ObjectMap
                    //Using an Object to getter to get Quote output
                    //https://www.youtube.com/watch?v=jJnm3YKfAUI
                    Map<String, Object> temp = documentSnapshot.getData();
                    //transaction=documentSnapshot.toObject(Transaction.class);
                    //Gets data from data base and attemps to create an object of the type that you specify
                    //it will get what ever values you get from fire store and try to set those as fields
                    //Log.i("Transaction",transaction.getAmount());

                    Map<String, String> transaction = (Map<String, String>) temp.get("Transaction 1");
                    //String transaction1=documentSnapshot.getString()
                    Log.i("Transaction", transaction.get("amount"));

                }
            }
        });*/
        //ends here

        //recyclerView.setHasFixedSize(true);


        /*plannedPaymentItemList.add(
                new PlannedPaymentItem(
                        "Phone Bill",
                        "Cash",
                        "-40.00",
                        "Today",
                        R.drawable.ic_phone_android_black_24dp
                ));*/


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        recentTransactionItemList = new ArrayList<>();
        recentTransactionItemList.add(
                new RecentTransactionItem(
                        "Phone",
                        "Cash",
                        "-40.00",
                        "Today",
                        "HEBUBPUBDOBPOB(",
                        "Haha",
                        "Expense",
                        R.drawable.ic_phone_android_black_24dp,
                        "iPhone7"
                ));



    }







}
