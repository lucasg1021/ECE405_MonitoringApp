package com.example.monitoring_system;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class utils {


    private Context context;
    private int equipTimerFlag = 0;
    private static int alertTimerFlag2;
    private int noticeTimerFlag3 = 0;

    public static String decrypt(int key, byte[] msgIn){
        byte[] messageOutBytes = new byte[msgIn.length];
        int key8b = key & 0xFF;

        for(int i = 0; i < msgIn.length; i++){
            messageOutBytes[i] = (byte) (key8b ^ msgIn[i]);
        }

        String messageOut = new String(messageOutBytes, StandardCharsets.US_ASCII);

        return messageOut;
    }

    public static byte[] encrypt(int key, String message){
        byte[] messageBytes = message.getBytes(StandardCharsets.US_ASCII);
        int key8b = key & 0xFF;

        for(int i = 0; i < messageBytes.length; i++){
            messageBytes[i] = (byte) (key8b ^ messageBytes[i]);
        }

        return messageBytes;
    }

    public static void alert(int alertFlag, Context context){

        String enc1 = "Enclosure Alert";

        switch(alertFlag){
            case 1:

                Log.i("ALERT", "TEMP TOO HIGH");

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Notification");
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                    builder.setContentTitle(enc1);
                    builder.setContentText("Hello your temperature is too high ");
                    builder.setSmallIcon(R.drawable.ic_notify_pic);
                    builder.setAutoCancel(true);
                    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                    managerCompat.notify(enc1.hashCode(), builder.build());

                break;
            case 2:

                Log.i("ALERT", "TEMP TOO LOW");

                        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat1 = NotificationManagerCompat.from(context);

                        builder1.setContentTitle(enc1);
                        builder1.setContentText("Hello your temperature is too low ");
                        builder1.setSmallIcon(R.drawable.ic_notify_pic);
                        builder1.setAutoCancel(true);
                        builder1.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat1.notify(enc1.hashCode() ,builder1.build());

                break;
            case 3:
                Log.i("ALERT", "HUMIDITY TOO HIGH");

                        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat2 = NotificationManagerCompat.from(context);

                        builder2.setContentTitle(enc1);
                        builder2.setContentText("Hello your humidity too high");
                        builder2.setSmallIcon(R.drawable.ic_notify_pic);
                        builder2.setAutoCancel(true);
                        builder2.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat2.notify(enc1.hashCode() ,builder2.build());

                break;
            case 4:
                Log.i("ALERT", "HUMIDITY TOO LOW");


                        NotificationCompat.Builder builder3 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat3 = NotificationManagerCompat.from(context);

                        builder3.setContentTitle(enc1);
                        builder3.setContentText("Hello your humidity is too low ");
                        builder3.setSmallIcon(R.drawable.ic_notify_pic);
                        builder3.setAutoCancel(true);
                        builder3.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat3.notify(enc1.hashCode() ,builder3.build());

                break;
            case 5:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO HIGH");

                        NotificationCompat.Builder builder4 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat4 = NotificationManagerCompat.from(context);

                        builder4.setContentTitle(enc1);
                        builder4.setContentText("Hello your temperature and humidity are too high ");
                        builder4.setSmallIcon(R.drawable.ic_notify_pic);
                        builder4.setAutoCancel(true);
                        builder4.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat4.notify(enc1.hashCode() ,builder4.build());

                break;
            case 6:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO HIGH");

                        NotificationCompat.Builder builder5 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat5 = NotificationManagerCompat.from(context);

                        builder5.setContentTitle(enc1);
                        builder5.setContentText("Hello your temperature is too low and humidity is too high ");
                        builder5.setSmallIcon(R.drawable.ic_notify_pic);
                        builder5.setAutoCancel(true);
                        builder5.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat5.notify(enc1.hashCode() ,builder5.build());

                break;
            case 7:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO LOW");

                        NotificationCompat.Builder builder6 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat6 = NotificationManagerCompat.from(context);

                        builder6.setContentTitle(enc1);
                        builder6.setContentText("Hello your temperature is too high and humidity is too low ");
                        builder6.setSmallIcon(R.drawable.ic_notify_pic);
                        builder6.setAutoCancel(true);
                        builder6.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat6.notify(enc1.hashCode() ,builder6.build());

                break;
            case 8:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO LOW");

                        NotificationCompat.Builder builder7 = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat7 = NotificationManagerCompat.from(context);

                        builder7.setContentTitle(enc1);
                        builder7.setContentText("Hello your temperature and humidity are too low ");
                        builder7.setSmallIcon(R.drawable.ic_notify_pic);
                        builder7.setAutoCancel(true);
                        builder7.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat7.notify(enc1.hashCode() ,builder7.build());

                break;

        }
    }

    public static void notice(int noticeFlag, Context context){

        // ******** REPLACE COMMENTED CODE WITH NEW "NOTICE" NOTIFICATION ***********

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        String enc2 = "Enclosure Notice";

        switch(noticeFlag){
            case 1:
                Log.i("NOTICE", "TEMP TOO HIGH");

                NotificationCompat.Builder builder8 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat8 = NotificationManagerCompat.from(context);

                builder8.setContentTitle(enc2);
                builder8.setContentText(" Temperature is too high");
                builder8.setSmallIcon(R.drawable.ic_launcher_foreground);
                builder8.setAutoCancel(true);
                builder8.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat8.notify(enc2.hashCode() ,builder8.build());

                break;
            case 2:
                Log.i("NOTICE", "TEMP TOO LOW");

                        NotificationCompat.Builder builder9 = new NotificationCompat.Builder(context,"Notification2");
                        NotificationManagerCompat managerCompat9 = NotificationManagerCompat.from(context);

                        builder9.setContentTitle(enc2);
                        builder9.setContentText(" Temperature is too low");
                        builder9.setSmallIcon(R.drawable.ic_launcher_background);
                        builder9.setAutoCancel(true);
                        builder9.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat9.notify(enc2.hashCode() ,builder9.build());

                break;
            case 3:
                Log.i("NOTICE", "HUMIDITY TOO HIGH");

                NotificationCompat.Builder builder10 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat10 = NotificationManagerCompat.from(context);

                builder10.setContentTitle(enc2);
                builder10.setContentText(" Humidity is too high");
                builder10.setSmallIcon(R.drawable.ic_launcher_background);
                builder10.setAutoCancel(true);
                builder10.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat10.notify(enc2.hashCode() ,builder10.build());

                break;
            case 4:
                Log.i("NOTICE", "HUMIDITY TOO LOW");

                NotificationCompat.Builder builder11 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat11 = NotificationManagerCompat.from(context);

                builder11.setContentTitle(enc2);
                builder11.setContentText(" humidity is too low");
                builder11.setSmallIcon(R.drawable.ic_launcher_background);
                builder11.setAutoCancel(true);
                builder11.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat11.notify(enc2.hashCode() ,builder11.build());

                break;
            case 5:
                Log.i("NOTICE", "TEMP TOO HIGH & HUMIDITY TOO HIGH");

                NotificationCompat.Builder builder12 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat12 = NotificationManagerCompat.from(context);

                builder12.setContentTitle(enc2);
                builder12.setContentText(" Temperature and humidity are too high");
                builder12.setSmallIcon(R.drawable.ic_launcher_background);
                builder12.setAutoCancel(true);
                builder12.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat12.notify(enc2.hashCode() ,builder12.build());

                break;
            case 6:
                Log.i("NOTICE", "TEMP TOO LOW & HUMIDITY TOO HIGH");

                NotificationCompat.Builder builder13 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat13 = NotificationManagerCompat.from(context);

                builder13.setContentTitle(enc2);
                builder13.setContentText(" Temperature is too low and humidity too high");
                builder13.setSmallIcon(R.drawable.ic_launcher_background);
                builder13.setAutoCancel(true);
                builder13.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat13.notify(enc2.hashCode() ,builder13.build());

                break;
            case 7:
                Log.i("NOTICE", "TEMP TOO HIGH & HUMIDITY TOO LOW");

                NotificationCompat.Builder builder14 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat14 = NotificationManagerCompat.from(context);

                builder14.setContentTitle(enc2);
                builder14.setContentText(" Temperature is too high and humidity is too low");
                builder14.setSmallIcon(R.drawable.ic_launcher_background);
                builder14.setAutoCancel(true);
                builder14.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat14.notify(enc2.hashCode() ,builder14.build());

                break;
            case 8:
                Log.i("NOTICE", "TEMP TOO LOW & HUMIDITY TOO LOW");

                NotificationCompat.Builder builder15 = new NotificationCompat.Builder(context,"Notification2");
                NotificationManagerCompat managerCompat15 = NotificationManagerCompat.from(context);

                builder15.setContentTitle(enc2);
                builder15.setContentText(" Temperature and humidity are too low");
                builder15.setSmallIcon(R.drawable.ic_launcher_background);
                builder15.setAutoCancel(true);
                builder15.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                managerCompat15.notify(enc2.hashCode() ,builder15.build());

                break;

        }
    }

    public static void equip(int equipFlag, Context context){

        // *********** REPLACE COMMENTED CODE WITH NEW "EQUIPMENT FAIL" NOTIFICATION *************

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

        String enc3 = "Equipment Fail";

        switch(equipFlag){
            case 1:
                Log.i("EQUIPMENT FAIL", "LAMP IS OFF");

                        NotificationCompat.Builder builder16 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat16 = NotificationManagerCompat.from(context);

                        builder16.setContentTitle(enc3);
                        builder16.setContentText(" Equipment is failing, lamp is off");
                        builder16.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder16.setAutoCancel(true);
                        builder16.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat16.notify(enc3.hashCode() ,builder16.build());

                break;
            case 2:
                Log.i("EQUIPMENT FAIL", "LAMP IS ON");

                        NotificationCompat.Builder builder17 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat17 = NotificationManagerCompat.from(context);

                        builder17.setContentTitle(enc3);
                        builder17.setContentText(" Equipment is failing, lamp is on");
                        builder17.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder17.setAutoCancel(true);
                        builder17.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat17.notify(enc3.hashCode() ,builder17.build());

                break;
            case 3:
                Log.i("EQUIPMENT", "MISTER IS OFF");

                        NotificationCompat.Builder builder18 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat18 = NotificationManagerCompat.from(context);

                        builder18.setContentTitle(enc3);
                        builder18.setContentText(" Equipment; mister is off");
                        builder18.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder18.setAutoCancel(true);
                        builder18.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat18.notify(enc3.hashCode() ,builder18.build());

                break;
            case 4:
                Log.i("EQUIPMENT FAIL", "MISTER IS ON");

                        NotificationCompat.Builder builder19 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat19 = NotificationManagerCompat.from(context);

                        builder19.setContentTitle(enc3);
                        builder19.setContentText(" Equipment, mister on");
                        builder19.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder19.setAutoCancel(true);
                        builder19.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat19.notify(enc3.hashCode() ,builder19.build());

                break;
            case 5:
                Log.i("EQUIPMENT FAIL", "LAMP & MISTER ARE OFF");

                        NotificationCompat.Builder builder20 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat20 = NotificationManagerCompat.from(context);

                        builder20.setContentTitle(enc3);
                        builder20.setContentText(" Equipment is failing, lamp and mister are off");
                        builder20.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder20.setAutoCancel(true);
                        builder20.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat20.notify(enc3.hashCode() ,builder20.build());

                break;
            case 6:
                Log.i("EQUIPMENT FAIL", "LAMP IS ON & MISTER IS OFF");

                        NotificationCompat.Builder builder21 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat21 = NotificationManagerCompat.from(context);

                        builder21.setContentTitle(enc3);
                        builder21.setContentText(" Equipment is failing, lamp is on and mister is off");
                        builder21.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder21.setAutoCancel(true);
                        builder21.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat21.notify(enc3.hashCode() ,builder21.build());

                break;
            case 7:
                Log.i("EQUIPMENT FAIL", "LAMP IS OFF & MISTER IS ON");

                        NotificationCompat.Builder builder22 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat22 = NotificationManagerCompat.from(context);

                        builder22.setContentTitle(enc3);
                        builder22.setContentText(" Equipment is failing, lamp is off and mister is on");
                        builder22.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder22.setAutoCancel(true);
                        builder22.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat22.notify(enc3.hashCode() ,builder22.build());

                break;
            case 8:
                Log.i("EQUIPMENT FAIL", "LAMP & MISTER ARE ON");

                        NotificationCompat.Builder builder23 = new NotificationCompat.Builder(context,"Notification3");
                        NotificationManagerCompat managerCompat23 = NotificationManagerCompat.from(context);

                        builder23.setContentTitle(enc3);
                        builder23.setContentText(" Equipment; lamp and mister are on ");
                        builder23.setSmallIcon(R.drawable.ic_launcher_foreground);
                        builder23.setAutoCancel(true);
                        builder23.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat23.notify(enc3.hashCode() ,builder23.build());

                break;

        }
    }
}
