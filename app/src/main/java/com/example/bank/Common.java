package com.example.bank;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class Common {
    public static final String USER_REF = "Users";
    public static User user;

    public static ArrayList<String> currencyS = new ArrayList<String>();
    public JSONArray balances = null;
    public static String[] bankCodes = new String[]{"044","033","011"};

    public static String bankName = "accessBank";
    public static String accountNum = "";
    public static String accountName = "";
    public static String currency = "";
    public static String balance = "";
    public static String amount = "";
    public static String dateTime = "";

    public boolean checkApi(Context context, String pKey, String eKey, String sKey) {
        boolean checking = false;
        try {
            if (pKey.isEmpty() || eKey.isEmpty() || sKey.isEmpty() ){
                return false;
            }
            else {
                URL url = new URL( context.getString(R.string.balanceUrl));
                HttpsURLConnection urlConnection = (HttpsURLConnection)(url.openConnection());
                urlConnection.setRequestProperty("Authorization", "Bearer " + sKey);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                InputStream is = null;
                if(urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    is = urlConnection.getInputStream();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        JSONObject balanceObj = new JSONObject(sb.toString());
                        balances = new JSONArray(balanceObj.get("data").toString());
                        for (int i=0;i<balances.length();i++){
                            JSONObject jsonObject = new JSONObject(balances.get(i).toString());
                            if ( !currencyS.contains(jsonObject.get("currency").toString()) ){
                                currencyS.add(jsonObject.get("currency").toString());
//                                if ( Integer.parseInt(jsonObject.get("ledger_balance").toString()) > 0){
//                                    currencyS.add(jsonObject.get("currency").toString());
//                                }
                            }
                        }
                        is.close();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result " + e.toString());
                    }
                    checking = true;
                }
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return checking;
    }
}
