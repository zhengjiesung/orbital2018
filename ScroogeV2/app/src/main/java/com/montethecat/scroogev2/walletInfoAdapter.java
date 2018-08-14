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

import com.bumptech.glide.Glide;

import java.util.List;

public class walletInfoAdapter extends RecyclerView.Adapter<walletInfoAdapter.WalletMemberViewHolder> {
    private List<Users> usersList;
    private Context ctx;
    public walletInfoAdapter(List<Users> usersList, Context ctx) {
        this.usersList = usersList;
        this.ctx = ctx;
    }
    @NonNull
    @Override
    public WalletMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.user_listlayout, parent,false);
        return new WalletMemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletMemberViewHolder holder, int position) {
        holder.user_email.setText(usersList.get(position).getUserID().substring(0,8));
        holder.user_name.setText(usersList.get(position).getName());
        holder.tickButton.setVisibility(View.GONE);
        if(usersList.get(position).getUserID().equals(MetaData.walletForEditName.walletCreatorID)){
            holder.creatorTag.setVisibility(View.VISIBLE);
        }else {
            holder.creatorTag.setVisibility(View.GONE);
        }
        if(usersList.get(position).getImage()!=null) {
            Log.i("download Url",usersList.get(position).getImage());
            Glide.with(holder.user_image.getContext())
                    .load(usersList.get(position).image)
                    .into(holder.user_image);
        }else {
            holder.user_image.setImageResource(R.drawable.basicgroot);
        }

    }

    @Override
    public int getItemCount() {
        if (usersList != null)
            return usersList.size();
        else
            return 0;
    }

    public class WalletMemberViewHolder extends RecyclerView.ViewHolder {
        TextView user_name, user_email,creatorTag;
        ImageView user_image;
        ImageView tickButton;

        public WalletMemberViewHolder(View view) {
            super(view);
            creatorTag=(TextView)view.findViewById(R.id.creatorTag);
            user_name = (TextView) view.findViewById(R.id.name_text);
            user_email = (TextView) view.findViewById(R.id.status_text);
            user_image = (ImageView) view.findViewById(R.id.profile_image);
            tickButton = (ImageView) view.findViewById(R.id.tickButton);



        }
    }

    }


