package com.montethecat.scroogev2;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Wallet {
    public String walletName;
    public String walletID;
    public  @ServerTimestamp Date timeStamp;
    public Boolean acceptRequest;
    public String walletCreator;
    public String walletCreatorID;
    public Wallet(){

    }

    public Wallet(String walletName, String walletID, Date timeStamp, Boolean acceptRequest, String walletCreator, String walletCreatorID) {
        this.walletName = walletName;
        this.walletID = walletID;
        this.timeStamp = timeStamp;
        this.acceptRequest = acceptRequest;
        this.walletCreator = walletCreator;
        this.walletCreatorID = walletCreatorID;
    }

    public String getWalletName() {
        return walletName;
    }

    public String getWalletID() {
        return walletID;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public Boolean getAcceptRequest() {
        return acceptRequest;
    }

    public String getWalletCreator() {
        return walletCreator;
    }

    public String getWalletCreatorID() {
        return walletCreatorID;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public void setWalletID(String walletID) {
        this.walletID = walletID;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAcceptRequest(Boolean acceptRequest) {
        this.acceptRequest = acceptRequest;
    }

    public void setWalletCreator(String walletCreator) {
        this.walletCreator = walletCreator;
    }

    public void setWalletCreatorID(String walletCreatorID) {
        this.walletCreatorID = walletCreatorID;
    }
}
