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
import java.net.UnknownHostException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning

public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";

    private int setTempValue, setHumidValue;
    private float currTempValue, currHumidValue;
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

        scheduledTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                tcpRunnable runnable = new tcpRunnable();
                new Thread(runnable).start();
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
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

//    class tcpRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            try {
//                int timeout = 2000;
//
//                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
//
//                Socket socket = new Socket();
//                socket.connect(new InetSocketAddress("23.127.196.133", 54321), timeout);
//
//                OutputStream out = socket.getOutputStream();
//                PrintWriter output = new PrintWriter(out);
//
//                Log.i("CONNECTION STATUS", "sending...");
//                output.println("Hello from Android");
//                output.flush();
//                Log.i("CONNECTION STATUS", "sent");
//
//                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String message = null;
//
//                long startTime = System.currentTimeMillis();
//                long endTime = startTime + 10000;
//
//                while ((System.currentTimeMillis()-startTime) < endTime) {
//
//                    message = input.readLine();
//
//                    if (message != null) {
//                        break;
//                    }
//                }
//                Log.i("MESSAGE RECEIVED", message);
//
////                socket.close()
//
//                if(message != null) {
//                    String tempS = message.substring(0, 5) + "°F";
//                    String humidS = message.substring(6) + "%";
//
//                    ((TextView) findViewById(R.id.currTemp)).setText(tempS);
//                    ((TextView) findViewById(R.id.currHumid)).setText(humidS);
//                }
//
//            } catch (IOException ioException) {
////                Log.i("CONNECTION STATUS", "disconnected");
//            }
//        }
//
//    }

    class tcpRunnable implements Runnable {

        @Override
        public void run() {
            try {
                int timeout = 2000;

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
                socket.receive(packetIn);

                String message = new String(msgIn, 0, packetIn.getLength());
                Log.i("MESSAGE RECEIVED", message);

                if(message != null) {
                    String tempS = message.substring(0, 5) + "°F";
                    String humidS = message.substring(6) + "%";

                    ((TextView) findViewById(R.id.currTemp)).setText(tempS);
                    ((TextView) findViewById(R.id.currHumid)).setText(humidS);
                }

                socket.close();

            } catch (IOException ioException) {
                Log.i("CONNECTION STATUS", "disconnected");
            }
        }

    }



}