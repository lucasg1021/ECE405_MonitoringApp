package com.example.monitoring_system;

import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;


public class utils {

    private Context context;

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


        switch(alertFlag){
            case 1:

                Log.i("ALERT", "TEMP TOO HIGH");

                Timer timer = new Timer();
                int begin = 1000;              // start timer at 1 second
                int timeinterval = 180 * 1000; //timer executes every 3 minutes
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature is too high ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin, timeinterval);



                break;
            case 2:

                Log.i("ALERT", "TEMP TOO LOW");

                Timer timer2 = new Timer();
                int begin2 = 1000;              // start timer at 1 second
                int timeinterval2 = 180 * 1000; //timer executes every 3 minutes
                timer2.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature is too low ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin2, timeinterval2);

                break;
            case 3:
                Log.i("ALERT", "HUMIDITY TOO HIGH");

                Timer timer3 = new Timer();
                int begin3 = 1000;              // start timer at 1 second
                int timeinterval3 = 180 * 1000; //timer executes every 3 minutes
                timer3.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your humidity too high");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin3, timeinterval3);

                break;
            case 4:
                Log.i("ALERT", "HUMIDITY TOO LOW");

                Timer timer4 = new Timer();
                int begin4 = 1000;              // start timer at 1 second
                int timeinterval4 = 180 * 1000; //timer executes every 3 minutes
                timer4.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your humidity is too low ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin4, timeinterval4);

                break;
            case 5:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO HIGH");

                Timer timer5 = new Timer();
                int begin5 = 1000;              // start timer at 1 second
                int timeinterval5 = 180 * 1000; //timer executes every 3 minutes
                timer5.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature and humidity are too high ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin5, timeinterval5);

                break;
            case 6:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO HIGH");

                Timer timer6 = new Timer();
                int begin6 = 1000;              // start timer at 1 second
                int timeinterval6 = 180 * 1000; //timer executes every 3 minutes
                timer6.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature is too low and humidity is too high ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin6, timeinterval6);

                break;
            case 7:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO LOW");

                Timer timer7 = new Timer();
                int begin7 = 1000;              // start timer at 1 second
                int timeinterval7 = 180 * 1000; //timer executes every 3 minutes
                timer7.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature is too high and humidity is too low ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());
                    }
                }, begin7, timeinterval7);

                break;
            case 8:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO LOW");

                Timer timer8 = new Timer();
                int begin8 = 1000;              // start timer at 1 second
                int timeinterval8 = 180 * 1000; //timer executes every 3 minutes
                timer8.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);

                        builder.setContentTitle("Enclosure Alert");
                        builder.setContentText("Hello your temperature and humidity are too low ");
                        builder.setSmallIcon(R.drawable.ic_notify_pic);
                        builder.setAutoCancel(true);
                        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

                        managerCompat.notify(1,builder.build());

                    }
                }, begin8, timeinterval8);

                break;

        }
    }

    public static void notice(int noticeFlag, Context context){

        // REPLACE COMMENTED CODE WITH NEW "NOTICE" NOTIFICATION

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        switch(noticeFlag){
            case 1:
                Log.i("NOTICE", "TEMP TOO HIGH");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//
//                managerCompat.notify(1,builder.build());
                break;
            case 2:
                Log.i("NOTICE", "TEMP TOO LOW");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//
//                managerCompat.notify(1,builder.build());
                break;
            case 3:
                Log.i("NOTICE", "HUMIDITY TOO HIGH");

//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;
            case 4:
                Log.i("NOTICE", "HUMIDITY TOO LOW");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;
            case 5:
                Log.i("NOTICE", "TEMP TOO HIGH & HUMIDITY TOO HIGH");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;
            case 6:
                Log.i("NOTICE", "TEMP TOO LOW & HUMIDITY TOO HIGH");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;
            case 7:
                Log.i("NOTICE", "TEMP TOO HIGH & HUMIDITY TOO LOW");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;
            case 8:
                Log.i("NOTICE", "TEMP TOO LOW & HUMIDITY TOO LOW");
//                builder.setContentTitle("Enclosure Alert");
//                builder.setContentText("Hello your temperature is low ");
//                builder.setSmallIcon(R.drawable.ic_launcher_background);
//                builder.setAutoCancel(true);
//
//                managerCompat.notify(1,builder.build());
                break;

        }
    }
}
