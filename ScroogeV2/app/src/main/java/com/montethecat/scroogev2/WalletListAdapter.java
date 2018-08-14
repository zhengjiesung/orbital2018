package com.montethecat.scroogev2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.WalletListViewHolder> {
    private Context ctx;
    private List<Wallet> walletList;
    private IMainActivity mIMainActivity;

    int  mSelectedTransactionIntex;
    Dialog myDialog;

    public WalletListAdapter(Context ctx, List<Wallet> walletList) {
        this.ctx = ctx;
        this.walletList = walletList;
    }

    @NonNull
    @Override
    public WalletListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.wallet_item, parent,false);
        return new WalletListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletListViewHolder holder, int position) {
        String uid = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
        }else {
            uid = user.getUid();
        }
        holder.walletItemName.setText(walletList.get(position).getWalletName());
        holder.walletCreator.setText(walletList.get(position).getWalletCreator());
        String dateForUse;
        String date = "";
        if(walletList.get(position).getTimeStamp() != null){
            date = walletList.get(position).getTimeStamp().toString();
        }
        String year = "";
        if (!date.equals("")) {
            String[] yearGetter = date.split(" ");
            year = yearGetter[5];
        }
        Pattern pattern = Pattern.compile("(.*?) GMT");
        Matcher matcher = pattern.matcher(date);
        StringBuilder builder = new StringBuilder();

        while (matcher.find()) {
            System.out.println(matcher.group(1));
            builder.append(matcher.group(1));
        }
        dateForUse = year+" "+builder.toString();
        holder.walletID.setText(dateForUse);
        if(walletList.get(position).getWalletCreatorID().equals(uid)){
            holder.walletCreator.setTextColor(ContextCompat.getColor(ctx,R.color.Red));
        }
        if(walletList.get(position).getAcceptRequest()){
        holder.pendingAccept.setVisibility(View.GONE);}
        else {
            holder.pendingAccept.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        if (walletList != null)
            return walletList.size();
        else
            return 0;

    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) ctx;
    }

    public class WalletListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        TextView walletItemName;
        Boolean LongClicked;
        TextView pendingAccept;
        TextView walletCreator;
        TextView walletID;

        public WalletListViewHolder(View itemView) {
            super(itemView);

            walletItemName=itemView.findViewById(R.id.walletItemName);
            pendingAccept=itemView.findViewById(R.id.pendingAccept);
            walletCreator=itemView.findViewById(R.id.walletCreator);
            walletID=itemView.findViewById(R.id.walletID);
            itemView.setOnClickListener(this);
            myDialog=new Dialog(ctx);
            myDialog.setContentView(R.layout.group_add_leave);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    //code you want to execute when the dialog is closed
                    LongClicked=false;
                }
            });
            LongClicked=false;
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(LongClicked==false) {
                mSelectedTransactionIntex = getAdapterPosition();
                if(walletList.get(mSelectedTransactionIntex).getAcceptRequest()) {
                    MetaData.walletName = walletList.get(mSelectedTransactionIntex).getWalletID();
                    MetaData.walletNameForReal=walletList.get(mSelectedTransactionIntex).getWalletName();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.putExtra("inwallet", true);
                    intent.putExtra("walletName", MetaData.walletName);
                    ctx.startActivity(intent);
                }
            }

        }

        @Override
        public boolean onLongClick(View view) {

            MetaData.selectedUsersListForEdit=new ArrayList<>();
            MetaData.hereForMembersEdit=false;

            mSelectedTransactionIntex=getAdapterPosition();
            Log.i("Clicked here",Integer.toString(mSelectedTransactionIntex));
            LongClicked=true;
            Toast.makeText(ctx, "TextClick", Toast.LENGTH_SHORT).show();
            Button leavedeletegroup=myDialog.findViewById(R.id.leavedeletegroup);
            Button acceptrequest =myDialog.findViewById(R.id.acceptrequest);
            Button editMembers=myDialog.findViewById(R.id.editMembers);
            Button walletInfo=myDialog.findViewById(R.id.walletInfo);
            Wallet walletforEdit=new Wallet();
            walletforEdit=MetaData.myWallets.get(mSelectedTransactionIntex);
            final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(walletforEdit.getWalletCreatorID().equals(userID)){
                editMembers.setVisibility(View.VISIBLE);
            }else {
                editMembers.setVisibility(View.GONE);
            }
            if(MetaData.myWallets.get(mSelectedTransactionIntex).getAcceptRequest()){
                acceptrequest.setVisibility(View.GONE);
            }else {
                acceptrequest.setVisibility(View.VISIBLE);
            }
            Button cancelEdit=myDialog.findViewById(R.id.cancelEditGroup);
            myDialog.show();
            final Wallet finalWalletforEdit = walletforEdit;
            leavedeletegroup.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                        //mDocRef to delete wallet in user wallet
                                                        //mDocRef2 to delete user from wallet list of user

                                                        //mDocRef3 to Change walletCreator and walletCreaterID in group/walletID
                                                        //mDocRef4 to Change walletCreator and walletCreaterID in all users/UserID/wallet/walletID
                                                        final DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("users/" + userID + "/wallet").document(finalWalletforEdit.getWalletID());

                                                        mDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ctx, "deleted Wallet", Toast.LENGTH_SHORT).show();
                                                                }
                                                                MetaData.myWallets.remove(mSelectedTransactionIntex);
                                                                walletFragment.walletListAdapter.notifyItemRemoved(mSelectedTransactionIntex);
                                                                DocumentReference mDocRef2=FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/members").document(userID);

                                                                mDocRef2.delete();

                                                                if(finalWalletforEdit.getWalletCreatorID().equals(userID)){
                                                                    final CollectionReference CollectRef3=FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/members");
                                                                    Query query=CollectRef3.orderBy("name");
                                                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            final ArrayList<Users> member=new ArrayList<>();
                                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                Users users = document.toObject(Users.class);
                                                                                if (!users.getUserID().equals(userID)) {
                                                                                    member.add(users);
                                                                                }
                                                                            }
                                                                                if(member.size()>0){
                                                                                DocumentReference mDocRef3=FirebaseFirestore.getInstance().document("group/" + finalWalletforEdit.getWalletID());
                                                                                HashMap<String, String> dataToSave=new HashMap<>();
                                                                                dataToSave.put("walletCreator",member.get(0).getName());
                                                                                dataToSave.put("walletCreatorID",member.get(0).getUserID());
                                                                                mDocRef3.set(dataToSave,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        for (int n=0; n<member.size();n++){
                                                                                            DocumentReference mDocRef4=FirebaseFirestore.getInstance().document("users/"+member.get(n).getUserID() +"/wallet/"+ finalWalletforEdit.getWalletID());
                                                                                            HashMap<String, String> dataToSave=new HashMap<>();
                                                                                            dataToSave.put("walletCreator",member.get(0).getName());
                                                                                            dataToSave.put("walletCreatorID",member.get(0).getUserID());
                                                                                            mDocRef4.set(dataToSave,SetOptions.merge());
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }else{
                                                                                    DocumentReference mDocRef4=FirebaseFirestore.getInstance().document("group/" + finalWalletforEdit.getWalletID());
                                                                                    mDocRef4.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            CollectionReference collectRef4=FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/transactionData");
                                                                                            Query query1=collectRef4;
                                                                                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                                        Transaction transaction = document.toObject(Transaction.class);
                                                                                                        DocumentReference mDocRef5=FirebaseFirestore.getInstance().document("group/" + finalWalletforEdit.getWalletID()+"/transactionData/"+transaction.getTransaction_id());
                                                                                                        mDocRef5.delete();
                                                                                                        }
                                                                                                    CollectionReference collectRef5=FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/Metadata");
                                                                                                    Query query2=collectRef5;
                                                                                                    query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                                                                                    // here you can get the id.


                                                                                                                    DocumentReference mDocRef6=FirebaseFirestore.getInstance().document("group/" + finalWalletforEdit.getWalletID()+"/Metadata/"+documentSnapshot.getString("doc Name"));
                                                                                                                    mDocRef6.delete();
                                                                                                                    // you can apply your actions...
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    });

                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    });


                                                                                }
                                                                    }});
                                                                }
                                                                /*Map<String,Object> updates=new HashMap<>();
                                                                updates.put(finalWalletforEdit.getMemberKey(), FieldValue.delete());
                                                                mDocRef2.update(updates);*/
                                                            }
                                                        });
                                                        myDialog.dismiss();
                                                    }
                                                });
            walletInfo.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  CollectionReference CollectRef3 = FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/members");
                                                  Query query = CollectRef3.orderBy("name");
                                                  query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                          MetaData.selectedUsersListForEdit.clear();
                                                          for (QueryDocumentSnapshot document : task.getResult()) {
                                                              Users users = document.toObject(Users.class);
                                                                  MetaData.selectedUsersListForEdit.add(users);

                                                          }
                                                          MetaData.hereForMembersEdit = true;
                                                          MetaData.walletForEditName = finalWalletforEdit;
                                                          mIMainActivity.walletInfo();
                                                          myDialog.dismiss();
                                                      }

                                                  });
                                              }

                                          });
            editMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CollectionReference CollectRef3 = FirebaseFirestore.getInstance().collection("group/" + finalWalletforEdit.getWalletID() + "/members");
                    Query query = CollectRef3.orderBy("name");
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            MetaData.selectedUsersListForEdit.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Users users = document.toObject(Users.class);
                                if (!users.getUserID().equals(userID)) {
                                    MetaData.selectedUsersListForEdit.add(users);
                                }
                            }
                            MetaData.hereForMembersEdit=true;
                            MetaData.walletForEditName=finalWalletforEdit;
                            mIMainActivity.editMembers();
                            myDialog.dismiss();

                        }
                    });

                }

            });
            acceptrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("users/" + userID + "/wallet").document(finalWalletforEdit.getWalletID());
                    HashMap<String, Boolean> accept=new HashMap<>();
                    accept.put("acceptRequest",true);
                    mDocRef.set(accept, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            myDialog.dismiss();
                            finalWalletforEdit.setAcceptRequest(true);
                            walletFragment.walletListAdapter.notifyItemChanged(mSelectedTransactionIntex);

                        }
                    });
                }
            });
            cancelEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ctx, "Cancelled", Toast.LENGTH_SHORT).show();

                    myDialog.dismiss();
                }
            });

            return false;
        }
    }
}
