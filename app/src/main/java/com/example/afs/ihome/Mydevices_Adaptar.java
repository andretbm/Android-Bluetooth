package com.example.afs.ihome;

/**
 * Created by fguimaraes on 09/07/2017.
 */


import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Mydevices_Adaptar extends ArrayAdapter<BluetoothDevice> {

    private final Context context;
    private final ArrayList<BluetoothDevice> modelsArrayList;

    public Mydevices_Adaptar(Context context, ArrayList<BluetoothDevice> modelsArrayList) {

        super(context, R.layout.devices_linha, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;

            rowView = inflater.inflate(R.layout.devices_linha, parent, false);

            // 3. Get icon,title & counter views from the rowView
            ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon_tipo);
            TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
            ImageView imgViewopcoes = (ImageView) rowView.findViewById(R.id.item_icon_opcoes);

            // 4. Set the text for textView
//        switch (((BluetoothDevice)modelsArrayList.get(position)).getBluetoothClass().getMajorDeviceClass()) {
//            case BluetoothClass.Device.Major.COMPUTER:
//                imgView.setImageResource(R.drawable.computador);
//            case BluetoothClass.Device.Major.PHONE:
//                imgView.setImageResource(R.drawable.telefone);
//            case BluetoothClass.Device.Major.PERIPHERAL:
//                imgView.setImageResource(R.drawable.rato);
//            case BluetoothClass.Device.Major.AUDIO_VIDEO:
//                imgView.setImageResource(R.drawable.phones);
//            default:
//                imgView.setImageResource(R.drawable.outros);
//        }
        BluetoothDevice device =(BluetoothDevice) modelsArrayList.get(position);
        if(device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO)
        {
            imgView.setImageResource(R.drawable.phones);
        }
        else if(device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER)
        {
            imgView.setImageResource(R.drawable.computador);
        }
        else if(device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PHONE)
        {
            imgView.setImageResource(R.drawable.telefone);
        }
        else if(device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.PERIPHERAL)
        {
            imgView.setImageResource(R.drawable.rato);
        }
        else{
            imgView.setImageResource(R.drawable.outros);
        }


        titleView.setText(((BluetoothDevice)modelsArrayList.get(position)).getName());
        imgViewopcoes.setImageResource(R.drawable.opcoes);
       // 5. retrn rowView
        return rowView;
    }
}