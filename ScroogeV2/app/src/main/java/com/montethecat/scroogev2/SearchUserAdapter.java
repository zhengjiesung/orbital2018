package com.montethecat.scroogev2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.utils.L;

import java.util.Arrays;
import java.util.List;
//Barnabas Move this
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.UserViewHolder> {
private List<Users> usersList;
private Context ctx;
int mSelectedTransactionIntex;
boolean repeated;

    public SearchUserAdapter(List<Users> usersList, Context ctx) {
        this.usersList = usersList;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.user_listlayout, parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.user_email.setText(usersList.get(position).getUserID().substring(0,8));
        holder.user_name.setText(usersList.get(position).getName());
        holder.tickButton.setVisibility(View.GONE);
        if(usersList.get(position).getImage()!=null) {
            Log.i("download Url",usersList.get(position).getImage());
            Glide.with(holder.user_image.getContext())
                    .load(usersList.get(position).image)
                    .into(holder.user_image);
        }else {
            holder.user_image.setImageResource(R.drawable.basicgroot);
        }
        for(int n=0;n<MetaData.selectedUsersList.size();n++){
            Log.i("Barnabas","Entered here");
            Log.i("Barnabas",MetaData.selectedUsersList.get(n).getUserID()+"\n"+usersList.get(position).getUserID());
            if(MetaData.selectedUsersList.get(n).getUserID().equals(usersList.get(position).getUserID())){
                holder.tickButton.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (usersList != null)
            return usersList.size();
        else
            return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView user_name,user_email;
        ImageView user_image;
        ImageView tickButton;
        public UserViewHolder(View view) {
            super(view);
            user_name = (TextView) view.findViewById(R.id.name_text);
            user_email = (TextView) view.findViewById(R.id.status_text);
            user_image = (ImageView) view.findViewById(R.id.profile_image);
            tickButton=(ImageView)view.findViewById(R.id.tickButton);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            mSelectedTransactionIntex=getAdapterPosition();
            Log.i("Clicked here",Integer.toString(mSelectedTransactionIntex));
            Toast.makeText(ctx, "TextClick", Toast.LENGTH_SHORT).show();
            if (MetaData.selectedUsersList.size()!=0){
                repeated=false;
                for (int n = 0; n<MetaData.selectedUsersList.size(); n++){
                    if(MetaData.selectedUsersList.get(n).getUserID().equals(usersList.get(mSelectedTransactionIntex).getUserID())){
                        MetaData.selectedUsersList.remove(n);
                        CreateWalletFragment.searchedUserSelectedAdapter.notifyItemRemoved(n);
                        repeated=true;
                    }
                }
                if (repeated==true){
                    Toast.makeText(ctx, "Already Selected", Toast.LENGTH_SHORT).show();
                    tickButton.setVisibility(View.GONE);

                }else {
                    int insertedIndex=MetaData.selectedUsersList.size();
                    MetaData.selectedUsersList.add(insertedIndex,usersList.get(mSelectedTransactionIntex));
                    Log.i("BarnabasAdd", Arrays.toString(MetaData.selectedUsersList.toArray()));
                    tickButton.setVisibility(View.VISIBLE);
                    CreateWalletFragment.searchedUserSelectedAdapter.notifyItemInserted(insertedIndex);
                }
            }else {
                MetaData.selectedUsersList.add(usersList.get(mSelectedTransactionIntex));
                Log.i("BarnabasAdd", Arrays.toString(MetaData.selectedUsersList.toArray()));
                CreateWalletFragment.heading_label.setVisibility(View.GONE);
                CreateWalletFragment.createWalletName.setVisibility(View.VISIBLE);
                CreateWalletFragment.createWalletButtonConfirm.setVisibility(View.VISIBLE);
                CreateWalletFragment.searchedUserSelectedAdapter.notifyDataSetChanged();
                tickButton.setVisibility(View.VISIBLE);
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




        }
    }
}
