package com.example.monitoring_system;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.lang.UCharacter;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.os.Bundle;
import org.w3c.dom.Text;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning
public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";
    private int setTempValue, setHumidValue, tolTValue, tolHValue, privNum, sendB, alertFlag, noticeFlag;
    private int key;
    private int connection = 0;     // indicates whether connection has been established
    //base 7, mod 2147483647 for C long long
    int pubMod = 2147483647;
    int pubBase = 7;

    TextView tTemp, tHumid, tempTolView, humidTolView;
    private Button menuTwo;

    ScheduledExecutorService scheduledTaskExecutorKey = Executors.newScheduledThreadPool(5);
    ScheduledExecutorService scheduledTaskExecutorUDP = Executors.newScheduledThreadPool(5);
    ScheduledExecutorService scheduledTaskExecutorSetPoints = Executors.newScheduledThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        setTempValue = settings.getInt("currentSetTemp", 70);
        setHumidValue = settings.getInt("currentSetHumid", 50);
        tolTValue = settings.getInt("currentTolT", 2);
        tolHValue = settings.getInt("currentTolH", 2);

        tTemp = findViewById(R.id.setTemp);
        tHumid = findViewById(R.id.setHumid);
        tempTolView = findViewById(R.id.tolT);
        humidTolView = findViewById(R.id.tolH);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification","My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        menuTwo = (Button) findViewById(R.id.switchButton);
        menuTwo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });
        // if connection established and key shared, send requests for temp&humid data
        if(connection != 0) {
            scheduledTaskExecutorUDP.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    udpRunnable runnable = new udpRunnable();
                    new Thread(runnable).start();
                }
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        // else try to send encryption data to MCU
        else {
            scheduledTaskExecutorKey.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    keyRunnable runnable = new keyRunnable();
                    new Thread(runnable).start();
                }
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
    }

    public void openActivity2() {
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        String outputTemp = setTempValue + "°F";
        String outputHumid = setHumidValue + "%";
        String outputTolT = tolTValue + "°F";
        String outputTolH = tolHValue + "%";

        tTemp.setText(outputTemp);
        tHumid.setText(outputHumid);
        tempTolView.setText(outputTolT);
        humidTolView.setText(outputTolH);
    }

    public void increaseSetTemp(View v) {
        // update temperature and save to preferences
        setTempValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetTemp", setTempValue);
        editor.commit();
        // convert temp back to string to output
        String output = setTempValue + "°F";
        // display
        ((TextView) findViewById(R.id.setTemp)).setText(output);
    }

    public void decreaseSetTemp(View v) {
        // update temperature and save to preferences
        setTempValue -= 1;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetTemp", setTempValue);
        editor.commit();
        // convert temp back to string to output
        String output = setTempValue + "°F";
        // display
        ((TextView) findViewById(R.id.setTemp)).setText(output);
    }

    public void increaseSetHumid(View v) {
        // update humidity and save to preferences
        setHumidValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetHumid", setHumidValue);
        editor.commit();
        // convert updated humidity back to string
        String output = setHumidValue + "%";
        // display
        ((TextView) findViewById(R.id.setHumid)).setText(output);
    }

    public void decreaseSetHumid(View v) {
        // update humidity and save to preferences
        setHumidValue -= 1;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentSetHumid", setHumidValue);
        editor.commit();
        // convert updated humidity back to string
        String output = setHumidValue + "%";
        // display
        ((TextView) findViewById(R.id.setHumid)).setText(output);
    }

    public void buttonSubmitSetPoints(View v){
        if(connection == 1) {
            scheduledTaskExecutorKey.shutdown();
            scheduledTaskExecutorUDP.shutdown();

            connection = 5;

            scheduledTaskExecutorSetPoints = Executors.newScheduledThreadPool(5);
            scheduledTaskExecutorSetPoints.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    setPointRunnable runnable = new setPointRunnable();
                    new Thread(runnable).start();
                }
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
    }

    public void increaseTempTol(View v){
        // update temp tolerance and save to preferences
        tolTValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentTolT", tolTValue);
        editor.commit();
        // convert updated temp tol back to string
        String output = tolTValue + "°F";
        // display
        ((TextView) findViewById(R.id.tolT)).setText(output);
    }

    public void decreaseTempTol(View v){
        // update temp tolerance and save to preferences
        tolTValue--;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentTolT", tolTValue);
        editor.commit();
        // convert updated temp tol back to string
        String output = tolTValue + "°F";
        // display
        ((TextView) findViewById(R.id.tolT)).setText(output);
    }

    public void increaseHumidTol(View v){
        // update temp tolerance and save to preferences
        tolHValue++;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentTolH", tolHValue);
        editor.commit();
        // convert updated humidity tol back to string
        String output = tolHValue + "%";
        // display
        ((TextView) findViewById(R.id.tolH)).setText(output);
    }

    public void decreaseHumidTol(View v){
        // update temp tolerance and save to preferences
        tolHValue--;
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("currentTolH", tolHValue);
        editor.commit();
        // convert updated humidity tol back to string
        String output = tolHValue + "%";
        // display
        ((TextView) findViewById(R.id.tolH)).setText(output);
    }

    class udpRunnable implements Runnable {

        @Override
        public void run() {
            try {
                String IpAddress;
                String PREFSTwo = Activity2.PREFSTwo;
                IpAddress = Activity2.ipAddress;

                SharedPreferences settings = getSharedPreferences(Activity2.PREFSTwo,0);
                IpAddress = settings.getString("ipstring","");

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String msgOut = "REQUESTDATA";
                int msgLen = msgOut.length();
                byte[] msg = utils.encrypt(key, msgOut);
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                socket.setBroadcast(true);
                socket.send(packet);

                byte[] msgIn = new byte[4096];
                DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                socket.setSoTimeout(10000);
                try {
                    socket.receive(packetIn);
                    String message = new String(msgIn, 0, packetIn.getLength());
                    Log.i("MESSAGE RECEIVED", message);

                    if(message.contains("STARTUPSEQ")){
                        connection = 0;
                        scheduledTaskExecutorUDP.shutdown();
                        scheduledTaskExecutorKey = Executors.newScheduledThreadPool(5);
                        scheduledTaskExecutorKey.scheduleAtFixedRate(new Runnable() {
                            public void run() {
                                keyRunnable runnable = new keyRunnable();
                                new Thread(runnable).start();
                            }
                        }, 0, 5000, TimeUnit.MILLISECONDS);

                        return;
                    }

                    // decode
                    String messageOut = utils.decrypt(key, msgIn);

                    int indA = messageOut.indexOf("ALERT");
                    int indN = messageOut.indexOf("NOTICE");
                    int indD = messageOut.indexOf("DATA");

                    if(indA != -1) {
                        alertFlag = Integer.parseInt(messageOut.substring(indA + 6, indA + 7));
                        utils.alert(alertFlag, MainActivity.this);
                    }

                    if(indN != -1){
                        noticeFlag = Integer.parseInt(messageOut.substring(indN+7, indN+8));
                        utils.notice(noticeFlag, MainActivity.this);
                    }
                    if(indD != -1) {
                        String tempS = messageOut.substring(indD-12, indD-7) + "°F";
                        String humidS = messageOut.substring(indD-6, indD-2) + "%";

                        ((TextView) findViewById(R.id.currTemp)).setText(tempS);
                        ((TextView) findViewById(R.id.currHumid)).setText(humidS);
                    }

                }
                catch(SocketTimeoutException e){
//                    Log.i("TIMEOUT", "Timed out waiting for response");
                }

                socket.close();
            } catch (IOException ioException) {
//                Log.i("CONNECTION STATUS", "disconnected");
            }
        }

    }

    class keyRunnable implements Runnable {

        @Override
        public void run() {
            // begin the key exchange sequence
            if (connection == 0) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // send message to ip and port
                    String msgOut = "STARTUP";
                    int msgLen = msgOut.length();
                    byte[] msg = msgOut.getBytes(StandardCharsets.UTF_8);
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                    socket.setBroadcast(true);
                    socket.send(packet);

                    // set timer for timeout, wait for packet
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 10000;
                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);

                    try {
                        socket.receive(packetIn);
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        if (message.contains("ACK")) {
                            setTempValue = Integer.parseInt(message.substring(4, 6));
                            setHumidValue = Integer.parseInt(message.substring(7, 9));
                            tolTValue = Integer.parseInt(message.substring(10, 11));
                            tolHValue = Integer.parseInt(message.substring(12, 13));

                            String output = setTempValue + "°F";
                            // display
                            ((TextView) findViewById(R.id.setTemp)).setText(output);

                            // convert updated humidity back to string
                            String output2 = setHumidValue + "%";
                            // display
                            ((TextView) findViewById(R.id.setHumid)).setText(output2);

                            // convert updated temp tol back to string
                            String output3 = tolTValue + "°F";
                            // display
                            ((TextView) findViewById(R.id.tolT)).setText(output3);

                            // convert updated temp tol back to string
                            String output4 = tolHValue + "%";
                            // display
                            ((TextView) findViewById(R.id.tolH)).setText(output4);

                            SharedPreferences settings = getSharedPreferences(PREFS, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("currentSetTemp", setTempValue);
                            editor.commit();
                            editor.putInt("currentSetHumid", setHumidValue);
                            editor.commit();
                            editor.putInt("currentTolT", tolTValue);
                            editor.commit();
                            editor.putInt("currentTolH", tolHValue);
                            editor.commit();

                            connection = 2;
                            privNum = new Random().nextInt(9) + 1;
                            sendB = Math.toIntExact(Math.round(Math.pow(pubBase, privNum) % pubMod));
                        }
                    } catch (SocketTimeoutException e) {
                        //                    Log.i("TIMEOUT", "Timed out waiting for response");
                    }

                    socket.close();
                } catch (IOException ioException) {
                    //                Log.i("CONNECTION STATUS", "disconnected");
                }
            }

            // calculate B and send, wait for A in return
            else if(connection == 2){
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String msgOut = sendB + " ENDSTARTUP";
                    int msgLen = msgOut.length();
                    byte[] msg = msgOut.getBytes(StandardCharsets.UTF_8);
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                    socket.setBroadcast(true);
                    socket.send(packet);
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 60000;
                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);
                    try {
                        socket.receive(packetIn);
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        if (message.contains("ACK")) {
                            // remove non digits from string
                            message = message.replaceAll("\\D+","");
                            Integer A = Integer.parseInt(message);
                            key = Math.toIntExact(Math.round(Math.pow(A, privNum) % pubMod));
                            connection = 1;
                            Log.i("KEY", String.valueOf(key));
                            scheduledTaskExecutorKey.shutdown();
                            scheduledTaskExecutorUDP = Executors.newScheduledThreadPool(5);
                            scheduledTaskExecutorUDP.scheduleAtFixedRate(new Runnable() {
                                public void run() {
                                    udpRunnable runnable = new udpRunnable();
                                    new Thread(runnable).start();
                                }
                            }, 0, 5000, TimeUnit.MILLISECONDS);
                        }
                    } catch (SocketTimeoutException e) {
                        //                    Log.i("TIMEOUT", "Timed out waiting for response");
                    }

                    socket.close();
                } catch (IOException ioException) {
                    //                Log.i("CONNECTION STATUS", "disconnected");
                }
            }

        }
    }

    class setPointRunnable implements Runnable {

        @Override
        public void run() {
            // begin the key exchange sequence
            if(connection == 5){
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // send message to ip and port
                    String msgOut =  "SETPOINTS " + setTempValue + " " + setHumidValue + " " + tolTValue + " " + tolHValue + " DONE";
                    int msgLen = msgOut.length();
                    byte[] msg = utils.encrypt(key, msgOut);
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                    socket.setBroadcast(true);
                    socket.send(packet);

                    // set timer for timeout, wait for packet
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 10000;
                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);

                    try {
                        socket.receive(packetIn);
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        String messageOut = utils.decrypt(key, msgIn);
                        if (messageOut.contains("ACK")) {
                            setTempValue = Integer.parseInt(messageOut.substring(4, 6));
                            setHumidValue = Integer.parseInt(messageOut.substring(7, 9));
                            tolTValue = Integer.parseInt(messageOut.substring(10, 11));
                            tolHValue = Integer.parseInt(messageOut.substring(12, 13));

                            String output = setTempValue + "°F";
                            // display
                            ((TextView) findViewById(R.id.setTemp)).setText(output);

                            // convert updated humidity back to string
                            String output2 = setHumidValue + "%";
                            // display
                            ((TextView) findViewById(R.id.setHumid)).setText(output2);

                            // convert updated temp tol back to string
                            String output3 = tolTValue + "°F";
                            // display
                            ((TextView) findViewById(R.id.tolT)).setText(output3);

                            // convert updated temp tol back to string
                            String output4 = tolHValue + "%";
                            // display
                            ((TextView) findViewById(R.id.tolH)).setText(output4);

                            SharedPreferences settings = getSharedPreferences(PREFS, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("currentSetTemp", setTempValue);
                            editor.commit();
                            editor.putInt("currentSetHumid", setHumidValue);
                            editor.commit();
                            editor.putInt("currentTolT", tolTValue);
                            editor.commit();
                            editor.putInt("currentTolH", tolHValue);
                            editor.commit();

                            connection = 1;

                            scheduledTaskExecutorSetPoints.shutdown();
                            scheduledTaskExecutorUDP = Executors.newScheduledThreadPool(5);
                            scheduledTaskExecutorUDP.scheduleAtFixedRate(new Runnable() {
                                public void run() {
                                    udpRunnable runnable = new udpRunnable();
                                    new Thread(runnable).start();
                                }
                            }, 0, 5000, TimeUnit.MILLISECONDS);

                        }
                    } catch (SocketTimeoutException e) {
                        //                    Log.i("TIMEOUT", "Timed out waiting for response");
                    }

                    socket.close();
                } catch (IOException ioException) {
                    //                Log.i("CONNECTION STATUS", "disconnected");
                }

                }
            }
        }

}
