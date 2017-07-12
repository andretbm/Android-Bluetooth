package com.example.afs.ihome;

/**
 * Created by andremuchagata on 08/07/2017.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class CommunicationThread extends Thread{

    private MainActivity context;
    private BluetoothSocket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;

    //============================================================================================//
    // CommunicationThread -> creates sockets
    //============================================================================================//
    public CommunicationThread(MainActivity main_activity, BluetoothSocket sock, String sockType) {

        context = main_activity;
        socket = sock;

        try {
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(main_activity, "Sockets not created", Toast.LENGTH_SHORT).show();
        }
    }
    //============================================================================================//
    //============================================================================================//


    //=======================================================================================//
    // run -> Receive data from HC05
    //=======================================================================================//
    public void run() {
        byte[] buffer = new byte[4];
        byte data;
        int recvNumBytes = 0;

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                //recvNumBytes = inStream.read(buffer);
                recvNumBytes = inStream.read();
                data = (byte) recvNumBytes;
                Log.d ("recvNumBytes", Integer.toString(recvNumBytes));

                // Send the obtained bytes to the UI Activity
                //context.receivedMsg(buffer);
                //context.receivedMsg(data);
            } catch (IOException e) {
                Thread thread = new Thread() {
                    public void run() {
                        //context.setState(ScanDevices.STATE_WAITING);
                        Log.i("Thread_tag1", "Connection Lost Reached here");
                        int count1 = 0;
                        while (count1<10){
                            count1=count1+1;
                            Log.i("Thread_Count: ", Integer.toString(count1));
                            try
                            {
                                Log.i("Thread_tag1", "connection lost Reached");
                                context.connectAsClient();
                                Thread.sleep(7000); // 20 second
                            } catch (Exception e)
                            {
                                Log.i("Thread_tag1", "connectAsClient lost Reached");
                                e.printStackTrace();
                            }
                        }
                        Log.i("Thread_tag1", "Free from REcover LOOP");
                    }
                };
                thread.start();

                break;
            }
        }
        Log.i(TAG, "Free from READ LOOP");
    }

    //=======================================================================================//
    // write -> Send data to HC05
    //=======================================================================================//
    public void write(byte[] buffer) {
        try {
            outStream.write(buffer);
            // Share the sent message back to the UI Activity
            //context.handler.obtainMessage(context.MESSAGE_GUI_SENDED, -1, -1, buffer).sendToTarget();

            context.setState(DevicesScanActivity.STATE_CONNECTED);
        } catch (IOException e) {
            //Log.e(DevicesScanActivity.class.getName(), "Exception during write", e);

        }
    }
    //============================================================================================//
    //============================================================================================//

    //============================================================================================//
    // cancel -> Close socket
    //============================================================================================//
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            //Log.e(LookDevices.class.getName(), "close() of connect socket failed", e);
        }
    }

}
