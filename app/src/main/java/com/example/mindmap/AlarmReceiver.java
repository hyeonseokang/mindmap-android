package com.example.mindmap;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
       push(context);  // AVD 확인용
    }

    public void push(Context context){
        int notifyID = 2;

        String randKor;
        randKor = getRandomKoreanWord(context);

        String pushTitle = "오늘의 단어";
        String pushText = "오늘의 단어는 '" + randKor + "'" + "입니다. 당신의 아이디어를 그려주세요!";

        if(android.os.Build.VERSION.SDK_INT > 25) {

            //푸시를 클릭했을때 이동//
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_ONE_SHOT);
            //푸시를 클릭했을때 이동//

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,  "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("Description");

            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            try {
                Notification notification = new Notification.Builder(context, CHANNEL_ID)
                        .setContentTitle(URLDecoder.decode(pushTitle, "UTF-8"))
                        .setContentText(URLDecoder.decode(pushText, "UTF-8"))
                        .setSmallIcon(R.drawable.ic_add)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();
                mNotificationManager.notify(notifyID, notification);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        else{
            NotificationManager notificationManager;
            PendingIntent intent2 = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder = null;
            try {
                builder = new Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_add)
                        .setDefaults(Notification.BADGE_ICON_NONE)
                        .setContentTitle(URLDecoder.decode(pushTitle, "UTF-8"))
                        .setContentText(URLDecoder.decode(pushText, "UTF-8"))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(intent2);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notifyID, builder.build());
        }
    }

    public String getRandomKoreanWord(Context context) {
        ArrayList<String> korList = new ArrayList<String>();

        InputStream inputStream = context.getResources().openRawResource(R.raw.korean2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String rawStr = "";

        try {
            while(true){
                int i = inputStream.read();
                if(i == -1) break;

                if(i == 64){
                    byteArrayOutputStream.write('\n');
                    byteArrayOutputStream.write('\n');
                }
                else byteArrayOutputStream.write(i);
            }

            rawStr = new String(byteArrayOutputStream.toByteArray(),"MS949");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        rawStr = rawStr
                .replaceAll("\t", "")
                .replaceAll("\r", "");
        korList.addAll(Arrays.asList(rawStr.split("\n")));

        Random random = new Random();
        int rIndex = random.nextInt(korList.size() - 1);

        return korList.get(rIndex);
    }

}