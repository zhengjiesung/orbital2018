package com.montethecat.scroogev2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.montethecat.scroogev2.IMainActivity;
import com.montethecat.scroogev2.RecentTransactionItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.RecentTransactionViewHolder>{
    private Context mCtx;
    private List<RecentTransactionItem> recentTransactionList;
    private int mSelectedTransactionIntex;
    String collectionNAME;
    Dialog myDialog;
    private IMainActivity mIMainActivity;

    public RecentTransactionAdapter(Context mCtx, List<RecentTransactionItem> recentTransactionList) {
        this.mCtx = mCtx;
        this.recentTransactionList = recentTransactionList;
    }

    @NonNull
    @Override
    public RecentTransactionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(mCtx);
        View view=LayoutInflater.from(mCtx).inflate(R.layout.recent_transaction_items, viewGroup,false);


        return new RecentTransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentTransactionViewHolder recentTransactionViewHolder, int i) {
        RecentTransactionItem recentTransactionItem=recentTransactionList.get(i);


        recentTransactionViewHolder.typeRecentTransactionTextView.setText(recentTransactionList.get(i).getRecentTransactionName());
        recentTransactionViewHolder.transactionModeTextView.setText(recentTransactionList.get(i).getRecetTransactionMode());
        TextView recentTransactionValue=recentTransactionViewHolder.valueRecentTransactionTextView;
                recentTransactionValue.setText("SGD$"+recentTransactionList.get(i).getRecentTansactionCost());
        recentTransactionViewHolder.dateRecentTransactionTextView.setText(recentTransactionList.get(i).getRecentTransactionDate());
        recentTransactionViewHolder.transactionTypeImg.setImageDrawable(mCtx.getResources().getDrawable(recentTransactionList.get(i).getRecentTransactionTypeImg()));

//        if(Double.parseDouble(recentTransactionList.get(i).getRecentTansactionCost())>=0){
//           recentTransactionValue.setTextColor(Color.parseColor("#00FF00"));
//        }else{
//            recentTransactionValue.setTextColor(Color.parseColor("#FF0000"));
//        }
    }

    @Override
    public int getItemCount() {
        if(recentTransactionList!=null)
        return recentTransactionList.size();
        else
            return 0;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) mCtx;
    }

    class RecentTransactionViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView transactionTypeImg;
        TextView typeRecentTransactionTextView, transactionModeTextView, valueRecentTransactionTextView, dateRecentTransactionTextView;


        public RecentTransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionTypeImg=(ImageView) itemView.findViewById(R.id.TransactionTypeImg);
            typeRecentTransactionTextView=(TextView) itemView.findViewById(R.id.typeTransactionTextView);
            transactionModeTextView=(TextView) itemView.findViewById(R.id.transactionModeTextView);
            valueRecentTransactionTextView=(TextView) itemView.findViewById(R.id.valueTransactionTextView);
            dateRecentTransactionTextView=(TextView) itemView.findViewById(R.id.dateTransactionTextView);
            myDialog=new Dialog(mCtx);
            myDialog.setContentView(R.layout.dialog_edit_delete);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            itemView.setOnLongClickListener(this);
        }

        //For Edit Delete Transaction
        @Override
        public boolean onLongClick(View view) {
            mSelectedTransactionIntex=getAdapterPosition();
            Log.i("Clicked here",Integer.toString(mSelectedTransactionIntex));
            Toast.makeText(mCtx, "TextClick", Toast.LENGTH_SHORT).show();
            Button editTransaction=myDialog.findViewById(R.id.editTransaction);
            Button deleteTransaction =myDialog.findViewById(R.id.deleteTransaction);
            Button cancelEdit=myDialog.findViewById(R.id.cancelEdit);
            editTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mCtx, "Transaction"+Integer.toString(mSelectedTransactionIntex)+"edited", Toast.LENGTH_SHORT).show();
                    mIMainActivity.editTransaction(recentTransactionList.get(mSelectedTransactionIntex));
                    myDialog.dismiss();
                }
            });
            deleteTransaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mCtx, "Transaction"+Integer.toString(mSelectedTransactionIntex)+"deleted", Toast.LENGTH_SHORT).show();
                    mIMainActivity.editTransaction(recentTransactionList.get(mSelectedTransactionIntex));
                    MetaData.hereForEdit=false;
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    collectionNAME="users/"+userID;
                    if(MetaData.inwallet==true){
                        userID=MetaData.walletName;
                        collectionNAME="group/"+userID;
                    }
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData").document(MetaData.recentTransactionItemForEdit.getRecentTransactionID());

                    mDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if(RecentTransactionFragment.recentTransactionAdapter != null) {
                                    RecentTransactionFragment.recentTransactionAdapter.notifyItemRemoved(mSelectedTransactionIntex);
                                }
                                Toast.makeText(mCtx, "deleted Transaction", Toast.LENGTH_SHORT).show();
                                //MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                //MainActivity.overallScreenArea.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(mCtx, "Failed. Check Log", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    String[] parts=MetaData.recentTransactionItemForEdit.getRecentTransactionDate().split(" ");
                    String year=parts[0];
                    String month=parts[2];
                    collectionNAME="users/"+userID;
                    if(MetaData.inwallet==true){
                        userID=MetaData.walletName;
                        collectionNAME="group/"+userID;
                    }

                    final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
                    mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, String> dataToSave=new HashMap<String, String>();
                            double total = 0.00;
                            if(documentSnapshot.exists()){
                                String totalString=documentSnapshot.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                String transactionMode = MetaData.recentTransactionItemForEdit.getRecetTransactionMode();
                                String expenseOrIncome = MetaData.recentTransactionItemForEdit.getRecentTransactionIncOrExp();
                                String transactionAmt = MetaData.recentTransactionItemForEdit.getRecentTansactionCost();
                                String currModeAmt = documentSnapshot.getString(transactionMode);
                                Log.i("Barnabas Editing",currModeAmt+"   "+transactionMode);
                                if(expenseOrIncome.equals("Income")){
                                    currModeAmt = String.valueOf(Double.parseDouble(currModeAmt) - Double.parseDouble(transactionAmt));
                                }else{
                                    currModeAmt = String.valueOf(Double.parseDouble(currModeAmt) + Double.parseDouble(transactionAmt));
                                }

                                dataToSave.put(transactionMode,currModeAmt);


                                if (totalString==null){
                                    totalString="0";
                                }
                                Log.i("Failed",totalString);
                                total=Double.parseDouble(totalString );

                                total-=Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                if((total)!=0) {
                                    dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(), String.valueOf(total));
                                    mDocRef2.set(dataToSave, SetOptions.merge());
                                }else {
                                    Map<String,Object> updates=new HashMap<>();
                                    updates.put(transactionMode,currModeAmt);
                                    updates.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(), FieldValue.delete());
                                    mDocRef2.update(updates);
                                }
                            }else{
                                total-=Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                String transactionMode = MetaData.recentTransactionItemForEdit.getRecetTransactionMode();
                                String expenseOrIncome = MetaData.recentTransactionItemForEdit.getRecentTransactionIncOrExp();
                                String transactionAmt = MetaData.recentTransactionItemForEdit.getRecentTansactionCost();
                                String currModeAmt = documentSnapshot.getString(transactionMode);
                                if(expenseOrIncome.equals("Income")){
                                    currModeAmt = String.valueOf(Double.parseDouble(currModeAmt) - Double.parseDouble(transactionAmt));
                                }else{
                                    currModeAmt = String.valueOf(Double.parseDouble(currModeAmt) + Double.parseDouble(transactionAmt));
                                }

                                dataToSave.put(transactionMode,currModeAmt);

                                if((total)!=0) {
                                    dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(), String.valueOf(total));
                                    mDocRef2.set(dataToSave, SetOptions.merge());
                                }else {
                                    Map<String,Object> updates=new HashMap<>();
                                    updates.put(transactionMode,currModeAmt);
                                    updates.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(), FieldValue.delete());
                                    mDocRef2.update(updates);
                                }
                            }
                            MainActivity.updateOverviewBalance();
                            MainActivity.analytics();
                            MainActivity.updateBudgetMeters();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                    MainActivity.overallScreenArea.setVisibility(View.GONE);
                                }
                            },1500);

                        }
                    });

                    myDialog.dismiss();
                }
            });
            cancelEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mCtx, "Cancelled", Toast.LENGTH_SHORT).show();

                    myDialog.dismiss();
                }
            });

            myDialog.show();
            return false;
        }

    }
}
