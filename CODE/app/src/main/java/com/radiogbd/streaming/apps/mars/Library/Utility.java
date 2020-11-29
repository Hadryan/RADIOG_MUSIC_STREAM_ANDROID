package com.radiogbd.streaming.apps.mars.Library;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.radiogbd.streaming.apps.mars.Activity.BrowserActivity;
import com.radiogbd.streaming.apps.mars.Activity.Splash;
import com.radiogbd.streaming.apps.mars.Adapter.OperatorAdapter;
import com.radiogbd.streaming.apps.mars.Http.BaseApiClient;
import com.radiogbd.streaming.apps.mars.Http.BaseApiInterface;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.SubscriptionInterfacce;
import com.radiogbd.streaming.apps.mars.Model.Bkash;
import com.radiogbd.streaming.apps.mars.Model.Config;
import com.radiogbd.streaming.apps.mars.Model.Master;
import com.radiogbd.streaming.apps.mars.Model.Msisdn;
import com.radiogbd.streaming.apps.mars.Model.SongModel;
import com.radiogbd.streaming.apps.mars.Receiver.AlarmReceiver;
import com.radiogbd.streaming.apps.mars.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by fojlesaikat on 8/24/17.
 */

public class Utility {

    public static final String SUBS_MOBILE = "mobile";
    public static final String SUBS_STATUS = "status";
    private static final String KEY_PHRASE = "BDFHJLNPpnljhfdb";


    Context context;
    ProgressDialog mProgressDialog;
    AESUtil aesUtil = new AESUtil();
    HashMap<String, String> pushApiMap;

