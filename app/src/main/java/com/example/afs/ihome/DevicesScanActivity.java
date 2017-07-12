package com.example.afs.ihome;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DevicesScanActivity extends AppCompatActivity implements PedidoAcao {
    private static final String TAG = "CMA-IHOME";
    private IntentFilter discoveryIntentFilter = new IntentFilter();
    private BluetoothAdapter bt_adapter;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_DISABLE_BT = 2;
    private static final int REQUEST_DISCOVERABLE = 3;
    private TextView ligadotxt;
    private TextView accaotxt;
    private ListView lvE, LvNE;
    private Switch btOnOff;
    private ArrayAdapter devicesAdapterE, devicesAdapterNE;
    //private List<BluetoothDevice> deviceArray = new ArrayList<BluetoothDevice>();
    private ProgressBar progressBar;
    private ImageView imgblue;
    private ArrayList<BluetoothDevice> DispositivosEmp = new ArrayList<BluetoothDevice>();
    private ArrayList<BluetoothDevice> DispositivosNEmp = new ArrayList<BluetoothDevice>();

    //private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_scan);
      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarscan);
        setSupportActionBar(toolbar);*/

        final TextView ligadotxt = (TextView) findViewById(R.id.ligado);
        final TextView accaotxt = (TextView) findViewById(R.id.accao);

        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
        progressBar.setVisibility(View.INVISIBLE);
        ImageView imgblue = (ImageView) findViewById(R.id.imagebl);


        btOnOff = (Switch) findViewById(R.id.simpleSwitch);


        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);


        //devicesAdapterE = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, deviceArray);

        //devicesAdapterE = new ArrayAdapter<Mydevices>(this,R.layout.devices_linha,DispositivosEmp);
        devicesAdapterE = new Mydevices_Adaptar(this, DispositivosEmp);

        //Mydevices_Adaptar adapterE = new Mydevices_Adaptar(this, DispositivosEmp);

        //devicesAdapterNE = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, discoveredDevices);
        devicesAdapterNE = new Mydevices_Adaptar(this, DispositivosNEmp);

        lvE = (ListView) findViewById(R.id.listViewConnected);
        lvE.setAdapter(devicesAdapterE);

        LvNE = (ListView) findViewById(R.id.listViewNotConnect);
        LvNE.setAdapter(devicesAdapterNE);

        bt_adapter = BluetoothAdapter.getDefaultAdapter();
        if (bt_adapter == null) {
            Toast.makeText(this, "Dispositivo sem suporte de Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Dispositivo Suporta Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bt_adapter.isEnabled()) {
            ligadotxt.setText("Não Ligado");
            imgblue.setImageResource(R.drawable.blue_off);
            btOnOff.setChecked(false);
            accaotxt.setEnabled(false);
        } else {
            ligadotxt.setText("Ligado");
            btOnOff.setChecked(true);
            imgblue.setImageResource(R.drawable.blue_on);
            accaotxt.setEnabled(true);
        }
        btOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    //verificar se está a fazer SCAN, cancelar pesquisa de dispositivos
                    bt_adapter.disable();
                    ligadotxt.setText("Não Ligado");
                }
            }
        });
        accaotxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt_adapter.isEnabled()) {
                    if (accaotxt.getText().equals("Pesquisar")) {
                        accaotxt.setText("Parar");
                        progressBar.setVisibility(View.VISIBLE);
                        startDeviceDiscovery();
                    } else {
                        accaotxt.setText("Pesquisar");
                        progressBar.setVisibility(View.INVISIBLE);
                        if (bt_adapter.isDiscovering())
                            bt_adapter.cancelDiscovery();
                    }
                }
            }
        });
        registerReceiver(discover_devices, discoveryIntentFilter);

        //deviceArray.addAll(bt_adapter.getBondedDevices());
        for (BluetoothDevice bt : bt_adapter.getBondedDevices()) {
            //Mydevices md= new Mydevices(bt.getType(),bt.getName(),bt.getAddress(),bt.getBluetoothClass().getMajorDeviceClass()+"");
            DispositivosEmp.add(bt);
        }
        registerForContextMenu(lvE);
        registerForContextMenu(LvNE);

        devicesAdapterE.notifyDataSetChanged();

        if (bt_adapter.isEnabled()) {
            progressBar.setVisibility(View.VISIBLE);
            startDeviceDiscovery();
            accaotxt.setText("Parar");
        }
    }

    private boolean startDeviceDiscovery() {
        if (!bt_adapter.isEnabled()) {
            Log.e(TAG, "Cannot startAsServer device discovery. Bluetooth adapter is disabled!");
            return false;
        }

        if (ContextCompat.checkSelfPermission(DevicesScanActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return false;
        }

        if (bt_adapter.isDiscovering())
            bt_adapter.cancelDiscovery();

        registerReceiver(discover_devices, discoveryIntentFilter);

        bt_adapter.startDiscovery();
        return true;
    }

    private void refreshDevices() {
        // deviceArray.clear();
//        if(tBt.isChecked()){
//            deviceArray.addAll(bt_adapter.getBondedDevices());
//        }
//
//        for(BluetoothDevice device : discoveredDevices)
//            if(!deviceArray.contains(device))
//                deviceArray.add(device);
//
        devicesAdapterNE.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (!bt_adapter.isEnabled()) {
                Log.e(TAG, "Cannot startAsServer device discovery. Bluetooth adapter is disabled!");

            }

            if (ContextCompat.checkSelfPermission(DevicesScanActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }

            if (bt_adapter.isDiscovering())
                bt_adapter.cancelDiscovery();
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView tv = (TextView) findViewById(R.id.ligado);
        ImageView imgblue = (ImageView) findViewById(R.id.imagebl);
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth ligado com sucesso", Toast.LENGTH_SHORT).show();
                    tv.setText("Ligado");
                    btOnOff.setChecked(true);
                    progressBar.setVisibility(View.VISIBLE);
                    imgblue.setImageResource(R.drawable.blue_on);
                    startDeviceDiscovery();
                } else {
                    Toast.makeText(this, "Bluetooth continua desligado", Toast.LENGTH_SHORT).show();
                    //finish();
                    tv.setText("Não Ligado");
                    btOnOff.setChecked(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    imgblue.setImageResource(R.drawable.blue_off);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public final BroadcastReceiver discover_devices = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            TextView tv = (TextView) findViewById(R.id.ligado);
            TextView accaotxt = (TextView) findViewById(R.id.accao);
            ImageView imgblue = (ImageView) findViewById(R.id.imagebl);

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                DispositivosNEmp.clear();
            }

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                DispositivosNEmp.clear();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG, device.getType() + device.getClass().toString() + "-->" + device.getName() + "-->" + device.getBluetoothClass() + (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO));
                DispositivosNEmp.add(device);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    tv.setText("Não Ligado");
                    imgblue.setImageResource(R.drawable.blue_off);
                }
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    tv.setText("Ligado");
                    imgblue.setImageResource(R.drawable.blue_on);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                bt_adapter.cancelDiscovery();
                unregisterReceiver(this);
                discoveryComplete(DispositivosNEmp);
                accaotxt.setText("Pesquisar");
            }
        }
    };

    protected void onResume() {
        super.onResume();

    }

    private void discoveryComplete(List<BluetoothDevice> devices) {
        //bt.setEnabled(true);
        refreshDevices();
        progressBar.setVisibility(View.INVISIBLE);
        setTitle("Selecionar device");

    }

    //menu LV
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listViewConnected) {
            super.onCreateContextMenu(menu, v, menuInfo);
            getMenuInflater().inflate(R.menu.menu_devices_emparelhados, menu);
        } else {
            super.onCreateContextMenu(menu, v, menuInfo);
            getMenuInflater().inflate(R.menu.menu_devices_poremparelhar, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        long id = info.id;
        // String  str = (String) devicesAdapterE.getItem(pos);

        switch (item.getItemId()) {
            case R.id.ligardevice:
                //Toast.makeText(getApplicationContext(), "Delete  option  selected:" + id + ":", Toast.LENGTH_LONG).show();
                if (bt_adapter.isDiscovering())
                    bt_adapter.cancelDiscovery();

                BluetoothDevice bluto=(BluetoothDevice) DispositivosEmp.get(pos);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("btDevName", bluto.getName());
                returnIntent.putExtra("btDevAddress", bluto.getAddress());
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
            case R.id.desemparelhardevice:
                BluetoothDevice blut=(BluetoothDevice) DispositivosEmp.get(pos);
                try {
                    Method m = blut.getClass().getMethod("removeBond", (Class[]) null);
                    m.invoke(blut, (Object[]) null);
                    DispositivosEmp.remove(blut);
                    devicesAdapterE.notifyDataSetChanged();
                    //removeBond.invoke(blut);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            case R.id.emparelhardevice:
                    BluetoothDevice blute=(BluetoothDevice) DispositivosNEmp.get(pos);
                    if(blute.createBond()){
                        DispositivosEmp.clear();
                        for (BluetoothDevice bt : bt_adapter.getBondedDevices()) {
                            //Mydevices md= new Mydevices(bt.getType(),bt.getName(),bt.getAddress(),bt.getBluetoothClass().getMajorDeviceClass()+"");
                            DispositivosEmp.add(bt);
                        }
                        devicesAdapterE.notifyDataSetChanged();
                        DispositivosNEmp.remove(blute);
                        devicesAdapterNE.notifyDataSetChanged();

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),
                                "Emparelhamento Cancelado...", Toast.LENGTH_LONG).show();
                    }
                 return true;
            default:
                return super.onContextItemSelected(item);
        }

    }


//    Intent discoverableIntent = new
//            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//    startActivity(discoverableIntent);
}
