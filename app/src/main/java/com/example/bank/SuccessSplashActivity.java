package com.example.bank;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class SuccessSplashActivity extends AppCompatActivity {

    LinearLayout success;

    String textBody = "";

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_splash);

        success = findViewById(R.id.baseVertical);
        sleepThread();
    }

    private void sleepThread() {
        switch (Common.bankName) {
            case "Access Bank":
                textBody = "\nCredit\nAmt:" + Common.currency + Common.amount + "\nAcc:" + Common.accountNum + "\nDesc:" + Common.accountName + "\nTime:" + Common.dateTime + "\nAvail Bal:" + Common.currency + Common.balance + "\nTotal : " + Common.currency + Common.balance;
                break;
            case "UBA":
                textBody = "\nTxn:Credit\nAcc:" + Common.accountNum + "\nAmt:" + Common.currency + Common.amount + "\nDesc:" + Common.accountName + "\nDate:" + Common.dateTime + "\nBal:" + Common.currency + Common.balance;
                break;
            case "First Bank":
                textBody = "\nCredit\nYour Acct  " + Common.accountNum + " Has Been Credited with " + Common.currency + Common.amount + " On " + Common.dateTime + " By FIP:" + Common.accountName + "\nBal:" + Common.currency + Common.balance;
                break;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL( getString(R.string.twilio_msgUrl) + getString(R.string.accountSID) + getString(R.string.msg_endpoint) );
                    HttpsURLConnection urlConnection = (HttpsURLConnection)(url.openConnection());
                    urlConnection.setRequestProperty("Authorization", "Basic " + getString(R.string.twilio_basic));
                    urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    Uri.Builder builder = new Uri.Builder()
//                        .appendQueryParameter("To", Common.user.getPhone())
                        .appendQueryParameter("To", "+2349020064883")
                        .appendQueryParameter("MessagingServiceSid", getString(R.string.serviceSID) )
                        .appendQueryParameter("Body", textBody);
                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(builder.build().getEncodedQuery());
                    writer.flush();
                    writer.close();
                    os.close();
                    if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                        try {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"), 8096);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                        } catch (Exception e) {
                            Log.e("Buffer Error", "Error converting result " + e.toString());
                        }
                    } else {
                        Log.e("Response Code Problem", " Response Data is not HTTP_OK ");
                    }
                    urlConnection.disconnect();
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SuccessSplashActivity.this," Get Problem in SMS ", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent myIntent = new Intent(SuccessSplashActivity.this, HomeActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                });
            }
        });
        thread.start();
    }
}