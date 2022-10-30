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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
@SuppressLint("ApplySharedPref")    // suppress "apply() instead of commit()" warning
public class MainActivity extends AppCompatActivity {

    public static final String PREFS = "MyPrefs";
    private int setTempValue, setHumidValue, tolTValue, tolHValue, privNum, sendB, alertFlag, noticeFlag, equipFlag;
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

        // initialize values to be saved to memory
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
        // load values from memory on startup
        String outputTemp = setTempValue + "°F";
        String outputHumid = setHumidValue + "%";
        String outputTolT = tolTValue + "°F";
        String outputTolH = tolHValue + "%";

        // update screen with loaded values
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
        // if connection established send new set points/tolerances to MCU
        if(connection == 1) {
            // stop key sharing and data request runnables
            scheduledTaskExecutorKey.shutdown();
            scheduledTaskExecutorUDP.shutdown();

            // connection = 5 for update set points
            connection = 5;

            // start runnable to tell MCU new set points
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

    /*********** Data request runnable ***********/
    // handles requesting of data and receiving data, alert flags, and set points from MCU
    // displays and saves values

    class udpRunnable implements Runnable {

        @Override
        public void run() {
            try {
                // get IP addr from memory in settings screen
                String IpAddress;
                String PREFSTwo = Activity2.PREFSTwo;
                IpAddress = Activity2.ipAddress;
                SharedPreferences settings2 = getSharedPreferences(Activity2.PREFSTwo,0);
                IpAddress = settings2.getString("ipstring","");

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                // send "REQUESTDATA" to MCU in order to receive current temp & humid
                String msgOut = "REQUESTDATA";
                int msgLen = msgOut.length();
                byte[] msg = utils.encrypt(key, msgOut);
                DatagramSocket socket = new DatagramSocket();

                // send message to set ip addr and portnum
                DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                socket.setBroadcast(true);
                socket.send(packet);

                // initialize array for received msg, set timeout to 10 seconds
                byte[] msgIn = new byte[4096];
                DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                socket.setSoTimeout(10000);
                try {
                    // wait for packet received
                    socket.receive(packetIn);
                    String message = new String(msgIn, 0, packetIn.getLength());
                    Log.i("MESSAGE RECEIVED", message);

                    // if message is unencrypted and contains "STARTUPSEQ", run key sharing runnable
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
                    int indE = messageOut.indexOf("EQUIP");

                    // init array to store all numbers in received msg
                    ArrayList nums;

                    // remove all non digits from message and place each int into list
                    String str = messageOut.replaceAll("[^-?0-9]+", " ");
                    nums = new ArrayList(Arrays.asList(str.trim().split(" ")));

                    // received message come in the following format:
                    // "EQUIP 0 ALERT 0 NOTICE 0 temp humid setT setH tolT tolH DATA"

                    // make sure equipment failure flag is present, get value
                    if(indE != -1){
                        equipFlag = Integer.parseInt((String) nums.get(0));
                        if(equipFlag != 0) {
                            // if flag is set to nonzero value - send alert
                            utils.equip(equipFlag, MainActivity.this);
                        }
                        // remove equipment fail flag
                        nums.remove(0);
                    }

                    // make sure alert value is preset, grab and alert if nonzero
                    if(indA != -1) {
                        alertFlag = Integer.parseInt((String) nums.get(0));
                        if(alertFlag != 0) {
                            utils.alert(alertFlag, MainActivity.this);
                        }
                        nums.remove(0);
                    }

                    // check for notice value, alert if nonzero
                    if(indN != -1){
                        noticeFlag = Integer.parseInt((String) nums.get(0));
                        if(noticeFlag != 0) {
                            utils.notice(noticeFlag, MainActivity.this);
                        }
                        nums.remove(0);
                    }

                    // make sure data is present
                    if(indD != -1) {
                        // get current temp whole num and decimal reading (will be separate values in list)
                        Integer tempInt = Integer.parseInt((String) nums.get(0));
                        Integer tempDec = Integer.parseInt((String) nums.get(1));

                        // get current humid whole num and decimal
                        Integer humidInt = Integer.parseInt((String) nums.get(2));
                        Integer humidDec = Integer.parseInt((String) nums.get(3));

                        // get set points and tolerances
                        setTempValue = Integer.parseInt((String) nums.get(4));
                        setHumidValue = Integer.parseInt((String) nums.get(5));
                        tolTValue = Integer.parseInt((String) nums.get(6));
                        tolHValue = Integer.parseInt((String) nums.get(7));

                        // format values
                        String tempS = tempInt + "." + tempDec + "°F";
                        String humidS = humidInt + "." + humidDec + "%";
                        String output = setTempValue + "°F";
                        String output2 = setHumidValue + "%";
                        String output3 = tolTValue + "°F";
                        String output4 = tolHValue + "%";

                        // save set points and tols to memory
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

                        // display formatted strings
                        ((TextView) findViewById(R.id.currTemp)).setText(tempS);
                        ((TextView) findViewById(R.id.currHumid)).setText(humidS);
                        ((TextView) findViewById(R.id.setTemp)).setText(output);
                        ((TextView) findViewById(R.id.setHumid)).setText(output2);
                        ((TextView) findViewById(R.id.tolT)).setText(output3);
                        ((TextView) findViewById(R.id.tolH)).setText(output4);
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

    /*********** Key sharing runnable ***********/
    // handles key exchange algorithm

    class keyRunnable implements Runnable {

        @Override
        public void run() {
            // begin the key exchange sequence if no key
            if (connection == 0) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // send message to begin startup
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

                        // init array to store all numbers in received msg
                        ArrayList nums;

                        // remove all non digits from message and place each int into list
                        String str = message.replaceAll("[^-?0-9]+", " ");
                        nums = new ArrayList(Arrays.asList(str.trim().split(" ")));

                        // MCU will respond with ACK as well as set points to ensure both devices
                        // have same values
                        if (message.contains("ACK")) {
                            setTempValue = Integer.parseInt((String) nums.get(0));
                            setHumidValue = Integer.parseInt((String) nums.get(1));
                            tolTValue = Integer.parseInt((String) nums.get(2));
                            tolHValue = Integer.parseInt((String) nums.get(3));

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

                            // save values to memory
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

                            // set connection flag to send public key and wait for the other
                            connection = 2;

                            // generate private integer and calculate public key
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

                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);
                    try {
                        socket.receive(packetIn);
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        if (message.contains("ACK")) {
                            // remove non digits from string, get MCU's public key
                            message = message.replaceAll("\\D+","");
                            Integer A = Integer.parseInt(message);

                            // calculate key, set connection to 1 for request data
                            key = Math.toIntExact(Math.round(Math.pow(A, privNum) % pubMod));
                            connection = 1;
                            Log.i("KEY", String.valueOf(key));

                            // stop key sharing runnable, start data req runnable
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


    /*********** Update set point runnable ***********/
    // notifies MCU of set point change and sends values
    class setPointRunnable implements Runnable {

        @Override
        public void run() {
            if(connection == 5){
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    // send message with updated values
                    String msgOut =  "SETPOINTS " + setTempValue + " " + setHumidValue + " " + tolTValue + " " + tolHValue + " DONE";
                    int msgLen = msgOut.length();
                    byte[] msg = utils.encrypt(key, msgOut);
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(msg, msgLen, InetAddress.getByName("23.127.196.133"), 54321);
                    socket.setBroadcast(true);
                    socket.send(packet);

                    // set timer for timeout, wait for packet
                    byte[] msgIn = new byte[4096];
                    DatagramPacket packetIn = new DatagramPacket(msgIn, msgIn.length);
                    socket.setSoTimeout(10000);
                    try {
                        socket.receive(packetIn);

                        // decrypt received message
                        String message = new String(msgIn, 0, packetIn.getLength());
                        Log.i("MESSAGE RECEIVED", message);
                        String messageOut = utils.decrypt(key, msgIn);

                        // init array to store all numbers in received msg
                        ArrayList nums;

                        // remove all non digits from message and place each int into list
                        String str = messageOut.replaceAll("[^-?0-9]+", " ");
                        nums = new ArrayList(Arrays.asList(str.trim().split(" ")));

                        // MCU will echo back set points to ensure both devices are the same
                        // change set points to echoed values
                        if (messageOut.contains("ACK")) {
                            setTempValue = Integer.parseInt((String) nums.get(0));
                            setHumidValue = Integer.parseInt((String) nums.get(1));
                            tolTValue = Integer.parseInt((String) nums.get(2));
                            tolHValue = Integer.parseInt((String) nums.get(3));

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

                            // save values to memory
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
