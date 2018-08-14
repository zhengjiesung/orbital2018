package com.montethecat.scroogev2;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaData extends AppCompatActivity {

    public static Users userinfo;

    public static List<RecentTransactionItem> recentTransactionItemList;
    public static List<RecentTransactionItem> recentIncomeItemList;

    public static List<RecentTransactionItem> recentTransactionItemListFull;
    public static List<RecentTransactionItem> recentIncomeItemListFull;
    public static List<BudgetMeterItemBudget> budgetMeterItemBudgetListFull;
    public static List<BudgetMeterItemProgress> budgetMeterItemProgresslistFull;
    public static List<BudgetMeterItemBudget> budgetMeterItemBudgetListTop3;
    public static List<BudgetMeterItemProgress> budgetMeterItemProgressListTop3;
    public static Map<String, Double> budgetMeterPercentageMap;
    public static String budgetCategory = "Food and Drinks"; // default budgetCategory


    //used in create wallet
    //For user search
    public static List<Users> searchedUsersItemListFull;
    public static List<Users> selectedUsersList;
    public static List<Users> selectedUsersListForEdit;
    public static Boolean hereForMembersEdit;
    public static Wallet walletForEditName;
    public static String profileImageDownloadURL;
    public static List<Wallet> myWallets;

    //added this for wallet
    public static Boolean inwallet=false;
    public static String walletName="Monte Carlo";
    public static String walletNameForReal="Monte Carlo";
    //For Editing
    public static Boolean hereForEdit=false;
    public static RecentTransactionItem recentTransactionItemForEdit;

    public static HashMap<String, Double> catTotal;
    public static HashMap<String, Double> catTotal1;
    public static HashMap<String, Double> incTotal;
    public static HashMap<String, Double> incTotal1;
    public static String date;

    public static ArrayList<Integer> colorExpense;
    public static ArrayList<Integer> colorExpense1;
    public static ArrayList<Integer> colorIncome;
    public static ArrayList<Integer> colorIncome1;
    //For setting decimal format use MetaData.df.format()
    public static DecimalFormat df=new DecimalFormat("#.00");
    public static int counter;

    public static HashMap<String, Double> getCatTotal() {
        return catTotal;
    }

    public static String chooseColor(String category) {
        String imageResource;
        switch (category) {
            case "Food and Drinks":
                imageResource = "#00ff00";
                break;
            case "Shopping":
                imageResource ="#ff0000" ;
                break;
            case "Housing":
                imageResource ="#cc66ff" ;
                break;
            case "Transport":
                imageResource = "#3366ff";
                break;
            case "Vehicle":
                imageResource = "#ff0066";
                break;
            case "Life & Entertainment":
                imageResource = "#003300";
                break;
            case "Communications,PC":
                imageResource = "#ffff00";
                break;
            case "Financial Expenses":
                imageResource = "#666699";
                break;
            case "Investment":
                imageResource = "#cc9900";
                break;
            case "Education":
                imageResource = "#800000";
                break;
            case "Insurance Payment":
                imageResource ="#006666" ;
                break;
            case "Transfer-out":
                imageResource = "#663300";
                break;
            case "Others (Expenditure)":
                imageResource = "#33ccff";
                break;
            case "Salary":
                imageResource="#00ff00";
                break;
            case "Business":
                imageResource = "#ff0000";
                break;
            case "Loan":
                imageResource = "#cc66ff";
                break;
            case "Parental Leave":
                imageResource = "#3366ff";
                break;
            case "Insurance Payout":
                imageResource="#800000";
                break;
            case "Transfer-in":
                imageResource="#663300";
                break;
            case "Others (income)":
                imageResource="#33ccff";
                break;
            default:
                imageResource = "#33ccff";

        }
        return imageResource;
    }

    public static int chooseImage(String category) {
        int imageResource = 0;
        switch (category) {
            case "Food and Drinks":
                imageResource = R.drawable.food;
                break;
            case "Shopping":
                imageResource = R.drawable.shopping;
                break;
            case "Housing":
                imageResource = R.drawable.housing;
                break;
            case "Transport":
                imageResource = R.drawable.transport;
                break;
            case "Vehicle":
                imageResource = R.drawable.vehicle;
                break;
            case "Life & Entertainment":
                imageResource = R.drawable.lifestyle;
                break;
            case "Communications,PC":
                imageResource = R.drawable.pc;
                break;
            case "Financial Expenses":
                imageResource = R.drawable.financial;
                break;
            case "Investment":
                imageResource = R.drawable.investment;
                break;
            case "Education":
                imageResource = R.drawable.education;
                break;
            case "Insurance Payment":
                imageResource = R.drawable.insurancepayment;
                break;
            case "Transfer-out":
                imageResource = R.drawable.transferout;
                break;
            case "Others (Expenditure)":
                imageResource = R.drawable.others;
                break;
            case "Salary":
                imageResource=R.drawable.salary;
                break;
            case "Business":
                imageResource = R.drawable.business;
                break;
            case "Loan":
                imageResource = R.drawable.loan;
                break;
            case "Parental Leave":
                imageResource = R.drawable.parentalleave;
                break;
            case "Insurance Payout":
                imageResource=R.drawable.insurance;
                break;
            case "Transfer-in":
                imageResource=R.drawable.transferin;
                break;
            case "Others (Income)":
                imageResource = R.drawable.others;
                break;
            default:imageResource=R.drawable.others;

        }
        return imageResource;
    }


    public static String setMonth(int month) {
        String month_Name;
        switch (month) {
            case 0:
                month_Name = "Jan";
                break;
            case 1:
                month_Name = "Feb";
                break;
            case 2:
                month_Name = "Mar";
                break;
            case 3:
                month_Name = "Apr";
                break;
            case 4:
                month_Name = "May";
                break;
            case 5:
                month_Name = "Jun";
                break;
            case 6:
                month_Name = "Jul";
                break;
            case 7:
                month_Name = "Aug";
                break;
            case 8:
                month_Name = "Sep";
                break;
            case 9:
                month_Name = "Oct";
                break;
            case 10:
                month_Name = "Nov";
                break;
            case 11:
                month_Name = "Dec";
                break;
            default:
                month_Name = "Jan";
                break;
        }
        return month_Name;

    }
}