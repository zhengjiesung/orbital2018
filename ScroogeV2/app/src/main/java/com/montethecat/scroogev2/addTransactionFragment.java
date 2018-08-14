package com.montethecat.scroogev2;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import io.grpc.okhttp.internal.Util;

public class addTransactionFragment extends Fragment {

    static EditText amount;
    TextView dateView;
    TextView timeView;
    Spinner category;
    Spinner type;
    EditText description;
    EditText name;
    Button income,expense,edit;
    TextView incomeExpenditure;
    ImageView addTransactionSpinnerImg;
    private FirebaseAuth auth;
    protected DocumentReference mDocRef;
    DocumentReference mDocRef3;
    String timePart;
    String collectionNAME;
    public static boolean timeSet=false;
    //ArrayList<String> expenseArray = new ArrayList<String>();
    //ArrayList<String> incomeArray = new ArrayList<String>();

    String oldDate;
    String oldTransactionMode;
    String oldExpenseOrIncome;
    String oldAmt;


    Calendar now = Calendar.getInstance();

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_add_transaction_2,container,false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add Transaction");
        final Calendar c = Calendar.getInstance(TimeZone.getDefault());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int hour=c.get(Calendar.HOUR);
        final int minute=c.get(Calendar.MINUTE);

        //If nothing was selected from date picker
        //Plus one is necessary for month to be sent correctly to database
        MetaData.date=year+"-"+(month+1)+"-"+day+"T";
        dateView = (TextView) view.findViewById(R.id.dateTextView);
        timeView = (TextView) view.findViewById(R.id.timeTextView);

        income = (Button) view.findViewById(R.id.buttonIncome);
        expense = (Button) view.findViewById(R.id.buttonExpense);
        edit=(Button)view.findViewById(R.id.buttonEdit);
        amount = (EditText) view.findViewById(R.id.amount);
        category = (Spinner) view.findViewById(R.id.category);
        type=(Spinner)view.findViewById(R.id.type);
        description = (EditText) view.findViewById(R.id.description);
        name = (EditText) view.findViewById(R.id.editTextName);
        incomeExpenditure=(TextView) view.findViewById(R.id.incomeExpenditure);
        addTransactionSpinnerImg=(ImageView) view.findViewById(R.id.addTransactionSpinnerImg);

        final String[] categorySpinner = new String[]{"Choose Category",
                "Food and Drinks",
                "Shopping",
                "Housing",
                "Transport",
                "Vehicle",
                "Life & Entertainment",
                "Communications,PC",
                "Financial Expenses",
                "Investment",
                "Education",
                "Insurance Payment",
                "Transfer-out",
                "Others (Expenditure)"};
        final String[] incomeSpinner=new String[]{"Choose Category",
                "Salary",
                "Business",
                "Loan",
                "Parental Leave",
                "Insurance Payout",
                "Transfer-in",
                "Others (Income)"};

        //expenseArray.addAll(Arrays.asList(categorySpinner));
        //incomeArray.addAll(Arrays.asList(incomeSpinner));

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
        //set the spinners adapter to the previously created one.
        category.setAdapter(adapter);
        final String[] typeSpinner = new String[]{"Cash",
                "Card",
                "Bank"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, typeSpinner);
        //set the spinners adapter to the previously created one.
        type.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();


