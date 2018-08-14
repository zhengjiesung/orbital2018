package com.montethecat.scroogev2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class walletInfoFragment extends Fragment {
    TextView walletNameWalletInfo,infoOnwallet,memberSize;
    RecyclerView listOfMembersInWalletInfo;
    walletInfoAdapter walletInfoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_wallet_info, null);
        walletNameWalletInfo=(TextView)view.findViewById(R.id.walletNameWalletInfo);
        infoOnwallet=(TextView)view.findViewById(R.id.infoOnwallet);
        memberSize=(TextView)view.findViewById(R.id.memberSize);


        walletNameWalletInfo.setText(MetaData.walletForEditName.getWalletName());
        String dateForUse;
        String date = "";
        if(MetaData.walletForEditName.getTimeStamp() != null){
            date = MetaData.walletForEditName.getTimeStamp().toString();
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
        infoOnwallet.setText("Created by:" +MetaData.walletForEditName.getWalletCreator()+", "+dateForUse);
        memberSize.setText("Members: "+Integer.toString(MetaData.selectedUsersListForEdit.size()));
        listOfMembersInWalletInfo=(RecyclerView)view.findViewById(R.id.listOfMembersInWalletInfo);
        listOfMembersInWalletInfo.setHasFixedSize(true);
        listOfMembersInWalletInfo.setLayoutManager(new LinearLayoutManager(getActivity()));
        walletInfoAdapter=new walletInfoAdapter(MetaData.selectedUsersListForEdit,getContext());
        listOfMembersInWalletInfo.setAdapter(walletInfoAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}
