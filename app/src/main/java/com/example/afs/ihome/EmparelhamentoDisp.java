package com.example.afs.ihome;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by fguimaraes on 10/07/2017.
 */

 public  class EmparelhamentoDisp extends BroadcastReceiver {

    public EmparelhamentoDisp() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
            try {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                //the pin in case you need to accept for an specific pin
                //Log.d("PIN", " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",0));
                //maybe you look for a name or address
                //Log.d("Bonded", device.getName());
                byte[] pinBytes;
                pinBytes = (""+pin).getBytes("UTF-8");
                device.setPin(pinBytes);
                //setPairing confirmation if neeeded
                device.setPairingConfirmation(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
