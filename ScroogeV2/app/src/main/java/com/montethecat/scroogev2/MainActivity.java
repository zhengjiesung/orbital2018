package com.montethecat.scroogev2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,IMainActivity {


    Fragment fragment3 = null;
    public static FrameLayout overallScreenArea, screenArea1, screenArea2, screenArea3, screenArea4, screenArea5,screenAreaGrid;
    GridLayout gridFragments;
    public static String layoutName1, layoutName2, layoutName3, layoutName4, layoutName5;
    ScrollView mainContentScroll;
    TextView textViewName;
    TextView textViewEmail;
    TextView textViewInWallet;
    ImageView imageViewNavDrawer;
    DocumentReference docRef;
    //for wallet or in account
    static String collectionNAME;
    public static FirebaseUser user;
    public static String uid;
    private static FirebaseAuth auth;

    public static ArrayList<Transaction> incomeArray;
    public static ArrayList<Transaction> expenseArray;
    public static ArrayList<String> month3;
    public static ArrayList<String> month6;
    public static ArrayList<String> month12;
    public static ArrayList<BarEntry> barEntries;
    public static ArrayList<BarEntry> barEntries1;
    public static ArrayList<BarEntry> barEntries2;
    public static ArrayList<BarEntry> barEntries3;
    public static ArrayList<BarEntry> barEntries4;
    public static ArrayList<BarEntry> barEntries5;
    public static String currMonth;
    public static String prevMonth;
    public static String prev2Month;
    public static String prev3Month;
    public static String prev4Month;
    public static String prev5Month;
    public static String prev6Month;
    public static String prev7Month;
    public static String prev8Month;
    public static String prev9Month;
    public static String prev10Month;
    public static String prev11Month;

    public static String currBank;
    public static String currCard;
    public static String currCash;

    public static String prevBank;
    public static String prevCard;
    public static String prevCash;


    //for balanceMonthlyChangeFragment
    /*public static int bankAmt;
    public static int bankChangeAmt;
    public static int creditAmt;
    public static int creditChangeAmt;
    public static int cashAmt;
    public static int cashChangeAmt;
    public static ArrayList<Transaction> incomeArray2;
    public static ArrayList<Transaction> expenseArray2;*/


    public static DocumentSnapshot lastQuiredIncome;
    public static DocumentSnapshot lastQuiredExpense;

    public static boolean enteredSettings;


    private static DocumentReference mDocRef;
    private static Map<String, Object> dataToSave;

    public static int yearCurrently;
    public static int monthCurrently;

    public void gotoProfile(View view){
        fragment3=new ProfilePageFragment();
        setFragment3(fragment3);
    }

    // obtain the layoutName as input and outputs the position of the layoutName (Sorting by User)
    public void sortLayout(String layoutName, FrameLayout screenArea){
        Fragment fragment = null;

        if (layoutName != null) {
            if (layoutName.equals("Balance and Monthly Change")) {
                fragment = new BalanceMonthlyChangeFragment();

            } else if (layoutName.equals("Expenditure and Income")) {
                fragment = new ExpenditureIncomeFragment();

            } else if (layoutName.equals("Budget Meter")) {
                fragment = new BudgetMeterFragment();

            //} else if (layoutName.equals("Planned Payment List")) {
                //fragment = new PlannedPaymentListsFragment();

            } else if (layoutName.equals("Recent Transactions")) {
                fragment = new RecentTransactionFragment();
            }
        }
        if (fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(screenArea.getId(), fragment);
            screenArea.setVisibility(View.VISIBLE);

            ft.commit();

        } else {
            screenArea.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //First action to check if User is logged in
        user=FirebaseAuth.getInstance().getCurrentUser();
        if (user== null) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }else{
            //check
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //For Wallet Barnabas
            if(getIntent().getExtras()!=null) {
                MetaData.walletName = getIntent().getExtras().getString("walletName");
                MetaData.inwallet = getIntent().getExtras().getBoolean("inwallet");
            }

        this.setTitle("Overview");


        //Initialise Views
        overallScreenArea  = (FrameLayout) findViewById(R.id.screen_area);
        gridFragments = findViewById(R.id.gridFrames);
        screenArea1 = (FrameLayout) findViewById(R.id.screen_area_1);
        screenArea2 = (FrameLayout) findViewById(R.id.screen_area_2);
        screenArea3 = (FrameLayout) findViewById(R.id.screen_area_3);
        screenArea4 = (FrameLayout) findViewById(R.id.screen_area_4);
        //screenArea5 = (FrameLayout) findViewById(R.id.screen_area_5);
        screenAreaGrid=(FrameLayout)findViewById(R.id.screen_area_grid);
        mainContentScroll=(ScrollView)findViewById(R.id.mainContentScroll);
        lastQuiredExpense=null;
        lastQuiredIncome=null;

            barEntries = new ArrayList<>();
            barEntries1 = new ArrayList<>();
            barEntries2 = new ArrayList<>();
            barEntries3 = new ArrayList<>();
            barEntries4 = new ArrayList<>();
            barEntries5 = new ArrayList<>();

            analytics();

            Calendar c = Calendar.getInstance(TimeZone.getDefault());
            yearCurrently = c.get(Calendar.YEAR);
            monthCurrently = c.get(Calendar.MONTH);

        tallyTotal();
        tallyTotalIncome();
        updateBudgetMeters();


        //Initialise Full Transaction and Income list
        MetaData.recentTransactionItemListFull=new ArrayList<>();
        MetaData.recentIncomeItemListFull=new ArrayList<>();

        //Initialise Full Budget Meter List
            MetaData.budgetMeterItemBudgetListFull = new ArrayList<>();
            MetaData.budgetMeterItemProgresslistFull = new ArrayList<>();
            //Initialise Top 3 Budget Meter List
            MetaData.budgetMeterItemBudgetListTop3 = new ArrayList<>();
            MetaData.budgetMeterItemProgressListTop3 = new ArrayList<>();
            // Intialise the budgetmeter percentage list
            MetaData.budgetMeterPercentageMap = new HashMap<>();


        //Onclick to addTransaction
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment3=new addTransactionFragment();
                MetaData.hereForEdit=false;
                setFragment3(fragment3);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // update Email and Name in navigation drawer
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null) {
                finish();
                startActivity(new Intent(MainActivity.this, Login.class));
            }else {
                uid = user.getUid();

                docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            MetaData.userinfo = document.toObject(Users.class);
                            if (MetaData.userinfo != null) {
                                textViewName.setText(MetaData.userinfo.getName());
                                textViewEmail.setText(MetaData.userinfo.getEmail());
                                if(MetaData.inwallet==true){
                                    textViewInWallet.setText(MetaData.walletNameForReal);
                                }else{
                                    textViewInWallet.setVisibility(View.GONE);
                                }
                                if(MetaData.userinfo.getImage()!=null) {
                                    MetaData.profileImageDownloadURL=MetaData.userinfo.getImage();
                                    Glide.with(getApplicationContext()).load(MetaData.profileImageDownloadURL).into(imageViewNavDrawer);
                                }else {
                                    MetaData.profileImageDownloadURL=null;
                                    Glide.with(getApplicationContext()).load(R.drawable.basicgroot).into(imageViewNavDrawer);
                                }


                                if (MetaData.userinfo.getLayoutName1() == null) {
                                    layoutName1 = "Balance and Monthly Change";
                                    layoutName2 = "Expenditure and Income";
                                    layoutName3 = "Budget Meter";
                                    layoutName4 = "Null";
                                    //layoutName5 = "Null";
                                } else {
                                    layoutName1 = MetaData.userinfo.getLayoutName1();
                                    layoutName2 = MetaData.userinfo.getLayoutName2();
                                    layoutName3 = MetaData.userinfo.getLayoutName3();
                                    layoutName4 = MetaData.userinfo.getLayoutName4();
                                    //layoutName5 = MetaData.userinfo.getLayoutName5();
                                }
                                // sort the layouts in the overview page
                                sortLayout(layoutName1, screenArea1);
                                sortLayout(layoutName2, screenArea2);
                                sortLayout(layoutName3, screenArea3);
                                sortLayout(layoutName4, screenArea4);
                                //sortLayout(layoutName5, screenArea5);
                            } else {
                                userLogout();
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }

                    }
                });
            }

        screenAreaGrid.setTag("3");
        screenAreaGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = screenAreaGrid.getVisibility();
                if(Integer.parseInt(String.valueOf(screenAreaGrid.getTag())) != newVis)
                {
                    screenAreaGrid.setTag(screenAreaGrid.getVisibility());
                    //visibility has changed
                    if(enteredSettings==true) {
                        sortLayout(layoutName1, screenArea1);
                        sortLayout(layoutName2, screenArea2);
                        sortLayout(layoutName3, screenArea3);
                        sortLayout(layoutName4, screenArea4);
                        //sortLayout(layoutName5, screenArea5);
                        mainContentScroll.fullScroll(ScrollView.FOCUS_UP);
                        enteredSettings=false;

                    }

                }
            }


        });}
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }else {
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/transactionData");
            Query transactionQuery;
            final DocumentSnapshot[] mLastQuiredDocument = {null};
            //For Transaction Data
            if (mLastQuiredDocument[0] == null) {
                transactionQuery = collectionRef.whereEqualTo("type", "Expense");
            } else {
                transactionQuery = collectionRef.whereEqualTo("type", "Expense").startAfter(mLastQuiredDocument[0]);
            }
            transactionQuery.orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                             @Override
                                             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                 MetaData.recentTransactionItemListFull.clear();
                                                 int n=0;
                                                 for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                     Transaction transaction = document.toObject(Transaction.class);

                                                     String dateForUse;
                                                     String[] changeDate=transaction.getTimeStamp().toString().split(" ");
                                                     String monthTransaction=changeDate[1];
                                                     String yearTransaction=changeDate[5];
                                                     //Current Month
                                                     String monthFromSpinner=MetaData.setMonth(monthCurrently);
                                                     String yearFromSpinner=Integer.toString(yearCurrently);
                                                     String date = "";
                                                     if (transaction.getTimeStamp() != null) {
                                                         date = transaction.getTimeStamp().toString();
                                                     }
                                                     Log.i("Barnabas Check Here",date);
                                                     String year = "";
                                                     if (!date.equals("")) {
                                                         String[] yearGetter = date.split(" ");
                                                         year = yearGetter[5];
                                                     }
                                                     Pattern pattern = Pattern.compile("(.*?):00 GMT");
                                                     Matcher matcher = pattern.matcher(date);
                                                     StringBuilder builder = new StringBuilder();

                                                     while (matcher.find()) {
                                                         System.out.println(matcher.group(1));
                                                         builder.append(matcher.group(1));
                                                     }
                                                     dateForUse = year+" "+builder.toString();
                                                    Log.i("Barnabas Check Here",dateForUse);
                                                     String category = transaction.getCategory();
                                                     if (category == null){
                                                         category = "Others (Expenditure)";
                                                     }
                                                     String account = transaction.getAccount();
                                                     if (account == null){
                                                         account = "Bank";
                                                     }
                                                     String amount = transaction.getAmount();
                                                     if (amount == null){
                                                         amount = "0";
                                                     }
                                                     String name = transaction.getName();
                                                     if (name == null || name.equals("")) {
                                                         name = category;
                                                     }
                                                     if (monthFromSpinner.equals(monthTransaction) && yearFromSpinner.equals(yearTransaction)) {
                                                         if (n < 5) {
                                                             MetaData.recentTransactionItemListFull.add(new RecentTransactionItem(
                                                                             category,
                                                                             account,
                                                                             amount,
                                                                             dateForUse,
                                                                             transaction.getTransaction_id(),
                                                                             transaction.getDescription(),
                                                                             transaction.type,
                                                                             MetaData.chooseImage(category),
                                                                             name
                                                                     )
                                                             );
                                                             n++;
                                                         }
                                                     }

                                                     if (queryDocumentSnapshots.size() != 0) {
                                                         mLastQuiredDocument[0] = queryDocumentSnapshots.getDocuments()
                                                                 .get(queryDocumentSnapshots.size() - 1);
                                                     }//updateArray();
                                                     //Check here
                                                     if (RecentTransactionFragment.recentTransactionItemList != null)
                                                         RecentTransactionFragment.recentTransactionAdapter.notifyDataSetChanged();
                                                     if (ExpenditureContentFragment.recentTransactionAdapter != null)
                                                         ExpenditureContentFragment.recentTransactionAdapter.notifyDataSetChanged();
                                                 }

                                             }
                                         }
                    );
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }else {
            //wallet Changes
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/transactionData");
            Query IncomeQuery;
            final DocumentSnapshot[] mLastQuiredDocument2 = {null};
            //For Transaction Data
            if (mLastQuiredDocument2[0] == null) {
                IncomeQuery = collectionRef.whereEqualTo("type", "Income");
            } else {
                IncomeQuery = collectionRef.whereEqualTo("type", "Income").startAfter(mLastQuiredDocument2[0]);
            }
            IncomeQuery.orderBy("timeStamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                             @Override
                                             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                 MetaData.recentIncomeItemListFull.clear();
                                                 for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                     Transaction transaction = document.toObject(Transaction.class);

                                                     String dateForUse;
                                                     String date = "";
                                                     if(transaction.getTimeStamp() != null){
                                                         date = transaction.getTimeStamp().toString();
                                                     }
                                                     String year = "";
                                                     if (!date.equals("")) {
                                                         String[] yearGetter = date.split(" ");
                                                         year = yearGetter[5];
                                                     }
                                                     Pattern pattern = Pattern.compile("(.*?):00 GMT");
                                                     Matcher matcher = pattern.matcher(date);
                                                     StringBuilder builder = new StringBuilder();

                                                     while (matcher.find()) {
                                                         System.out.println(matcher.group(1));
                                                         builder.append(matcher.group(1));
                                                     }
                                                     dateForUse = year+" "+builder.toString();

                                                     String category = transaction.getCategory();
                                                     if (category == null){
                                                         category = "Others (Income)";
                                                     }
                                                     String account = transaction.getAccount();
                                                     if (account == null){
                                                         account = "Bank";
                                                     }
                                                     String amount = transaction.getAmount();
                                                     if (amount == null){
                                                         amount = "0";
                                                     }
                                                     String name = transaction.getName();
                                                     if(name == null || name.equals("")){
                                                         name = category;
                                                     }

                                                     MetaData.recentIncomeItemListFull.add(new RecentTransactionItem(category,
                                                             account,
                                                             amount,
                                                             dateForUse,
                                                             transaction.getTransaction_id(),
                                                             transaction.getDescription(),
                                                             transaction.getType(),
                                                             MetaData.chooseImage(category),
                                                             name
                                                             )
                                                     );

                                                     if (queryDocumentSnapshots.size() != 0) {
                                                         mLastQuiredDocument2[0] = queryDocumentSnapshots.getDocuments()
                                                                 .get(queryDocumentSnapshots.size() - 1);
                                                     }
                                                     //Check here
                                                     if (IncomeContentFragment.recentTransactionAdapterIncome != null)
                                                         IncomeContentFragment.recentTransactionAdapterIncome.notifyDataSetChanged();
                                                 }

                                             }
                                         }
                    );
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        textViewEmail = findViewById(R.id.textViewEmail);
        textViewName = findViewById(R.id.textViewName);
        imageViewNavDrawer=findViewById(R.id.imageViewNavDrawer);
        textViewInWallet=findViewById(R.id.textViewInWallet);




        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        
        if(id == R.id.action_settings) {
            fragment3 = new AddSortFragment();
            setFragment3(fragment3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void hideScreenAreas(){
        screenAreaGrid.setVisibility(View.GONE);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.


        int id = item.getItemId();

        if (id == R.id.wallet) {
            fragment3 = new walletFragment();

        } else if (id == R.id.createWallet) {
            MetaData.hereForMembersEdit=false;
            fragment3 = new CreateWalletFragment();

        } else if (id == R.id.overview) {
            screenAreaGrid.setVisibility(View.VISIBLE);
            overallScreenArea.setVisibility(View.GONE);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            mainContentScroll.fullScroll(ScrollView.FOCUS_UP);
            setTitle("Overview");


        } else if (id == R.id.transaction) {
            fragment3=new DataFragment();

        } else if (id == R.id.analytics) {
            fragment3 = new AnalyticsMainFragment();

        } else if (id == R.id.budget) {
            fragment3 = new BudgetFragment();

        } else if (id == R.id.budgetMeterDrawer) {
            fragment3 = new BudgetMeterPageFragment();

        } else if (id == R.id.newsfeed) {
            fragment3 = new NewsFeedFragment();

        } else if (id == R.id.uploadBankStatement){

            fragment3 = new UploadBankStatementFragment();

        } else if (id == R.id.logout) {
            userLogout();

        }

        if(id!=R.id.overview) {
            setFragment3(fragment3);
            mainContentScroll.fullScroll(ScrollView.FOCUS_UP);

        }
        return true;
    }
    public void setFragment3(Fragment fragment3){
        if (fragment3 != null){

            hideScreenAreas();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment3);
            overallScreenArea.setVisibility(View.VISIBLE);
            ft.commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public static void upDateSettings(){
        dataToSave = new HashMap<String, Object>();
        dataToSave.put("layoutName1", layoutName1);
        dataToSave.put("layoutName2", layoutName2);
        dataToSave.put("layoutName3", layoutName3);
        dataToSave.put("layoutName4", layoutName4);
        //dataToSave.put("layoutName5", layoutName5);
        // get unique id of user to store data of the user
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {

        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

        }
        else {
            uid = user.getUid();;

            mDocRef = FirebaseFirestore.getInstance().document("users/" + uid);

            mDocRef.set(dataToSave, SetOptions.merge());
        }

    }

    public static void tallyTotal() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

        }else {
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            MetaData.catTotal = new HashMap<>();
            //for pieChart Colors
            MetaData.colorExpense=new ArrayList<>();
            final ArrayList<String> category = new ArrayList<>();
            category.add("Food and Drinks");
            category.add("Shopping");
            category.add("Housing");
            category.add("Transport");
            category.add("Vehicle");
            category.add("Life & Entertainment");
            category.add("Communications,PC");
            category.add("Financial Expenses");
            category.add("Investment");
            category.add("Education");
            category.add("Insurance Payment");
            category.add("Transfer-out");
            category.add("Others (Expenditure)");

            String month_str = MetaData.setMonth(monthCurrently);

            mDocRef = FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + month_str + yearCurrently);
            mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    for (int n = 0; n < category.size(); n++) {
                        if (documentSnapshot != null) {
                            String totalString = documentSnapshot.getString(category.get(n));

                            if (totalString == null) {
                                Double arr = MetaData.catTotal.get(category);
                                if (arr != null) {

                                } else {
                                    Log.i("Removed", category.get(n));
                                    MetaData.catTotal.remove(category.get(n));
                                }
                            } else {
                                MetaData.catTotal.put(category.get(n), Double.parseDouble(totalString));
                                Log.i("HashMap1", String.valueOf(MetaData.catTotal));
                            }


                        }
                    }
                    Log.i("HashMap1Final", String.valueOf(MetaData.catTotal));

                    //Update PieChart in Expenditure and Income Fragment
                    if (ExpenditureIncomeFragment.yValues != null) {
                        ExpenditureIncomeFragment.yValues.clear();
                       /*/
                        changes this part for colors to work
                        */
                        //for pie chart colors
                        //added
                        MetaData.colorExpense.clear();
                        ExpenditureIncomeFragment.colorExpense=new int[MetaData.catTotal.size()];

                        for (String key : MetaData.catTotal.keySet()) {
                            ExpenditureIncomeFragment.yValues.add(new PieEntry(Float.parseFloat(MetaData.catTotal.get(key).toString()), key));
                            MetaData.colorExpense.add(ExpenditureIncomeFragment.rgb(MetaData.chooseColor(key)));

                        }


                        ExpenditureIncomeFragment.dataSet = new PieDataSet(ExpenditureIncomeFragment.yValues, "Countries");

                        ExpenditureIncomeFragment.dataSet.setColors(convertIntegers(MetaData.colorExpense));
                        PieData data = new PieData(ExpenditureIncomeFragment.dataSet);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(Color.YELLOW);
                        ExpenditureIncomeFragment.pieChart.getLegend().setEnabled(false);
                        ExpenditureIncomeFragment.pieChart.setData(data);
                        //Ends here
//                        if (ExpenditureContentFragment.pieChart != null) {
//                            ExpenditureContentFragment.pieChart.notifyDataSetChanged();
//                        }
                        ExpenditureIncomeFragment.pieChart.notifyDataSetChanged();
                    }
                }
            });
        }


    }
    public static void tallyTotalIncome() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {

        }else {
            uid = user.getUid();
            collectionNAME="users/"+uid;
            //wallet Changes
            if(MetaData.inwallet==true){
                uid=MetaData.walletName;
                collectionNAME="group/"+uid;
            }
            MetaData.incTotal = new HashMap<>();
            //for pieChart Colors
            MetaData.colorIncome=new ArrayList<>();

            final ArrayList<String> category = new ArrayList<>();
            category.add("Salary");
            category.add("Business");
            category.add("Loan");
            category.add("Parental Leave");
            category.add("Insurance Payout");
            category.add("Transfer-in");
            category.add("Others (Income)");

            String month_str = MetaData.setMonth(monthCurrently);

            mDocRef = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/"+month_str+yearCurrently);
            mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    for (int n = 0; n < category.size(); n++) {
                        if (documentSnapshot != null) {
                            String totalString = documentSnapshot.getString(category.get(n));

                            if (totalString == null) {
                                Double arr = MetaData.incTotal.get(n);
                                if (arr != null) {

                                } else {
                                    MetaData.incTotal.remove(category.get(n));
                                }
                            } else {
                                MetaData.incTotal.put(category.get(n), Double.parseDouble(totalString));
                                Log.i(category.get(n), totalString);
                                Log.i("HashMap2 ", String.valueOf(MetaData.incTotal));
                            }


                        }
                    }
                    Log.i("HashMap2final", String.valueOf(MetaData.incTotal));

                    //Update PieChart in Expenditure and Income Fragment
                    if (ExpenditureIncomeFragment.yValuesIncome != null) {
                        ExpenditureIncomeFragment.yValuesIncome.clear();
                        /*/
                        changes this part for colors to work
                        */
                        //for pie chart colors
                        //added
                        MetaData.colorIncome.clear();
                        ExpenditureIncomeFragment.colorIncome = new int[MetaData.incTotal.size()];

                        for (String key : MetaData.incTotal.keySet()) {
                            ExpenditureIncomeFragment.yValuesIncome.add(new PieEntry(Float.parseFloat(MetaData.incTotal.get(key).toString()), key));
                            MetaData.colorIncome.add(ExpenditureIncomeFragment.rgb(MetaData.chooseColor(key)));

                        }


                        ExpenditureIncomeFragment.dataSetIncome = new PieDataSet(ExpenditureIncomeFragment.yValuesIncome, "Countries");

                        ExpenditureIncomeFragment.dataSetIncome.setColors(convertIntegers(MetaData.colorIncome));
                        PieData data = new PieData(ExpenditureIncomeFragment.dataSetIncome);
                        data.setValueTextSize(10f);
                        data.setValueTextColor(Color.YELLOW);
                        ExpenditureIncomeFragment.pieChartIncome.getLegend().setEnabled(false);
                        ExpenditureIncomeFragment.pieChartIncome.setData(data);
                        //Ends here

//                        if (IncomeContentFragment.pieChartIncome != null) {
//                            IncomeContentFragment.pieChartIncome.notifyDataSetChanged();
//                        }
                        ExpenditureIncomeFragment.pieChartIncome.notifyDataSetChanged();
                    }
                }
            });
        }

    }
    // Signout User and return user to LoginActivity
    private void userLogout() {
        upDateSettings();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }



    /*public static void balanceMonthlyChangeFragment(){
        Calendar now = Calendar.getInstance();
        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};

        int value1 = now.get(Calendar.MONTH);
        String currMonth = monthName[value1];

        int value2 = now.get(Calendar.MONTH) - 1;
        if(value2 < 0){
            value2 = 12 + value2;
        }
        String prevMonth = monthName[value2];

        int currYear = now.get(Calendar.YEAR);
        int prevYear = now.get(Calendar.YEAR);

        if (currMonth.equals("Jan")) {
            prevYear -= 1;
        }

        ArrayList<String> temp = new ArrayList<String>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        uid = user.getUid();
        collectionNAME="users/"+uid;
        //wallet Changes
        if(MetaData.inwallet==true){
            uid=MetaData.walletName;
            collectionNAME="group/"+uid;
        }

        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData");
        Query IncomeQuery;
        if(lastQuiredIncome!=null){
            IncomeQuery = collectionRef.whereEqualTo("type","Income").startAfter(lastQuiredIncome);
        }else {
            IncomeQuery = collectionRef.whereEqualTo("type","Income");
        }
        Query ExpenseQuery;
        if(lastQuiredExpense!=null){
            ExpenseQuery= collectionRef.whereEqualTo("type","Expense").startAfter(lastQuiredExpense);
        }else {
            ExpenseQuery= collectionRef.whereEqualTo("type","Expense");
        }

        incomeArray2 = new ArrayList<Transaction>();
        expenseArray2 = new ArrayList<Transaction>();


        IncomeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    barEntries.clear();
                    barEntries2.clear();
                    barEntries4.clear();
                    //retrieving a list of documents by looping through the list by calling get results
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        Log.i("transaction", transaction.getAmount());
                        incomeArray.add(transaction);
                        Log.i("incomeArray", Integer.toString(incomeArray.size()));
                    }
                    if (task.getResult().size() != 0) {
                        lastQuiredIncome = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }
                } else {
                    Log.i("transaction", "none");
                }
            }
        });


        ExpenseQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    barEntries1.clear();
                    barEntries3.clear();
                    barEntries5.clear();
                    //retrieving a list of documents by looping through the list by calling get results
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        //Log.i("transaction", transaction.getAmount());
                        expenseArray.add(transaction);
                        //Log.i("incomeArray", Integer.toString(expenseArray.size()));
                        if (task.getResult().size() != 0) {
                            lastQuiredExpense = task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);
                        }
                    }
                } else {
                    Log.i("transaction", "none");
                }
            }
        });







    }*/

    public static void analytics(){
        barEntries.clear();
        barEntries1.clear();
        barEntries2.clear();
        barEntries3.clear();
        barEntries4.clear();
        barEntries5.clear();

        Calendar now = Calendar.getInstance();

        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};


        int value1 = now.get(Calendar.MONTH);
        currMonth = monthName[value1];

        int value2 = now.get(Calendar.MONTH) - 1;
        if(value2 < 0){
            value2 = 12 + value2;
        }
        prevMonth = monthName[value2];

        int value3 = now.get(Calendar.MONTH) - 2;
        if(value3 < 0){
            value3 = 12 + value3;
        }
        prev2Month = monthName[value3];

        int value4 = now.get(Calendar.MONTH) - 3;
        if(value4 < 0){
            value4 = 12 + value4;
        }
        prev3Month = monthName[value4];

        int value5 = now.get(Calendar.MONTH) - 4;
        if(value5 < 0){
            value5 = 12 + value5;
        }
        prev4Month = monthName[value5];

        int value6 = now.get(Calendar.MONTH) - 5;
        if(value6 < 0){
            value6 = 12 + value6;
        }
        prev5Month = monthName[value6];

        int value7 = now.get(Calendar.MONTH) - 6;
        if(value7 < 0){
            value7 = 12 + value7;
        }
        prev6Month = monthName[value7];

        int value8 = now.get(Calendar.MONTH) - 7;
        if(value8 < 0){
            value8 = 12 + value8;
        }
        prev7Month = monthName[value8];

        int value9 = now.get(Calendar.MONTH) - 8;
        if(value9 < 0){
            value9 = 12 + value9;
        }
        prev8Month = monthName[value9];

        int value10 = now.get(Calendar.MONTH) - 9;
        if(value10 < 0){
            value10 = 12 + value10;
        }
        prev9Month = monthName[value10];

        int value11 = now.get(Calendar.MONTH) - 10;
        if(value11 < 0){
            value11 = 12 + value11;
        }
        prev10Month = monthName[value11];

        int value12 = now.get(Calendar.MONTH) - 11;
        if(value12 < 0){
            value12 = 12 + value12;
        }
        prev11Month = monthName[value12];

        int currYear = now.get(Calendar.YEAR);
        int prevYear = now.get(Calendar.YEAR);
        int prev2Year = now.get(Calendar.YEAR);
        int prev3Year = now.get(Calendar.YEAR);
        int prev4Year = now.get(Calendar.YEAR);
        int prev5Year = now.get(Calendar.YEAR);
        int prev6Year = now.get(Calendar.YEAR);
        int prev7Year = now.get(Calendar.YEAR);
        int prev8Year = now.get(Calendar.YEAR);
        int prev9Year = now.get(Calendar.YEAR);
        int prev10Year = now.get(Calendar.YEAR);
        int prev11Year = now.get(Calendar.YEAR);


        if (currMonth.equals("Jan")) {
            prevYear -= 1;
            prev2Year -= 1;
            prev3Year -= 1;
            prev4Year -= 1;
            prev5Year -= 1;
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Feb")) {
            prev2Year -= 1;
            prev3Year -= 1;
            prev4Year -= 1;
            prev5Year -= 1;
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Mar")) {
            prev3Year -= 1;
            prev4Year -= 1;
            prev5Year -= 1;
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Apr")) {
            prev4Year -= 1;
            prev5Year -= 1;
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("May")) {
            prev5Year -= 1;
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Jun")) {
            prev6Year -= 1;
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Jul")) {
            prev7Year -= 1;
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Aug")) {
            prev8Year -= 1;
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Sep")) {
            prev9Year -= 1;
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Oct")) {
            prev10Year -= 1;
            prev11Year -= 1;

        } else if (currMonth.equals("Nov")) {
            prev11Year -= 1;

        }

        month3 = new ArrayList<String>();
        month6 = new ArrayList<String>();
        month12 = new ArrayList<String>();
        month3.add(prev2Month+prev2Year);
        month3.add(prevMonth+prevYear);
        month3.add(currMonth+currYear);

        month6.add(prev5Month+prev5Year);
        month6.add(prev4Month+prev4Year);
        month6.add(prev3Month+prev3Year);
        month6.add(prev2Month+prev2Year);
        month6.add(prevMonth+prevYear);
        month6.add(currMonth+currYear);

        month12.add(prev11Month+prev11Year);
        month12.add(prev10Month+prev10Year);
        month12.add(prev9Month+prev9Year);
        month12.add(prev8Month+prev8Year);
        month12.add(prev7Month+prev7Year);
        month12.add(prev6Month+prev6Year);
        month12.add(prev5Month+prev5Year);
        month12.add(prev4Month+prev4Year);
        month12.add(prev3Month+prev3Year);
        month12.add(prev2Month+prev2Year);
        month12.add(prevMonth+prevYear);
        month12.add(currMonth+currYear);


        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        uid = user.getUid();
        collectionNAME="users/"+uid;
        //wallet Changes
        if(MetaData.inwallet==true){
            uid=MetaData.walletName;
            collectionNAME="group/"+uid;
        }

        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData");
        Query IncomeQuery;
        if(lastQuiredIncome!=null){
            IncomeQuery = collectionRef.whereEqualTo("type","Income").startAfter(lastQuiredIncome);
        }else {
            IncomeQuery = collectionRef.whereEqualTo("type","Income");
        }
        Query ExpenseQuery;
        if(lastQuiredExpense!=null){
            ExpenseQuery= collectionRef.whereEqualTo("type","Expense").startAfter(lastQuiredExpense);
        }else {
            ExpenseQuery= collectionRef.whereEqualTo("type","Expense");
        }




        incomeArray = new ArrayList<Transaction>();
        expenseArray = new ArrayList<Transaction>();


        IncomeQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    barEntries.clear();
                    barEntries2.clear();
                    barEntries4.clear();
                    //retrieving a list of documents by looping through the list by calling get results
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        Log.i("transaction", transaction.getAmount());
                        incomeArray.add(transaction);
                        Log.i("incomeArray", Integer.toString(incomeArray.size()));
                    }
                    if (task.getResult().size() != 0) {
                        lastQuiredIncome = task.getResult().getDocuments()
                                .get(task.getResult().size() - 1);
                    }
                }else{
                    Log.i("transaction", "none");
                }

                int count = 1;

                for(String temp: month12){
                    double currAmt = 0.00;
                    for(Transaction curr: incomeArray){
                        String date = curr.getTimeStamp().toString().substring(4,7);
                        String year = curr.getTimeStamp().toString().substring(30,34);
                        String fulldate = date + year;
                        if(fulldate.equals(temp)){
                            currAmt += Double.parseDouble(curr.getAmount());
                            Log.i("curr amount1", curr.getAmount());
                        }
                    }

                    Log.i("current month", temp);
                    Log.i("total amount1", Double.toString(currAmt));
                    barEntries4.add(new BarEntry(count, (int)currAmt));
                    count++;
                }

                if(CashflowsFragment.barChart != null){
                    CashflowsFragment.barChart.invalidate();
                }
            }

            ;
        });


        ExpenseQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    barEntries1.clear();
                    barEntries3.clear();
                    barEntries5.clear();
                    //retrieving a list of documents by looping through the list by calling get results
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        //Log.i("transaction", transaction.getAmount());
                        expenseArray.add(transaction);
                        //Log.i("incomeArray", Integer.toString(expenseArray.size()));
                        if (task.getResult().size() != 0) {
                            lastQuiredExpense= task.getResult().getDocuments()
                                    .get(task.getResult().size() - 1);
                        }
                    }
                }else{
                    Log.i("transaction", "none");
                }

                int count = 1;

                for(String temp: month12){
                    double currAmt2 = 0.00;
                    for(Transaction curr: expenseArray){
                        String date2 = curr.getTimeStamp().toString().substring(4,7);
                        String year2 = curr.getTimeStamp().toString().substring(30,34);
                        String fulldate = date2 + year2;
                        if(fulldate.equals(temp)){
                            currAmt2 += Double.parseDouble(curr.getAmount());
                            Log.i("curr amount2", curr.getAmount());
                        }
                    }
                    Log.i("current month", temp);
                    Log.i("total amount2", Double.toString(currAmt2));
                    barEntries5.add(new BarEntry(count, (int)currAmt2));
                    count++;
                }

                if(CashflowsFragment.barChart != null){
                    CashflowsFragment.barChart.invalidate();
                }
            }

            ;
        });
    }
    //for colors
    public static int[] convertIntegers(List<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    @Override
    public void deleteBudgetMeter(){
        fragment3 = new BudgetMeterPageFragment();
        setFragment3(fragment3);
    }

    @Override
    public void editBudgetMeter(String category){
        MetaData.budgetCategory = category;
        fragment3 = new BudgetFragment();
        setFragment3(fragment3);

    }


    @Override
    public void editTransaction(RecentTransactionItem recentTransactionItem) {
        fragment3=new addTransactionFragment();
        MetaData.hereForEdit=true;
        MetaData.recentTransactionItemForEdit=recentTransactionItem;
        setFragment3(fragment3);
    }

    @Override
    public void createdWallet() {
        fragment3=new walletFragment();

        setFragment3(fragment3);

    }

    @Override
    public void editMembers() {
        fragment3=new CreateWalletFragment();

        setFragment3(fragment3);
    }

    public static void updateProfileBalance(){
        Calendar now = Calendar.getInstance();
        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};


        int value1 = now.get(Calendar.MONTH);
        String currMonth = monthName[value1];
        int currYear = now.get(Calendar.YEAR);

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String collectionNAME="users/"+userID;
        //wallet Changes
        if(MetaData.inwallet==true){
            userID=MetaData.walletName;
            collectionNAME="group/"+userID;
        }


        String monthYear = currMonth + currYear;
        DocumentReference mDocRef3=FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + monthYear);
        mDocRef3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String bankAcc = documentSnapshot.getString("Bank");
                    String cardAcc = documentSnapshot.getString("Card");
                    String cashAcc = documentSnapshot.getString("Cash");
                    if(bankAcc==null){
                        bankAcc = "0.00";
                    }
                    if(cardAcc==null){
                        cardAcc = "0.00";
                    }
                    if(cashAcc==null){
                        cashAcc = "0.00";
                    }

                    if(Double.parseDouble(bankAcc) < 0){
                        bankAcc = bankAcc.substring(0,1) + "$" + bankAcc.substring(1);
                        ProfilePageFragment.bankAmt.setText(bankAcc);
                        ProfilePageFragment.bankAmt.setTextColor(Color.rgb(194,24,91));

                    }else{
                        ProfilePageFragment.bankAmt.setText("$" + bankAcc);
                        ProfilePageFragment.bankAmt.setTextColor(Color.rgb(1,170,113));

                    }
                    if(Double.parseDouble(cardAcc) < 0){
                        cardAcc = cardAcc.substring(0,1) + "$" + cardAcc.substring(1);
                        ProfilePageFragment.cardAmt.setText(cardAcc);
                        ProfilePageFragment.cardAmt.setTextColor(Color.rgb(194,24,91));

                    }else{
                        ProfilePageFragment.cardAmt.setText("$" + cardAcc);
                        ProfilePageFragment.cardAmt.setTextColor(Color.rgb(1,170,113));

                    }
                    if(Double.parseDouble(cashAcc) < 0){
                        cashAcc = cashAcc.substring(0,1) + "$" + cashAcc.substring(1);
                        ProfilePageFragment.cashAmt.setText(cashAcc);
                        ProfilePageFragment.cashAmt.setTextColor(Color.rgb(194,24,91));

                    }else{
                        ProfilePageFragment.cashAmt.setText("$" + cashAcc);
                        ProfilePageFragment.cashAmt.setTextColor(Color.rgb(1,170,113));

                    }

                }else{

                    ProfilePageFragment.cashAmt.setText("$0.00");
                    ProfilePageFragment.bankAmt.setText("$0.00");
                    ProfilePageFragment.cardAmt.setText("$0.00");


                }
            }
        });

    }


    public static void updateOverviewBalance(){
        Calendar now = Calendar.getInstance();
        String[] monthName = {"Jan", "Feb",
                "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov",
                "Dec"};


        int value1 = now.get(Calendar.MONTH);
        String currMonth = monthName[value1];
        int currYear = now.get(Calendar.YEAR);

        int value2 = now.get(Calendar.MONTH) - 1;
        if(value2 < 0){
            value2 = 12 + value2;
        }

        String prevMonth = monthName[value2];
        int prevYear = now.get(Calendar.YEAR);
        if (currMonth.equals("Jan")) {
            prevYear -= 1;
        }

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String collectionNAME="users/"+userID;
        //wallet Changes
        if(MetaData.inwallet==true){
            userID=MetaData.walletName;
            collectionNAME="group/"+userID;
        }


        String monthYear = currMonth + currYear;
        final String prevmonthYear = prevMonth + prevYear;


        DocumentReference mDocRef3=FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + monthYear);
        final DocumentReference mDocRef4=FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + prevmonthYear);
        mDocRef3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String bankAcc = documentSnapshot.getString("Bank");
                    String cardAcc = documentSnapshot.getString("Card");
                    String cashAcc = documentSnapshot.getString("Cash");
                    if(bankAcc==null){
                        bankAcc = "0.00";
                    }
                    if(cardAcc==null){
                        cardAcc = "0.00";
                    }
                    if(cashAcc==null){
                        cashAcc = "0.00";
                    }

                    currBank = bankAcc;
                    currCard = cardAcc;
                    currCash = cashAcc;

                    if(Double.parseDouble(bankAcc) < 0){
                        BalanceMonthlyChangeFragment.bankAmt.setText(bankAcc.substring(0,1) + "$" + bankAcc.substring(1));
                        BalanceMonthlyChangeFragment.bankAmt.setTextColor(Color.rgb(194,24,91));
                    }else{
                        BalanceMonthlyChangeFragment.bankAmt.setText("$" + bankAcc);
                        BalanceMonthlyChangeFragment.bankAmt.setTextColor(Color.rgb(1,170,113));
                    }
                    if(Double.parseDouble(cardAcc) < 0){
                        BalanceMonthlyChangeFragment.creditAmt.setText(cardAcc.substring(0,1) + "$" + cardAcc.substring(1));
                        BalanceMonthlyChangeFragment.creditAmt.setTextColor(Color.rgb(194,24,91));
                    }else{
                        BalanceMonthlyChangeFragment.creditAmt.setText("$" + cardAcc);
                        BalanceMonthlyChangeFragment.creditAmt.setTextColor(Color.rgb(1,170,113));
                    }
                    if(Double.parseDouble(cashAcc) < 0){
                        BalanceMonthlyChangeFragment.cashAmt.setText(cashAcc.substring(0,1) + "$" + cashAcc.substring(1));
                        BalanceMonthlyChangeFragment.cashAmt.setTextColor(Color.rgb(194,24,91));
                    }else{
                        BalanceMonthlyChangeFragment.cashAmt.setText("$" + cashAcc);
                        BalanceMonthlyChangeFragment.cashAmt.setTextColor(Color.rgb(1,170,113));
                    }

                }else{
                    BalanceMonthlyChangeFragment.bankAmt.setText("$0.00");
                    BalanceMonthlyChangeFragment.creditAmt.setText("$0.00");
                    BalanceMonthlyChangeFragment.cashAmt.setText("$0.00");
                    currBank = "0.00";
                    currCard = "0.00";
                    currCash = "0.00";
                }
                mDocRef4.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String bankAcc = documentSnapshot.getString("Bank");
                            String cardAcc = documentSnapshot.getString("Card");
                            String cashAcc = documentSnapshot.getString("Cash");
                            if(bankAcc==null){
                                bankAcc = "0.00";
                            }
                            if(cardAcc==null){
                                cardAcc = "0.00";
                            }
                            if(cashAcc==null){
                                cashAcc = "0.00";
                            }

                            prevBank = bankAcc;
                            prevCard = cardAcc;
                            prevCash = cashAcc;

                        }else{
                            prevBank = "0.00";
                            prevCard = "0.00";
                            prevCash = "0.00";
                        }

                        double bankChange = Double.parseDouble(currBank) - Double.parseDouble(prevBank);
                        double cardChange = Double.parseDouble(currCard) - Double.parseDouble(prevCard);
                        double cashChange = Double.parseDouble(currCash) - Double.parseDouble(prevCash);

                        if(bankChange < 0) {
                            BalanceMonthlyChangeFragment.bankChangeAmt.setText(String.valueOf(bankChange).substring(0, 1) + "$" + String.valueOf(bankChange).substring(1));
                            BalanceMonthlyChangeFragment.bankChangeAmt.setTextColor(Color.rgb(194, 24, 91));
                        }else if(bankChange == 0){
                            BalanceMonthlyChangeFragment.bankChangeAmt.setText("$" + String.valueOf(bankChange) + "0");
                            BalanceMonthlyChangeFragment.bankChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }else{
                            BalanceMonthlyChangeFragment.bankChangeAmt.setText("$" + String.valueOf(bankChange));
                            BalanceMonthlyChangeFragment.bankChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }
                        if(cardChange < 0){
                            BalanceMonthlyChangeFragment.creditChangeAmt.setText(String.valueOf(cardChange).substring(0,1) + "$" + String.valueOf(cardChange).substring(1));
                            BalanceMonthlyChangeFragment.creditChangeAmt.setTextColor(Color.rgb(194,24,91));
                        }else if(cardChange == 0){
                            BalanceMonthlyChangeFragment.creditChangeAmt.setText("$" + String.valueOf(cardChange) + "0");
                            BalanceMonthlyChangeFragment.creditChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }else{
                            BalanceMonthlyChangeFragment.creditChangeAmt.setText("$" + String.valueOf(cardChange));
                            BalanceMonthlyChangeFragment.creditChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }
                        if(cashChange < 0){
                            BalanceMonthlyChangeFragment.cashChangeAmt.setText(String.valueOf(cashChange).substring(0,1) + "$" + String.valueOf(cashChange).substring(1));
                            BalanceMonthlyChangeFragment.cashChangeAmt.setTextColor(Color.rgb(194,24,91));
                        }else if(cashChange == 0){
                            BalanceMonthlyChangeFragment.cashChangeAmt.setText("$" + String.valueOf(cashChange) + "0");;
                            BalanceMonthlyChangeFragment.cashChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }else{
                            BalanceMonthlyChangeFragment.cashChangeAmt.setText("$" + String.valueOf(cashChange));
                            BalanceMonthlyChangeFragment.cashChangeAmt.setTextColor(Color.rgb(1,170,113));
                        }

                    }
                });

            }
        });

    }
    @Override
    public void walletInfo() {
        fragment3=new CreateWalletFragment();

        setFragment3(fragment3);
    }

    public static void updateBudgetMeters() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
        }else {
            uid = user.getUid();
            collectionNAME = "users/" + uid;
            //wallet Changes
            if (MetaData.inwallet == true) {
                uid = MetaData.walletName;
                collectionNAME = "group/" + uid;
            }
            // obtain data from budget
            DocumentReference documentReferenceBudget = FirebaseFirestore.getInstance().document(collectionNAME + "/budgetData/budgetDataDocument");
            documentReferenceBudget.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.i("Document Exists", "Yes");
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            MetaData.budgetMeterItemBudgetListFull.clear();
                            for (String category : data.keySet()) {
                                Log.i("Category", category);
                                BudgetMeterItemBudget budgetMeterItemBudget = new BudgetMeterItemBudget(category, document.getString(category).toString());
                                MetaData.budgetMeterItemBudgetListFull.add(budgetMeterItemBudget);
                            }
                        }
                    }
                }
            });
            // obtain data from metadata (current expenditure)
            String month = MetaData.setMonth(monthCurrently);
            String year = Integer.toString(yearCurrently);
            DocumentReference documentReferenceProgress = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
            documentReferenceProgress.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        MetaData.budgetMeterItemProgresslistFull.clear();
                        MetaData.budgetMeterPercentageMap.clear();
                        for (BudgetMeterItemBudget budgetMeterItemBudget : MetaData.budgetMeterItemBudgetListFull) {
                            String category = budgetMeterItemBudget.getCategory();
                            Log.i("Category Progress", category);
                            String currentSpending = "0";
                            if (document.getString(category) != null) {
                                currentSpending = document.getString(category).toString();
                            }
                            // calculate the percentages
                            Double percentage = Double.parseDouble(currentSpending) / Double.parseDouble(budgetMeterItemBudget.getCurrentBudget());
                            // insert the respective category and percentage into hashmap
                            MetaData.budgetMeterPercentageMap.put(category, percentage);
                            BudgetMeterItemProgress budgetMeterItemProgress = new BudgetMeterItemProgress(category, currentSpending);
                            MetaData.budgetMeterItemProgresslistFull.add(budgetMeterItemProgress);
                        }
                    } else {
                        MetaData.budgetMeterItemProgresslistFull.clear();
                        MetaData.budgetMeterPercentageMap.clear();
                        for (BudgetMeterItemBudget budgetMeterItemBudget : MetaData.budgetMeterItemBudgetListFull) {
                            String category = budgetMeterItemBudget.getCategory();
                            Log.i("Category Progress", category);
                            String currentSpending = "0";
                            BudgetMeterItemProgress budgetMeterItemProgress = new BudgetMeterItemProgress(category, currentSpending);
                            // calculate the percentages
                            Double percentage = Double.parseDouble(currentSpending) / Double.parseDouble(budgetMeterItemBudget.getCurrentBudget());
                            // insert the respective category and percentage into hashmap
                            MetaData.budgetMeterPercentageMap.put(category, percentage);
                            MetaData.budgetMeterItemProgresslistFull.add(budgetMeterItemProgress);
                        }
                    }
                }
            });
            // sort the budget meter percentages in descending order
            if (MetaData.budgetMeterPercentageMap != null) {
                Object[] percentages = MetaData.budgetMeterPercentageMap.entrySet().toArray();
                Arrays.sort(percentages, new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        return ((Map.Entry<String, Double>) o2).getValue()
                                .compareTo(((Map.Entry<String, Double>) o1).getValue());
                    }
                });
                // populate
                int counter = 0;
                MetaData.budgetMeterItemBudgetListTop3.clear();
                MetaData.budgetMeterItemProgressListTop3.clear();
                for (Object percentage : percentages) {
                    if (counter == 3) {
                        break;
                    }
                    final String category = ((Map.Entry<String, Double>) percentage).getKey();
                    // populate budgetMeterItemBudgetListTop3
                    DocumentReference mDocRef = FirebaseFirestore.getInstance().document(collectionNAME + "/budgetData/budgetDataDocument");
                    mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                Log.i("Document Exists", "Yes");
                                if (document.getString(category) != null) {
                                    BudgetMeterItemBudget budgetMeterItemBudget = new BudgetMeterItemBudget(category, document.getString(category).toString());
                                    MetaData.budgetMeterItemBudgetListTop3.add(budgetMeterItemBudget);
                                }
                            }
                        }
                    });
                    // populate budgetMeterItemProgressListTop3
                    DocumentReference mDocRef2 = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
                    mDocRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                String currentSpending = "0";
                                if (document.getString(category) != null) {
                                    currentSpending = document.getString(category).toString();
                                }
                                BudgetMeterItemProgress budgetMeterItemProgress = new BudgetMeterItemProgress(category, currentSpending);
                                MetaData.budgetMeterItemProgressListTop3.add(budgetMeterItemProgress);
                            } else {
                                String currentSpending = "0";
                                BudgetMeterItemProgress budgetMeterItemProgress = new BudgetMeterItemProgress(category, currentSpending);
                                MetaData.budgetMeterItemProgressListTop3.add(budgetMeterItemProgress);
                            }
                        }
                    });
                    counter++;
                }
                // sort the the top 3 budget meters by percentage values
//            for (BudgetMeterItemBudget budgetMeterItemBudget : MetaData.budgetMeterItemBudgetListFull){
//
//            }
//            MetaData.budgetMeterItemBudgetListTop3;
//            MetaData.budgetMeterItemProgressListTop3;
            }
        }
        if (BudgetMeterPageFragment.budgetMeterViewAdapter != null)
            BudgetMeterPageFragment.budgetMeterViewAdapter.notifyDataSetChanged();
        if (BudgetMeterFragment.budgetMeterViewAdapter != null)
            BudgetMeterFragment.budgetMeterViewAdapter.notifyDataSetChanged();
    }




}

