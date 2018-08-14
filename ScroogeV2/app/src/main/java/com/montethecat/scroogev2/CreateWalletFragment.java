package com.montethecat.scroogev2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Barnabas Move this
public class CreateWalletFragment extends Fragment {
    private EditText mSearchField;
    private ImageButton mSearchBtn;
    private ImageButton buttonBlocker;
    private RecyclerView mResultList;
    private RecyclerView mSelectedResultList;
    public static EditText createWalletName;
    public static Button createWalletButtonConfirm,createWalletButtonEdit;
    public static TextView heading_label;
    /*DocumentSnapshot mLastQuiredDocument;*/
    public static SearchUserAdapter searchUserAdapter;
    public static SearchedUserSelectedAdapter searchedUserSelectedAdapter;

    private IMainActivity mIMainActivity;

    /*Boolean beenHere=false;*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //createWallet is a booloean that is created to see if user has selected participant

        mIMainActivity = (IMainActivity) getContext();

        View view=inflater.inflate(R.layout.fragment_search_users, null);
        //This two view only appear when array list selectedUsers is not equals null
        createWalletButtonConfirm=(Button)view.findViewById(R.id.createWalletButtonConfirm);
        createWalletButtonEdit=(Button) view.findViewById(R.id.createWalletButtonEdit);
        createWalletName=(EditText)view.findViewById(R.id.createWalletName);
        createWalletButtonConfirm.setVisibility(View.GONE);
        createWalletButtonEdit.setVisibility(View.GONE);
        heading_label=(TextView)view.findViewById(R.id.heading_label);
        //Array list to hold the Users that are generated from search and selected in the order
        MetaData.searchedUsersItemListFull=new ArrayList<Users>();
        MetaData.selectedUsersList=new ArrayList<Users>();
        mSearchField = (EditText) view.findViewById(R.id.search_field);
        mSearchBtn = (ImageButton) view.findViewById(R.id.search_btn);
        buttonBlocker=(ImageButton) view.findViewById(R.id.searchButtonBlocker);
        buttonBlocker.setVisibility(View.GONE);
        mResultList = (RecyclerView) view.findViewById(R.id.result_list);
        mResultList.setHasFixedSize(true);
        mResultList.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchUserAdapter=new SearchUserAdapter(MetaData.searchedUsersItemListFull,getContext());
        mResultList.setAdapter(searchUserAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        mSelectedResultList = (RecyclerView) view.findViewById(R.id.result_list_selected);
        mSelectedResultList.setLayoutManager(layoutManager);
        searchedUserSelectedAdapter=new SearchedUserSelectedAdapter(MetaData.selectedUsersList,getContext());
        mSelectedResultList.setAdapter(searchedUserSelectedAdapter);
        /*mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mLastQuiredDocument=null;
                beenHere=false;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String searchText = mSearchField.getText().toString();
                userSearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mLastQuiredDocument=null;
                beenHere=false;
                String searchText = mSearchField.getText().toString();
                userSearch(searchText);

            }
        });*/
        getActivity().setTitle("Create Wallet");
        Log.i("Entered this if NAY ",MetaData.hereForMembersEdit.toString() );
        if(MetaData.hereForMembersEdit==true){
            Log.i("Entered this if ",MetaData.hereForMembersEdit.toString() );
            for(int n=0;n<MetaData.selectedUsersListForEdit.size();n++) {
                MetaData.selectedUsersList.add(MetaData.selectedUsersListForEdit.get(n));
            }
            getActivity().setTitle("Edit Wallet");
            searchedUserSelectedAdapter.notifyDataSetChanged();
            mSelectedResultList.setAdapter(searchedUserSelectedAdapter);

        }
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchBtn.setVisibility(View.GONE);
                buttonBlocker.setVisibility(View.VISIBLE);
                InputMethodManager inputManager =
                        (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                String searchText = mSearchField.getText().toString();
                userSearch(searchText);

            }
        });

        createWalletButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for user removed
                for(int n=0;n<MetaData.selectedUsersListForEdit.size();n++){
                    for(int k=0;k<MetaData.selectedUsersList.size();k++){
                        if(MetaData.selectedUsersList.get(k)!=null&&MetaData.selectedUsersListForEdit!=null) {
                            if (MetaData.selectedUsersListForEdit.get(n).getUserID().equals(MetaData.selectedUsersList.get(k).getUserID())) {
                                MetaData.selectedUsersListForEdit.set(n, null);
                                MetaData.selectedUsersList.set(k, null);
                                break;
                            }
                        }
                    }
                }
                //removed user
                for (int n=0;n<MetaData.selectedUsersListForEdit.size();n++){
                    if(MetaData.selectedUsersListForEdit.get(n)!=null) {

                        DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("users/" + MetaData.selectedUsersListForEdit.get(n).getUserID() + "/wallet").document(MetaData.walletForEditName.getWalletID());

                        mDocRef.delete();
                        DocumentReference mDocRef2 = FirebaseFirestore.getInstance().collection("group/" + MetaData.walletForEditName.getWalletID() + "/members").document(MetaData.selectedUsersListForEdit.get(n).getUserID());

                        mDocRef2.delete();
                    }
                }
                //added user
                for (int n=0;n<MetaData.selectedUsersList.size();n++){
                    if(MetaData.selectedUsersList.get(n)!=null){
                        DocumentReference mDocRef2 =  FirebaseFirestore.getInstance().collection("group").document(MetaData.walletForEditName.getWalletID()).collection("members").document(MetaData.selectedUsersList.get(n).getUserID());
                        mDocRef2.set(MetaData.selectedUsersList.get(n));


                        DocumentReference mDocRef3 = FirebaseFirestore.getInstance().collection("users").document(MetaData.selectedUsersList.get(n).getUserID()).collection("wallet").document(MetaData.walletForEditName.getWalletID());
                        //added this to generate time stamp
                        Wallet wallet=new Wallet();
                        wallet.setWalletID(MetaData.walletForEditName.getWalletID());
                        wallet.setWalletName(MetaData.walletForEditName.getWalletName());
                        wallet.setWalletCreatorID(MetaData.walletForEditName.getWalletCreatorID());
                        //wallet.setMemberKey("member"+(j++));
                        wallet.setAcceptRequest(false);
                        wallet.setWalletCreator(MetaData.walletForEditName.getWalletCreator());
                        //3.
                        mDocRef3.set(wallet);

                    }

                    }
                    mIMainActivity.createdWallet();
                }
            });
        createWalletButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String uid;
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(getActivity(), Login.class));
                }else {
                    uid = user.getUid();
                    if(createWalletName.getText().toString().equals("")){
                        createWalletName.requestFocus();

                    }else {
                        //1.mdocRef - to Create group document
                        //2.mdocRef2- create individual member document(other than Creator) group/walletID/members list;
                        //3.mdocRef3- set wallets in to each individual members(other than Creator) place under users/uid/wallet/walletID
                        //4.mdocRef4- set wallets in to master members(Creator) place under users/uid/wallet/walletID
                        //5.mdocRef5- create individual Creator document( Creator) group/walletID/members list;
                        String time=String.valueOf(System.currentTimeMillis());
                        final String walletID=uid + createWalletName.getText().toString()+time;
                        final DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("group").document(walletID);
                        Users walletData=new Users();
                        walletData=MetaData.selectedUsersList.get(MetaData.selectedUsersList.size()-1);
                        HashMap<String,String>dataToSave=new HashMap<>();
                        dataToSave.put("Group name",createWalletName.getText().toString());
                        dataToSave.put("Group userID",walletID);
                        dataToSave.put("walletCreator",MetaData.userinfo.getName());
                        dataToSave.put("walletCreatorID",uid);

                        //1.
                        mDocRef.set(dataToSave, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                for(int n=0;n<MetaData.selectedUsersList.size();n++) {
                                    DocumentReference mDocRef2 =  FirebaseFirestore.getInstance().collection("group").document(walletID).collection("members").document(MetaData.selectedUsersList.get(n).getUserID());
                                    //HashMap<String, String> membersData =  new HashMap<>();
                                    //2.
                                    mDocRef2.set(MetaData.selectedUsersList.get(n));
                                    //membersData.set("member"+n, MetaData.selectedUsersList.get(n).getUserID());
                                }
                                //membersData.put("master",uid);
                               /* mDocRef2.set(membersData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {*/
                                        //int j=0;
                                        for (int n = 0; n < MetaData.selectedUsersList.size(); n++) {
                                            //final Users lastusers=MetaData.selectedUsersList.get(MetaData.selectedUsersList.size()-1);

                                            DocumentReference mDocRef3 = FirebaseFirestore.getInstance().collection("users").document(MetaData.selectedUsersList.get(n).getUserID()).collection("wallet").document(walletID);

                                            /*HashMap<String, String> walletstoSave = new HashMap<>();
                                            walletstoSave.put("walletID", walletID);
                                            walletstoSave.put("walletName", createWalletName.getText().toString());*/

                                            //added this to generate time stamp
                                            Wallet wallet=new Wallet();
                                            wallet.setWalletID(walletID);
                                            wallet.setWalletName(createWalletName.getText().toString());
                                            wallet.setWalletCreatorID(uid);
                                            //wallet.setMemberKey("member"+(j++));
                                            wallet.setAcceptRequest(false);
                                            wallet.setWalletCreator(MetaData.userinfo.getName());
                                            //3.
                                            mDocRef3.set(wallet);
                                        }

                                        final DocumentReference mDocRef4 = FirebaseFirestore.getInstance().collection("users").document(uid).collection("wallet").document(walletID);

                                        HashMap<String, String> walletstoSaveMaster = new HashMap<>();
                                        final HashMap<String, Boolean> walletstoSaveMasterAcceptRequest = new HashMap<>();
                                        walletstoSaveMaster.put("walletID", walletID);
                                        walletstoSaveMaster.put("walletName", createWalletName.getText().toString());
                                        walletstoSaveMaster.put("walletCreatorID", uid);
                                        walletstoSaveMasterAcceptRequest.put("acceptRequest",true);
                                        walletstoSaveMaster.put("walletCreator",MetaData.userinfo.getName());

                                        final Map<String, Object> timeStamp = new HashMap<>();
                                        timeStamp.put("timeStamp", FieldValue.serverTimestamp());
                                        /*final Wallet walletmaster=new Wallet();
                                        walletmaster.setWalletID(walletID);
                                        walletmaster.setWalletName(createWalletName.getText().toString());
                                        walletmaster.setAcceptRequest(true);*/

                                        //4.
                                        mDocRef4.set(walletstoSaveMaster,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDocRef4.set(walletstoSaveMasterAcceptRequest,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mDocRef4.set(timeStamp,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                DocumentReference mDocRef5 = FirebaseFirestore.getInstance().collection("users").document(uid);
                                                                //5.
                                                                mDocRef5.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        Users masterUsers=new Users();
                                                                        masterUsers=task.getResult().toObject(Users.class);
                                                                        DocumentReference mDocRef6 =  FirebaseFirestore.getInstance().collection("group").document(walletID).collection("members").document(uid);
                                                                        mDocRef6.set(masterUsers);

                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                                InputMethodManager inputManager =
                                                        (InputMethodManager) getActivity().
                                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                                inputManager.hideSoftInputFromWindow(
                                                        getActivity().getCurrentFocus().getWindowToken(),
                                                        InputMethodManager.HIDE_NOT_ALWAYS);

                                                mIMainActivity.createdWallet();

                                            }
                                        });
                                   /* }


                                });*/


                            }
                        });
                    }
            }
        }});

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }
    private void userSearch(final String searchText) {
        MetaData.searchedUsersItemListFull.clear();
        mResultList.removeAllViews();
        if (searchText.equals("")) {
            Toast.makeText(getActivity(), "No Input", Toast.LENGTH_SHORT).show();
            mSearchBtn.setVisibility(View.VISIBLE);
            buttonBlocker.setVisibility(View.GONE);
        } else {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            //wallet Changes
            CollectionReference transacionCollectionRef = db.collection("users");

            Query transactionQuery = null;
            /*if (mLastQuiredDocument != null) {

                transactionQuery = transacionCollectionRef.orderBy("name").startAt(searchText).endAt(searchText + "\uf8ff").startAfter(mLastQuiredDocument);
            } else {*/

            transactionQuery = transacionCollectionRef.orderBy("userID").startAt(searchText).endAt(searchText + "\uf8ff");


            /*}*/

            final String uid;
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                startActivity(new Intent(getActivity(), Login.class));
            }else {
                uid = user.getUid();
            transactionQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        //retrieving a list of documents by looping through the list by calling get results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Users users = document.toObject(Users.class);
                            if(document.getString("userID").substring(0,8).equals(searchText)) {
                                if (!users.getUserID().equals(uid)) {
                                    MetaData.searchedUsersItemListFull.add(users);
                                }
                            }


                        }

                        searchUserAdapter.notifyDataSetChanged();
                        mResultList.setAdapter(searchUserAdapter);
                        if(MetaData.searchedUsersItemListFull.size()<1){
                            Toast.makeText(getActivity(), "No Results", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Query Failed. Check Logs", Toast.LENGTH_SHORT).show();
                    }
                    mSearchBtn.setVisibility(View.VISIBLE);
                    buttonBlocker.setVisibility(View.GONE);
                }
            });
        }
    }

    }}













