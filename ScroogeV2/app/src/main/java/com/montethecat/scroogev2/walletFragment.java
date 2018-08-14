package com.montethecat.scroogev2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class walletFragment extends Fragment {

    TextView home;
    String uid;
    DocumentSnapshot mLastQuiredDocument;
    RecyclerView listOfWallet;
    public static WalletListAdapter walletListAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragmen_wallet, null);
        getActivity().setTitle("Wallet");
        MetaData.myWallets=new ArrayList<>();
        getWalletAvailable();
        home=(TextView) view.findViewById(R.id.home);
        listOfWallet = (RecyclerView) view.findViewById(R.id.listOfWallet);
        listOfWallet.setHasFixedSize(true);
        listOfWallet.setLayoutManager(new LinearLayoutManager(getActivity()));
        walletListAdapter=new WalletListAdapter(getContext(),MetaData.myWallets);
        listOfWallet.setAdapter(walletListAdapter);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Intent intent=new Intent(getActivity(),MainActivity.class);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent.putExtra("inwallet",false);
                intent.putExtra("walletName", "Home");
                startActivity(intent);
            }
        });

    }

    public void getWalletAvailable() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getActivity(), Login.class));
        } else {
            uid = user.getUid();
            CollectionReference colRef = FirebaseFirestore.getInstance().collection("users/" + uid + "/wallet");
            Query query;
            if( mLastQuiredDocument==null) {
                query=colRef.orderBy("timeStamp",Query.Direction.DESCENDING);
            }else {
                query=colRef.orderBy("timeStamp",Query.Direction.DESCENDING).startAfter(mLastQuiredDocument);
            }

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //retrieving a list of documents by looping through the list by calling get results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Wallet wallets = document.toObject(Wallet.class);

                            MetaData.myWallets.add(wallets);

                        }if (task.getResult().size() != 0) {
                            mLastQuiredDocument = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);
                        }
                    }
                    walletListAdapter.notifyDataSetChanged();
                    listOfWallet.setAdapter(walletListAdapter);

                }
            });

        }
    }
}
