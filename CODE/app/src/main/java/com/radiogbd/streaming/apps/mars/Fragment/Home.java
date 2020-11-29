package com.radiogbd.streaming.apps.mars.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.radiogbd.streaming.apps.mars.Activity.Main;
import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.App.MyApplication;
import com.radiogbd.streaming.apps.mars.Http.ContentApiClient;
import com.radiogbd.streaming.apps.mars.Http.ContentApiInterface;
import com.radiogbd.streaming.apps.mars.Interface.NoticeInterface;
import com.radiogbd.streaming.apps.mars.Library.KeyWord;
import com.radiogbd.streaming.apps.mars.Library.SubscriptionBox;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Model.Banner;
import com.radiogbd.streaming.apps.mars.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hp on 9/11/2017.
 */

public class Home extends Fragment {

    Context context;
    Utility utility;
    ContentApiInterface apiInterface = ContentApiClient.getBaseClient().create(ContentApiInterface.class);
    List<Banner> banners;
    SliderLayout sliderLayout;
    HashMap<String, Integer> screens = new HashMap<>();
    //HashMap<Integer, String> hash_file_maps;
    TabLayout tabLayout;
    ViewPager viewPager;
    int item = 3;
    LinearLayout llNoticeLayout;
    TextView tvNoticeText;
    ImageView ivNoticeClose;
    NoticeInterface noticeInterface;
    SubscriptionBox subscriptionBox;

    public Home() {
    }