    ContentApiInterface masterApiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);

    SubscriptionInterfacce subscriptionInterfacce;

    //EditText pinNumber;

    public Utility(Context context) {
        this.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        freeMemory();
    }

    public Utility(Context context, SubscriptionInterfacce subscriptionInterfacce) {
        this.context = context;
        this.subscriptionInterfacce = subscriptionInterfacce;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        freeMemory();
    }


    public boolean isAreaRestricted() {
//        TelephonyManager tm;
//        String countryCodeValue = "";
//        try {
//            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            countryCodeValue = tm.getNetworkCountryIso();
//        } catch (Exception ex) {
//            countryCodeValue = "xx";
//        }
//        String[] country_list = context.getResources().getStringArray(R.array.country_code);
//        for (int i = 0; i < country_list.length; i++) {
//            String code = country_list[i];
//            if (countryCodeValue.equals(code)) {
//                return true;
//            }
//        }
        return false;
    }

    public void setRefreshState(boolean refreshValue) {
        SharedPreferences sharedPref = context.getSharedPreferences("REFRESH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("doRefresh", refreshValue);
        editor.commit();
    }

    public boolean getRefreshState() {
        SharedPreferences sharedPref = context.getSharedPreferences("REFRESH", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("doRefresh", false);
    }

    public void setLanguage(String language) {
        SharedPreferences sharedPref = context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language", language);
        editor.commit();
    }

    public String getLangauge() {
        SharedPreferences sharedPref = context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        return sharedPref.getString("language", "en");
    }

    public void writeSubscriptionStatus(HashMap<String, String> map) {
        SharedPreferences sharedPref = context.getSharedPreferences("SUBSCRIBE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        for (String key : map.keySet()) {
            editor.putString(key, map.get(key));
        }
        editor.commit();
    }

    public HashMap<String, String> getSubscriptionData() {
        HashMap<String, String> map = new HashMap<String, String>();
        SharedPreferences sharedPref = context.getSharedPreferences("SUBSCRIBE", Context.MODE_PRIVATE);
        Map<String, ?> m = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : m.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
        }
        return map;
    }

    /*public void checkInfo() {
        String phone, status, message;
        phone = status = message = "";
        HashMap<String, String> map = new HashMap<>();
        map = getSubscriptionData();
        if (map.size() > 0) {
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String value = (String) map.get(key);
                switch (key) {
                    case SUBS_MOBILE:
                        phone = value;
                        break;
                    case SUBS_STATUS:
                        status = value;
                        break;
                }
            }
            if (status.equals("success")) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject jsonAuth = new JSONObject();
                    jsonAuth.put("username", "gseries");
                    jsonAuth.put("api_key", "yIraI2Q5k3U79FHv");
                    jsonAuth.put("api_secret", "W!>1/{8,&TVbVhWK");
                    JSONObject request = new JSONObject();
                    request.put("request", "STATUS");
                    request.put("mobile", phone);
                    jsonObject.put("auth", jsonAuth);
                    jsonObject.put("request_data", request);
                    String postBody = jsonObject.toString();
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
                    checkSubscription(body);
                } catch (Exception ex) {
                    showToast(ex.toString());
                }
            } else {
                generatePinDialog();
            }
        } else {
            generatePinDialog();
        }
//        HashMap<String, String> map = new HashMap<String, String>();
//        if (checkCountryISO()) {
//            map.put(SUBS_IP_FOUND, "yes");
//            writeSubscriptionStatus(map);
//            try {
//                JSONObject jsonObject = new JSONObject();
//                JSONObject jsonAuth = new JSONObject();
//                jsonAuth.put("username", "gseries");
//                jsonAuth.put("api_key", "yIraI2Q5k3U79FHv");
//                jsonAuth.put("api_secret", "W!>1/{8,&TVbVhWK");
//                JSONObject request = new JSONObject();
//                request.put("request", "STATUS");
//                request.put("mobile", finalMsisdn);
//                jsonObject.put("auth", jsonAuth);
//                jsonObject.put("request_data", request);
//                String postBody = jsonObject.toString();
//                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postBody);
//                checkSubscription(body);
//            } catch (Exception ex) {
//
//            }
//        } else {
//            map.put(SUBS_IP_FOUND, "no");
//            writeSubscriptionStatus(map);
//        }
    }*/

    private void generatePinDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.number_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
        final EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
        Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
        Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (isNetworkAvailable()) {
                    try {
                        if (phoneNumber.length() > 0) {
                            pushApiMap = new HashMap<String, String>();
                            try {
                                pushApiMap.put("user", AESUtil.encrypt("radiog", KEY_PHRASE));
                                pushApiMap.put("password", AESUtil.encrypt("radiog9865", KEY_PHRASE));
                                pushApiMap.put("mobile", phoneNumber.getText().toString());
                                pushApiMap.put("msisdn", AESUtil.encrypt(phoneNumber.getText().toString(), KEY_PHRASE));
                                pushApiMap.put("package", AESUtil.encrypt("1185", KEY_PHRASE));
                                pushApiMap.put("txn", String.valueOf(System.currentTimeMillis()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String urlString = "http://pt5.etisalat.ae/Moneta/pushPIN.htm?" +
                                    "usr=" + pushApiMap.get("user") +
                                    "&pwd=" + pushApiMap.get("password") +
                                    "&msisdn=" + pushApiMap.get("msisdn") +
                                    "&packageid=" + pushApiMap.get("package") +
                                    "&txnid=" + pushApiMap.get("txn");
                            URL url = new URL(urlString);
                            new EtisalatPushAPI().execute(url);
                            //generatePin(pushApiMap);
                        } else {
                            showToast("Please Write Number");
                        }
                    } catch (Exception ex) {
                        //utility.showToast(ex.toString());
                        Log.d("RESULT", ex.toString());
                    }
                } else {
                    showToast("No Internet");
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }


    private class EtisalatPushAPI extends AsyncTask<URL, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        protected String doInBackground(URL... urls) {
            try {
                //String url = "http://pt5.etisalat.ae/Moneta/pushPIN.htm?usr=BcH9ZG4i9nKL7vjMvO%2BXtg%3D%3D%0A&pwd=akyH7JWTJTUUZKbkrAndEQ%3D%3D%0A&msisdn=uwokd6xSIdrIpjyzaSiMeA%3D%3D%0A&packageid=5veSuTKhoeUM14IJK1A5Wg%3D%3D%0A&txnid=82321892";
                URL obj = urls[0];
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //showToast(response.toString());
                return response.toString();
            } catch (Exception ex) {
                showToast(ex.toString());
                return "error";
            }
        }

        protected void onPostExecute(String result) {
            hideProgress();
            try {
                String status = result.substring(0, result.lastIndexOf('|'));
                String token = result.substring(result.lastIndexOf('|') + 1);
                if (status.equals("pin_sent")) {
                    pushApiMap.put("token", token);
                    checkPinDialog();
                } else {
                    showToast(status);
                }
            } catch (Exception ex) {
                showToast(ex.toString());
            }
        }

    }

    private class EtisalatConfirmAPI extends AsyncTask<URL, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        protected String doInBackground(URL... urls) {
            try {
                //String url = "http://pt5.etisalat.ae/Moneta/pushPIN.htm?usr=BcH9ZG4i9nKL7vjMvO%2BXtg%3D%3D%0A&pwd=akyH7JWTJTUUZKbkrAndEQ%3D%3D%0A&msisdn=uwokd6xSIdrIpjyzaSiMeA%3D%3D%0A&packageid=5veSuTKhoeUM14IJK1A5Wg%3D%3D%0A&txnid=82321892";
                URL obj = urls[0];
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //showToast(response.toString());
                return response.toString();
            } catch (Exception ex) {
                showToast(ex.toString());
                return "error";
            }
        }

        protected void onPostExecute(String result) {
            hideProgress();
            try {
                String status = result.substring(0, result.lastIndexOf('|'));
                String token = result.substring(result.lastIndexOf('|') + 1);
                HashMap<String, String> map = new HashMap<String, String>();
                if (status.equals("success") || status.equals("Already_Active")) {
                    showToast("Verified");
                    map.put(SUBS_MOBILE, pushApiMap.get("mobile"));
                    map.put(SUBS_STATUS, "success");
                } else {
                    showToast("Not Verified");
                    map.put(SUBS_MOBILE, pushApiMap.get("mobile"));
                    map.put(SUBS_STATUS, "failed");
                }
                writeSubscriptionStatus(map);
            } catch (Exception ex) {
                showToast(ex.toString());
            }
        }

    }

//    private void generatePin(final HashMap<String, String> map) {
//        showProgress();
//        Call<String> call = etisalatApiInterface.pinPushAPI(
//                map.get("user"),
//                map.get("password"),
//                map.get("msisdn"),
//                map.get("package"),
//                map.get("txn")
//        );
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                hideProgress();
//                if (response.isSuccessful() && response.code() == 200) {
//                    try {
//                        String result = response.body();
//                        String status = result.substring(0, result.lastIndexOf('|'));
//                        String token = result.substring(result.lastIndexOf('|')+1);
//                        if (status.equals("pin_sent")) {
//                            map.put("token",aesUtil.encrypt(token,KEY_PHRASE));
//                            checkPinDialog(map);
//                        } else {
//                            showToast(status);
//                        }
//                    } catch (Exception ex) {
//                        Log.d("RESULT", ex.toString());
//                    }
//                } else {
//                    showToast("Response not found");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.d("RESULT", t.toString());
//                hideProgress();
//            }
//        });
//    }

    /*
     * This function finds out the OTP Pin from String Provided*/
//    public void setPin(String message){
//        String pin = message.substring(message.indexOf("PIN:")+5,message.indexOf("PIN")+9);
//        pinNumber.setText(pin);
//    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    private void checkPinDialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.number_layout);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
        title.setText("Verify Pin");
        final EditText pinNumber = (EditText) dialog.findViewById(R.id.phone_number);
        pinNumber.setHint("Enter Your Pin");
//        IntentFilter intentFilter = new IntentFilter(
//                "android.provider.Telephony.SMS_RECEIVED");
//        IncomingSMS incomingSMS = new IncomingSMS() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                final Bundle bundle = intent.getExtras();
//                try {
//                    if (bundle != null) {
//                        final Object[] pdusObj = (Object[]) bundle.get("pdus");
//                        for (int i = 0; i < pdusObj.length; i++) {
//                            SmsMessage currentMessage = getIncomingMessage(pdusObj[i], bundle);
//                            String phoneNumber = currentMessage.getDisplayOriginatingAddress();
//                            String senderNum = phoneNumber;
//                            String message = currentMessage.getMessageBody();
//                            try {
//                                if (senderNum.equals(context.getString(R.string.short_code))) {
//                                    showToast(message);
//                                    String pin = message.substring(message.indexOf("PIN") + 5, message.indexOf("PIN") + 9);
//                                    showToast(pin);
//                                    if (dialog.isShowing()) {
//                                        pinNumber.setText(pin);
//                                    }
//                                    break;
//                                }
//                            } catch (Exception e) {
//                                Log.d("Radio", e.toString());
//                            }
//                        }
//                    }
//                } catch (Exception ex) {
//                    // do nothing
//                }
//            }
//        };
//        context.registerReceiver(incomingSMS, intentFilter);
        Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
        Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (isNetworkAvailable()) {
                    try {
                        if (pinNumber.length() > 0) {
                            pushApiMap.put("pin", AESUtil.encrypt(pinNumber.getText().toString(), KEY_PHRASE));
                            String urlString = "http://pt5.etisalat.ae/Moneta/confirmPinSubscription.htm?" +
                                    "usr=" + pushApiMap.get("user") +
                                    "&pwd=" + pushApiMap.get("password") +
                                    "&msisdn=" + pushApiMap.get("msisdn") +
                                    "&packageid=" + pushApiMap.get("package") +
                                    "&pin=" + pushApiMap.get("pin") +
                                    "&token=" + pushApiMap.get("token") +
                                    "&txnid=" + pushApiMap.get("txn");
                            URL url = new URL(urlString);
                            new EtisalatConfirmAPI().execute(url);
                        } else {
                            showToast("Please Write Number");
                        }
                    } catch (Exception ex) {
                        //utility.showToast(ex.toString());
                        Log.d("RESULT", ex.toString());
                    }
                } else {
                    showToast("No Internet");
                }
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

//    private void verifyPin(HashMap<String, String> map) {
//        showProgress();
//        Call<String> call = etisalatApiInterface.confirmPinSubscription(
//                map.get("user"),
//                map.get("password"),
//                map.get("msisdn"),
//                map.get("package"),
//                map.get("pin"),
//                map.get("token"),
//                map.get("txn")
//        );
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//                hideProgress();
//                if (response.isSuccessful() && response.code() == 200) {
//                    try {
//                        String result = response.body();
//                        String status = result.substring(0, result.lastIndexOf('|'));
//                        String token = result.substring(result.lastIndexOf('|')+1);
//                        HashMap<String, String> map = new HashMap<String, String>();
//                        if (status.equals("success")||status.equals("Already_Active")) {
//                            showToast("Verified");
//                            map.put(SUBS_MOBILE, map.get("msisdn"));
//                            map.put(SUBS_STATUS, "success");
//                        } else {
//                            showToast("Not Verified");
//                            map.put(SUBS_MOBILE, map.get("msisdn"));
//                            map.put(SUBS_STATUS, "failed");
//                        }
//                        writeSubscriptionStatus(map);
//                    } catch (Exception ex) {
//                        Log.d("RESULT", ex.toString());
//                    }
//                } else {
//                    showToast("Response not found");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.d("RESULT", t.toString());
//                hideProgress();
//            }
//        });
//    }

    /*private void checkSubscription(RequestBody requestBody) {
        showProgress();
        Call<ResponseBody> call = apiInterface.getSubscription(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                hideProgress();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.d("RESULT", jsonObject.toString());
                        JSONObject jsonResult = jsonObject.optJSONObject("result");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(SUBS_MOBILE, jsonResult.optString("mobile"));
                        map.put(SUBS_STATUS, jsonResult.optString("status"));
                        writeSubscriptionStatus(map);
                        if (jsonResult.optString("status").equals("failed")) {
                            generatePinDialog();
                        } else {
                            showToast("Already Subscribed");
                        }
                    } catch (Exception ex) {
                        Log.d("RESULT", ex.toString());
                    }
                } else {
                    showToast("Response not found");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("RESULT", t.toString());
                hideProgress();
            }
        });
    }*/


    /*
   ================ Show Progress Dialog ===============
   */
    public void showProgress(boolean isCancelable) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
    }

    /*
    ================ Hide Progress Dialog ===============
    */
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }


    /*
================ Show Toast Message ===============
*/
    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /*
    ================ Error Called Function ===============
    */

    public void call_error(Exception ex) {
        String error = ex.getMessage();
        //StackTraceElement[] message = ex.getStackTrace();
        showToast(error);
        Intent intent = new Intent(context, Splash.class);
        /*StringBuilder builder = new StringBuilder();
        int i=1;
        for (StackTraceElement trace : message) {
            builder.append("Exception "+i+"<br>File:"+trace.getFileName()+" | Method: "+trace.getMethodName()+" | Line: "+trace.getLineNumber()+"<br>");
            i++;
        }
        builder.append("Caused By:"+ex.toString());
        intent.putExtra("error",error);
        intent.putExtra("description",builder.toString());*/
        context.startActivity(intent);
    }

    /*
    =============== Set Window FullScreen ===============
    */
    public void setFullScreen() {
        Activity activity = ((Activity) context);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /*
        ================ Get Screen Width ===============
        */
    public HashMap<String, Integer> getScreenRes() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        map.put(KeyWord.SCREEN_WIDTH, width);
        map.put(KeyWord.SCREEN_HEIGHT, height);
        return map;
    }


    /*
    ================ Log function ===============
     */
    public void logger(String message) {
        Log.d(context.getString(R.string.tag), message);
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String date = sdf.format(new Date());
        //writeToFile(date+" -> "+message);
    }

    /*
    =============== Set Font ===============
    */
    public void setFont(View view) {
        Typeface tf = null;
        tf = Typeface.createFromAsset(context.getAssets(), "SolaimanLipi.ttf");
//        if(fontName.equals(Font.REGULAR)){
//            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
//        }
//        else if(fontName.equals(Font.LIGHT)){
//            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
//        }
//        else{
//            tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
//        }
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTypeface(tf);
        } else if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setTypeface(tf);
        } else if (view instanceof Button) {
            Button btn = (Button) view;
            btn.setTypeface(tf);
        } else if (view instanceof MenuItem) {
            MenuItem menuItem = (MenuItem) view;
            SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
            mNewTitle.setSpan(tf, 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            menuItem.setTitle(mNewTitle);
        }

    }

    public void setFonts(View[] views) {
        Typeface tf = null;
        tf = Typeface.createFromAsset(context.getAssets(), "SolaimanLipi.ttf");
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            if (getLangauge().equals("bn")) {
            }
            if (view instanceof RadioButton) {
                RadioButton rb = (RadioButton) view;
                rb.setTypeface(tf);
                if (getLangauge().equals("bn")) {
                    rb.setTextSize(16);
                } else {
                    rb.setTextSize(14);
                }
            } else if (view instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) view;
                checkBox.setTypeface(tf);
                if (getLangauge().equals("bn")) {
                    checkBox.setTextSize(16);
                } else {
                    checkBox.setTextSize(14);
                }
            } else if (view instanceof EditText) {
                EditText et = (EditText) view;
                et.setTypeface(tf);
                if (getLangauge().equals("bn")) {
                    et.setTextSize(16);
                } else {
                    et.setTextSize(14);
                }
            } else if (view instanceof TextView) {
                TextView tv = (TextView) view;
                tv.setTypeface(tf);
                if (getLangauge().equals("bn")) {
                    tv.setTextSize(16);
                } else {
                    tv.setTextSize(14);
                }
            } else if (view instanceof Button) {
                Button btn = (Button) view;
                btn.setTypeface(tf);
                if (getLangauge().equals("bn")) {
                    btn.setTextSize(16);
                } else {
                    btn.setTextSize(14);
                }
            } else if (view instanceof MenuItem) {
                MenuItem menuItem = (MenuItem) view;
                SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
                mNewTitle.setSpan(tf, 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                menuItem.setTitle(mNewTitle);
            }
        }
    }

    /*
   =============== Set Font ===============
   */
    public void setMenuFont(Menu menu) {
        Typeface tf = null;
        tf = Typeface.createFromAsset(context.getAssets(), "SolaimanLipi.ttf");
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
            mNewTitle.setSpan(new CustomTypefaceSpan("", tf), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            mNewTitle.setSpan(new AbsoluteSizeSpan(16, true), 0, mNewTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            menuItem.setTitle(mNewTitle);
        }

    }

    /*
============== Base64 Decode =========
*/
    public String decodeBase64(String message) {
        String text = "Conversion Error";
        try {
            byte[] bytes = null;
            bytes = Base64.decode(message, Base64.DEFAULT);
            text = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            logger(ex.toString());
        }
        return text;
    }

    /*
    ============== Base64 Encode =========
     */
    public String encodeBase64(String message) {
        String text = "Conversion Error";
        try {
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            text = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception ex) {
            logger(ex.toString());
        }
        return text;
    }


    /*
    =============== Get Image Resource ID by Name ==============
    */
    public int getResourceId(String name) {
        name = name.replace(" ", "").toLowerCase();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }


    /*
    =============== Create Circle Background ==============
    */
    public void customView(View v, int startColor, int endColor) {
        int[] colors = {startColor, endColor};
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        v.setBackground(shape);
        //v.setBackgroundDrawable(shape);
    }

    /*
    ================ Clear Text for EditText, Button, TextView ===============
    */
    public void clearText(View[] view) {
        for (View v : view) {
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            } else if (v instanceof Button) {
                ((Button) v).setText("");
            } else if (v instanceof TextView) {
                ((TextView) v).setText("");
            }
        }
    }

    /*
    ================ Hide Keyboard from Screen ===============
    */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /*
    ================ Show Keyboard to Screen ===============
    */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /*
    ================ Hide & Show Views ===============
    */
    public void hideAndShowView(View[] views, View view) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
        view.setVisibility(View.VISIBLE);
    }

    public void hideViews(View[] views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }


    /*
    ================ Bangla Number Converter ============
    */
    public String convertToBangle(String numbers) {
        String banglaNumber = "";
        for (int i = 0; i < numbers.length(); i++) {
            switch (numbers.charAt(i)) {
                case '1':
                    banglaNumber += context.getString(R.string.one);
                    break;
                case '2':
                    banglaNumber += context.getString(R.string.two);
                    break;
                case '3':
                    banglaNumber += context.getString(R.string.three);
                    break;
                case '4':
                    banglaNumber += context.getString(R.string.four);
                    break;
                case '5':
                    banglaNumber += context.getString(R.string.five);
                    break;
                case '6':
                    banglaNumber += context.getString(R.string.six);
                    break;
                case '7':
                    banglaNumber += context.getString(R.string.seven);
                    break;
                case '8':
                    banglaNumber += context.getString(R.string.eight);
                    break;
                case '9':
                    banglaNumber += context.getString(R.string.nine);
                    break;
                case '0':
                    banglaNumber += context.getString(R.string.zero);
                    break;
                default:
                    banglaNumber += numbers.charAt(i);
                    break;
            }
        }
        return banglaNumber;
    }


    private long getNextSchedule() {
        try {
            String[] schedules = context.getResources().getStringArray(R.array.schedules);
            long timeDistance = 0;
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
            long currentTimeInMillis = calendar.getTimeInMillis();
            for (int i = 0; i < schedules.length; i++) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                String date = sdf.format(new Date()) + " " + schedules[i];
                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.ENGLISH);
                Date formattedDate = sdf2.parse(date);
                long timeInMillis = formattedDate.getTime();
                long distance = timeInMillis - currentTimeInMillis;
                if (distance >= 0) {
                    if (timeDistance != 0) {
                        if (distance < timeDistance) {
                            timeDistance = distance;
                        }
                    } else {
                        timeDistance = distance;
                    }
                }
            }
            return timeDistance;
        } catch (Exception ex) {
            logger(ex.toString());
            return 0;
        }
    }

    /*
    ================ Write to file ============
    */
    public void writeToFile(String message) {
/*        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            String date = sdf.format(new Date());
            String value = date + " -> "+ message+"\n";
            if (isExternalStorageWritable() && isExternalStorageReadable()) {
                File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/RadioG");
                if (!directory.exists()) {
                    if(directory.mkdirs()){
                        logger("Directory Created");
                    }
                    else{
                        logger("Directory Not Created");
                    }
                }
                File file = new File(directory, "radiog.txt");
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                fileOutputStream.write(value.getBytes());
                fileOutputStream.close();
            } else {
                logger("External/Internal Storage is not available");
            }
        }
        catch (Exception ex){
            logger(ex.toString());
        }*/
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            logger("Directory not created");
        }
        return file;
    }

    public void setAlarm(int hour, int minute, int requestId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(context, requestId, intent, PendingIntent.FLAG_NO_CREATE) != null);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestId, intent, 0);
        if (!alarmUp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            logger("Next " + requestId + " Trigger After " + 2 * 60 + " Sec(s)");
            writeToFile("Setting Next Alarm on " + hour + ":" + minute);
        } else {
            writeToFile("Active Alarm " + hour + ":" + minute);
        }
    }

    public boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        }
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        }
        return (availableBlocks * blockSize);
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        long totalBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalBlocks = stat.getBlockCountLong();
        } else {
            totalBlocks = stat.getBlockCount();
        }
        return (totalBlocks * blockSize);
    }

    public String getAvailableExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }
            long availableBlocks = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                availableBlocks = stat.getAvailableBlocks();
            }
            return formatSize(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public String getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }
            long totalBlocks = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                totalBlocks = stat.getBlockCountLong();
            } else {
                totalBlocks = stat.getBlockCount();
            }
            return formatSize(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /*
   ================ Check File Size ===============
   */
    public long checkFileSize(String fileUrl) {
        long file_size = 0;
        try {
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            file_size = urlConnection.getContentLength();
        } catch (Exception ex) {
            logger(ex.toString());
        }
        return file_size;
    }


    /*
   ================ Check File Exists ===============
   */
    public boolean checkIfSongExists(SongModel songModel) {
        long file_size = 0;
        try {
            File directory = new File(context.getFilesDir(), "RadioG");
            if (!directory.exists()) {
                if (directory.mkdir()) {
                    logger("Directory Created");
                } else {
                    logger("Directory Not Created");
                }
            }
            File file = new File(directory.getAbsolutePath() + "/" + songModel.getId() + ".mp3");
            //                long fileSize = checkFileSize(context.getString(R.string.image_url)+songModel.getLink());
            //                if(file.length()<file_size){
            //                    file.delete();
            //                }
            return file.exists();
        } catch (Exception ex) {
            logger(ex.toString());
            return false;
        }
    }

    public Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if (!ussd.startsWith("tel:"))
            uriString += "tel:";

        for (char c : ussd.toCharArray()) {

            if (c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

    public String convertSecondsToHour(long seconds) {
        String second = String.valueOf(seconds % 60);
        String minute = String.valueOf((seconds / 60) % 60);
        String hour = String.valueOf((seconds / 60 / 60) % 60);
        if (second.length() < 2) {
            second = "0" + second;
        }
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (hour.length() < 2) {
            hour = "0" + hour;
        }
        return hour + ":" + minute + ":" + second;
    }


    public HashMap<String, Long> getNetworkInfo() {
        HashMap<String, Long> map = new HashMap<>();
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //get the UID for the selected app
            if (packageInfo.packageName.equals(context.getPackageName())) {
                int uid = packageInfo.uid;
                long received = TrafficStats.getUidRxBytes(uid);
                long send = TrafficStats.getUidTxBytes(uid);
                map.put("send", send /*+ getDataUsage("totalSend")*/);
                map.put("received", received /*+ getDataUsage("totalReceived")*/);
//                writeDataUsage(send, received);
                Log.v("" + uid, "Send :" + send + ", Received :" + received);
                return map;
            }
        }
        map.put("Send", Long.parseLong("0"));
        map.put("Received", Long.parseLong("0"));
        return map;
    }

    public void writeSubscriptionStatus(int trackId, Master master) {
        Gson gson = new GsonBuilder().create();
        SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(String.valueOf(trackId), gson.toJson(master));
        editor.commit();
    }

    /*
  =============== Check Version ===============
  */
    public boolean checkVersion(int versionCode) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int currentCode = packageInfo.versionCode;
            return currentCode < versionCode;
        } catch (Exception ex) {
            return false;
        }
    }

    /*public boolean isSubscribed(int trackId) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
            String value = sharedPref.getString(String.valueOf(trackId), "{}");
            JSONObject jsonObject = new JSONObject(value);
            if(!getOperator().equals("BK")) {
                if (getMdn().equals("17")) {
                    return (System.currentTimeMillis() <= Long.parseLong(jsonObject.optString("expiry")));
                } else {
                    Map<String, ?> allEntries = sharedPref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        value = sharedPref.getString(entry.getKey(), "{}");
                        if(entry.getKey().equals(String.valueOf(trackId)))
                            break;
                    }
                    jsonObject = new JSONObject(value);
                    return (System.currentTimeMillis() <= Long.parseLong(jsonObject.optString("expiry")));
                }
            }
            else{
                if(getBkashSubscription().getStatus().equals("subscribed")){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch (Exception ex){
            logger(ex.toString());
            return false;
        }
    }*/

    public void clearSubscription() {
        SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        SharedPreferences.Editor editor = sharedPref.edit();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            editor.putString(entry.getKey(), "{}");
        }
        editor.commit();
    }

    public String getPrice(int trackId) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
            String value = sharedPref.getString(String.valueOf(trackId), "{}");
            JSONObject jsonObject = new JSONObject(value);
            String price = jsonObject.optString("price");
            return getLangauge().equals("bn") ? convertToBangle(price) : price;
        } catch (Exception ex) {
            logger(ex.toString());
            return "0.00";
        }
    }

    public void writeDataUsage(long send, long received) {
        SharedPreferences sharedPref = context.getSharedPreferences("DATA_USAGE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("send", send);
        editor.putLong("received", received);
        editor.commit();
    }

    public void writeTotalDataUsage(long send, long received) {
        logger("Send: " + send);
        logger("Received: " + received);
        SharedPreferences sharedPref = context.getSharedPreferences("DATA_USAGE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("totalSend", getDataUsage("totalSend") + send);
        editor.putLong("totalReceived", getDataUsage("totalReceived") + received);
        editor.commit();
    }

    public long getDataUsage(String name) {
        SharedPreferences sharedPref = context.getSharedPreferences("DATA_USAGE", Context.MODE_PRIVATE);
        return sharedPref.getLong(name, 0);
    }

    public void writeMsisdn(Msisdn msisdn) {
        SharedPreferences sharedPref = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("id", msisdn.getId());
        editor.putString("operator", msisdn.getOperator());
        editor.putString("msisdn", String.valueOf(msisdn.getMsisdn()));
        editor.putString("comment", msisdn.getComment());
        editor.commit();
        clearSubscription();
    }

    /*public String getMsisdn() {
        SharedPreferences sharedPref = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return sharedPref.getString("msisdn", "8800000000000");
    }

    public String getOperator() {
        SharedPreferences sharedPref = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return sharedPref.getString("operator", "NA");
    }*/

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public HashMap<String, String> getDeviceInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Serial", Build.SERIAL);
        map.put("Model", Build.MODEL);
        map.put("Id", Build.ID);
        map.put("Manufacture", Build.MANUFACTURER);
        map.put("Type", Build.TYPE);
        map.put("User", Build.USER);
        map.put("Base", String.valueOf(Build.VERSION_CODES.BASE));
        map.put("Incremental", Build.VERSION.INCREMENTAL);
        map.put("Board", Build.BOARD);
        map.put("Brand", Build.BRAND);
        map.put("Host", Build.HOST);
        map.put("Version Code", Build.VERSION.RELEASE);
        return map;
    }

    /*public String getMdn(){
        String msisdnValue = getMsisdn();
        return msisdnValue.substring(3,5);
    }

    public boolean operatorExisted(){
        String msisdnValue = getMsisdn();
        String mdn = msisdnValue.substring(3,5);
        String[] operator = context.getResources().getStringArray(R.array.operator);
        for(int i=0; i<operator.length; i++){
            if(mdn.equals(operator[i])){
                return true;
            }
        }
        return false;
    }*/

    public String validateMsisdn(String msisdn) {
        if (msisdn.length() != 13) {
            return "13 Digit required";
        }
/*        if(msisdn.substring(0,5).equals("88015")){
            return "Operator not supported";
        }*/
        if (
                msisdn.substring(0, 5).equals("88010") ||
                        msisdn.substring(0, 5).equals("88011") ||
                        msisdn.substring(0, 5).equals("88012")
        ) {
            return "Invalid Operator";
        }
        return "OK";
    }

    public void setFirebaseToken(String token) {
        SharedPreferences sharedPref = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public String getFirebaseToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        return sharedPref.getString("token", "");
    }



    /*private void showConfirmation(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        switch (getMdn()){
            case "16":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "17":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.gp_confirmation_msg_bn) : context.getString(R.string.gp_confirmation_msg_en));
                break;
            case "18":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "19":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.banglalink_confirmation_msg_bn) : context.getString(R.string.banglalink_confirmation_msg_en));
                break;
        }
        //tv.setText(getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //checkMasterSubscription(0);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }*/

    private void showDeactivationConfirmation() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        tv.setText(getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //checkMasterSubscription(0);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    /*public void showPremiumDialog(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        if(!getOperator().equals("BK")) {
            switch (getMdn()) {
                case "18":
                    tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                    break;
                case "16":
                    tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                    break;
                case "13":
                    tv.setText(getLangauge().equals("bn") ? context.getString(R.string.gp_premium_message_bn) : context.getString(R.string.gp_premium_message_en));
                    break;
                case "17":
                    tv.setText(getLangauge().equals("bn") ? context.getString(R.string.gp_premium_message_bn) : context.getString(R.string.gp_premium_message_en));
                    break;
                case "19":
                    tv.setText(getLangauge().equals("bn") ? context.getString(R.string.bangalink_premium_message_bn) : context.getString(R.string.banglalink_premium_message_en));
                    break;
            }
        }
        else{
            tv.setText(getLangauge().equals("bn") ? context.getString(R.string.bkash_premium_message_bn) : context.getString(R.string.bkash_premium_message_en));
        }
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(!getOperator().equals("BK")) {
                    //activateSubscription("0");
                }
                else{
                    activeBkash();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }*/

    /*public void makeSubscriptionDialog(boolean showInterface) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            //Spinner spOperator = (Spinner)dialog.findViewById(R.id.sp_operator);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            RadioGroup rgOperator = (RadioGroup)dialog.findViewById(R.id.rg_operator);
            RadioButton rbGp = (RadioButton)dialog.findViewById(R.id.rb_gp);
            RadioButton rbRobi = (RadioButton)dialog.findViewById(R.id.rb_robi);
            //RadioButton rbAirtel = (RadioButton)dialog.findViewById(R.id.rb_airtel);
            RadioButton rbBlink = (RadioButton)dialog.findViewById(R.id.rb_blink);
            RadioButton rbBkash = (RadioButton)dialog.findViewById(R.id.rb_bkash);
            TextView phoneCode = (TextView)dialog.findViewById(R.id.phone_code);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            setFonts(new View[]{title, rbGp, rbRobi, rbBlink, rbBkash, phoneCode, phoneNumber, cancelBtn, submitBtn});
            //String[] operators = context.getResources().getStringArray(getLangauge().equals("bn")?R.array.operator_list_bn:R.array.operator_list_en);
            //OperatorAdapter operatorAdapter = new OperatorAdapter(context, operators);
            //spOperator.setAdapter(operatorAdapter);
            title.setText(getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
            rbGp.setText(getLangauge().equals("bn")?context.getString(R.string.gp_bn):context.getString(R.string.gp_en));
            rbRobi.setText(getLangauge().equals("bn")?context.getString(R.string.rb_bn):context.getString(R.string.rb_en));
            rbBlink.setText(getLangauge().equals("bn")?context.getString(R.string.bl_bn):context.getString(R.string.bl_en));
            rbBkash.setText(getLangauge().equals("bn")?context.getString(R.string.bk_bn):context.getString(R.string.bk_en));
            cancelBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    RadioButton rb = (RadioButton)dialog.findViewById(rgOperator.getCheckedRadioButtonId());
                    String msisdn = "88"+phoneNumber.getText().toString();
                    String message = validateMsisdn(msisdn);
                    if(message.equals("OK")){
                        Msisdn mdn = new Msisdn();
                        mdn.setId(1);
                        mdn.setOperator(rb.getTag().toString());
                        mdn.setMsisdn(Long.parseLong(msisdn));
                        mdn.setComment("User Prompt");
                        writeMsisdn(mdn);
                        //showToast("Number Set");
                        if(showInterface) {
                            subscriptionInterfacce.numberSet();
                        }
                    }
                    else{
                        showToast(message);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            showToast(ex.toString());
        }
    }*/

    private void validatePinDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.rg_operator);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            TextView phoneCode = (TextView) dialog.findViewById(R.id.phone_code);
            phoneCode.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(getLangauge().equals("bn") ? context.getString(R.string.number_pin_bn) : context.getString(R.string.number_pin_en));
            phoneNumber.setHint("PIN e.g. XXXX");
            cancelBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = phoneNumber.getText().toString();
                    if (pin.length() > 0) {
                        dialog.dismiss();
                        //activateSubscription(phoneNumber.getText().toString());
                    } else {
                        showToast("Pin Required");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            showToast(ex.toString());
        }
    }


