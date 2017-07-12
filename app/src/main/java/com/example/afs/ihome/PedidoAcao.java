package com.example.afs.ihome;

/**
 * Created by andremuchagata on 10/07/2017.
 */

import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.widget.Toast;
import java.util.UUID;

/**
 * Created by andremuchagata on 05/07/2017.
 */

public interface PedidoAcao {

    public static final int REQUEST_ENABLE_BT = 2;
    public static final int REQUEST_DISCOVERY = 3;

    public static final String DEVICE_EXTRA = "BT_DEVICE";
    public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /******************************
     *      Messages to Send      *
     *******************************/
    // Mask
    public static final byte MASK               = (byte) 0xFF;
    public static final byte DATA_0x00          = (byte) 0x00;

    // Tipos de mensagens
    public static final byte PEDIDO             = (byte) 0x10;
    public static final byte ACAO               = (byte) 0x11;
    public static final byte ACK                = (byte) 0xA0;
    public static final byte TIMEOUT            = (byte) 0xF0;

    // Tipos de sensores
    public static final byte SENSOR_LUZ         = (byte) 0x20;
    public static final byte SENSOR_POSICAO     = (byte) 0x21;
    public static final byte ATUADOR_MANUAL     = (byte) 0x30;
    public static final byte ATUADOR_AUTOMATICO = (byte) 0x31;
    public static final byte DISPLAY            = (byte) 0x40;

    public static final byte ATUADOR_MANUAL_ON  = (byte) 0x00;
    public static final byte ATUADOR_MANUAL_OFF = (byte) 0x01;

    // Endereços dos sensores/atuadores
    public static final byte ENDERECO_1         = (byte) 0x01;
    public static final byte ENDERECO_2         = (byte) 0x02;
    public static final byte ENDERECO_3         = (byte) 0x03;
    public static final byte ENDERECO_4         = (byte) 0x04;

    public static final int MESSAGE_RECEIVED = 1;
    public static final int MESSAGE_STATE_CHANGE = 2;
    public static final int MESSAGE_NEW_CONN = 3;
    public static final int MESSAGE_CONN_FAILED = 5;
    public static final int STATE_DISCONNECTED = 0; // we're doing nothing
    public static final int STATE_CONNECTED = 1;  	// now connected to a remote mBluetoothDevice
    public static final int STATE_WAITING = 2;  	// now waiting for a remote mBluetoothDevice
    public static final int STATE_ERROR = 5;

    // Numeros a apresentar no Display
    public static final byte DISPLAY_0          = (byte) 0x00;
    public static final byte DISPLAY_1          = (byte) 0x01;
    public static final byte DISPLAY_2          = (byte) 0x02;
    public static final byte DISPLAY_3          = (byte) 0x03;
    public static final byte DISPLAY_4          = (byte) 0x04;
    public static final byte DISPLAY_5          = (byte) 0x05;
    public static final byte DISPLAY_6          = (byte) 0x06;
    public static final byte DISPLAY_7          = (byte) 0x07;
    public static final byte DISPLAY_8          = (byte) 0x08;
    public static final byte DISPLAY_9          = (byte) 0x09;

    public static final byte SEEKBAR_00          = (byte) 0x00;
    public static final byte SEEKBAR_25          = (byte) 0x40;
    public static final byte SEEKBAR_50          = (byte) 0x80;
    public static final byte SEEKBAR_75          = (byte) 0xC0;
    public static final byte SEEKBAR_100         = (byte) 0xFF;

    public static final byte LIMITE_MINIMO      = (byte) 0x00;
    public static final byte LIMITE_MAXIMO      = (byte) 0xFF;

    class MSG {     // 4 bytes
        public byte tipoMensagem;
        public byte tipoDispositivo;
        public byte enderecoDispositivo;
        public byte dados;
        private byte[] msgToSend = new byte[4];

        public MSG(byte tipoMensagem, byte tipoDispositivo, byte enderecoDispositivo, byte dados) {
            this.tipoMensagem = tipoMensagem;
            this.tipoDispositivo = tipoDispositivo;
            this.enderecoDispositivo = enderecoDispositivo;
            this.dados = dados;


        }
        public MSG() {

        }

        public byte[] getMessage() {
            buildMsg();
            return msgToSend;
        }
        //============================================================================================//
        // buildMsg -> build msg to send
        //============================================================================================//
        public void buildMsg () {
            msgToSend[0] = tipoMensagem;
            msgToSend[1] = tipoDispositivo;
            msgToSend[2] = enderecoDispositivo;
            msgToSend[3] = dados;

        }
    }

}
