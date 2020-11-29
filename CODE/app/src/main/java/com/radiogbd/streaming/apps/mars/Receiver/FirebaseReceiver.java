package com.radiogbd.streaming.apps.mars.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.radiogbd.streaming.apps.mars.Activity.PlayList;
import com.radiogbd.streaming.apps.mars.Library.Utility;
import com.radiogbd.streaming.apps.mars.Model.AlbumModel;
import com.radiogbd.streaming.apps.mars.Notification.NotificationUtils;
import com.radiogbd.streaming.apps.mars.R;

/**
 * Created by Hp on 2/28/2018.
 */

public class FirebaseReceiver extends BroadcastReceiver {

    Context context;
    Utility utility;
    int FIREBASE_NOTIFICATION = 48788784;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        utility = new Utility(this.context);
        Intent resultIntent = new Intent(this.context, PlayList.class);
        AlbumModel albumModel = new AlbumModel();
        albumModel.setId(897);
        albumModel.setTitle("Shohor Chere");
        albumModel.setTitle_bn("&#2486;&#2489;&#2480; &#2459;&#2503;&#2524;&#2503;");
        albumModel.setThumbnail("/images/art-album/897.jpeg");
        albumModel.setRelease_date("2018-02-22");
        albumModel.setTotal("1");
        resultIntent.putExtra("Album", albumModel);
        showNotificationMessageWithBigImage(
                FIREBASE_NOTIFICATION,
                this.context,
                utility.getLangauge().equals("bn")?this.context.getString(R.string.notificationBn):this.context.getString(R.string.notificationEn),
                intent.getStringExtra("Message"),
                resultIntent,
                this.context.getString(R.string.image_url)+albumModel.getThumbnail());
    }

    private void showNotificationMessageWithBigImage(int notificationId, Context context, String title, String message, Intent intent, String imageUrl) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = false;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT_WATCH) {
                isScreenOn = pm.isInteractive();
            }
            else {
                isScreenOn = pm.isScreenOn();
            }
            if (isScreenOn == false) {
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
                wl_cpu.acquire(10000);
            }
            NotificationUtils notificationUtils = new NotificationUtils(context);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationUtils.showNotificationMessage(notificationId, title, message, intent, imageUrl);
        }
        catch (Exception ex){
            utility.logger(ex.toString());
        }
    }
}
