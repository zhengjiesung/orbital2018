package com.montethecat.scroogev2;

/*
RecyclerView.Adapter
//binds data to view
RecyclerView.ViewHolder
//holders holds the view
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PlannedPaymentAdapter extends RecyclerView.Adapter<PlannedPaymentAdapter.PlannedPaymentViewHolder> {
    private Context mCtx;
    private List<PlannedPaymentItem> plannedPaymentList;

    public PlannedPaymentAdapter(Context mCtx, List<PlannedPaymentItem> plannedPaymentList) {
        this.mCtx = mCtx;
        this.plannedPaymentList = plannedPaymentList;
    }

    @NonNull
    @Override
    public PlannedPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //This Methods creates a viewHolder Instance
        LayoutInflater inflater=LayoutInflater.from(mCtx);
        View view=LayoutInflater.from(mCtx).inflate(R.layout.planned_payment_items, viewGroup,false);


        return new PlannedPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannedPaymentViewHolder plannedPaymentViewHolder, int i) {
        //this methods binds data to viewHolder
        PlannedPaymentItem plannedPaymentItem=plannedPaymentList.get(i);

        plannedPaymentViewHolder.typePlannedPaymentTextView.setText(plannedPaymentList.get(i).getPlannedPaymentType());
        plannedPaymentViewHolder.paymentModeTextView.setText(plannedPaymentList.get(i).getPlannedPaymentMode());
        plannedPaymentViewHolder.valuePlannedPaymentTextView.setText(plannedPaymentList.get(i).getPlannedPaymentCost());
        plannedPaymentViewHolder.dateTextView.setText(plannedPaymentList.get(i).getPlanedPaymentDate());
        plannedPaymentViewHolder.paymentTypeImg.setImageDrawable(mCtx.getResources().getDrawable(plannedPaymentList.get(i).getPlannedPaymentTypeImg()));

    }

    @Override
    public int getItemCount() {
        //returns size of list
        return plannedPaymentList.size();
    }

    class PlannedPaymentViewHolder extends RecyclerView.ViewHolder{
        ImageView paymentTypeImg;
        TextView typePlannedPaymentTextView, paymentModeTextView, valuePlannedPaymentTextView, dateTextView;

        public PlannedPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            paymentTypeImg=(ImageView) itemView.findViewById(R.id.paymentTypeImg);
            typePlannedPaymentTextView=(TextView) itemView.findViewById(R.id.typePlannedPaymentTextView);
            paymentModeTextView=(TextView) itemView.findViewById(R.id.paymentModeTextView);
            valuePlannedPaymentTextView=(TextView) itemView.findViewById(R.id.valuePlannedPaymentTextView);
            dateTextView=(TextView) itemView.findViewById(R.id.dateTextView);
        }
    }
}
