package com.example.afs.ihome;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by fguimaraes on 11/07/2017.
 */

public class MyService extends Service {
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static InputStream inStream = null;
    static OutputStream outStream = null;

    private MainActivity context;
    BluetoothDevice device = null;
    private static final String TAG = "CMA-IHOME";
    BluetoothSocket mSocket = null;



    @Override
    public void onCreate() {
        Toast.makeText(this, " CMA-IHOME- MyService: Serviço online!", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        long millis;
        String accao;
        accao = (String) intent.getExtras().getString("accao");

        String resultado="";
        ItemMessageQuee msgq;
        Toast.makeText(this, " CMA-IHOME- MyService: request!" + accao, Toast.LENGTH_SHORT).show();
        try {
            if (accao.equals("accao")) {
                    //msgq=(ItemMessageQuee) intent.getExtras().getSerializable("dados");
                    byte[] msgToSend = new byte[4];
                    msgToSend=intent.getExtras().getByteArray("msg");
                 if(outStream == null){
                     outStream = mSocket.getOutputStream();
                 }

                    outStream.write(msgToSend);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byte[] buffer = new byte[4];
                    byte data;
                    int recvNumBytes = 0;
                    boolean flag=false;
                    while ( flag==false) {
                        recvNumBytes = inStream.read();
                       // inStream.reset();
                        data = (byte) recvNumBytes;
                        Log.d("recvNumBytes", Integer.toString(recvNumBytes));
                        byte valor = (byte) (data & PedidoAcao.MASK);
                        if (valor == PedidoAcao.ACK) {
                            Toast.makeText(this, "chegou um ACK !", Toast.LENGTH_SHORT).show();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.MyIntentMyServiceReceiver.PROCESS_RESPONSE);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra(MainActivity.MyIntentMyServiceReceiver.RESPONSE_MSG, "");
                            broadcastIntent.putExtra("accao","accao");
                            broadcastIntent.putExtra("msg","msgToSend");
                            broadcastIntent.putExtra("res","TIMEOUT");
                            sendBroadcast(broadcastIntent);
                            flag=true;
                        }
                        if (valor == PedidoAcao.TIMEOUT) {
                            Toast.makeText(this, "chegou um TIMEOUT", Toast.LENGTH_SHORT).show();
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.MyIntentMyServiceReceiver.PROCESS_RESPONSE);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra(MainActivity.MyIntentMyServiceReceiver.RESPONSE_MSG, "");
                            broadcastIntent.putExtra("accao","accao");
                            broadcastIntent.putExtra("msg",msgToSend);
                            broadcastIntent.putExtra("res","TIMEOUT");
                            sendBroadcast(broadcastIntent);
                            flag=true;
                        }
                    }
            }
            if (accao.equals("pedido")) {
                byte[] msgToSend = new byte[4];
                msgToSend=intent.getExtras().getByteArray("msg");
                if(outStream == null){
                    outStream = mSocket.getOutputStream();
                }
                outStream.write(msgToSend);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[4];
                byte data;
                int recvNumBytes = 0;
                float tensaoSensorLuz = 0, luminosidade = 0, resistenciaRecebida = 0;
                String posicaoPorta = "";
                boolean flag=false;
                while ( flag==false) {
                    recvNumBytes = inStream.read();
                   // inStream.reset();
                    data = (byte) recvNumBytes;
                    Log.d("recvNumBytes", Integer.toString(recvNumBytes));
                    byte valor = (byte) (data & PedidoAcao.MASK);
                    if (valor != PedidoAcao.TIMEOUT) {
                        if (msgToSend[1]==PedidoAcao.SENSOR_LUZ){
                            int valorRecebido = (int)valor;
                             tensaoSensorLuz = (((float)valorRecebido*5)/255);
                            luminosidade = (2500/tensaoSensorLuz-500)/4.7f;

                        }
                        if (msgToSend[1]==PedidoAcao.SENSOR_POSICAO){
                            int valorRecebido = (int)valor;
                            float tensao = (((float)valorRecebido * 5)/255);
                            if(tensao == 5) {
                                posicaoPorta = "Aberta";
                            } else if (tensao == 0) {
                                posicaoPorta = "Fechada";
                            } else {
                                posicaoPorta = "Entreaberta";
                            }
                            resistenciaRecebida = 10000 - tensao * 2000;


                        }

                        Toast.makeText(this, "chegou um Valor" + valor, Toast.LENGTH_SHORT).show();
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(MainActivity.MyIntentMyServiceReceiver.PROCESS_RESPONSE);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra(MainActivity.MyIntentMyServiceReceiver.RESPONSE_MSG, "");
                        broadcastIntent.putExtra("accao","pedido");
                        broadcastIntent.putExtra("msg",msgToSend);
                        broadcastIntent.putExtra("res","ACK");
                        broadcastIntent.putExtra("posicaoPorta",posicaoPorta);
                        broadcastIntent.putExtra("resistenciaRecebidaArduino",resistenciaRecebida);
                        broadcastIntent.putExtra("luz",luminosidade);
                        broadcastIntent.putExtra("tensaoLuz",tensaoSensorLuz);
                        sendBroadcast(broadcastIntent);


                        flag=true;
                    }
                    if (valor == PedidoAcao.TIMEOUT) {
                        Toast.makeText(this, "chegou um TIMEOUT", Toast.LENGTH_SHORT).show();

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(MainActivity.MyIntentMyServiceReceiver.PROCESS_RESPONSE);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra(MainActivity.MyIntentMyServiceReceiver.RESPONSE_MSG, "");
                        broadcastIntent.putExtra("accao","pedido");
                        broadcastIntent.putExtra("msg",msgToSend);
                        broadcastIntent.putExtra("res","TIMEOUT");
                        sendBroadcast(broadcastIntent);

                        flag=true;
                    }
                    else{
                        Toast.makeText(this, "chegou uma msg com:" + valor, Toast.LENGTH_SHORT).show();
                        flag=true;
                    }

                }
            }
            if (accao.equals("connectar")) {
                BluetoothDevice btt = (BluetoothDevice) intent.getExtras().getParcelable("device");
                if (btt.getBondState() == device.BOND_BONDED) {
                    Log.d(TAG, btt.getName());
                    device=btt;
                    //BluetoothSocket mSocket=null;
                    try {
                        mSocket = btt.createRfcommSocketToServiceRecord(MY_UUID);
                        //mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, "Sem suporte de ligação");

                    }
                    try {
                        mSocket.connect();
                        inStream = mSocket.getInputStream();
                        outStream = mSocket.getOutputStream();
                    } catch (IOException e) {
                        try {
                            mSocket.close();
                            Log.e(TAG, "Sem suporte de ligação");
                        } catch (IOException e1) {
                            Log.e(TAG, "Sem suporte de ligação");
                            e1.printStackTrace();
                        }
                    }

                    resultado="Connectado";
                    //ATE AQUI

                    Log.e(TAG, "LIGADO!");
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(MainActivity.MyIntentMyServiceReceiver.PROCESS_RESPONSE);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(MainActivity.MyIntentMyServiceReceiver.RESPONSE_MSG, resultado);
                    broadcastIntent.putExtra("accao","connectar");
                    sendBroadcast(broadcastIntent);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return START_STICKY;
    }


    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "TID:" + Thread.currentThread().getId() + ": onDestroy: Service Destroyed");
        Toast.makeText(this, "MyService: Parou", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