    @SuppressLint("ValidFragment")
    public Home(Context context, NoticeInterface noticeInterface) {
        this.context = context;
        utility = new Utility(context);
        subscriptionBox = new SubscriptionBox(context);
        screens = utility.getScreenRes();
        this.noticeInterface = noticeInterface;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, null);
        sliderLayout = (SliderLayout) view.findViewById(R.id.slider);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        llNoticeLayout = (LinearLayout) view.findViewById(R.id.ll_notice_layout);
        tvNoticeText = (TextView) view.findViewById(R.id.tv_notice_text);
        ivNoticeClose = (ImageView) view.findViewById(R.id.iv_notice_close);
        try {
            if (utility.getConfig().getNotice().equals("ON")) {
                llNoticeLayout.setVisibility(View.VISIBLE);
                tvNoticeText.setText(utility.getLangauge().equals("bn") ? Html.fromHtml(utility.getConfig().getNotice_msg_bn()) : utility.getConfig().getNotice_msg_en());
                ivNoticeClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNoticeLayout.setVisibility(View.GONE);
                    }
                });
                tvNoticeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        llNoticeLayout.setVisibility(View.GONE);
                        noticeInterface.openNotice();
                    }
                });
            } else {
                llNoticeLayout.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
        viewPager.setAdapter(new ProfileAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        if (isAdded()) {
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        tabLayout.setupWithViewPager(viewPager);
                        changeTabsFont();
                    } catch (Exception ex) {
                        utility.call_error(ex);
                    }
                }
            });
        }
        ViewGroup.LayoutParams params = sliderLayout.getLayoutParams();
        params.width = screens.get(KeyWord.SCREEN_WIDTH);
        params.height = (screens.get(KeyWord.SCREEN_WIDTH) / 3);
        sliderLayout.setLayoutParams(params);
        initiateBanner();
        //initiateBanners();
        return view;
    }

    private void initiateBanners(List<Banner> banners) {
        try {
            //JSONArray jsonObject = new JSONArray(utility.loadJSONFromAsset("banner"));
            for (int i = 0; i < banners.size(); i++) {
                Banner banner = banners.get(i);
                DefaultSliderView defaultSliderView = new DefaultSliderView(context);
                final int finalI = i;
                defaultSliderView
                        .image(banner.getUri_banner())
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                            @Override
                            public void onSliderClick(BaseSliderView slider) {
                                if (banner.getBrowse().equals("EXTERNAL")) {
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(banner.getBrowse_uri_external()));
                                    context.startActivity(i);
                                } else if (banner.getBrowse().equals("INAPP")) {
                                    String action = banner.getBrowse_uri_inapp();
                                    String[] parts = action.split("-");
                                    if (parts.length == 1) {
                                        Intent intent = new Intent("banner.click");
                                        intent.putExtra("name", parts[0]);
                                        context.sendBroadcast(intent);
                                    } else {
                                        if (parts[0].equals("PLAY")) {
                                            if (parts.length == 2) {
                                                getSingleAlbum(parts[1], "0");
                                            }
                                            if (parts.length == 3) {
                                                getSingleAlbum(parts[1], parts[2]);
                                            }
                                        }
                                        if (parts[0].equals("SEARCH")) {
                                            Intent intent = new Intent("banner.click");
                                            intent.putExtra("name", parts[0]);
                                            intent.putExtra("keyword", parts[1]);
                                            context.sendBroadcast(intent);
                                        }
                                    }
                                }
                            }
                        });
                defaultSliderView.bundle(new Bundle());
                defaultSliderView.getBundle()
                        .putString("extra", banner.getUri_banner());
                sliderLayout.addSlider(defaultSliderView);
            }
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(10000);
            sliderLayout.setCurrentPosition(banners.size() - 1);
            sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //utility.logger("Page Changed: "+position+" Link: "+jsonArray.optJSONObject(position).optString("coverImg"));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } catch (Exception ex) {
            utility.logger(ex.toString());
        }
    }

    private void getSingleAlbum(String albumId, String trackId) {
        Call<List<AlbumModel>> call = apiInterface.getSingleAlbum(getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken(), "single_album-" + albumId);
        call.enqueue(new Callback<List<AlbumModel>>() {
            @Override
            public void onResponse(Call<List<AlbumModel>> call, Response<List<AlbumModel>> response) {
                if (response.isSuccessful() && response.code() == 200) {
                    List<AlbumModel> albumModels = response.body();
                    if (albumModels.size() > 0) {
                        Intent intent = new Intent(context, PlayList.class);
                        intent.putExtra("Album", albumModels.get(0));
                        if (!trackId.equals("0")) {
                            intent.putExtra("trackId", trackId);
                        }
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, PlayList.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AlbumModel>> call, Throwable t) {
                utility.logger(t.toString());
                Intent intent = new Intent(context, PlayList.class);
                startActivity(intent);
            }
        });
    }

    class ProfileAdapter extends FragmentPagerAdapter {

        public ProfileAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Exclusive(context);
                case 1:
                    return new NewAlbum(context);
                case 2:
                    return new Category(context);
                /*case 2: return new ScheduleFragment(context);*/
            }
            return null;
        }

        @Override
        public int getCount() {
            return item;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {
                    String topTitle = getResources().getString(R.string.exclusive_tab);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.exclusive_tab_bn);
                    }
                    return topTitle;
                }
                case 1: {
                    String topTitle = getResources().getString(R.string.new_tab);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.new_tab_bn);
                    }
                    return topTitle;
                }
                case 2: {
                    String topTitle = getResources().getString(R.string.category_tab);
                    if (utility.getLangauge().equals("bn")) {
                        topTitle = getResources().getString(R.string.category_tab_bn);
                    }
                    return topTitle;
                }
                /*case 2: return "Schedule";*/
            }
            return null;
        }
    }

    private void changeTabsFont() {
        try {
            ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
            int tabsCount = vg.getChildCount();
            for (int j = 0; j < tabsCount; j++) {
                ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
                int tabChildsCount = vgTab.getChildCount();
                for (int i = 0; i < tabChildsCount; i++) {
                    View tabViewChild = vgTab.getChildAt(i);
                    if (tabViewChild instanceof TextView) {
                        utility.setFont((TextView) tabViewChild);
                        ((TextView) tabViewChild).setTextSize(16);
                    }
                }
            }
        } catch (Exception ex) {
            utility.call_error(ex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //MyApplication.getInstance().trackScreenView("Footer Fragment");
    }

    private void initiateBanner() {
        if (utility.isNetworkAvailable()) {
            utility.showProgress(false);
            Call<List<Banner>> call = apiInterface.getBanner(context.getString(R.string.authorization_key), subscriptionBox.getOperator(), subscriptionBox.getMobile(), utility.getFirebaseToken());
            call.enqueue(new Callback<List<Banner>>() {
                @Override
                public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                    utility.hideProgress();
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            banners = response.body();
                            initiateBanners(banners);
                        } catch (Exception ex) {
                            Log.d("RESULT", ex.toString());
                        }
                    } else {
                        utility.showToast("Response Code " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<Banner>> call, Throwable t) {
                    utility.hideProgress();
                    utility.logger(t.toString());
                    utility.showToast(context.getString(R.string.http_error));
                }
            });
        } else {
            utility.showToast(context.getString(R.string.no_internet));
        }
    }
}
