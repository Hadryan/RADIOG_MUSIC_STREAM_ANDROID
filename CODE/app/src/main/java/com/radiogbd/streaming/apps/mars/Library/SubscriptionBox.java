package com.radiogbd.streaming.apps.mars.Library;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Activity.BrowserActivity;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.SubscriptionInterfacce;
import com.radiogbd.streaming.apps.mars.Model.Msisdn;
import com.radiogbd.streaming.apps.mars.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscriptionBox {

    private static String FILE_NAME = "subscription";
    private static String MOBILE_NUMBER = "mobile";
    private static String OPERATOR = "operator";
    private static String EXPIRE_TIME = "expire_time";
    private static String SUBSCRIPTION_STATUS = "sub_status";
    //String[] operators = new String[4];
    Context context;
    Utility utility;
    int apiCounter = 0;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    boolean needResult = false;
    SubscriptionInterfacce subscriptionInterfacce;

    public SubscriptionBox(Context context) {
        try {
            this.context = context;
            utility = new Utility(this.context);
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
    }

    public SubscriptionBox(Context context, SubscriptionInterfacce subscriptionInterfacce, boolean needResult) {
        this.context = context;
        utility = new Utility(this.context);
        this.subscriptionInterfacce = subscriptionInterfacce;
        this.needResult = needResult;
    }

    public boolean showSubscription() {
        return getOperator().equals("NA") || getMobile().length() > 0;
    }

    public String[] getOperators() {
        String[] operators = context.getResources().getStringArray(R.array.tags);
        return operators;
    }

    public void setApiCounter(int apiCounter) {
        this.apiCounter = apiCounter;
    }

    public void setMobile(String mobileNumber) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobile() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(MOBILE_NUMBER, "");
    }

    public void setOperator(String operator) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(OPERATOR, operator);
        editor.commit();
    }

    public String getOperator() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(OPERATOR, "NA");
    }

    public void setExpireTime(String expireTime) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(EXPIRE_TIME, expireTime);
        editor.commit();
    }

    public String getExpireTime() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(EXPIRE_TIME, "0");
    }

    public void setSubscription(String trackId, String subscriptionStatus) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(trackId, subscriptionStatus);
        editor.commit();
    }

    public String getSubscription(String trackId) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(trackId, "{}");
    }

    public boolean isSubscribed(String trackId) {
        try {
            if (!getOperator().equals("GP") || true) {
                JSONObject content = new JSONObject(getSubscription("0"));
                return System.currentTimeMillis() <= Long.parseLong(content.optString("expiry"));
            } else {
                JSONObject content = new JSONObject(getSubscription(trackId));
                return System.currentTimeMillis() <= Long.parseLong(content.optString("expiry"));
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
            utility.logger("sub");
            return false;
        }
    }

    public boolean isSubscribedDataSet() {
        try {
            return !getSubscription("0").equals("{}");
        } catch (Exception ex) {
            utility.logger(ex.toString());
            return false;
        }
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SUBSCRIPTION_STATUS, subscriptionStatus);
        editor.commit();
    }

    public String getSubscriptionStatus() {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(SUBSCRIPTION_STATUS, "");
    }

    public String getOperatorName() {
        if (getOperator().equals(context.getString(R.string.tag_gp))) {
            return utility.getLangauge().equals("bn") ? context.getString(R.string.gp_bn) : context.getString(R.string.gp_en);
        } else if (getOperator().equals(context.getString(R.string.tag_rb))) {
            return utility.getLangauge().equals("bn") ? context.getString(R.string.rb_bn) : context.getString(R.string.rb_en);
        } else if (getOperator().equals(context.getString(R.string.tag_bl))) {
            return utility.getLangauge().equals("bn") ? context.getString(R.string.bl_bn) : context.getString(R.string.bl_en);
        } else if (getOperator().equals(context.getString(R.string.tag_gr))) {
            return utility.getLangauge().equals("bn") ? context.getString(R.string.bk_gr_bn) : context.getString(R.string.bk_gr_en);
        } else if (getOperator().equals(context.getString(R.string.tag_on))) {
            return utility.getLangauge().equals("bn") ? context.getString(R.string.bk_on_bn) : context.getString(R.string.bk_on_en);
        } else {
            return "";
        }
        /*switch (getOperator()){
            case "GP":
                return utility.getLangauge().equals("bn")?context.getString(R.string.gp_bn):context.getString(R.string.gp_en);
            case "RB":
                return utility.getLangauge().equals("bn")?context.getString(R.string.rb_bn):context.getString(R.string.rb_en);
            case "BL":
                return utility.getLangauge().equals("bn")?context.getString(R.string.bl_bn):context.getString(R.string.bl_en);
            case "GR":
                return utility.getLangauge().equals("bn")?context.getString(R.string.bk_gr_bn):context.getString(R.string.bk_gr_en);
            case "ON":
                return utility.getLangauge().equals("bn")?context.getString(R.string.bk_on_bn):context.getString(R.string.bk_on_en);
        }
        return "";*/
    }

    /*private void observInitialSubscriptionCheck(){
        if(apiCounter==3){
            //utility.hideProgress();
            if(needResult) {
                subscriptionInterfacce.numberSet();
            }
        }
    }*/

    /*################## Dialog Parts ####################*/

    /*public void showNumberInputDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            RadioGroup rgOperator = (RadioGroup)dialog.findViewById(R.id.rg_operator);
            RadioButton rbGp = (RadioButton)dialog.findViewById(R.id.rb_gp);
            RadioButton rbRobi = (RadioButton)dialog.findViewById(R.id.rb_robi);
            RadioButton rbBlink = (RadioButton)dialog.findViewById(R.id.rb_blink);
            RadioButton rbBkash = (RadioButton)dialog.findViewById(R.id.rb_bkash);
            TextView phoneCode = (TextView)dialog.findViewById(R.id.phone_code);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            rgOperator.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, rbGp, rbRobi, rbBlink, rbBkash, phoneCode, phoneNumber, cancelBtn, submitBtn});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
            rbGp.setText(utility.getLangauge().equals("bn")?context.getString(R.string.gp_bn):context.getString(R.string.gp_en));
            rbRobi.setText(utility.getLangauge().equals("bn")?context.getString(R.string.rb_bn):context.getString(R.string.rb_en));
            rbBlink.setText(utility.getLangauge().equals("bn")?context.getString(R.string.bl_bn):context.getString(R.string.bl_en));
            rbBkash.setText(utility.getLangauge().equals("bn")?context.getString(R.string.bk_bn):context.getString(R.string.bk_en));
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
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
                    String message = utility.validateMsisdn("88"+phoneNumber.getText().toString());
                    if(message.equals("OK")){
                        setMobile("88"+phoneNumber.getText().toString());
                        utility.showProgress(true);
                        apiCounter = 0;
                        for(int i=0; i<operators.length; i++){
                            checkInitialSubscription(operators[i]);
                        }

                    }
                    else{
                        utility.showToast(message);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            utility.showToast(ex.toString());
        }
    }*/

    /*public void showOperatorSelectDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            RadioGroup rgOperator = (RadioGroup)dialog.findViewById(R.id.rg_operator);
            RadioButton rbGp = (RadioButton)dialog.findViewById(R.id.rb_gp);
            RadioButton rbRobi = (RadioButton)dialog.findViewById(R.id.rb_robi);
            RadioButton rbBlink = (RadioButton)dialog.findViewById(R.id.rb_blink);
            RadioButton rbBkash = (RadioButton)dialog.findViewById(R.id.rb_bkash);
            TextView phoneCode = (TextView)dialog.findViewById(R.id.phone_code);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            title.setVisibility(View.GONE);
            phoneCode.setVisibility(View.GONE);
            phoneNumber.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, rbGp, rbRobi, rbBlink, rbBkash, phoneCode, phoneNumber, cancelBtn, submitBtn});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
            rbGp.setText(utility.getLangauge().equals("bn")?context.getString(R.string.gp_bn):context.getString(R.string.gp_en));
            rbRobi.setText(utility.getLangauge().equals("bn")?context.getString(R.string.rb_bn):context.getString(R.string.rb_en));
            rbBlink.setText(utility.getLangauge().equals("bn")?context.getString(R.string.bl_bn):context.getString(R.string.bl_en));
            rbBkash.setText(utility.getLangauge().equals("bn")?context.getString(R.string.bk_bn):context.getString(R.string.bk_en));
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
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
                    setOperator(rb.getTag().toString());
                    if(needResult) {
                        subscriptionInterfacce.numberSet();
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            utility.showToast(ex.toString());
        }
    }*/

    private void validatePinDialog(String trackId) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
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
            utility.setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_pin_bn) : context.getString(R.string.number_pin_en));
            phoneNumber.setHint("PIN e.g. XXXX");
            cancelBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
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
                        validateSubscription(trackId, phoneNumber.getText().toString());
                    } else {
                        utility.showToast("Pin Required");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    private void showConfirmation() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
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
        switch (getOperator()) {
            case "RB":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "GP":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_confirmation_msg_bn) : context.getString(R.string.gp_confirmation_msg_en));
                break;
