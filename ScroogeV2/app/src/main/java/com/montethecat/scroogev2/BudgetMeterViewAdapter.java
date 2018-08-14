package com.montethecat.scroogev2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetMeterViewAdapter extends RecyclerView.Adapter<BudgetMeterViewAdapter.BudgetMeterViewHolder> {


    private static final String TAG = "BudgetMeterViewAdapter";

    private Context mCtx;
    private List<BudgetMeterItemBudget> budgetMeterItemBudgetList;
    private List<BudgetMeterItemProgress> budgetMeterItemProgressList;
    private int mSelectedBudgetMeterIndex;
    Dialog myDialog;
    private IMainActivity mIMainActivity;
    String collectionNAME;
    FirebaseUser user;
    String uid;
    FirebaseAuth auth;

    public BudgetMeterViewAdapter(Context mCtx, List<BudgetMeterItemBudget> budgetMeterItemBudgetList, List<BudgetMeterItemProgress> budgetMeterItemProgressList) {
        this.mCtx = mCtx;
        this.budgetMeterItemBudgetList = budgetMeterItemBudgetList;
        this.budgetMeterItemProgressList = budgetMeterItemProgressList;

    }

    @NonNull
    @Override
    public BudgetMeterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(mCtx);

        View view=LayoutInflater.from(mCtx).inflate(R.layout.layout_budgetmeter, viewGroup,false);

        return new BudgetMeterViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull BudgetMeterViewHolder budgetMeterViewHolder, int position) {

//        Handler handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 1500);

        if (budgetMeterItemBudgetList.size() != 0 && budgetMeterItemProgressList.size() != 0) {
            BudgetMeterItemBudget budgetMeterItemBudget = budgetMeterItemBudgetList.get(position);
            BudgetMeterItemProgress budgetMeterItemProgress = budgetMeterItemProgressList.get(position);
            Log.i("fuck",String.valueOf(budgetMeterItemProgressList.size()));

            // set the category
            budgetMeterViewHolder.category.setText(budgetMeterItemBudget.getCategory());

            // set the ratio
            String currentBudget = budgetMeterItemBudget.getCurrentBudget();
            String currentProgress = budgetMeterItemProgress.getCurrentSpending();
            budgetMeterViewHolder.ratioOfBudget.setText(currentProgress + "/" + currentBudget);

            // set the percentage
            Double percentage = Double.parseDouble(currentProgress) / Double.parseDouble(currentBudget) * 100;
            String percentage_str = String.format("%.2f", percentage);
            budgetMeterViewHolder.percentageText.setText(percentage_str + "%");

            // set color
            if (percentage > 70) {
                budgetMeterViewHolder.percentageText.setTextColor(Color.rgb(194,24,91));
            } else {
                budgetMeterViewHolder.percentageText.setTextColor(Color.rgb(1,170,113));
            }

            // set the seekBar
            budgetMeterViewHolder.budgetSeekBar.setMax((int) Double.parseDouble(currentBudget)); // budgetlimit
            budgetMeterViewHolder.budgetSeekBar.setProgress((int) Double.parseDouble(currentProgress)); // current expenditure


            budgetMeterViewHolder.budgetSeekBar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

//            budgetMeterViewHolder.budgetSeekBar.getThumb().mutate().setAlpha(0);

            // set the image
            budgetMeterViewHolder.categoryImg.setImageDrawable(mCtx.getResources().getDrawable(MetaData.chooseImage(budgetMeterItemBudget.getCategory())));
        }

    }

    @Override
    public int getItemCount() {

        if(budgetMeterItemBudgetList!=null){
            return budgetMeterItemBudgetList.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMainActivity = (IMainActivity) mCtx;
    }

    class BudgetMeterViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {


        ImageView categoryImg;
        TextView category;
        Button budgetButton;
        FrameLayout budgetMeter;
        SeekBar budgetSeekBar;
        TextView ratioOfBudget;
        EditText budget;
        TextView percentageText;


        public BudgetMeterViewHolder (View itemView) {
            super(itemView);
            categoryImg = itemView.findViewById(R.id.categoryImg);
            category = itemView.findViewById(R.id.budgetCategory);
//            budgetButton = itemView.findViewById(R.id.budgetButton);
            budgetMeter = itemView.findViewById(R.id.budgetMeter);
            budgetSeekBar = itemView.findViewById(R.id.budgetSeekBar);
            ratioOfBudget = itemView.findViewById(R.id.ratioOfBudget);
            budget = itemView.findViewById(R.id.budget);
            percentageText = itemView.findViewById(R.id.percentageText);
            myDialog=new Dialog(mCtx);
            myDialog.setContentView(R.layout.dialog_budget_meters);
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            itemView.setOnLongClickListener(this);
        }

        // To Edit Delete the budget meters
        @Override
        public boolean onLongClick(View v) {
            mSelectedBudgetMeterIndex = getAdapterPosition();

            Button editBudgetMeter = myDialog.findViewById(R.id.editBudgetMeter);
            Button deleteBudgetMeter = myDialog.findViewById(R.id.deleteBudgetMeter);
            Button cancelEdit = myDialog.findViewById(R.id.cancelEditBudgetMeter);

            BudgetMeterItemBudget budgetMeterItemBudget = budgetMeterItemBudgetList.get(mSelectedBudgetMeterIndex);
            final String category = budgetMeterItemBudget.getCategory();

            editBudgetMeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    mIMainActivity.editBudgetMeter(category);
                    myDialog.dismiss();
                }
            });

            deleteBudgetMeter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {

                    } else {
                        uid = user.getUid();
                        collectionNAME = collectionNAME = "users/" + uid;

                        if (MetaData.inwallet == true) {
                            uid = MetaData.walletName;
                            collectionNAME = "group/" + uid;
                        }
                    }

                    // obtain data from budget
                    DocumentReference documentReferenceBudget = FirebaseFirestore.getInstance().document(collectionNAME + "/budgetData/budgetDataDocument");
                    // Remove the category field from the document
                    Map<String,Object> updates = new HashMap<>();
                    updates.put(category, FieldValue.delete());

                    // update the docRef
                    documentReferenceBudget.update(updates);

                    // refresh
                    BudgetMeterFragment.budgetMeterViewAdapter.notifyItemRemoved(mSelectedBudgetMeterIndex);
                    MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                    MainActivity.overallScreenArea.setVisibility(View.GONE);
                    mIMainActivity.deleteBudgetMeter();
                    myDialog.dismiss();

                }
            });

            cancelEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });

            myDialog.show();
            return false;
        }
    }

}
