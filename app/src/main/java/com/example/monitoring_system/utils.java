package com.example.monitoring_system;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.nio.charset.StandardCharsets;


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

                break;
            case 2:
                Log.i("ALERT", "TEMP TOO LOW");
                break;
            case 3:
                Log.i("ALERT", "HUMIDITY TOO HIGH");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"My Notification");
                builder.setContentTitle("Enclosure Alert");
                builder.setContentText("Hello your temperature is low ");
                builder.setSmallIcon(R.drawable.ic_launcher_background);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(1,builder.build());
                break;
            case 4:
                Log.i("ALERT", "HUMIDITY TOO LOW");
                break;
            case 5:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO HIGH");
                break;
            case 6:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO HIGH");
                break;
            case 7:
                Log.i("ALERT", "TEMP TOO HIGH & HUMIDITY TOO LOW");
                break;
            case 8:
                Log.i("ALERT", "TEMP TOO LOW & HUMIDITY TOO LOW");
                break;

        }
    }
}