/*            case "BK":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;*/
            case "BL":
                tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.banglalink_confirmation_msg_bn) : context.getString(R.string.banglalink_confirmation_msg_en));
                break;
        }
        //tv.setText(getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (getOperator()) {
                    case "BL":
                        checkSubscription("0");
                        break;
                    case "RB":
                        checkSubscription("0");
                        break;
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
    }

    private void showDeactivationConfirmation() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = utility.getScreenRes();
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
        tv.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams) yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (getOperator()) {
                    case "BL":
                        checkSubscription("0");
                        break;
                    case "RB":
                        checkSubscription("0");
                        break;
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
    }


    /*######################### API Call Parts ########################*/

    /*public void checkInitialSubscription(String operatorCode){
        //utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.viewstatus(utility.getAuthorization(), operatorCode, getMobile(), utility.getFirebaseToken(), "0");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //utility.hideProgress();
                if(response.isSuccessful()&&response.code()==200){
                    try{
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        if(System.currentTimeMillis() <= Long.parseLong(content.optString("expiry"))){
                            setOperator(operatorCode);
                            setExpireTime(content.optString("expiry"));
                        }
                    }
                    catch (Exception ex){
                        utility.logger(ex.toString());
                    }
                }
                apiCounter++;
                observInitialSubscriptionCheck();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //utility.hideProgress();
                utility.logger(t.toString());
                apiCounter++;
                observInitialSubscriptionCheck();
            }
        });
    }*/


    public void checkSubscription(String trackId) {
        utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.viewstatus(utility.getAuthorization(), getOperator(), getMobile(), utility.getFirebaseToken(), trackId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utility.hideProgress();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        setSubscription(trackId, content.toString());
                        if (needResult) {
                            subscriptionInterfacce.viewStatus();
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.hideProgress();
                utility.logger(t.toString());
            }
        });
    }

    public void activateSubscription(String trackId, String pin) {
        utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.activation(utility.getAuthorization(), getOperator(), getMobile(), utility.getFirebaseToken(), trackId, pin);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utility.hideProgress();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        utility.logger("abir get msg" + trackId + content.toString());
                        setSubscription(trackId, content.toString());
                        Intent intent = new Intent(context, BrowserActivity.class);
                        if (getOperator().equals(context.getString(R.string.tag_bl))) {
                            validatePinDialog("0");
                        } else if (getOperator().equals(context.getString(R.string.tag_rb))) {
                            showConfirmation();
                        } else if (getOperator().equals(context.getString(R.string.tag_gr))) {
                            intent.putExtra("url", content.optString("url"));
                            intent.putExtra("tag", "GR");
                            context.startActivity(intent);
                        } else if (getOperator().equals(context.getString(R.string.tag_on))) {
                            intent.putExtra("url", content.optString("url"));
                            intent.putExtra("tag", "ON");
                            context.startActivity(intent);
                        } else if (getOperator().equals(context.getString(R.string.tag_skitto))) {
                            setSubscription("0", content.toString());
                            String url = content.optString("url");
                            if (!TextUtils.isEmpty(url)) {
                                intent.putExtra("url", url);
                                intent.putExtra("tag", "SK");
                                context.startActivity(intent);
                            } else {
                                utility.showToast("Not a Skitto Sim");
                            }

                        } else if (getOperator().equals(context.getString(R.string.tag_gp))) {
                            validatePinDialog(trackId);
                            //utility.showToast("Please Subscribe to view this content");
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.hideProgress();
                utility.logger(t.toString());
            }
        });
    }

    public void validateSubscription(String trackId, String pin) {
        utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.activation(utility.getAuthorization(), getOperator(), getMobile(), utility.getFirebaseToken(), trackId, pin);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utility.hideProgress();
                utility.logger("abir valid" + trackId + response.toString());
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);

                        if (getOperator().equals(context.getString(R.string.tag_bl))) {
                            Intent intent = new Intent(context, BrowserActivity.class);
                            intent.putExtra("url", content.optString("comment"));
                            intent.putExtra("tag", "BL");
                            context.startActivity(intent);
                        } else {
                            setSubscription(trackId, content.toString());
                            showConfirmation();
                            if (needResult) {
                                subscriptionInterfacce.viewStatus();
                            }
                        }
                        if (getOperator().equalsIgnoreCase("GP")) {
                            //utility.logger("abir valid time" +  content.toString());
                            if (System.currentTimeMillis() <= Long.parseLong(content.optString("expiry"))) {
                                setExpireTime(content.optString("expiry"));
                                setSubscription("0", content.toString());
                            }
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.hideProgress();
                utility.logger(t.toString());
            }
        });
    }

    public void deactivateSubscription() {
        utility.showProgress(false);
        try {
            Call<ResponseBody> call = apiInterface.deactivation(context.getString(R.string.authorization_key), getOperator(), getMobile(), utility.getFirebaseToken());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            JSONArray responseBody = new JSONArray(response.body().string());
                            JSONObject content = responseBody.optJSONObject(0);
                            setSubscription("0", content.toString());
                            showDeactivationConfirmation();
                        } catch (Exception ex) {
                            utility.logger(ex.toString());
                        }
                    } else {
                        utility.logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                }
            });
        } catch (Exception ex) {
            utility.hideProgress();
        }
    }

    public void viewSubscription(String trackId) {
        utility.showProgress(true);
        Call<ResponseBody> call = apiInterface.viewstatus(utility.getAuthorization(), getOperator(), getMobile(), utility.getFirebaseToken(), trackId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                utility.hideProgress();
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        setSubscription(trackId, content.toString());
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.hideProgress();
                utility.logger(t.toString());
            }
        });
    }

}
