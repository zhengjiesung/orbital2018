package com.montethecat.scroogev2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class readCSV extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_csv);

        readData();
    }


    private List<TransactionSample> transactionSamples = new ArrayList<>();


    private void readData() {
        InputStream is = getResources().openRawResource(R.raw.banktest);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line = "";
        try{
            // Step over blank lines and headers
            for (int i = 0; i < 19; i ++) {
                reader.readLine();
            }
            while ( (line = reader.readLine()) != null) {
                Log.d("MyActivity", "Line: " + line);

                // Split by ','
                String[] tokens = line.split(",");

                // Read the data
                TransactionSample sample = new TransactionSample();
                sample.setTransactionDate(tokens[0]);
                if (tokens.length >= 2 && tokens[1].length() > 0) {
                    sample.setReference(tokens[1]);

                } else {
                    sample.setReference("");
                }
                if (tokens.length >= 3 && tokens[2].length() > 0) {
                    sample.setDebitAmount(tokens[2]);
                } else {
                    sample.setDebitAmount("");
                }
                if (tokens.length >=4 && tokens[3].length() > 0) {
                    sample.setCreditAmount(tokens[3]);
                } else {
                    sample.setCreditAmount("");
                }

                if (tokens.length >=5 && tokens[4].length() > 0)  {
                    sample.setTransactionRef1(tokens[4]);

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
                transactionSamples.add(sample);

                Log.d("MyActivity", "Just created: " + sample);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line " + line, e);
            e.printStackTrace();
        }
    }
}