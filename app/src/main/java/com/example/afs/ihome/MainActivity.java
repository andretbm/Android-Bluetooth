package com.example.afs.ihome;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;


//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements PedidoAcao, View.OnClickListener {
   private static final String TAG  = "CMA-IHOME";
    static ProgressDialog pd_ring;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private SeekBar seekBar;
    private Button btAtualizar, btTensao, btVerPosicaoPorta, btPotenciometro, upButton, downButton, btDisplay, btAutomatico;
    public TextView controlador,tvResistencia, tvPosicao, tvLuzAmbiente, tvTensaoArduino;
    public EditText etValues;
    public ConnectThread connectThread;
    private CommunicationThread commThread;
    InputStream inStream = null;
    OutputStream outStream = null;
    static Menu menu;
    int currentWidgetId;

    private static NotificationManager notificationManager;

    static int numNotification = 1;
    static int notificationID = 100;


    MyIntentMyServiceReceiver receiver;
    ImageView imgconectado;
    PedidoAcao.MSG msgToSend = new PedidoAcao.MSG();


   // private IntentFilter discoveryIntentFilter = new IntentFilter();
    private BluetoothAdapter bt_adapter;
    private static final int SELECT_DEVICE = 2;
    BluetoothDevice device = null;
    private TextView txtHome;

    BluetoothSocket mSocket = null;

    private int uprange = 9;
    private int downrange = 0;
    private int values = 0;
    int state;


    protected void onDestroy(){
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        controlador = (TextView) findViewById(R.id.tvControlador);
        tvResistencia = (TextView) findViewById(R.id.tvResistencia);
        tvPosicao = (TextView) findViewById(R.id.tvPosicaoPorta);
        tvLuzAmbiente = (TextView) findViewById(R.id.tvLuzAmbiente);
        controlador.setText(R.string.nothing_connected);
        tvTensaoArduino = (TextView) findViewById(R.id.tvTensaoArduino);
        btAtualizar = (Button) findViewById(R.id.btAtualizarLuz);
        btAtualizar.setOnClickListener(this);
        btVerPosicaoPorta = (Button) findViewById(R.id.btVerPosicaoPorta);
        btVerPosicaoPorta.setOnClickListener(this);
        btAutomatico  = (Button) findViewById(R.id.btAutomatico);
        btAutomatico.setOnClickListener(this);
        etValues = (EditText) findViewById(R.id.values);
         imgconectado= (ImageView) findViewById(R.id.action_alarme);

        etValues.setText("0");

        upButton = (Button) findViewById(R.id.upButton);
        upButton.setText("+");
        upButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (values >= downrange && values <= uprange)
                    values++;
                if (values > uprange)
                    values = downrange;
                etValues.setText("" + values);

            }
        });



        bt_adapter = BluetoothAdapter.getDefaultAdapter();

        downButton = (Button) findViewById(R.id.downButton);
        downButton.setText("-");
        downButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (values >= downrange && values <= uprange)
                    values--;

                if (values < downrange)
                    values = uprange;

                etValues.setText(values + "");
            }
        });

        btDisplay = (Button) findViewById(R.id.display);
        btDisplay.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.turnLight);
        seekBar.setMax(4);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                switch (progress) {
                    case 0:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_00);
                        break;
                    case 1:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_25);
                        break;
                    case 2:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_50);
                        break;
                    case 3:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_75);
                        break;
                    case 4:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_100);
                        break;
                    default:
                        msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_MANUAL, ENDERECO_1, SEEKBAR_00);
                        break;
                }
                if (controlador.getText().equals("Connectado")) {
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    intent.putExtra("accao", "accao");
                    intent.putExtra("msg", msgToSend.getMessage());
                    startService(intent);
                }
                else{
                   // Toast.makeText(this, "Sockets not creeated", Toast.LENGTH_SHORT).show();
                }

                    try{

                    } catch (Exception e) {
                        // Toast.makeText(TAG, "Sockets not creeated", Toast.LENGTH_SHORT).show();
                    }

                                    progressValue = progress;

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        IntentFilter filter = new IntentFilter(MyIntentMyServiceReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyIntentMyServiceReceiver();
        registerReceiver(receiver, filter);
    }

    public void displayNumber(int value) {
        switch (value) {
            case 0:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_0);
                break;
            case 1:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_1);
                break;
            case 2:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_2);
                break;
            case 3:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_3);
                break;
            case 4:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_4);
                break;
            case 5:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_5);
                break;
            case 6:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_6);
                break;
            case 7:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_7);
                break;
            case 8:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_8);
                break;
            case 9:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_9);
                break;
            default:
                msgToSend = new MSG(ACAO, DISPLAY, ENDERECO_1, DISPLAY_0);
                break;


        }


        if (controlador.getText().equals("Connectado")) {
            Intent intent = new Intent(MainActivity.this, MyService.class);
            intent.putExtra("accao", "accao");
            intent.putExtra("msg",msgToSend.getMessage());
            startService(intent);
        }
        else{
            Toast.makeText(this, "Sem suporte de ligação ", Toast.LENGTH_SHORT).show();
        }

    }


    private  void createNotificationInboxStyle(String titulo,String msg, String tk) {

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("EXTRA_MESSAGE", "Create notification InboxSytle");
        intent.putExtra("EXTRA_ID", numNotification);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pending = stackBuilder.getPendingIntent( 0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder  notificationBuilder =  new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(titulo);
        notificationBuilder.setContentText(msg);
        notificationBuilder.setTicker(tk);
        notificationBuilder.setSmallIcon(R.drawable.iconapp);
        notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        notificationBuilder.setContentIntent(pending);


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();


        String[] events = new String[6];
        events[0] = new String("This is first line....");
        events[1] = new String("This is second line...");
        events[2] = new String("This is third line...");
        events[3] = new String("This is 4th line...");
        events[4] = new String("This is 5th line...");
        events[5] = new String("This is 6th line...");

        inboxStyle.setBigContentTitle("Big Title Details:");

        for (int i=0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        notificationBuilder.setStyle(inboxStyle);

        notificationBuilder.setNumber(events.length);
        notificationBuilder.setContentText("+" + (events.length -1)+ " more");


        Notification notification = notificationBuilder.build();

        notificationManager.notify(notificationID, notification);
        numNotification++;
    }
    @Override
    protected void onResume() {
        super.onResume();
 }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(!bt_adapter.isEnabled()) {
                Log.e(TAG, "Cannot startAsServer device discovery. Bluetooth adapter is disabled!");

            }

            if (ContextCompat.checkSelfPermission(MainActivity.this , Manifest.permission. ACCESS_COARSE_LOCATION)  != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this , new String[]{Manifest.permission. ACCESS_COARSE_LOCATION}, 0);
            }

            if(bt_adapter.isDiscovering())
                bt_adapter.cancelDiscovery();

            /*Toast.makeText(this, "PASSOU!....", Toast.LENGTH_SHORT).show();
            IntentFilter filter = new IntentFilter(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            registerReceiver(mReceiver, filter);
            bt_adapter.startDiscovery();*/
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
            return true;
        }

        if (id == R.id.action_select) {

            Intent intent = new Intent(MainActivity.this, com.example.afs.ihome.DevicesScanActivity.class);
            startActivityForResult(intent,SELECT_DEVICE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        String accao="";
        switch (v.getId()) {

            case R.id.btAtualizarLuz:
                msgToSend = new PedidoAcao.MSG(PEDIDO, SENSOR_LUZ, ENDERECO_1, DATA_0x00);
                accao="PEDIDO";
                break;
            case R.id.btTensao:
                msgToSend = new PedidoAcao.MSG(PEDIDO, SENSOR_LUZ, ENDERECO_1, DATA_0x00);
                accao="PEDIDO";
                break;

            case R.id.btVerPosicaoPorta:
                msgToSend = new PedidoAcao.MSG(PEDIDO, SENSOR_POSICAO, ENDERECO_1, DATA_0x00);
                accao="PEDIDO";
                break;

            case R.id.display:
                displayNumber(Integer.parseInt(etValues.getText().toString()));
                break;

            case R.id.btAutomatico:
                msgToSend = new PedidoAcao.MSG(ACAO, ATUADOR_AUTOMATICO, ENDERECO_1, DATA_0x00);
                accao="ACCAO";
                break;
            default:
                break;
        }

        if (controlador.getText().equals("Connectado")) {
            if(accao.equals("PEDIDO")){
                Intent intent = new Intent(MainActivity.this, MyService.class);
                intent.putExtra("accao", "pedido");
                intent.putExtra("msg",msgToSend.getMessage());
                startService(intent);}
            else if(accao.equals("ACCAO")){
                Intent intent = new Intent(MainActivity.this, MyService.class);
                intent.putExtra("accao", "accao");
                intent.putExtra("msg",msgToSend.getMessage());
                startService(intent);
            }

        }
        else{
            Toast.makeText(this, "Sockets not creeated", Toast.LENGTH_SHORT).show();
        }

    }

    public void actualizaWidget(String msg){
        currentWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Log.i(TAG, "0:currentWidgetId: "+currentWidgetId);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            currentWidgetId = extras.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID);
            Log.i(TAG, "1:currentWidgetId: " + currentWidgetId);
            RemoteViews remoteViews = new RemoteViews(MainActivity.this.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.textView, msg.toString());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
            appWidgetManager.updateAppWidget(currentWidgetId, remoteViews);
            Intent result = new Intent();
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, currentWidgetId);
            setResult(RESULT_OK, result);
            //finish();
        }
        if (currentWidgetId == INVALID_APPWIDGET_ID) {
            Log.i(TAG, "I am invalid");
        }
        //finish();
    }
    public final BroadcastReceiver filter = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            final String PROCESS_RESPONSE = "pt.pbscaf.s06_4_intentservice.action.PROCESS_RESPONSE";
            final String RESPONSE_MSG = "MyIntentMyServiceResponse";
             final String RESPONSE_CONNECT = "MyIntentMyServiceResponse";
            String accao = intent.getStringExtra("accao");

            if(accao.equals("connectar")){
                pd_ring.dismiss();
                String resp = intent.getStringExtra(RESPONSE_MSG);
                controlador = (TextView) findViewById(R.id.tvControlador);
                controlador.setText(resp);
                if(resp.equals("Connectado")){
                    MainActivity.menu.getItem(0).setIcon(R.drawable.blue_on);
                }
            }

            if(accao.equals("pedido") || accao.equals("accao")){
                String res = intent.getStringExtra("res");
                byte[] msgToSend = new byte[4];
                msgToSend=intent.getExtras().getByteArray("msg");
                String resistenciaRecebida = intent.getStringExtra("resistenciaRecebida");
                String posicaoPorta = intent.getStringExtra("posicaoPorta");
                String luminosidade = intent.getStringExtra("luz");
                String tensaoArduino = intent.getStringExtra("tensaoLuz");
                tvResistencia.setText(resistenciaRecebida);
                tvPosicao.setText(posicaoPorta);
                tvLuzAmbiente.setText(luminosidade);
                tvTensaoArduino.setText(tensaoArduino);



                if(res.equals("TIMEOUT")){

                    if (msgToSend != null)
                        createNotificationInboxStyle("IHOME - ALERTA","A mensagem " + msgToSend.toString() + " FALHOU!","");
                    else
                        createNotificationInboxStyle("IHOME - ALERTA","A mensagem  FALHOU!","");
                }
            }
            //t.setText(resp);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final String nome, address;
        switch (requestCode) {
            case SELECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    // get Bluetooth device

//                    device = data.getExtras().getParcelable(DevicesScanActivity.DEVICE_EXTRA);
//                    if (device == null)
//                        return;
                    nome=data.getExtras().getString("btDevName");
                    address=data.getExtras().getString("btDevAddress");
                    bt_adapter = BluetoothAdapter.getDefaultAdapter();
                    for (BluetoothDevice bt : bt_adapter.getBondedDevices()) {
                        //Mydevices md= new Mydevices(bt.getType(),bt.getName(),bt.getAddress(),bt.getBluetoothClass().getMajorDeviceClass()+"");
                        if(bt.getName().equals(nome) && bt.getAddress().equals(address)) {
                            device=bt;
                                //DAQUI
                                //verificar se já não está ligado
                           //  connectAsClient();
                           // connectaDEvice(device);
                            pd_ring = new ProgressDialog(MainActivity.this);
                            pd_ring.setMessage("Por favor aguarde...");
                            pd_ring.setTitle("A Ligar " + device.getName());
                            pd_ring.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            pd_ring.setCancelable(false);
                            pd_ring.setIndeterminate(true);
                            pd_ring.show();

                            Intent intent = new Intent(MainActivity.this, MyService.class);
                            intent.putExtra("accao", "connectar");
                            intent.putExtra("device",device);
                            startService(intent);
                            break;
                                //ATE AQUI
                        }
                        //DispositivosEmp.add(bt);
                    }
                    //Toast.makeText(this, "Try to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "Try to connect to " + nome, Toast.LENGTH_SHORT).show();
                    // Connect to device
                    //connectAsClient ();

                } else {
                    Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public boolean connectAsClient() {


        // Cancel any thread attempting to make a connection
        if (state == STATE_WAITING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Start the thread to connect with the given device
        connectThread = new ConnectThread(this, device);
        connectThread.start();
        setState(STATE_WAITING);

        return true;

        //setState(STATE_CONNECTED);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {

        //blueDeviceName.setText("connected");

        // Cancel the thread that completed the connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (commThread != null) {
            commThread.cancel();
            commThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        commThread = new CommunicationThread(this, socket, socketType);
        commThread.start();         // Start thread

        // Send the name of the connected device back to the UI Activity
        Message msg = handler.obtainMessage(MESSAGE_NEW_CONN);
        Bundle bundle = new Bundle();
        bundle.putString("DEVICE_NAME", device.getName());
        msg.setData(bundle);
        handler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    public void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = handler.obtainMessage(MESSAGE_CONN_FAILED);
        Bundle bundle = new Bundle();
        bundle.putString("INFO", "Unable to connect device");
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


    public synchronized void setState(int state) {
        this.state = state;

        // give the new state to the Handler so the UI Activity can update
        if(handler != null)
            handler.obtainMessage(MESSAGE_STATE_CHANGE, state, 0).sendToTarget();
    }
    public synchronized int getState() {
        return state;
    }
    public final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            int state = msg.arg1;
            //handleStateMsg(state);

            switch (msg.what) {
                case MESSAGE_RECEIVED:
                    byte readBuf = (byte) msg.obj;
                    byte valor = (byte) (readBuf & MASK);

                    if (valor == ACK) {
                        // terminal(" -> ACK", false);
                        //vibrator.vibrate(VIBRATOR_MILLISECONDS_OK);
                        return true;
                    }

                    if (valor == TIMEOUT) {
                        // terminal(" -> Timeout", false);
                        // vibrator.vibrate(VIBRATOR_MILLISECONDS_NOK);
                        return true;
                    }
/*
                    if (readBuf[0] == LIMITE_MINIMO || readBuf[0] == LIMITE_MAXIMO)
                        createNotification(readBuf[0]);
*/
                    //dadosET.append (String.format("%X", valor));
                    //scrollTV.fullScroll(View.FOCUS_DOWN);

                    //terminal (" -> " + String.format("%X", value), false);

                    return true;

                case MESSAGE_NEW_CONN:
                    controlador.setText(" ====== Connected to " + device.getName());
                    return true;

                case MESSAGE_CONN_FAILED:
                    controlador.setText("  === Connection Failed ===\n");
                    return true;

                case MESSAGE_STATE_CHANGE:
                    if (state == STATE_CONNECTED)
                        controlador.setText("Connected to " + device.getName());
                    if (state == STATE_WAITING)
                        controlador.setText("Connecting...");
                    if (state == STATE_DISCONNECTED)
                        controlador.setText("Disconneted");
                    return true;
            }

            return false;
        }
    });

    private  void connectaDEvice(final BluetoothDevice btt) {
        AsyncTask<BluetoothDevice, String, BluetoothSocket> task = new AsyncTask<BluetoothDevice, String, BluetoothSocket>() {
            @Override
            protected void onPreExecute() {
                pd_ring = new ProgressDialog(MainActivity.this);
                pd_ring.setMessage("A ligar");
                pd_ring.setTitle("Ligar " + btt.getName());
                pd_ring.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd_ring.setCancelable(false);
                pd_ring.setIndeterminate(true);
                pd_ring.show();
            }

            @Override
            protected BluetoothSocket doInBackground(BluetoothDevice... params) {

                BluetoothDevice btt = (params[0]);
                //AQUI

                if (btt.getBondState() == device.BOND_BONDED) {
//                    Log.d(TAG, device.getName());
//                    //BluetoothSocket mSocket=null;
//                    try {
//                        mSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//                        //mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//                    } catch (IOException e1) {
//                        // TODO Auto-generated catch block
//                        Log.d(TAG, "socket not created");
//
//                    }
//                    try {
//                        mSocket.connect();
//                    } catch (IOException e) {
//                        try {
//                            mSocket.close();
//                            Log.d(TAG, "Cannot connect");
//                        } catch (IOException e1) {
//                            Log.d(TAG, "Socket not closed");
//                            e1.printStackTrace();
//                        }
//                    }
                    int i;
                    i = 0;

                    publishProgress("A ligar...");

                    //ATE AQUI

                    //Log.d(TAG, "LIGADO!");
                    Intent intent = new Intent(MainActivity.this, MyService.class);
                    intent.putExtra("accao", "connectar");
                    intent.putExtra("device",btt);
                    startService(intent);

                }
                return mSocket;
            }
            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);

            }

            @Override
            protected void onPostExecute(BluetoothSocket mSocket) {
//
               // TextView txt = (TextView) findViewById(R.id.textView);
//                controlador.setText("Executed"); // txt.setText(result)
                super.onPostExecute(mSocket);
//                pd_ring.dismiss();
//
//                try{
//                inStream = mSocket.getInputStream();
//                outStream = mSocket.getOutputStream();
//
//                    Intent intent = new Intent(MainActivity.this, MyService.class);
//                    ItemMessageQuee aa = new ItemMessageQuee();
//                    MSG msgToSend = new MSG ();
//                    msgToSend= new MSG((byte) 0x11, (byte) 0x30, (byte) 0x01, (byte) 0xff);
//
//                    aa.setMsgToSend(msgToSend);
//                    aa.setDataEnvio( new Date());
//                    aa.setAccao("accao");
//                    intent.putExtra("IN", aa);
//                    intent.putExtra("BT",this);
//                    startService(intent);
//
//
////                    outStream.write(msgToSend.getMessage());
////                    msgToSend= new MSG((byte) 0x11, (byte) 0x40, (byte) 0x01, (byte) 0x01);
////                    outStream.write(msgToSend.getMessage());
//
//            } catch (IOException e) {
//               // Toast.makeText(TAG, "Sockets not creeated", Toast.LENGTH_SHORT).show();
//            }


            }
        };
        task.execute(btt);
    }
    public class MyIntentMyServiceReceiver extends BroadcastReceiver {
        public static final String PROCESS_RESPONSE = "pt.pbscaf.s06_4_intentservice.action.PROCESS_RESPONSE";
        public static final String RESPONSE_MSG = "MyIntentMyServiceResponse";
        public static final String RESPONSE_CONNECT = "MyIntentMyServiceResponse";
        @Override
        public void onReceive(Context context, Intent intent) {
            String accao = intent.getStringExtra("accao");

            if(accao.equals("connectar")){
                pd_ring.dismiss();
                String resp = intent.getStringExtra(RESPONSE_MSG);
                controlador = (TextView) findViewById(R.id.tvControlador);
                controlador.setText(resp);
                if(resp.equals("Connectado")){
                    MainActivity.menu.getItem(0).setIcon(R.drawable.blue_on);
                }
            }

            if(accao.equals("pedido") || accao.equals("accao")){
                String res = intent.getStringExtra("res");
                byte[] msgToSend = new byte[4];
                msgToSend=intent.getExtras().getByteArray("msg");

                if(res.equals("TIMEOUT")){
//                    try {
//                        if (msgToSend != null)
//                            //createNotificationInboxStyle("IHOME - ALERTA","A mensagem " + msgToSend.toString() + " FALHOU!","");
//                        else
//                            //createNotificationInboxStyle("IHOME - ALERTA","A mensagem  FALHOU!","");
//                    } catch (Exception e) {
//                        Log.d("CMA",e.toString());
//                    }

                }
                if(res.equals("pedido")){

                }
            }
            //t.setText(resp);
        }
    };
}
