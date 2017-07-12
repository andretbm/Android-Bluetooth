package com.example.afs.ihome;

/**
 * Created by andremuchagata on 08/07/2017.
 */

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;


public class ConnectThread extends Thread {

    private BluetoothSocket socket = null;
    private BluetoothDevice dev = null;
    private String socketType = "client";
    private MainActivity context;

    //============================================================================================//
    // ConnectThread -> Creates socket
    //============================================================================================//
    public ConnectThread (MainActivity activity, BluetoothDevice device) {

        if (device == null)
            return;

        context = activity;
        dev = device;

        try {
            socket = device.createRfcommSocketToServiceRecord(PedidoAcao.DEVICE_UUID);
            //context.dadosET.append("\n-> Socket created");
        } catch (IOException e) {
            //context.setState(com.stam.trabalhobt.LookDevices.STATE_ERROR);
        }
    }
    //============================================================================================//
    //============================================================================================//

    //============================================================================================//
    // run -> connect
    //============================================================================================//
    public void run() {

        //context.dadosET.append("\n-> run");
        // Always cancel discovery because it will slow down a connection
        //context.adapter.cancelDiscovery();

        try {
            socket.connect();               // Make a connection to the BluetoothSocket
        } catch (IOException e) {
            cancel();
            context.connectionFailed();
            return;
        }

        //context.dadosET.append("\n-> connected");
        // Reset the ConnectThread because we're done
        synchronized (context) {
            context.connectThread = null;
        }

        // Start the communication thread to retry!
        context.connected(socket, dev, socketType);
    }

    //============================================================================================//
    // cancel -> Close socket
    //============================================================================================//
    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {

        }
    }
}