        dateView.setText(Integer.toString(day) + MetaData.setMonth(month) +Integer.toString(year));
        timeView.setText("--");
        //For Edit Delete Transaction
        //if User is here for Editing, all Selections will already be preset for them
        if(MetaData.hereForEdit==true){
            oldTransactionMode = MetaData.recentTransactionItemForEdit.getRecetTransactionMode();
            oldExpenseOrIncome = MetaData.recentTransactionItemForEdit.getRecentTransactionIncOrExp();
            oldAmt = MetaData.recentTransactionItemForEdit.getRecentTansactionCost();

            String unBrokenDate=MetaData.recentTransactionItemForEdit.getRecentTransactionDate();
            String[] dateParts=unBrokenDate.split(" ");
            String monthPart=dateParts[2];
            String dayPart=dateParts[3];
            timePart=dateParts[4];
            String yearPart=dateParts[0];
            dateView.setText(dayPart+monthPart+yearPart);

            oldDate = monthPart + yearPart;

            timeView.setText(timePart);
            amount.setText(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
            description.setText(MetaData.recentTransactionItemForEdit.getRecentTransactionDescription());
            name.setText(MetaData.recentTransactionItemForEdit.getRecentTransactionName());
            for(int n=0;n<typeSpinner.length;n++){
                Log.i("Barnabas Check",typeSpinner[n]+" "+MetaData.recentTransactionItemForEdit.getRecetTransactionMode());
                if(typeSpinner[n].equals(MetaData.recentTransactionItemForEdit.getRecetTransactionMode())){
                    type.setSelection(n);
                }
            }
           // Log.i("Barnabas CHeck Here",MetaData.recentTransactionItemForEdit.getRecentTransactionIncOrExp().toString());

            String[] whichArray;
            if (MetaData.recentTransactionItemForEdit.getRecentTransactionIncOrExp().equals("Income")){
                incomeExpenditure.setText("Income");
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, incomeSpinner);
                //set the spinners adapter to the previously created one.
                category.setAdapter(adapter);
                income.setVisibility(View.GONE);
                expense.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);

                whichArray=incomeSpinner;
            }else {
                incomeExpenditure.setText("Expenditure");
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
                //set the spinners adapter to the previously created one.
                category.setAdapter(adapter);
                income.setVisibility(View.GONE);
                expense.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                whichArray=categorySpinner;
            }


