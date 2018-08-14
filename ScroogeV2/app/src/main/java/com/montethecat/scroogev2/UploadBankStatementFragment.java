package com.montethecat.scroogev2;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadBankStatementFragment extends Fragment {

    private List<TransactionSample> transactionSamples = new ArrayList<>();

    private ProgressBar mProgressBar;

    ArrayList<String> category;

    ArrayList<String> expenseArray2;
    ArrayList<String> incomeArray2;


    Button selectFile;
    TextView notification;
    Uri csvUri; // uri are actually URLs that are meant for local storage

    FirebaseStorage storage; // used for upload files
    // FirebaseDatabase database; // used to store URLs of uploaded files
    ProgressDialog progressDialog;
    FirebaseFirestore database; // used to store URLS of uploaded files

    DocumentReference mDocRef;
    FirebaseUser user;
    String uid = "";
    String collectionNAME;

    // store all expenditure keyWords associated with category
    HashMap<String, String> expenditureKeyWords = new HashMap<String, String>();
    HashMap<String, String> incomeKeyWords = new HashMap<String, String>();

    HashMap<String, HashMap<String, String>> totalMonthYear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Upload Bank Statement");

        expenseArray2 = new ArrayList<String>();
        incomeArray2 = new ArrayList<String>();

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

        expenseArray2.addAll(Arrays.asList(categorySpinner));
        incomeArray2.addAll(Arrays.asList(incomeSpinner));

        category = new ArrayList<>();
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
        category.add("Salary");
        category.add("Business");
        category.add("Loan");
        category.add("Parental Leave");
        category.add("Insurance Payout");
        category.add("Transfer-in");
        category.add("Others (Income)");


        return inflater.inflate(R.layout.fragment_uploadbankstatement, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = getView().findViewById(R.id.progressBar);

        storage = FirebaseStorage.getInstance(); // return an object of FireBase Storage
        // database = FirebaseDatabase.getInstance(); // return an object of FireBase Database
        database = FirebaseFirestore.getInstance(); // return an object of FireBase Firestore
        MetaData.counter = 0;

        // Food and Drinks
        expenditureKeyWords.put("mcdonald's", "Food and Drinks");
        expenditureKeyWords.put("kanpai", "Food and Drinks");
        expenditureKeyWords.put("hanbaobao", "Food and Drinks");
        expenditureKeyWords.put("subway", "Food and Drinks");
        expenditureKeyWords.put("assembly ground", "Food and Drinks");

        // Transport
        expenditureKeyWords.put("grab", "Transport");
        expenditureKeyWords.put("transit", "Transport");
        expenditureKeyWords.put("ofoglobal", "Transport");

        // Life & Entertainment
        expenditureKeyWords.put("f club", "Life & Entertainment");
        expenditureKeyWords.put("itunes.com/bill", "Life & Entertainment");
        expenditureKeyWords.put("gv", "Life & Entertainment");
        expenditureKeyWords.put("cathay", "Life & Entertainment");
        expenditureKeyWords.put("shaw", "Life & Entertainment");

        // Education
        expenditureKeyWords.put("udemy", "Education");
        expenditureKeyWords.put("university", "Education");

        // Insurance Payment
        expenditureKeyWords.put("aviva", "Insurance Payment");

        // Transer-out
        expenditureKeyWords.put("i-bank", "Transfer-out");


        // Income
        incomeKeyWords.put("i-bank", "Transfer-in");


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user ==  null){
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            getActivity().finish();

        } else {
            uid = user.getUid();
        }

        selectFile = view.findViewById(R.id.selectFile);
        notification = view.findViewById(R.id.notification);

        // keep
        selectFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectCsv();
                    //MainActivity.updateOverviewBalance();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });



    }

    // keep
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectCsv();
        } else {
            Toast.makeText(getActivity(), "please provide permission..", Toast.LENGTH_SHORT).show();
        }

    }

    // Keep
    private void selectCsv(){

        // to offer user to select a file using file manager

        // we will be using an Intent

        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT); // to fetch files
        startActivityForResult(intent, 86);
    }


    // Keep
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // check whether use has selected a file or not (ex: pdf)

        if (requestCode == 86 && resultCode == Activity.RESULT_OK && data!=null){
            
            csvUri = data.getData(); // return the uri of selected file..
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(csvUri);
                totalMonthYear = new HashMap<>();
                totalMonthYear.clear();
                readData(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //notification.setText("A file is selected : " + data.getData().getLastPathSegment());
            notification.setText("Loading CSV File, do not touch");

        } else {
            Toast.makeText(getActivity(), "Please select a file", Toast.LENGTH_SHORT).show();


        }

    }

    // Keep
    private void readData(InputStream is) {
        mProgressBar.setVisibility(View.VISIBLE);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try{
            // Step over blank lines and headers
            for (int i = 0; i < 19; i ++) {
                reader.readLine();
            }

            double totalIncome = 0;
            double totalExpense = 0;

            while ( (line = reader.readLine()) != null) {
                double income = 0;
                double expense = 0;
                String year = "";
                String month_letter = "";
                Log.d("MyActivity", "Line: " + line);

                // Split by ','
                String[] tokens = line.split(",");
                if (tokens[0]!=null) {
                    // Read the data
                    final Transaction transaction = new Transaction();
                    TransactionSample sample = new TransactionSample();

                    sample.setTransactionDate(tokens[0]);
                    transaction.setDate(tokens[0]);
                    if (transaction.getDate() != "") {
                        year = tokens[0].substring(7);
                        String day = tokens[0].substring(0, 2);
                        month_letter = tokens[0].substring(3, 6);

                        String month = tokens[0].substring(3, 6).toLowerCase();
                        Log.i("Barnabas Date", "'" + month + "'");
                        HashMap<String, String> monthChanger = new HashMap<>();
                        monthChanger.put("jan", "1");
                        monthChanger.put("feb", "2");
                        monthChanger.put("mar", "3");
                        monthChanger.put("apr", "4");
                        monthChanger.put("may", "5");
                        monthChanger.put("jun", "6");
                        monthChanger.put("jul", "7");
                        monthChanger.put("aug", "8");
                        monthChanger.put("sep", "9");
                        monthChanger.put("oct", "10");
                        monthChanger.put("nov", "11");
                        monthChanger.put("dec", "12");


                        for (String key : monthChanger.keySet()) {

                            if (key.equals(month)) {
                                month = monthChanger.get(key);
                            }
                        }


                        transaction.setTimeStamp(getDateFromString(year + "-" + month + "-" + day + "T00:00:00Z"));
                    }

                    if (tokens.length >= 2 && tokens[1].length() > 0) {
                        sample.setReference(tokens[1]);

                    } else {
                        sample.setReference("");
                    }
                    if (tokens.length >= 3 && tokens[2].length() > 1) {
                        if (!tokens[2].equals(" ")) {
                            sample.setDebitAmount(tokens[2].substring(1));
                            transaction.setAmount(tokens[2].substring(1));
                            transaction.setType("Expense");

                        }

                    } else {
                        sample.setDebitAmount("");
                    }
                    if (tokens.length >= 4 && tokens[3].length() > 1) {

                        if (!tokens[3].equals(" ")) {
                            sample.setCreditAmount(tokens[3].substring(1));
                            transaction.setAmount(tokens[3].substring(1));
                            transaction.setType("Income");
                        }


                    } else {
                        sample.setCreditAmount("");
                    }
                    //Log.i("Barnabas Transaction", transaction.getType());
                    if (tokens.length >= 5 && tokens[4].length() > 0) {
                        sample.setTransactionRef1(tokens[4]);
                        transaction.setDescription(tokens[4]);

                        String category = "";
                        if (transaction.getType().equals("Expense")) {
                            category = findCategory("Expense", expenditureKeyWords, tokens[4], transaction);
                            transaction.setCategory(category);
                        } else {
                            category = findCategory("Income", incomeKeyWords, tokens[4], transaction);
                            transaction.setCategory(category);
                        }

                    } else {
                        sample.setTransactionRef1("");
                    }
                    if (tokens.length >= 6 && tokens[5].length() > 0) {
                        sample.setTransactionRef2(tokens[5]);
                    } else {
                        sample.setTransactionRef2("");
                    }

                    if (tokens.length >= 7 && tokens[6].length() > 0) {
                        sample.setTransactionRef3(tokens[6]);
                    } else {
                        sample.setTransactionRef3("");
                    }

                    // set Account
                    transaction.setAccount("Bank");

                    // create a hashmap of hashmaps -->
                    String key = "";
                    if (!month_letter.equals("") && !year.equals("")){
                        key = month_letter + year;
                    }

                    if (!key.equals("")) {
                        // check whether the hashmap contains the key
                        if (totalMonthYear.containsKey(key)){
                            HashMap<String, String> totalOfEachCat = totalMonthYear.get(key);

                            if (totalOfEachCat.get(transaction.category) == null&&transaction.category!=null) {
                                totalOfEachCat.put(transaction.category, transaction.amount);
                            } else if(transaction.category!=null){
                                Double currTotal = Double.parseDouble(totalOfEachCat.get(transaction.category));
                                currTotal += Double.parseDouble(transaction.amount);
                                totalOfEachCat.put(transaction.category, Double.toString(currTotal));
                            }

                        } else {

                            HashMap<String, String> totalOfEachCat = new HashMap<String, String>();
                            if (totalOfEachCat.get(transaction.category) == null&&transaction.category!=null) {
                                totalOfEachCat.put(transaction.category, transaction.amount);

                            } else if(transaction.category!=null){
                                Double currTotal = Double.parseDouble(totalOfEachCat.get(transaction.category));
                                currTotal += Double.parseDouble(transaction.amount);
                                totalOfEachCat.put(transaction.category, Double.toString(currTotal));
                            }

                            totalMonthYear.put(key, totalOfEachCat);
                        }
                    }


                    if(transaction.category!=null) {
                        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        collectionNAME="users/"+userID;
                        //wallet Changes
                        if(MetaData.inwallet==true){
                            userID=MetaData.walletName;
                            collectionNAME="group/"+userID;
                        }
                        mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME+ "/transactionData").document();
                        transaction.setTransaction_id(mDocRef.getId());
                        mDocRef.set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                        /*final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document("users/" + userID + "/Metadata/data");
                        mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, String> dataToSave=new HashMap<String, String>();
                                double total = 0.00;
                                String category = transaction.getCategory();
                                Log.i("Barnabas Category","'"+category+"'");
                                if (category ==  null){
                                    category = "Others (Expenditure)";
                                }
                                String amount = transaction.getAmount();
                                Log.i("Barnabas Amount","'"+amount+"'");
                                if (amount ==  null){
                                    amount = "0";
                                }
                                if(documentSnapshot.exists()){


                                    String totalString=documentSnapshot.getString(category);
                                    if (totalString==null){
                                        totalString="0";
                                    }
                                    Log.i("Barnabas TotalAmount",totalString);
                                    total=Double.parseDouble(totalString );

                                    total+=Double.parseDouble(amount);

                                    dataToSave.put(category,String.valueOf(total));
                                    mDocRef2.set(dataToSave,SetOptions.merge());

                                }else{
                                    total+=Double.parseDouble(amount);
                                    dataToSave.put(category,String.valueOf(total));
                                    mDocRef2.set(dataToSave,SetOptions.merge());
                                }

                            }
                        });*/

                            }

                        });
                    }


                    transactionSamples.add(sample);

                    Log.d("MyActivity", "Just created: " + sample);
                    /*String type = transaction.getType();
                    if (type == null) {
                        income = 0;
                        expense = 0;
                    } else {

                        if (transaction.getType().equals("Income")) {
                            income = Double.parseDouble(transaction.getAmount());
                            totalIncome += income;
                            Log.i("income", String.valueOf(totalIncome));

                        } else {
                            expense = Double.parseDouble(transaction.getAmount());
                            totalExpense += expense;
                            Log.i("expense", String.valueOf(totalExpense));
                        }
                    }*/

                }
            }

        // Store everything in one giant hashmap called totalOfEachCat
            // totalOfEachCat_Month_Year --> 3 months --> 3 totalOfEachCat

        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }


        //To seperate expense and income


        //To seperate expense and income

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        collectionNAME="users/"+userID;
        //wallet Changes
        if(MetaData.inwallet==true){
            userID=MetaData.walletName;
            collectionNAME="group/"+userID;
        }

        for (final String monthYear: totalMonthYear.keySet()){

            final HashMap<String, String> totalOfEachCat = totalMonthYear.get(monthYear);
            final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/" + monthYear);
            mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, String> dataToSave=new HashMap<String, String>();
                    double bankTotal;
                    String originalBankTotal = documentSnapshot.getString("Bank");
                    if(originalBankTotal==null){
                        originalBankTotal = "0";
                    }
                    bankTotal = Double.parseDouble(originalBankTotal);
                    for(int n=0;n<category.size();n++){
                        //Map<String, String> dataToSave=new HashMap<String, String>();
                        Double currTotal;
                        String temp = category.get(n);
                        if(totalOfEachCat.get(category.get(n))==null) {

                        }else if(documentSnapshot.getString(category.get(n))==null){
                            if (expenseArray2.contains(temp)) {
                                bankTotal -= Double.parseDouble(totalOfEachCat.get(category.get(n)));
                                Log.i("yes", Double.toString(bankTotal));
                            } else if (incomeArray2.contains(temp)) {
                                bankTotal += Double.parseDouble(totalOfEachCat.get(category.get(n)));
                                Log.i("no", Double.toString(bankTotal));
                            } else {

                            }
                        }else {
                            currTotal=Double.parseDouble(documentSnapshot.getString(category.get(n)));
                            currTotal+=Double.parseDouble(totalOfEachCat.get(category.get(n)));
                            //String typeString = documentSnapshot.getString("Bank");
                            //if(typeString==null){
                                //typeString = "0";
                            //}
                            //bankTotal = Double.parseDouble(typeString);
                            if(expenseArray2.contains(category.get(n))){
                                bankTotal -= Double.parseDouble(totalOfEachCat.get(category.get(n)));;
                                Log.i("yes",Double.toString(bankTotal));
                            }else if(incomeArray2.contains(category.get(n))){
                                bankTotal += Double.parseDouble(totalOfEachCat.get(category.get(n)));;
                                Log.i("no",Double.toString(bankTotal));
                            }else{

                            }
                            //dataToSave.put("Bank",String.valueOf(bankTotal));
                            //mDocRef2.set(dataToSave,SetOptions.merge());
                            totalOfEachCat.put(category.get(n),Double.toString(currTotal));



                        }
                        totalOfEachCat.put("doc Name",monthYear);
                        Log.i("Total of Each 2", String.valueOf(totalOfEachCat));
                        mDocRef2.set(totalOfEachCat, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.screenAreaGrid.setVisibility(View.VISIBLE);
                                MainActivity.overallScreenArea.setVisibility(View.GONE);
                                MainActivity.lastQuiredIncome=null;
                                MainActivity.lastQuiredExpense=null;
                                MetaData.counter += 1;
                                if(MetaData.counter <= 1)
                                {
                                    MainActivity.analytics();
                                }

                            }
                        });
                    }
                    dataToSave.put("Bank",String.valueOf(bankTotal));
                    mDocRef2.set(dataToSave,SetOptions.merge());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Total of Each 3", String.valueOf(totalOfEachCat));
                }
            });

        }
        MainActivity.updateOverviewBalance();

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
    /*SimpleDateFormat format = new SimpleDateFormat("dd MMMM YYYY hh:mm:ss aa");
    public Date getDateFromString(String dateToSaved){

        try {
            Date date = format.parse(dateToSaved);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }*/

    private String findCategory(String type, HashMap<String, String>keyWords, String description, Transaction transaction) {
        String category = "";
        for (String word : keyWords.keySet()){
            if (description.toLowerCase().indexOf(word.toLowerCase()) != -1 ) {
                category = keyWords.get(word);
                transaction.setName(word);
                return category;
            }
        }

        if (type.equals("Expense")){
            transaction.setName("Others");
            return "Others (Expenditure)";
        } else {
            transaction.setName("Others");
            return "Others (Income)";
        }
    }


}


