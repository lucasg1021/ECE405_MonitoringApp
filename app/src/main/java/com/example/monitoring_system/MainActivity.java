package com.example.monitoring_system;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.lang.UCharacter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning
public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";
    private int setTempValue, setHumidValue, pubMod, pubBase, privNum, sendB, key;
    private int connection = 0;     // indicates whether connection has been established
    TextView tTemp, tHumid;
    private Button menuTwo;
    ScheduledExecutorService scheduledTaskExecutor = Executors.newScheduledThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS, 0);
        setTempValue = settings.getInt("currentSetTemp", 70);
        setHumidValue = settings.getInt("currentSetHumid", 50);
        tTemp = findViewById(R.id.setTemp);
        tHumid = findViewById(R.id.setHumid);
        menuTwo = (Button) findViewById(R.id.switchButton);
        menuTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });
        // if connection established and key shared, send requests for temp&humid data
        if(connection != 0) {
            scheduledTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    udpRunnable runnable = new udpRunnable();
                    new Thread(runnable).start();
                }
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }
        // else try to send encryption data to MCU
        else {
            scheduledTaskExecutor.scheduleAtFixedRate(new Runnable() {
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
        tTemp.setText(outputTemp);
        tHumid.setText(outputHumid);
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

    class udpRunnable implements Runnable {

        @Override
        public void run() {
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                String msgOut = "Hello from Android";
                int msgLen = msgOut.length();
                byte[] msg = msgOut.getBytes(StandardCharsets.UTF_8);
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                socket.setBroadcast(true);
                socket.send(packet);
                long startTime = System.currentTimeMillis();
                long endTime = startTime + 10000;
                byte[] msgIn = new byte[4096];
                DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                socket.setSoTimeout(10000);
                try {
                    socket.receive(packetIn);
                    String message = new String(msgIn, 0, packetIn.getLength());
                    Log.i("MESSAGE RECEIVED", message);
                    String tempS = message.substring(0, 5) + "°F";
                    String humidS = message.substring(6) + "%";
                    ((TextView) findViewById(R.id.currTemp)).setText(tempS);
                    ((TextView) findViewById(R.id.currHumid)).setText(humidS);
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
            if (connection == 0) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Integer mod = new Random().nextInt(21) + 10;     // public modulus, p, range 10-30
                    Integer base = new Random().nextInt(9) + 1;
                    String msgOut = mod + " " + base + " STARTUP";
                    int msgLen = msgOut.length();
                    byte[] msg = msgOut.getBytes(StandardCharsets.UTF_8);
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                    socket.setBroadcast(true);
                    socket.send(packet);
                    long startTime = System.currentTimeMillis();
                    long endTime = startTime + 10000;
                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);
                    try {
                        socket.receive(packetIn);
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        if (message.equals("ACK")) {
                            connection = 2;
                            pubMod = mod;
                            pubBase = base;
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
                            scheduledTaskExecutor.scheduleAtFixedRate(new Runnable() {
                                public void run() {
                                    udpRunnable runnable = new udpRunnable();
                                    new Thread(runnable).start();
                                }
                            }, 0, 5000, TimeUnit.MILLISECONDS);
                        }
                    } catch (SocketTimeoutException e) {
                        //                    Log.i("TIMEOUT", "Timed out waiting for response");
                        connection = 0;
                    }

                    socket.close();
                } catch (IOException ioException) {
                    //                Log.i("CONNECTION STATUS", "disconnected");
                }
            }

        }
    }



}
