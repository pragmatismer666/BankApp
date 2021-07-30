package com.example.bank;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.Date;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    ProgressDialog progressDialog;

    EditText accountName, accountNum, amount;
    RadioGroup bankGroup;
    RadioButton selectedBank;
    Button transferBtn;

    Spinner currency;
    String bankCode = "044";
//    String selectCurrency = Common.currencyS.get(0);
    String selectCurrency = "NGN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        defineUIElement();
    }

    private void defineUIElement() {
        accountName = findViewById(R.id.accountNameEt);
        accountNum = findViewById(R.id.accountNumEt);
        amount = findViewById(R.id.amountEt);

        bankGroup = findViewById(R.id.bankGroup);

        currency = findViewById(R.id.currencylist);

        Common.currencyS.add("NGN");
        Common.currencyS.add("ABC");
        accountName.setText("Moti Aondohemba James");
        accountNum.setText("0802005209");
        amount.setText("1");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Common.currencyS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency.setAdapter(adapter);
        selectCurrency = currency.getSelectedItem().toString();

        transferBtn = findViewById(R.id.transferBtn);
        transferBtn.setOnClickListener(v->{
            transfer();
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void transfer() {
        String accountNameStr = accountName.getText().toString().trim();
        String accountNumStr = accountNum.getText().toString().trim();
        String amountStr = amount.getText().toString().trim();
        selectedBank  = findViewById(bankGroup.getCheckedRadioButtonId());

        if ( accountNameStr.trim().isEmpty() ) {
            accountName.setError("Enter user name");
            accountName.requestFocus();
        } else if ( accountNumStr.trim().isEmpty() ) {
            accountNum.setError("Enter user name");
            accountNum.requestFocus();
        } else if ( amountStr.trim().isEmpty() ) {
            amount.setError("Enter user name");
            amount.requestFocus();
        }
        else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            long nowTime = Clock.system(TimeZone.getTimeZone("GMT+1").toZoneId() ).millis();
            Common.accountName = accountNameStr.split(" ")[0] + " " + accountNameStr.split(" ")[2];
            switch (selectedBank.getText().toString()) {
                case "Access Bank":
                    bankCode = Common.bankCodes[0];
                    Common.dateTime = sdf.format(new Date(nowTime));
                    Common.accountNum = accountNumStr.substring(0,3) + "******" + accountNumStr.substring(6,9);
                    break;
                case "UBA":
                    bankCode = Common.bankCodes[1];
                    sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
                    Common.dateTime = sdf.format(new Date(nowTime));
                    Common.accountNum = accountNumStr.charAt(0) + "XX.." + accountNumStr.substring(6,8) + "X";
                    Common.accountName = Common.accountName + "/IFT34661278";
                    break;
                case "First Bank":
                    bankCode = Common.bankCodes[2];
                    sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                    Common.dateTime = sdf.format(new Date(nowTime));
                    Common.accountNum = accountNumStr.substring(0,3) + "XXXX" + accountNumStr.substring(6,9);
                    Common.accountName = Common.accountName + "/4563";
                    break;
            }

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Transfer");
            progressDialog.setMessage("Payment Transferring ...");

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
//                        URL url = new URL( HomeActivity.this.getString(R.string.transferUrl) );
//                        HttpsURLConnection urlConnection = (HttpsURLConnection)(url.openConnection());
//                        urlConnection.setRequestProperty("Authorization", "Bearer " + Common.user.getSKey());
//                        urlConnection.setRequestProperty("Content-Type", "application/json");
//                        urlConnection.setDoInput(true);
//                        Uri.Builder builder = new Uri.Builder()
//                                .appendQueryParameter("account_bank", bankCode)
//                                .appendQueryParameter("account_number", accountNumStr )
//                                .appendQueryParameter("amount", amountStr)
//                                .appendQueryParameter("narration", "This is api test.")
//                                .appendQueryParameter("currency", selectCurrency);
//                        OutputStream os = urlConnection.getOutputStream();
//                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//                        writer.write(builder.build().getEncodedQuery());
//                        writer.flush();
//                        writer.close();
//                        os.close();
//                        InputStream is = null;
//                        if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
//                            is = urlConnection.getInputStream();
//                        try {
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//                            StringBuilder sb = new StringBuilder();
//                            String line = null;
//                            while ((line = reader.readLine()) != null) {
//                                sb.append(line + "\n");
//                            }
//                            is.close();
//                        } catch (Exception e) {
//                            Log.e("Buffer Error", "Error converting result " + e.toString());
//                        }
//                        }
//                        urlConnection.disconnect();
//                        Common common = new Common();
//                        common.checkApi(HomeActivity.this, Common.user.pKey, Common.user.eKey, Common.user.sKey);
//                        for (int i=0;i<common.balances.length();i++){
//                            JSONObject jsonObject = new JSONObject(common.balances.get(i).toString());
//                            if ( jsonObject.get("currency").toString().equals(selectCurrency) ){
//                                Common.balance = jsonObject.get("available_balance").toString();
//                                break;
//                            }
//                        }
                        Common.balance = "209";
                        Common.bankName = selectedBank.getText().toString();
                        Common.currency = selectCurrency;
                        Common.amount = amountStr;

                        progressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent myIntent = new Intent(HomeActivity.this, SuccessSplashActivity.class);
                                startActivity(myIntent);
                                finish();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

}