/*    private void activateSubscription(String pin){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.activation(context.getString(R.string.authorization_key), getOperator(), getMsisdn(), getFirebaseToken(), "0", pin);
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        if(getMdn().equals("18")){
                            showConfirmation();
                        }
                        else {
                            if (master.getComment().equals("PIN Request Success")) {
                                if (getMdn().equals("19")||getMdn().equals("17")) {
                                    validatePinDialog();
                                } else {
                                    showConfirmation();
                                }
                            } else if (master.getComment().equals("Charge Request Success")) {
                                showConfirmation();
                            } else {
                                showToast("PIN Process Failed");
                            }
                        }
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
        }
    }

    public void deactivateSubscription(){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.deactivation(context.getString(R.string.authorization_key), getOperator(), getMsisdn(), getFirebaseToken());
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        writeSubscriptionStatus(0,master);
                        if(master.getExpiry().equals("0")) {
                            showDeactivationConfirmation();
                        }
                        else{
                            showToast("Deactivation Failed");
                        }
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
        }
    }

    public void checkMasterSubscription(int trackId){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.viewstatus(context.getString(R.string.authorization_key), getOperator(), getMsisdn(), getFirebaseToken(), String.valueOf(trackId));
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        writeSubscriptionStatus(trackId, master);
                        subscriptionInterfacce.viewStatus();
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
            logger(ex.toString());
        }
    }*/

    public List<SongModel> filterSong(List<SongModel> songModels) {
        List<SongModel> models = new ArrayList<>();
        for (int i = 0; i < songModels.size(); i++) {
            SongModel songModel = songModels.get(i);
            if (songModel.getId() != 0) {
                models.add(songModel);
            }
        }
        return models;
    }

    public int getSongPosition(List<SongModel> songModels, int songId) {
        for (int i = 0; i < songModels.size(); i++) {
            SongModel songModel = songModels.get(i);
            if (songModel.getId() == songId) {
                return i;
            }
        }
        return -1;
    }

    public void shareTrack(String name, int trackId) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = context.getString(R.string.share_url) + trackId;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", shareBodyText);
        clipboardManager.setPrimaryClip(clipData);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(intent, "Share with"));
    }

    public String getAuthorization() {
        return "Basic " + encodeBase64("gtech:gtech321").trim();
    }

    public void setConfig(Config config) {
        SharedPreferences sharedPref = context.getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Config.LANGUAGE, config.getDefault_language());
        editor.putString(Config.MODE, config.getMode());
        editor.putString(Config.NOTICE, config.getNotice());
        editor.putString(Config.NOTICE_MSG_BN, config.getNotice_msg_bn());
        editor.putString(Config.NOTICE_MSG_EN, config.getNotice_msg_en());
        editor.putInt(Config.VERSION_CONTROL, config.getVersion_control());
        editor.putString(Config.VERSION_MSG_BN, config.getVersion_msg_bn());
        editor.putString(Config.VERSION_MSG_EN, config.getVersion_msg_en());
        editor.putString(Config.VERSION_UPDATE, config.getVersion_update());
        editor.commit();
    }

    public Config getConfig() {
        SharedPreferences sharedPref = context.getSharedPreferences("CONFIG", Context.MODE_PRIVATE);
        Config config = new Config();
        config.setDefault_language(sharedPref.getString(Config.LANGUAGE, "EN"));
        config.setMode(sharedPref.getString(Config.MODE, "PREMIUM"));
        config.setNotice(sharedPref.getString(Config.NOTICE, "OFF"));
        config.setNotice_msg_bn(sharedPref.getString(Config.NOTICE_MSG_BN, ""));
        config.setNotice_msg_en(sharedPref.getString(Config.NOTICE_MSG_EN, ""));
        config.setVersion_control(sharedPref.getInt(Config.VERSION_CONTROL, 1));
        config.setVersion_msg_bn(sharedPref.getString(Config.VERSION_MSG_BN, ""));
        config.setVersion_msg_en(sharedPref.getString(Config.VERSION_MSG_EN, ""));
        config.setVersion_update(sharedPref.getString(Config.VERSION_UPDATE, "REGULAR"));
        return config;
    }

    public void setFirstTime() {
        SharedPreferences sharedPref = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();
    }

    public boolean isFirstTime() {
        SharedPreferences sharedPref = context.getSharedPreferences("APP_CONFIG", Context.MODE_PRIVATE);
        return sharedPref.getBoolean("firstTime", true);
    }

    /*public String getOperatorName(){
        switch (getOperator()){
            case "GP":
                return getLangauge().equals("bn")?context.getString(R.string.gp_bn):context.getString(R.string.gp_en);
            case "RB":
                return getLangauge().equals("bn")?context.getString(R.string.rb_bn):context.getString(R.string.rb_en);
            case "BL":
                return getLangauge().equals("bn")?context.getString(R.string.bl_bn):context.getString(R.string.bl_en);
            case "BK":
                return getLangauge().equals("bn")?context.getString(R.string.bk_bn):context.getString(R.string.bk_en);
        }
        return "";
    }*/

    public void setBkashSubscription(Bkash bkash) {
        SharedPreferences sharedPref = context.getSharedPreferences("BKASH", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("msisdn", bkash.getMsisdn());
        editor.putString("status", bkash.getStatus());
        editor.putLong("expiry", bkash.getExpiry());
        editor.putString("url", bkash.getUrl());
        editor.commit();
    }

    public Bkash getBkashSubscription() {
        SharedPreferences sharedPref = context.getSharedPreferences("BKASH", Context.MODE_PRIVATE);
        Bkash bkash = new Bkash();
        bkash.setMsisdn(sharedPref.getString("msisdn", ""));
        bkash.setStatus(sharedPref.getString("status", ""));
        bkash.setExpiry(sharedPref.getLong("expiry", 0));
        bkash.setUrl(sharedPref.getString("url", ""));
        return bkash;
    }

    /*public void viewBkashStatus(){
        showProgress(true);
        Call<List<Bkash>> call = masterApiInterface.bkashViewStatus(getAuthorization(), getOperator(), getMsisdn(), getFirebaseToken());
        call.enqueue(new Callback<List<Bkash>>() {
            @Override
            public void onResponse(Call<List<Bkash>> call, Response<List<Bkash>> response) {
                hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    List<Bkash> bkashes = response.body();
                    Bkash b = bkashes.get(0);
                    setBkashSubscription(b);
                    subscriptionInterfacce.viewStatus();
                }
                else{
                    showToast(String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Bkash>> call, Throwable t) {
                hideProgress();
            }
        });
    }*/

    /*public void activeBkash(){
        showProgress(true);
        Call<List<Bkash>> call = masterApiInterface.bkashActivation(getAuthorization(), getOperator(), getMsisdn(), getFirebaseToken());
        call.enqueue(new Callback<List<Bkash>>() {
            @Override
            public void onResponse(Call<List<Bkash>> call, Response<List<Bkash>> response) {
                hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    List<Bkash> bkashes = response.body();
                    Bkash b = bkashes.get(0);
                    setBkashSubscription(b);
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("url", b.getUrl());
                    context.startActivity(intent);
                }
                else{
                    showToast(String.valueOf(response.code()));
                    //initiateView();
                }
            }

            @Override
            public void onFailure(Call<List<Bkash>> call, Throwable t) {
                hideProgress();
                //initiateView();
            }
        });
    }*/

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public String time_convert(String s) {
        String r = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            r = formatter.format(new Date(s));
        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return r;
    }

    public String millisToDate(long millis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String date = getFormattedDate(mYear, mMonth, mDay);
        return date;
    }

    public String getFormattedDate(int year, int month, int day) {
        month = month + 1;
        String y = String.valueOf(year);
        String m = String.valueOf(month);
        String d = String.valueOf(day);
        if (m.length() == 1) m = "0" + m;
        if (d.length() == 1) d = "0" + d;
        return d + "-" + m + "-" + y;
    }

    public String unicodetobangla(String s) {
        String r = "";
        try {
            r = Html.fromHtml(s).toString();

        } catch (Exception e) {
            Log.d("Error Line Number", Log.getStackTraceString(e));
        }
        return r;
    }


}