            for(int n=0;n<whichArray.length;n++){

                if(whichArray[n].equals(MetaData.recentTransactionItemForEdit.getRecentTransactionType())){
                    category.setSelection(n);
                    //Log.i("Barnabas Check2",whichArray[n]+" "+MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                }
            }


            HashMap<String, String> monthChanger = new HashMap<>();
            monthChanger.put("Jan", "1");
            monthChanger.put("Feb", "2");
            monthChanger.put("Mar", "3");
            monthChanger.put("Apr", "4");
            monthChanger.put("May", "5");
            monthChanger.put("jun", "6");
            monthChanger.put("Jul", "7");
            monthChanger.put("Aug", "8");
            monthChanger.put("Sep", "9");
            monthChanger.put("Oct", "10");
            monthChanger.put("Nov", "11");
            monthChanger.put("Dec", "12");


            for (String key : monthChanger.keySet()) {

                if (key.equals(monthPart)) {
                    monthPart = monthChanger.get(key);
                }
            }

            MetaData.date=yearPart+"-"+monthPart+"-"+dayPart+"T";

        }
        dateView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                MetaData.date="";
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(), "date picker");
            }
        });

        timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getFragmentManager(), "time picker");

            }
        });



        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addTransactionSpinnerImg.setImageResource(MetaData.chooseImage(category.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //For Edit Delete Transaction
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetaData.hereForEdit=false;

                if (timeView.getText()=="--"&&timeSet!=true){
                    if(hour<10){
                        if(minute<10){
                            MetaData.date+="0"+hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+="0"+hour+":"+minute+":00Z";
                        }
                    }else {
                        if(minute<10){
                            MetaData.date+=hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+=hour+":"+minute+":00Z";
                        }
                    }

                }else {
                    MetaData.date+=timePart+":00Z";
                }
                timeSet=false;
                Log.i("Date",MetaData.date);
                if(checkForm()==true){
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    collectionNAME="users/"+userID;
                    //wallet Changes
                    if(MetaData.inwallet==true){
                        userID=MetaData.walletName;
                        collectionNAME="group/"+userID;
                    }
                    /*Good TO Stay*/
                    mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData").document(MetaData.recentTransactionItemForEdit.getRecentTransactionID());

                    Transaction transaction = new Transaction();
                    Double doubleAmount= Double.parseDouble(amount.getText().toString());

                    transaction.setAmount(MetaData.df.format(doubleAmount));
                    transaction.setCategory(category.getSelectedItem().toString());

                    transaction.setTime(timeView.getText().toString());
                    transaction.setDescription(description.getText().toString());
                    transaction.setTimeStamp(getDateFromString(MetaData.date));
                    transaction.setAccount(type.getSelectedItem().toString());
                    transaction.setName(name.getText().toString());
                    //Barnabas Transaction_id
                    transaction.setTransaction_id(mDocRef.getId());




                    String incomeExpenditureString="Expense";
                    if(incomeExpenditure.getText().toString().equals("Expenditure")){
                        incomeExpenditureString="Expense";
                    }else {
                        incomeExpenditureString="Income";
                    }
                    transaction.setType(incomeExpenditureString);
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);


                    String month = transaction.getTimeStamp().toString().substring(4, 7);
                    String year = transaction.getTimeStamp().toString().substring(30, 34);

                    mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData").document(MetaData.recentTransactionItemForEdit.getRecentTransactionID());

                    mDocRef.set(transaction,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Created new Transaction", Toast.LENGTH_SHORT).show();
                                MainActivity.updateBudgetMeters();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                        MainActivity.overallScreenArea.setVisibility(View.GONE);
                                    }
                                }, 1500);

                            } else {
                                Toast.makeText(getActivity(), "Failed. Check Log", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Jeremy Stuff
                    final String newTransactionMode = transaction.getAccount();
                    final String newExpenseOrIncome = transaction.getType();
                    final String newAmt = transaction.getAmount();
                    final String newDate = transaction.getTimeStamp().toString().substring(4, 7) + transaction.getTimeStamp().toString().substring(30, 34);



                        mDocRef3 = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + oldDate);
                        mDocRef3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, String> dataToSave = new HashMap<String, String>();
                                double total = 0.00;
                                double totalForNewItem = 0.00;
                                if (documentSnapshot.exists()) {
                                    String totalString = documentSnapshot.getString(oldTransactionMode);
                                    if (oldExpenseOrIncome.equals("Income")) {
                                        totalString = String.valueOf(Double.parseDouble(totalString) - Double.parseDouble(oldAmt));
                                    } else {
                                        totalString = String.valueOf(Double.parseDouble(totalString) + Double.parseDouble(oldAmt));
                                    }

                                    dataToSave.put(oldTransactionMode, totalString);
                                    mDocRef3.set(dataToSave, SetOptions.merge());


                                }

                            }
                        });
                    //Jeremy Stuff



                        mDocRef=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
                        final String monthYear=month + year;
                        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                                //added this
                                Map<String, String> dataToSave=new HashMap<String, String>();
                                double totalForNewItem=0.00;
                                if(documentSnapshot.exists()){
                                    String totalStringForNewItem=documentSnapshot.getString(category.getSelectedItem().toString());

                                    if (totalStringForNewItem == null) {
                                        totalStringForNewItem = "0";
                                    }
                                    totalForNewItem = Double.parseDouble(totalStringForNewItem);
                                    totalForNewItem += Double.parseDouble(amount.getText().toString());
                                    dataToSave.put(category.getSelectedItem().toString(), String.valueOf(totalForNewItem));
                                    dataToSave.put("doc Name",monthYear);
                                    //Jeremy Stuff
                                    //update folder in newDate
                                    String currAmt = documentSnapshot.getString(newTransactionMode);
                                    if(currAmt==null){
                                        currAmt = "0";
                                    }
                                    if(newExpenseOrIncome.equals("Income")){
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) + Double.parseDouble(newAmt));
                                    }else{
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) - Double.parseDouble(newAmt));
                                    }
                                    dataToSave.put(newTransactionMode,currAmt);
                                    //Jeremy Stuff
                                    mDocRef.set(dataToSave,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + oldDate);
                                            mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, String> dataToSave=new HashMap<String, String>();
                                                    double total = 0.00;
                                                    String totalString=documentSnapshot.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                                    if (totalString==null){
                                                        totalString="0";
                                                    }
                                                    total=Double.parseDouble(totalString )-Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                                    dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(),String.valueOf(total));

                                                    mDocRef2.set(dataToSave,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Zheng Jie Move to Msin Psge here
                                                        }
                                                    });


                                                }
                                            });
                                        }
                                    });//added this Ends here
                                //Comment Out
                                /*Map<String, String> dataToSave=new HashMap<String, String>();
                                double total = 0.00;
                                double totalForNewItem=0.00;
                                if(documentSnapshot.exists()){
                                    String currAmt = documentSnapshot.getString(newTransactionMode);
                                    if(currAmt==null){
                                        currAmt = "0";
                                    }
                                    if(newExpenseOrIncome.equals("Income")){
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) + Double.parseDouble(newAmt));
                                    }else{
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) - Double.parseDouble(newAmt));
                                    }
                                    dataToSave.put(newTransactionMode,currAmt);
                                    String totalString=documentSnapshot.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                    String totalStringForNewItem=documentSnapshot.getString(category.getSelectedItem().toString());
                                    //if category has been changed
                                    //totalStringForNewItem is for the new category item
                                    if(!category.getSelectedItem().toString().equals(MetaData.recentTransactionItemForEdit.getRecentTransactionType())) {
                                        if (totalStringForNewItem == null) {
                                            totalStringForNewItem = "0";
                                        }
                                        totalForNewItem = Double.parseDouble(totalStringForNewItem);
                                        totalForNewItem += Double.parseDouble(amount.getText().toString());
                                        dataToSave.put(category.getSelectedItem().toString(), String.valueOf(totalForNewItem));
                                    }

                                    if (totalString==null){
                                        totalString="0";
                                    }

                                    Log.i("Failed",totalString);
                                    total=Double.parseDouble(totalString );
                                    //if the category has been changed just minus off the old category value
                                    //else if category not changed added to old category the new value then minus old value
                                    Log.i("Failed2",category.getSelectedItem().toString()+"\n"+MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                    if(!category.getSelectedItem().toString().equals(MetaData.recentTransactionItemForEdit.getRecentTransactionType())) {
                                        Log.i("Failed3","Entered");
                                        total -= Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                    }else {
                                        if(newDate.equals(oldDate)){
                                            total += Double.parseDouble(amount.getText().toString())-Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                        }else{
                                            total += Double.parseDouble(amount.getText().toString());
                                            final DocumentReference mDocRef2 = FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + oldDate);
                                            mDocRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        HashMap<String,String> dataToSave = new HashMap<String,String>();
                                                        DocumentSnapshot document = task.getResult();
                                                        String total = "0";
                                                        if(document.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType())!=null){
                                                            total = document.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                                        }
                                                        total = String.valueOf(Double.parseDouble(total) - Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost()));


                                                        *//*String currAmt = documentSnapshot.getString(oldTransactionMode);
                                                        if(currAmt==null){
                                                            Log.i("fuck222","Barney");
                                                            currAmt = "0";
                                                        }
                                                        Log.i("fuck222",currAmt);
                                                        if(oldExpenseOrIncome.equals("Income")){
                                                            currAmt = String.valueOf(Double.parseDouble(currAmt) - Double.parseDouble(oldAmt));
                                                            Log.i("old amt",oldAmt);
                                                        }else{
                                                            currAmt = String.valueOf(Double.parseDouble(currAmt) + Double.parseDouble(oldAmt));
                                                            Log.i("old amt",oldAmt);
                                                        }
                                                        dataToSave.put(oldTransactionMode,currAmt);*//*
                                                        dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(),total);
                                                        mDocRef2.set(dataToSave,SetOptions.merge());
                                                    }


                                                }
                                            });

                                        }

                                    }



                                    dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(),String.valueOf(total));
                                    dataToSave.put("doc Name",monthYear);
                                    mDocRef.set(dataToSave,SetOptions.merge());*/
                                //Comment Out Ends here Barnabas
                                }else{

                                //Added this here Barnabas
                                    totalForNewItem+=Double.parseDouble(amount.getText().toString());
                                    dataToSave.put("doc Name",monthYear);
                                    dataToSave.put(category.getSelectedItem().toString(),String.valueOf(totalForNewItem));
                                    //Jeremy Stuff
                                    //update folder in newDate
                                    String currAmt = documentSnapshot.getString(newTransactionMode);
                                    if(currAmt==null){
                                        currAmt = "0";
                                    }
                                    if(newExpenseOrIncome.equals("Income")){
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) + Double.parseDouble(newAmt));
                                    }else{
                                        currAmt = String.valueOf(Double.parseDouble(currAmt) - Double.parseDouble(newAmt));
                                    }
                                    dataToSave.put(newTransactionMode,currAmt);
                                    //Jeremy Stuff
                                    mDocRef.set(dataToSave,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + oldDate);
                                            mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, String> dataToSave=new HashMap<String, String>();
                                                    double total = 0.00;
                                                    String totalString=documentSnapshot.getString(MetaData.recentTransactionItemForEdit.getRecentTransactionType());
                                                    if (totalString==null){
                                                        totalString="0";
                                                    }
                                                    total=Double.parseDouble(totalString )-Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                                    dataToSave.put(MetaData.recentTransactionItemForEdit.getRecentTransactionType(),String.valueOf(total));
                                                    mDocRef2.set(dataToSave,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Zheng Jie Move to Msin Psge here
                                                        }
                                                    });


                                                }
                                            });

                                        }
                                    });
                                    //Added this Ends here Barnabas
                                                    //Comment Out here
                                    /*total+=Double.parseDouble(amount.getText().toString())-Double.parseDouble(MetaData.recentTransactionItemForEdit.getRecentTansactionCost());
                                    dataToSave.put("doc Name",monthYear);
                                    dataToSave.put(category.getSelectedItem().toString(),String.valueOf(total));
                                    mDocRef.set(dataToSave,SetOptions.merge());*/
                                    //Comment Out here End here
                                }
                                MainActivity.lastQuiredIncome=null;
                                MainActivity.lastQuiredExpense=null;
                                MainActivity.analytics();

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                        MainActivity.overallScreenArea.setVisibility(View.GONE);
                                    }
                                }, 1500);
                            }
                        });


                }

            }
        });

        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeView.getText()=="--"||timeSet!=true){
                    if(hour<10){
                        if(minute<10){
                            MetaData.date+="0"+hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+="0"+hour+":"+minute+":00Z";
                        }
                    }else {
                        if(minute<10){
                            MetaData.date+=hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+=hour+":"+minute+":00Z";
                        }
                    }

                }
                timeSet=false;
                Log.i("Date",MetaData.date);
                if(checkForm()==true){
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    collectionNAME="users/"+userID;
                    //wallet Changes
                    if(MetaData.inwallet==true){
                        userID=MetaData.walletName;
                        collectionNAME="group/"+userID;
                    }
                mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/transactionData").document();

                Transaction transaction = new Transaction();
                Double doubleAmount= Double.parseDouble(amount.getText().toString());

                transaction.setAmount(MetaData.df.format(doubleAmount));
                transaction.setCategory(category.getSelectedItem().toString());

                transaction.setTime(timeView.getText().toString());
                transaction.setDescription(description.getText().toString());
                transaction.setTimeStamp(getDateFromString(MetaData.date));
                transaction.setAccount(type.getSelectedItem().toString());
                transaction.setName(name.getText().toString());
                //Barnabas Transaction_id
                transaction.setTransaction_id(mDocRef.getId());
                transaction.setType("Expense");
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String month = transaction.getTimeStamp().toString().substring(4, 7);
                String year = transaction.getTimeStamp().toString().substring(30, 34);

                mDocRef.set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Created new Transaction", Toast.LENGTH_SHORT).show();

                            MainActivity.updateBudgetMeters();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                    MainActivity.overallScreenArea.setVisibility(View.GONE);
                                }
                            }, 1500);

                            } else {
                            Toast.makeText(getActivity(), "Failed. Check Log", Toast.LENGTH_SHORT).show();
                            }
                    }
                });
                    mDocRef=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
                    final String monthYear=month + year;
                    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, String> dataToSave=new HashMap<String, String>();
                           double total = 0.00;
                           double total2 = 0.00; //for balance account
                            if(documentSnapshot.exists()){
                                String totalString=documentSnapshot.getString(category.getSelectedItem().toString());
                                String typeString = documentSnapshot.getString(type.getSelectedItem().toString());
                                if (totalString==null){
                                    totalString="0";
                                }
                                if(typeString==null){
                                    typeString="0";
                                }
                                Log.i("Failed",totalString);
                                total=Double.parseDouble(totalString );
                                total2=Double.parseDouble(typeString );

                                total+=Double.parseDouble(amount.getText().toString());
                                total2-=Double.parseDouble(amount.getText().toString());

                                dataToSave.put(category.getSelectedItem().toString(),String.valueOf(total));
                                dataToSave.put(type.getSelectedItem().toString(),String.valueOf(total2));
                                dataToSave.put("doc Name",monthYear);
                                mDocRef.set(dataToSave,SetOptions.merge());
                            }else{
                                total+=Double.parseDouble(amount.getText().toString());
                                total2-=Double.parseDouble(amount.getText().toString());
                                dataToSave.put(category.getSelectedItem().toString(),String.valueOf(total));
                                dataToSave.put(type.getSelectedItem().toString(),String.valueOf(total2));
                                dataToSave.put("doc Name",monthYear);
                                mDocRef.set(dataToSave,SetOptions.merge());
                            }
                            MainActivity.lastQuiredIncome=null;
                            MainActivity.lastQuiredExpense=null;
                            MainActivity.analytics();
                            MainActivity.updateOverviewBalance();
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
                }
            }
    });
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeView.getText()=="--"||timeSet!=true){
                    if(hour<10){
                        if(minute<10){
                            MetaData.date+="0"+hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+="0"+hour+":"+minute+":00Z";
                        }
                    }else {
                        if(minute<10){
                            MetaData.date+=hour+":0"+minute+":00Z";
                        }else{
                            MetaData.date+=hour+":"+minute+":00Z";
                        }
                    }

                }
                timeSet=false;
                Log.i("Date",MetaData.date);
                if(checkForm()==true){
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    collectionNAME="users/"+userID;
                    //wallet Changes
                    if(MetaData.inwallet==true){
                        userID=MetaData.walletName;
                        collectionNAME="group/"+userID;
                    }
                mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/transactionData").document();
                Transaction transaction = new Transaction();

                transaction.setAmount(amount.getText().toString());
                transaction.setCategory(category.getSelectedItem().toString());
                transaction.setDate(dateView.getText().toString());

                transaction.setTimeStamp(getDateFromString(MetaData.date));
                transaction.setDescription(description.getText().toString());
                transaction.setType("Income");
                transaction.setAccount(type.getSelectedItem().toString());
                transaction.setName(name.getText().toString());
                //Barnabas Transaction_id
                transaction.setTransaction_id(mDocRef.getId());
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);

                String month = transaction.getTimeStamp().toString().substring(4, 7);
                String year = transaction.getTimeStamp().toString().substring(30, 34);

                mDocRef.set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Created new Income", Toast.LENGTH_SHORT).show();
                            MainActivity.updateBudgetMeters();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                    MainActivity.overallScreenArea.setVisibility(View.GONE);
                                }
                            }, 1500);

                        } else {
                            Toast.makeText(getActivity(), "Failed. Check Log", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                    mDocRef=FirebaseFirestore.getInstance().document(collectionNAME + "/Metadata/" + month + year);
                    final String monthYear=month + year;
                    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, String> dataToSave=new HashMap<String, String>();
                            double total = 0.00;
                            double total2 = 0.00;
                            if(documentSnapshot.exists()){
                                String totalString=documentSnapshot.getString(category.getSelectedItem().toString());
                                String typeString=documentSnapshot.getString(type.getSelectedItem().toString());
                                if (totalString==null){
                                    totalString="0";
                                }
                                if(typeString==null){
                                    typeString="0";
                                }
                                Log.i("Failed",totalString);
                                total=Double.parseDouble(totalString );
                                total2=Double.parseDouble(typeString );

                                total+=Double.parseDouble(amount.getText().toString());
                                total2+=Double.parseDouble(amount.getText().toString());

                                dataToSave.put(category.getSelectedItem().toString(),String.valueOf(total));
                                dataToSave.put(type.getSelectedItem().toString(),String.valueOf(total2));
                                dataToSave.put("doc Name",monthYear);
                                mDocRef.set(dataToSave,SetOptions.merge());
                            }else {
                                total += Double.parseDouble(amount.getText().toString());
                                total2 += Double.parseDouble(amount.getText().toString());
                                dataToSave.put(category.getSelectedItem().toString(), String.valueOf(total));
                                dataToSave.put(type.getSelectedItem().toString(), String.valueOf(total2));
                                dataToSave.put("doc Name", monthYear);
                                mDocRef.set(dataToSave, SetOptions.merge());
                            }

                            MainActivity.lastQuiredIncome=null;
                            MainActivity.lastQuiredExpense=null;
                            MainActivity.analytics();
                            MainActivity.updateOverviewBalance();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                    MainActivity.overallScreenArea.setVisibility(View.GONE);
                                }
                            }, 1500);

                        }
                    });


            }
        }
    });
        incomeExpenditure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (incomeExpenditure.getText()=="Expenditure"){
                    incomeExpenditure.setText("Income");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, incomeSpinner);
                    //set the spinners adapter to the previously created one.
                    category.setAdapter(adapter);
                    income.setVisibility(View.VISIBLE);
                    expense.setVisibility(View.GONE);
                    if(MetaData.hereForEdit==true){
                        edit.setVisibility(View.VISIBLE);
                        income.setVisibility(View.GONE);
                        expense.setVisibility(View.GONE);
                    }
                }else {
                    incomeExpenditure.setText("Expenditure");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categorySpinner);
                    //set the spinners adapter to the previously created one.
                    category.setAdapter(adapter);
                    income.setVisibility(View.GONE);
                    expense.setVisibility(View.VISIBLE);
                    if(MetaData.hereForEdit==true){
                        edit.setVisibility(View.VISIBLE);
                        income.setVisibility(View.GONE);
                        expense.setVisibility(View.GONE);
                    }
                }
            }
        });
}
    public boolean checkForm(){
        boolean complete;
        if (amount.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "InputAmount", Toast.LENGTH_SHORT).show();
            amount.requestFocus();
            complete=false;
        }else if (category.getSelectedItem().toString()=="Choose Category"){

            Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
            category.requestFocus();
            complete=false;
        }else {
            complete=true;
        }
        return complete;
    }
    static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public Date getDateFromString(String datetoSaved){

        try {
            Date date = format.parse(datetoSaved);
            return date ;
        } catch (ParseException e){
            return null ;
        }

    }

}




        /*expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();
                mDocRef = FirebaseFirestore.getInstance().document("users/" + userID + "/transactionData/data");
                mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> temp = document.getData();
                            Transaction test = new Transaction(amount.getText().toString(), dateView.getText().toString(), timeView.getText().toString(), category.getSelectedItem().toString(), description.getText().toString(), "Expense");
                            if(temp == null){
                                dataToSave.put("Transaction " + 1, test);
                                Toast.makeText(getActivity(), "Clicked " + 1, Toast.LENGTH_SHORT).show();
                                mDocRef.set(dataToSave);
                            }else{
                                int num = temp.size() + 1;
                                dataToSave.put("Transaction " + num , test);
                                Toast.makeText(getActivity(), "Clicked " + num, Toast.LENGTH_SHORT).show();
                                mDocRef.set(dataToSave, SetOptions.merge());
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });*/

        /*income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();
                String userID = user.getUid();
                mDocRef = FirebaseFirestore.getInstance().document("users/" + userID + "/transactionData/data");
                mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> temp = document.getData();
                            Transaction test = new Transaction(amount.getText().toString(), dateView.getText().toString(), timeView.getText().toString(), category.getSelectedItem().toString(), description.getText().toString(), "Income");
                            if(temp == null){
                                dataToSave.put("Transaction " + 1, test);
                                Toast.makeText(getActivity(), "Clicked " + 1, Toast.LENGTH_SHORT).show();
                                mDocRef.set(dataToSave);
                            }else{
                                int num = temp.size() + 1;
                                dataToSave.put("Transaction " + num , test);
                                Toast.makeText(getActivity(), "Clicked " + num, Toast.LENGTH_SHORT).show();
                                mDocRef.set(dataToSave, SetOptions.merge());
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

    }

}*/


