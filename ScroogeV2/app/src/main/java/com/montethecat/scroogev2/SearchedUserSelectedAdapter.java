package com.montethecat.scroogev2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class SearchedUserSelectedAdapter extends RecyclerView.Adapter<SearchedUserSelectedAdapter.SearchedUserSelectedViewHolder>{
    private List<Users> usersList;
    private Context ctx;
    int mSelectedTransactionIntex;

    public SearchedUserSelectedAdapter(List<Users> usersList, Context ctx) {
        this.usersList = usersList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public SearchedUserSelectedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.userlistlayout_selected, parent,false);
        return new SearchedUserSelectedViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull SearchedUserSelectedViewHolder holder, int position) {
        if(usersList.get(position).getImage()!=null) {
            Log.i("download Url",usersList.get(position).getImage());
            holder.selectedUserImg.setScaleX(0);
            holder.selectedUserImg.setScaleY(0);
            Glide.with(ctx)
                    .load(usersList.get(position).getImage())
                    .into(holder.selectedUserImg);
            holder.selectedUserImg.animate().scaleX(1).scaleY(1).setDuration(500);
            if(usersList.get(position).getName().length()>5) {
                holder.searchedUserName.setText(usersList.get(position).getName().substring(0, 5) + "...");
            }else {
                holder.searchedUserName.setText(usersList.get(position).getName());
            }
            if(usersList.get(position).getUserID().length()>8) {
                holder.searchedUserID.setText(usersList.get(position).getUserID().substring(0, 8));
            }else {
                holder.searchedUserID.setText(usersList.get(position).getUserID());
            }
            holder.searchedUserName.setVisibility(View.VISIBLE);
            holder.searchedUserID.setVisibility(View.VISIBLE);
        }else {
            holder.selectedUserImg.setImageResource(R.drawable.basicgroot);
        }
    }

    @Override
    public int getItemCount() {
        if (usersList != null)
            return usersList.size();
        else
            return 0;
    }


    public class SearchedUserSelectedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        ImageView selectedUserImg;
        TextView searchedUserName;
        TextView searchedUserID;
        public SearchedUserSelectedViewHolder(View itemView) {
            super(itemView);
            selectedUserImg=(ImageView) itemView.findViewById(R.id.profile_image_selected);
            searchedUserName=(TextView) itemView.findViewById(R.id.searchedUserName);
            searchedUserID=(TextView) itemView.findViewById(R.id.searchedUserID);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedTransactionIntex=getAdapterPosition();
            int position=-1;
            for (int n=0;n<MetaData.searchedUsersItemListFull.size();n++){
                if(MetaData.searchedUsersItemListFull.get(n).getUserID().equals(MetaData.selectedUsersList.get(mSelectedTransactionIntex).getUserID())){
                    position=n;
                }
            }
            CreateWalletFragment.searchUserAdapter.notifyItemChanged(position);
            MetaData.selectedUsersList.remove(mSelectedTransactionIntex);
            Log.i("BarnabasRemoved", Arrays.toString(MetaData.selectedUsersList.toArray()));
            if(MetaData.selectedUsersList.size()==0) {
                Log.i("Barnabas","Entered here");
                CreateWalletFragment.heading_label.setVisibility(View.VISIBLE);
                CreateWalletFragment.createWalletName.setVisibility(View.GONE);
                CreateWalletFragment.createWalletButtonConfirm.setVisibility(View.GONE);
                CreateWalletFragment.createWalletButtonEdit.setVisibility(View.GONE);
            }
            if(MetaData.selectedUsersList.size()!=0&&MetaData.hereForMembersEdit==false) {
                Log.i("Barnabas","Entered here");
                CreateWalletFragment.heading_label.setVisibility(View.GONE);
                CreateWalletFragment.createWalletName.setVisibility(View.VISIBLE);
                CreateWalletFragment.createWalletButtonConfirm.setVisibility(View.VISIBLE);
            }
            if(MetaData.selectedUsersList.size()!=0&&MetaData.hereForMembersEdit==true) {
                Log.i("Barnabas","Entered here");
                CreateWalletFragment.heading_label.setVisibility(View.VISIBLE);
                CreateWalletFragment.createWalletName.setVisibility(View.GONE);
                CreateWalletFragment.heading_label.setText(MetaData.walletForEditName.getWalletName());
                CreateWalletFragment.createWalletButtonEdit.setVisibility(View.VISIBLE);
            }
            CreateWalletFragment.searchedUserSelectedAdapter.notifyItemRemoved(mSelectedTransactionIntex);




        }


        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

}
