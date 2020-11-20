package com.example.mindmap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * 개발자 : 20191583 나민형
 * 마지막 수정일 : 2020-11-19
 * 기능 : 부팅 시 broadcast 시작
 * 추가 설명 : 매일 9시에 푸쉬알람을 보내는 데에 있어, 사용자가 앱을 실행하지 않거나 재부팅한 상태에서도 푸쉬 알림을 보냄
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent _intent) {
        if (_intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0 ,intent, 0);

            // 지정한 시간에 매일 알림
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),  AlarmManager.INTERVAL_DAY, pIntent);
        }
    }
}