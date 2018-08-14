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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UploadTestingFragment extends Fragment {

    private List<TransactionSample> transactionSamples = new ArrayList<>();

    Button selectFile, upload, retrieve;
    TextView notification;
    Uri csvUri; // uri are actually URLs that are meant for local storage

    FirebaseStorage storage; // used for upload files
    // FirebaseDatabase database; // used to store URLs of uploaded files
    ProgressDialog progressDialog;
    FirebaseFirestore database; // used to store URLS of uploaded files

    DocumentReference documentReference;
    DocumentReference mDocRef;
    FirebaseUser user;
    String collectionNAME;
    String uid = "";

    // store all expenditure keyWords associated with category
    HashMap<String, String> expenditureKeyWords = new HashMap<String, String>();
    HashMap<String, String> incomeKeyWords = new HashMap<String, String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Upload Bank Statement");
        return inflater.inflate(R.layout.fragment_uploadtesting, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storage = FirebaseStorage.getInstance(); // return an object of FireBase Storage
        // database = FirebaseDatabase.getInstance(); // return an object of FireBase Database
        database = FirebaseFirestore.getInstance(); // return an object of FireBase Firestore

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
        upload = view.findViewById(R.id.upload);
        notification = view.findViewById(R.id.notification);

        // keep
        selectFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    selectCsv();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });


        // not needed
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (csvUri != null) { // the user has selected the file
                    final String fileName = uploadFile(csvUri);
                    Toast.makeText(getActivity(), "fileName is " + fileName, Toast.LENGTH_SHORT).show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            //retrieveFile(fileName);
                        }
                    }, 1500);


                } else {
                    Toast.makeText(getActivity(), "Select a File", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // not needed
    private void retrieveFile(final String fileName){
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("Uploads").child(fileName);
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                InputStream is = new ByteArrayInputStream(bytes);
                //readData(is);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        //deleteFile(fileName);

                    }
                }, 1500);

            }
        });
    }

    // not needed
    private void deleteFile(String fileName){
        StorageReference storageRef = storage.getReference();
        StorageReference ref = storageRef.child("Uploads").child(fileName);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "csv File deleted from storage", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Error", "exception cannot delete file", e);
            }
        });

    }

    // not needed
    private String uploadFile(final Uri csvUri){

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis()+"";
        StorageReference storageRef = storage.getReference(); // returns root path

        final StorageReference ref = storageRef.child("Uploads").child(fileName);

        documentReference = database.collection("users/" + uid + "/csvData").document(fileName);

        ref.putFile(csvUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url = ref.getDownloadUrl().toString();
                        Map<String, String> mapURL = new HashMap<>();
                        mapURL.put("csv", url);
                        // store the url in realtime database
                        // DatabaseReference reference = database.getReference(); // return the path to root

                        // store the url in firebase firecloud
                        documentReference.set(mapURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    Toast.makeText(getActivity(), "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "File not successfully uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getActivity(), "File not successfully uploaded", Toast.LENGTH_SHORT).show();


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                // track the progress of our upload
                int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });

        return fileName;

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
                readData(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            notification.setText("A file is selected : " + data.getData().getLastPathSegment());

        } else {
            Toast.makeText(getActivity(), "Please select a file", Toast.LENGTH_SHORT).show();


        }

    }

    // Keep
    private void readData(InputStream is) {
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
                Log.d("MyActivity", "Line: " + line);

                // Split by ','
                String[] tokens = line.split(",");

                // Read the data
                final Transaction transaction = new Transaction();
                TransactionSample sample = new TransactionSample();

                sample.setTransactionDate(tokens[0]);
                transaction.setDate(tokens[0]);
                if (transaction.getDate() != "") {
                    String year = tokens[0].substring(8);
                    String day = tokens[0].substring(0, 2);
                    String month = tokens[0].substring(3, 6);
                    ArrayList<String> monthChanger = new ArrayList<String>(Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));

                    for (int n = 0; n < 12; n++) {
                        if (month == monthChanger.get(n)) ;
                        month = Integer.toString(n + 1);
                    }

                    transaction.setTimeStamp(getDateFromString(year + "-" + month + "-" + day + "T00:00:00Z"));
                }

                if (tokens.length >= 2 && tokens[1].length() > 0) {
                    sample.setReference(tokens[1]);

                } else {
                    sample.setReference("");
                }
                if (tokens.length >= 3 && tokens[2].length() > 0) {
                    if (!tokens[2].equals(" ")){
                        sample.setDebitAmount(tokens[2]);
                        transaction.setAmount(tokens[2]);
                        transaction.setType("Expense");

                    }

                } else {
                    sample.setDebitAmount("");
                }
                if (tokens.length >=4 && tokens[3].length() > 0) {

                    if (!tokens[3].equals(" ")) {
                        sample.setCreditAmount(tokens[3]);
                        transaction.setAmount(tokens[3]);
                        transaction.setType("Income");
                    }


                } else {
                    sample.setCreditAmount("");
                }

                if (tokens.length >=5 && tokens[4].length() > 0)  {
                    sample.setTransactionRef1(tokens[4]);
                    transaction.setDescription(tokens[4]);

                    String category = "";
                    if (transaction.getType().equals("Expense")){
                        category = findCategory("Expense", expenditureKeyWords, tokens[4]);
                        transaction.setCategory(category);
                    } else {
                        category = findCategory("Income", incomeKeyWords, tokens[4]);
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

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                collectionNAME="users/"+userID;
                //wallet Changes
                if(MetaData.inwallet==true){
                    userID=MetaData.walletName;
                    collectionNAME="group/"+userID;
                }
                mDocRef = FirebaseFirestore.getInstance().collection(collectionNAME + "/transactionData").document();
                mDocRef.set(transaction).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        final DocumentReference mDocRef2=FirebaseFirestore.getInstance().document(collectionNAME+ "/Metadata/data");
                        mDocRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, String> dataToSave=new HashMap<String, String>();
                                double total = 0.00;
                                String category = transaction.getCategory();
                                if (category ==  null){
                                    category = "Others (Expenditure)";
                                }
                                String amount = transaction.getAmount();
                                if (amount ==  null){
                                    amount = "0";
                                }
                                if(documentSnapshot.exists()){


                                    String totalString=documentSnapshot.getString(category);
                                    if (totalString==null){
                                        totalString="0";
                                    }
                                    Log.i("Failed",totalString);
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
                        });

                    }

                });

                transactionSamples.add(sample);

                Log.d("MyActivity", "Just created: " + sample);
                String type = transaction.getType();
                if (type == null){
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
                }

            }



        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
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

    private String findCategory(String type, HashMap<String, String>keyWords, String description) {
        String category = "";
        for (String word : keyWords.keySet()){
            if (description.toLowerCase().indexOf(word.toLowerCase()) != -1 ) {
                category = keyWords.get(word);
                return category;
            }
        }

        if (type.equals("Expense")){
            return "Others (Expenditure)";
        } else {
            return "Others (Income)";
        }
    }


}


