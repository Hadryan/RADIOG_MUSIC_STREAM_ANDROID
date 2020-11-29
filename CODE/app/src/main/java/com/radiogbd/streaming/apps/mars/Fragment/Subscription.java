package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.radiogbd.streaming.apps.mars.Adapter.NoticeAdapter;
import com.radiogbd.streaming.apps.mars.Http.BlbaseInterface;
import com.radiogbd.streaming.apps.mars.Http.Blbaseclient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.SubscriptionInterfacce;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.NoticeModel;
import com.radiogbd.streaming.apps.mars.Model.Plan;
import com.radiogbd.streaming.apps.mars.Model.Plantext;
import com.radiogbd.streaming.apps.mars.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Subscription extends Fragment implements SubscriptionInterfacce {

    Context context;
    Utility utility;
    SubscriptionBox subscriptionBox;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    //Subscription Layout
    LinearLayout llSubscribed;
    TextView tvMsisdnLabel, tvMsisdn, tvOperatorlabel, tvOperator, tvPrice, tvTime, tvTimelevel, tvStatusLabel, tvStatus, tvPackage;
    Button btnAction;

    //Unsubscription Layout
    LinearLayout llUnsubscribed, llOption;
    TextView tvNumberLabel, tvNumber;
    EditText etPhoneNumber;
    Button btnCheck, btnSubmit;
    RadioGroup rgOperator;
    RadioButton rbGp, rbRobi, rbBlink, rbGrBkash, rbOnBkash, rbskitto;

    int apiCounter = 0;
    String mobile = "";

    Plantext plangp;
    Plantext planrobi;
    Plantext planBanglalink;
    Plantext planghuri;
    Plantext planonmbl;
    Plantext planskitto;


    public Subscription() {
    }

    @SuppressLint("ValidFragment")
    public Subscription(Context context) {
        this.context = context;
        utility = new Utility(this.context, this);
        subscriptionBox = new SubscriptionBox(this.context, Subscription.this, true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.service_layout, null);

        //Subscribed Layout
        llSubscribed = (LinearLayout) view.findViewById(R.id.ll_subscribed);
        tvMsisdnLabel = (TextView) view.findViewById(R.id.tv_msisdn_label);
        tvMsisdn = (TextView) view.findViewById(R.id.tv_msisdn);
        tvOperatorlabel = (TextView) view.findViewById(R.id.tv_operator_label);
        tvOperator = (TextView) view.findViewById(R.id.tv_operator);
        tvPrice = (TextView) view.findViewById(R.id.tv_price);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTimelevel = (TextView) view.findViewById(R.id.tv_time_label);
        tvStatusLabel = (TextView) view.findViewById(R.id.tv_status_label);
        tvStatus = (TextView) view.findViewById(R.id.tv_status);
        tvPackage = (TextView) view.findViewById(R.id.tv_package);
        btnAction = (Button) view.findViewById(R.id.btn_action);

        //Unsubscribed Layout
        llUnsubscribed = (LinearLayout) view.findViewById(R.id.ll_unsusbcribed);
        llOption = (LinearLayout) view.findViewById(R.id.ll_option);
        tvNumberLabel = (TextView) view.findViewById(R.id.tv_number_label);
        tvNumber = (TextView) view.findViewById(R.id.tv_number);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
        btnCheck = (Button) view.findViewById(R.id.btn_check);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        rgOperator = (RadioGroup) view.findViewById(R.id.rg_operator);
        rbGp = (RadioButton) view.findViewById(R.id.rb_gp);
        rbRobi = (RadioButton) view.findViewById(R.id.rb_robi);
        rbBlink = (RadioButton) view.findViewById(R.id.rb_blink);
        rbGrBkash = (RadioButton) view.findViewById(R.id.rb_bkash_ghoori);
        rbOnBkash = (RadioButton) view.findViewById(R.id.rb_bkash_onmobile);
        rbskitto = (RadioButton) view.findViewById(R.id.rb_skitoo);
        utility.setFonts(
                new View[]{
                        tvMsisdnLabel,
                        tvMsisdn,
                        tvOperatorlabel,
                        tvOperator,
                        tvPrice,
                        tvTime,
                        tvTimelevel,
                        tvStatusLabel,
                        tvStatus,
                        tvPackage,
                        btnAction,
                        tvNumberLabel,
                        tvNumber,
                        etPhoneNumber,
                        btnCheck,
                        btnSubmit,
                        rbGp,
                        rbRobi,
                        rbBlink,
                        rbGrBkash,
                        rbOnBkash,
                        rbskitto
                });
        initiateView();
        return view;
    }

    private void initiateView() {
        if (subscriptionBox.getMobile().length() == 0 || subscriptionBox.getOperator().equals("NA")) {
            unsubscribedView();
        } else {
            showSubscribedView(true);
        }
    }

    private void unsubscribedView() {
        utility.hideAndShowView(new View[]{llSubscribed, llUnsubscribed}, llUnsubscribed);
        tvNumberLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
        btnCheck.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.check_subscription_bn) : context.getString(R.string.check_subscription_en));
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utility.hideKeyboard(view);
                String message = utility.validateMsisdn("88" + etPhoneNumber.getText().toString());
                if (message.equals("OK")) {
                    utility.showProgress(true);
                    getSubscriptionText();
                    mobile = etPhoneNumber.getText().toString();
                    String[] operators = subscriptionBox.getOperators();
                    subscriptionBox.setApiCounter(0);
                    for (int i = 0; i < operators.length; i++) {
                        checkInitialSubscription("88" + etPhoneNumber.getText().toString(), operators[i]);
                    }
                } else {
                    utility.showToast(message);
                }
            }
        });
    }

    private void showSubscribedView(boolean checkSubscription) {
        utility.hideAndShowView(new View[]{llSubscribed, llSubscribed}, llSubscribed);
        tvStatusLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.service_status_bn) : context.getString(R.string.service_status_en));
        tvMsisdnLabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.service_number_bn) : context.getString(R.string.service_number_en));
        tvOperatorlabel.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.service_operator_bn) : context.getString(R.string.service_operator_en));
        tvOperator.setText(subscriptionBox.getOperatorName());
        String number = subscriptionBox.getMobile().substring(2);
        if (utility.getLangauge().equals("bn")) {
            tvMsisdn.setText(utility.convertToBangle(number));
        } else {
            tvMsisdn.setText(number);
        }
        String operator = subscriptionBox.getOperator();
        if (!subscriptionBox.getExpireTime().equalsIgnoreCase("0")) {
            String time = utility.millisToDate(Long.parseLong(subscriptionBox.getExpireTime()));
            //utility.logger("time " + time);
            if (!TextUtils.isEmpty(time)) {
                tvTime.setVisibility(View.VISIBLE);
                tvTimelevel.setText(utility.getLangauge().equals("bn") ? getString(R.string.end_time_bn) : getString(R.string.end_time_en));
                tvTime.setText(time);
            } else {
                tvTime.setVisibility(View.GONE);
            }
        }

        if (operator.equals(context.getString(R.string.tag_gp))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? getString(R.string.gp_package_bn) : getString(R.string.gp_package_en));
            tvStatus.setText(utility.getLangauge().equals("bn") ? getString(R.string.status_on_bn) : getString(R.string.status_on_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.gp_message_bn) : getString(R.string.price_msg_en) + "\n" + getString(R.string.gp_message_en));
            utility.hideViews(new View[]{tvStatusLabel, tvStatus, tvPackage, btnAction});
        } else if (operator.equals(context.getString(R.string.tag_rb))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.robi_package_bn) : context.getString(R.string.robi_package_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.rb_message_bn) : getString(R.string.price_msg_en) + "\n" + getString(R.string.rb_message_en));
            if (checkSubscription) {
                subscriptionBox.checkSubscription("0");
            }
        } else if (operator.equals(context.getString(R.string.tag_bl))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.blink_package_bn) : context.getString(R.string.blink_package_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.bl_message_bn) : getString(R.string.price_msg_en) + "\n" + getString(R.string.bl_message_en));
            if (checkSubscription) {
                subscriptionBox.checkSubscription("0");
            }
        } else if (operator.equals(context.getString(R.string.tag_gr))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.rg_monthly_bn) : context.getString(R.string.rg_monthly_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.bk_gr_message_bn).replace("\n", "") : getString(R.string.price_msg_en) + "\n" + getString(R.string.bk_gr_message_en).replace("\n", ""));
            if (checkSubscription) {
                subscriptionBox.checkSubscription("0");
            }
        } else if (operator.equals(context.getString(R.string.tag_on))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.rg_monthly_bn) : context.getString(R.string.rg_monthly_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.bk_on_message_bn).replace("\n", "") : getString(R.string.price_msg_en) + "\n" + getString(R.string.bk_on_message_en).replace("\n", ""));
            if (checkSubscription) {
                subscriptionBox.checkSubscription("0");
            }
        } else if (operator.equals(context.getString(R.string.tag_skitto))) {
            tvPackage.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_message_bn) : context.getString(R.string.skitto_message_en));
            //tvPrice.setText(utility.getLangauge().equals("bn") ? getString(R.string.price_msg_bn) + "\n" + getString(R.string.bk_on_message_bn).replace("\n", "") : getString(R.string.price_msg_en) + "\n" + getString(R.string.bk_on_message_en).replace("\n", ""));
            tvPrice.setText("");
            if (checkSubscription) {
                subscriptionBox.checkSubscription("0");
            }
        }
    }

    public void checkInitialSubscription(String mobile, String operatorCode) {
        Call<ResponseBody> call = apiInterface.viewstatus(utility.getAuthorization(), operatorCode, mobile, utility.getFirebaseToken(), "0");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        JSONArray responseBody = new JSONArray(response.body().string());
                        JSONObject content = responseBody.optJSONObject(0);
                        utility.logger("abir" + operatorCode + content.toString());
                        String bl_url = content.optString("comment");
                        if (bl_url != null && !TextUtils.isEmpty(bl_url)) {
                            bl_check(bl_url.replaceAll("\\\\", ""));
                        }
                        if (System.currentTimeMillis() <= Long.parseLong(content.optString("expiry"))) {
                            subscriptionBox.setMobile(mobile);
                            subscriptionBox.setOperator(operatorCode);
                            subscriptionBox.setExpireTime(content.optString("expiry"));
                            subscriptionBox.setSubscription("0", content.toString());
                        }
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
                apiCounter++;
                observInitialSubscriptionCheck();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                utility.logger(t.toString());
                apiCounter++;
                observInitialSubscriptionCheck();
            }
        });
    }

    private void observInitialSubscriptionCheck() {
        if (apiCounter == 5) {
            utility.hideProgress();
            if (subscriptionBox.getMobile().length() == 0 || subscriptionBox.getOperator().equals("NA")) {
                llOption.setVisibility(View.VISIBLE);
                utility.hideAndShowView(new View[]{etPhoneNumber, btnCheck}, tvNumber);
                tvNumber.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
                //rbGp.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_message_bn) : context.getString(R.string.gp_message_en));
                //rbRobi.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.rb_message_bn) : context.getString(R.string.rb_message_en));
                //rbBlink.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bl_message_bn) : context.getString(R.string.bl_message_en));
                //rbGrBkash.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bk_gr_message_bn) : context.getString(R.string.bk_gr_message_en));
                //rbOnBkash.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.bk_on_message_bn) : context.getString(R.string.bk_on_message_en));
                //rbskitto.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_message_bn) : context.getString(R.string.skitto_message_en));
                btnSubmit.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.submit_bn) : context.getString(R.string.submit_en));
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (rgOperator.getCheckedRadioButtonId() != -1) {
                            RadioButton rb = (RadioButton) llOption.findViewById(rgOperator.getCheckedRadioButtonId());
                            if (rb.getTag().toString().equals(context.getString(R.string.tag_gp))) {
                                GpConfirmation(rb.getTag().toString());
                                //showConfirmation(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_message_bn) : context.getString(R.string.gp_message_en), rb.getTag().toString());
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_rb))) {
                                showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planrobi.getPlans().get(0).getTextplanBn()) : planrobi.getPlans().get(0).getTextplan(), rb.getTag().toString());
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_bl))) {
                                showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planBanglalink.getPlans().get(0).getTextplanBn()) : planBanglalink.getPlans().get(0).getTextplan(), rb.getTag().toString());
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_gr))) {
                                showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planghuri.getPlans().get(0).getTextplanBn()) : planghuri.getPlans().get(0).getTextplan(), rb.getTag().toString());
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_on))) {
                                showConfirmation(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planonmbl.getPlans().get(0).getTextplanBn()) : planonmbl.getPlans().get(0).getTextplan(), rb.getTag().toString());
                            } else if (rb.getTag().toString().equals(context.getString(R.string.tag_skitto))) {
                                showSkittoConfirmation(rb.getTag().toString());
                            }
                        } else {
                            utility.showToast("Please choose operator");
                        }
                    }
                });
            } else {
                viewStatus();
            }
        }
    }

    public void showConfirmation(String message, String tag) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parent_layout);
            TextView title = (TextView) dialog.findViewById(R.id.tv_title);
            TextView number = (TextView) dialog.findViewById(R.id.tv_number);
            TextView plan = (TextView) dialog.findViewById(R.id.tv_plan);
            Button btnNo = (Button) dialog.findViewById(R.id.btn_no);
            Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
            ViewGroup.LayoutParams params = parentLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            parentLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, number, plan, btnNo, btnYes});
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.subscription_confirmation_message_bn) : context.getString(R.string.subscription_confirmation_message_en));
            number.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
            plan.setText(message);
            btnNo.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
            btnYes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (utility.isNetworkAvailable()) {
                        subscriptionBox.setMobile("88" + mobile);
                        subscriptionBox.setOperator(tag);
                        if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_gp))) {
                            showSubscribedView(false);
                        } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_bl))) {
                            if (subscriptionBox.isSubscribed("0")) {
                                subscriptionBox.deactivateSubscription();
                            } else {
                                subscriptionBox.activateSubscription("0", "0");
                            }
                        } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_rb))) {
                            if (subscriptionBox.isSubscribed("0")) {
                                subscriptionBox.deactivateSubscription();
                            } else {
                                subscriptionBox.activateSubscription("0", "0");
                            }
                        } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_gr))) {
                            subscriptionBox.activateSubscription("0", "0");
                        } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_on))) {
                            subscriptionBox.activateSubscription("0", "0");
                        }
                    } else {
                        utility.showToast("No Internet");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    public void showSkittoConfirmation(String tag) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = utility.getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_skitto_confirmation);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parent_layout);
            TextView title = (TextView) dialog.findViewById(R.id.tv_title);
            TextView number = (TextView) dialog.findViewById(R.id.tv_number);
            TextView plan = (TextView) dialog.findViewById(R.id.tv_plan);
            RadioGroup skittogrp = (RadioGroup) dialog.findViewById(R.id.rb_skitto_package);
            RadioButton sk_daily = (RadioButton) dialog.findViewById(R.id.rb_skitto_daily);
            RadioButton sk_weekly = (RadioButton) dialog.findViewById(R.id.rb_skitto_weekly);
            RadioButton sk_monthly = (RadioButton) dialog.findViewById(R.id.rb_skitto_monthly);
            Button btnNo = (Button) dialog.findViewById(R.id.btn_no);
            Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
            ViewGroup.LayoutParams params = parentLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            parentLayout.setLayoutParams(params);
            utility.setFonts(new View[]{title, number, plan, btnNo, btnYes});
            if (planskitto.getPlans().get(0) != null) {
                sk_daily.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(0).getTextplanBn()) : planskitto.getPlans().get(0).getTextplan());
            }
            if (planskitto.getPlans().get(1) != null) {
                sk_weekly.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(1).getTextplanBn()) : planskitto.getPlans().get(1).getTextplan());
            }
            if (planskitto.getPlans().get(2) != null) {
                sk_monthly.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(2).getTextplanBn()) : planskitto.getPlans().get(2).getTextplan());
            }
            //sk_daily.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_daily_message_bn) : context.getString(R.string.skitto_daily_message_en));
            //sk_weekly.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_weekly_message_bn) : context.getString(R.string.skitto_weekly_message_en));
            //sk_monthly.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_month_message_bn) : context.getString(R.string.skitto_month_message_en));
            title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.subscription_confirmation_message_bn) : context.getString(R.string.subscription_confirmation_message_en));
            number.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
            btnNo.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
            btnYes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
            skittogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        if (sk_daily.isChecked()) {
                            if (planskitto.getPlans().get(0) != null) {
                                plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(0).getTextplanBn()) : planskitto.getPlans().get(0).getTextplan());
                            }
                            //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_daily_message_bn) : context.getString(R.string.skitto_daily_message_en));
                        } else if (sk_weekly.isChecked()) {
                            if (planskitto.getPlans().get(1) != null) {
                                plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(1).getTextplanBn()) : planskitto.getPlans().get(1).getTextplan());
                            }
                            //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_weekly_message_bn) : context.getString(R.string.skitto_weekly_message_en));
                        } else if (sk_monthly.isChecked()) {
                            if (planskitto.getPlans().get(2) != null) {
                                plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(planskitto.getPlans().get(2).getTextplanBn()) : planskitto.getPlans().get(2).getTextplan());
                            }
                            //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.skitto_month_message_bn) : context.getString(R.string.skitto_month_message_en));
                        }
                    }
                }
            });
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (utility.isNetworkAvailable()) {
                        if (!TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                            mobile = etPhoneNumber.getText().toString();
                            subscriptionBox.setMobile("88" + mobile);
                        }
                        subscriptionBox.setOperator(tag);
                        if (subscriptionBox.isSubscribed("0")) {
                            subscriptionBox.deactivateSubscription();
                        } else {
                            if (sk_daily.isChecked()) {
                                subscriptionBox.activateSubscription("1", "0");
                            } else if (sk_weekly.isChecked()) {
                                subscriptionBox.activateSubscription("7", "0");
                            } else if (sk_monthly.isChecked()) {
                                subscriptionBox.activateSubscription("30", "0");
                            } else {
                                utility.showToast("Please Choose A Package");
                            }
                        }
                    } else {
                        utility.showToast("No Internet");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    public void GpConfirmation(String tag) {
        try {
            if (plangp != null) {
                final Dialog dialog = new Dialog(context);
                HashMap<String, Integer> screenRes = utility.getScreenRes();
                int width = screenRes.get(KeyWord.SCREEN_WIDTH);
                int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
                int mywidth = (width / 10) * 8;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_skitto_confirmation);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                LinearLayout parentLayout = (LinearLayout) dialog.findViewById(R.id.parent_layout);
                TextView title = (TextView) dialog.findViewById(R.id.tv_title);
                TextView number = (TextView) dialog.findViewById(R.id.tv_number);
                TextView plan = (TextView) dialog.findViewById(R.id.tv_plan);
                RadioGroup skittogrp = (RadioGroup) dialog.findViewById(R.id.rb_skitto_package);
                RadioButton sk_daily = (RadioButton) dialog.findViewById(R.id.rb_skitto_daily);
                RadioButton sk_weekly = (RadioButton) dialog.findViewById(R.id.rb_skitto_weekly);
                RadioButton sk_monthly = (RadioButton) dialog.findViewById(R.id.rb_skitto_monthly);
                Button btnNo = (Button) dialog.findViewById(R.id.btn_no);
                Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
                ViewGroup.LayoutParams params = parentLayout.getLayoutParams();
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                params.width = mywidth;
                parentLayout.setLayoutParams(params);
                utility.setFonts(new View[]{title, number, plan, btnNo, btnYes});
                if (plangp.getPlans().get(0) != null) {
                    sk_daily.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(0).getTextplanBn()) : plangp.getPlans().get(0).getTextplan());
                }
                if (plangp.getPlans().get(1) != null) {
                    sk_weekly.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(1).getTextplanBn()) : plangp.getPlans().get(1).getTextplan());
                }
                if (plangp.getPlans().get(2) != null) {
                    sk_monthly.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(2).getTextplanBn()) : plangp.getPlans().get(2).getTextplan());
                }
                //sk_daily.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_daily_message_bn) : context.getString(R.string.gp_daily_message_en));
                //sk_weekly.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_weekly_message_bn) : context.getString(R.string.gp_weekly_message_en));
                //sk_monthly.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_month_message_bn) : context.getString(R.string.gp_month_message_en));
                title.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.subscription_confirmation_message_bn) : context.getString(R.string.subscription_confirmation_message_en));
                number.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.mobile_bn) + " " + utility.convertToBangle(mobile) : context.getString(R.string.mobile_en) + " " + mobile);
                btnNo.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
                btnYes.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
                skittogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if (radioGroup.getCheckedRadioButtonId() != -1) {
                            if (sk_daily.isChecked()) {
                                if (plangp.getPlans().get(0) != null) {
                                    plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(0).getTextplanBn()) : plangp.getPlans().get(0).getTextplan());
                                }
                                //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_daily_message_bn) : context.getString(R.string.gp_daily_message_en));
                            } else if (sk_weekly.isChecked()) {
                                if (plangp.getPlans().get(1) != null) {
                                    plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(1).getTextplanBn()) : plangp.getPlans().get(1).getTextplan());
                                }
                                //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_weekly_message_bn) : context.getString(R.string.gp_weekly_message_en));
                            } else if (sk_monthly.isChecked()) {
                                if (plangp.getPlans().get(2) != null) {
                                    plan.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(plangp.getPlans().get(2).getTextplanBn()) : plangp.getPlans().get(2).getTextplan());
                                }
                                //plan.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.gp_month_message_bn) : context.getString(R.string.gp_month_message_en));
                            }
                        }
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (utility.isNetworkAvailable()) {
                            if (!TextUtils.isEmpty(etPhoneNumber.getText().toString())) {
                                mobile = etPhoneNumber.getText().toString();
                                subscriptionBox.setMobile("88" + mobile);
                            }
                            subscriptionBox.setOperator(tag);
                            if (subscriptionBox.isSubscribed("0")) {
                                subscriptionBox.deactivateSubscription();
                            } else {
                                if (sk_daily.isChecked()) {
                                    subscriptionBox.activateSubscription("1", "0");
                                } else if (sk_weekly.isChecked()) {
                                    subscriptionBox.activateSubscription("7", "0");
                                } else if (sk_monthly.isChecked()) {
                                    subscriptionBox.activateSubscription("30", "0");
                                } else {
                                    utility.showToast("Please Choose A Package");
                                }
                            }
                        } else {
                            utility.showToast("No Internet");
                        }
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            } else {
                utility.showToast("Check Internet");
            }

        } catch (Exception ex) {
            utility.showToast(ex.toString());
        }
    }

    @Override
    public void activate() {
        utility.showToast("Activated");
    }

    @Override
    public void deactivate() {

    }

    @Override
    public void viewStatus() {
        if (subscriptionBox.isSubscribed("0")) {
            tvStatus.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.status_on_bn) : context.getString(R.string.status_on_en));
            btnAction.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.button_off_bn) : context.getString(R.string.button_off_en));
                /*if (subscriptionBox.getOperator().equals("ON")) {
                    btnAction.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.renew_bn) : context.getString(R.string.renew_en));
                }*/
            if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_on))) {
                btnAction.setVisibility(View.GONE);
            }
        } else {
            tvStatus.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.status_off_bn) : context.getString(R.string.status_off_en));
            btnAction.setText(utility.getLangauge().equals("bn") ? context.getString(R.string.button_on_bn) : context.getString(R.string.button_on_en));
        }
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_bl))) {
                    if (subscriptionBox.isSubscribed("0")) {
                        subscriptionBox.deactivateSubscription();
                    } else {
                        subscriptionBox.activateSubscription("0", "0");
                    }
                } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_rb))) {
                    if (subscriptionBox.isSubscribed("0")) {
                        subscriptionBox.deactivateSubscription();
                    } else {
                        subscriptionBox.activateSubscription("0", "0");
                    }
                } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_skitto))) {
                    if (subscriptionBox.isSubscribed("0")) {
                        subscriptionBox.deactivateSubscription();
                    } else {
                        showSkittoConfirmation(context.getString(R.string.tag_skitto));
                    }
                } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_gr))) {
                    subscriptionBox.activateSubscription("0", "0");
                } else if (subscriptionBox.getOperator().equals(context.getString(R.string.tag_on))) {
                    subscriptionBox.activateSubscription("0", "0");
                }
            }
        });
        showSubscribedView(false);
    }

    @Override
    public void numberSet() {
        initiateView();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            subscriptionBox.checkSubscription("0");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("bkash.payment.check");
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(broadcastReceiver);
    }


    //get subscription text
    private void getSubscriptionText() {
        Call<List<Plantext>> call = apiInterface.getplantext(utility.getAuthorization(), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
        call.enqueue(new Callback<List<Plantext>>() {
            @Override
            public void onResponse(Call<List<Plantext>> call, Response<List<Plantext>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<Plantext> noticeModels = response.body();
                    if (noticeModels.size() > 0) {
                        for (Plantext p : noticeModels) {
                            if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_gp))) {
                                rbGp.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                plangp = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_rb))) {
                                rbRobi.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                planrobi = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_bl))) {
                                rbBlink.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                planBanglalink = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_gr))) {
                                rbGrBkash.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                planghuri = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_on))) {
                                rbOnBkash.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                planonmbl = p;
                            } else if (p.getAbbreviation().equalsIgnoreCase(context.getResources().getString(R.string.tag_skitto))) {
                                rbskitto.setText(utility.getLangauge().equals("bn") ? utility.unicodetobangla(p.getCarrierBn()) : p.getCarrier());
                                planskitto = p;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Plantext>> call, Throwable t) {

            }
        });
    }

    public void bl_check(String s) {
        BlbaseInterface blbaseInterface = Blbaseclient.getBaseClient().create(BlbaseInterface.class);
        Call<String> call = blbaseInterface.geturl(s);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    try {
                        String check = response.body().trim().toString();
                        utility.logger("check " + check);
                    } catch (Exception ex) {
                        utility.logger(ex.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                utility.logger("error " + t.toString());
            }
        });
    }
}
