package com.fugao.infusion.bluetooth.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class BlueToothService {

    private BluetoothAdapter adapter;
    private Context context;
    private static int mState;
    private int scanState = 1;
    private Boolean D = true;
    private String TAG = "BlueToothService";
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote

    public static final int STATE_CONNTCTED_ONCE=5;
    public static final int STATE_CONNTCTED_NEVER=6;

    // device
    public static final int LOSE_CONNECT = 4;


    public static final int FAILED_CONNECT = 5;
    public static final int SUCCESS_CONNECT = 6; // now connected to a remote

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int STATE_SCANING = 0;// 扫描状态
    public static final int STATE_SCAN_STOP = 1;

    private static final int WRITE_READ = 2;
    private static final int WRITE_WAIT = 3;
    private static int writeState = 2;
    public static int times = 0;
    private static int PrinterType = 0;
    private static int PrinterTypeNow = 0;
    private int timeout;

//   private static int printting = 0;//正在打印
//   private static int volltage = 1;//打印机电压
//   private static int tempreture = 2;//打印机的温度
//   private  static int no_page = 3;//打印机没有纸

    private void SetWriteState(int state) {
        synchronized (this) {
            writeState = state;
        }
    }

    private static final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME = "BTPrinter";

    private Handler mHandler;

    public BlueToothService(Context context) {
        this.context = context;
        mState = STATE_NONE;
        adapter = BluetoothAdapter.getDefaultAdapter();
    }
    public BlueToothService(Context context, Handler handler) {
        this.context = context;
        this.mHandler = handler;
        mState = STATE_NONE;
        adapter = BluetoothAdapter.getDefaultAdapter();

    }

    public boolean HasDevice() {
        if (adapter != null) {
            return true;
        }
        return false;

    }

    public boolean IsOpen() {
        synchronized (this) {
            if (adapter.isEnabled()) {
                return true;
            }
            return false;
        }
    }

    /**
     * 打开蓝牙设备
     */
    public void OpenDevice() {
        Toast.makeText(context, "正在打开蓝牙", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 请求开启蓝牙设备
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);

    }

    public void CloseDevice() {
        adapter.disable();
    }

    // 获取已经配对的蓝牙设备的物理地址
    public Set<BluetoothDevice> GetBondedDevice() {

        Set<BluetoothDevice> devices = adapter.getBondedDevices();
        return devices;
    }

    // 扫描蓝牙设备

    public void ScanDevice() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        SetScanState(BlueToothService.STATE_SCANING);
        // Request discover from BluetoothAdapter
        adapter.startDiscovery();
    }

    public void StopScan() {

        context.unregisterReceiver(mReceiver);
        adapter.cancelDiscovery();
        SetScanState(BlueToothService.STATE_SCAN_STOP);
    }

    public OnReceiveDataHandleEvent OnReceive = null;

    public interface OnReceiveDataHandleEvent {
        public void OnReceive(BluetoothDevice device);
    }

    public OnReceiveDataHandleEvent getOnReceive() {
        return OnReceive;
    }

    public void setOnReceive(OnReceiveDataHandleEvent onReceive) {
        OnReceive = onReceive;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    SetScanState(BlueToothService.STATE_SCANING);
                    OnReceive.OnReceive(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                SetScanState(BlueToothService.STATE_SCAN_STOP);

                OnReceive.OnReceive(null);
            }
        }

        private void OnFinished() {
            // TODO Auto-generated method stub

        }
    };
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    public void ConnectToDevice(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = adapter.getRemoteDevice(address);
            PrinterType = 0;// 还原打印机类型
            PrinterTypeNow = 0;
            connect(device);
        }
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        if (r != null) {
            r.write(out);
        } else {
            DisConnected();
            Nopointstart();

        }
    }

    public synchronized void start() {
        if (D)
            Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(STATE_LISTEN);
    }

    private synchronized void setState(int state) {
        mState = state;
    }

    public static synchronized int getState() {
        return mState;

    }

    private synchronized void SetScanState(int state) {
        scanState = state;
    }

    public synchronized int GetScanState() {
        return scanState;

    }

    public synchronized void connect(BluetoothDevice device) {

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void DisConnected() {

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        setState(STATE_NONE);
    }

    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
        //        context.sendBroadcast(new Intent().setAction("ConnectSuccessReceiver"));
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D)
            Log.d(TAG, "stop");
        setState(STATE_NONE);
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
    }

    private void connectionSuccess() {
        setState(STATE_CONNECTED);
        SetPrinterInf();
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, SUCCESS_CONNECT, -1)
                .sendToTarget();
    }

    private void SetPrinterInf() {
        // TODO Auto-generated method stub

        new Thread() {
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                PrinterTypeNow = PrinterType;

            }
        }.start();
    }

    private void connectionFailed() {
        setState(STATE_LISTEN);
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, FAILED_CONNECT, -1)
                .sendToTarget();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        setState(STATE_LISTEN);
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, LOSE_CONNECT, -1)
                .sendToTarget();

    }

    private void Nopointstart() {
        setState(STATE_LISTEN);
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, LOSE_CONNECT, 0)
                .sendToTarget();

    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted (or
     * until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            if (D)
                Log.d(TAG, "BEGIN mAcceptThread" + this);
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BlueToothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {

                                }
                                break;
                        }
                    }
                }
            }

        }

        public void cancel() {
            if (D)
                Log.d(TAG, "cancel " + this);
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of server failed", e);
            }
        }
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            adapter.cancelDiscovery();
            SetScanState(STATE_SCAN_STOP);

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                connectionSuccess();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG,
                            "unable to close() socket during connection failure",
                            e2);
                }
                // Start the service over to restart listening mode
                BlueToothService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BlueToothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    public void SetPrinterType(int type) {

        PrinterType = type;
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean isCancle = false;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            isCancle = false;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @Override
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            int bytes;

            // Keep listening to the InputStream while connected
            while (!isCancle) {
                try {
                    byte[] buffer = new byte[30];
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if (bytes > 0) {
                        SetPrinterType(2);
                        // Send the obtained bytes to the UI Activity
                        byte[] by = new byte[bytes];
                        System.arraycopy(buffer, 0, by, 0, bytes);
                        String str = new String(by);
                        if (buffer[0] != 17) {
                            SetWriteState(WRITE_WAIT);
                            // mHandler.obtainMessage(WRITE_WAIT, bytes, -1,
                            // buffer).sendToTarget();
                        } else
                            SetWriteState(WRITE_READ);
                        // mHandler.obtainMessage(MESSAGE_WRITE, bytes, -1,
                        // buffer)
                        // .sendToTarget();

                    } else {
                        Log.e(TAG, "disconnected1");
                        connectionLost();
                        isCancle = true;
                        // if (mState != STATE_NONE) {
                        // Log.e(TAG, "disconnected");
                        // // Start the service over to restart listening mode
                        // BlueToothService.this.start();
                        // }
                        break;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected2", e);
                    connectionLost();
                    isCancle = true;
                    // add by chongqing jinou
                    // if (mState != STATE_NONE) {
                    // // Start the service over to restart listening mode
                    // BlueToothService.this.start();
                    // }
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                Log.i("BTPWRITE", new String(buffer, "GBK"));
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {

            }
        }

        public void cancel() {
            try {
                isCancle = true;
                mmSocket.close();
                Log.d(TAG, "562cancel suc");
                setState(STATE_LISTEN);
            } catch (IOException e) {
                Log.d(TAG, "565cancel failed");
            }
        }
    }

    public byte[] convert(String str) {
        str = (str == null ? "" : str);
        String tmp;
        byte[] send = new byte[str.length() * 2];
        char c;
        int i, j;

        for (i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            j = (c >>> 8); // 取出高8位
            send[i * 2] = (byte) j;
            j = (c & 0xFF); // 取出低8位
            send[i * 2 + 1] = (byte) j;
        }
        return send;
    }

    /*
     * @Western Text by Unicode
     *
     *
     */
    public void PrintCharactersUnicode(String str) {
        byte[] send;
        send = string2Unicode(str);
        ////String hexStr=bytesToHexString(send);
        write(send);
    }

    static byte[] string2Unicode(String s) {
        try {
            StringBuffer out = new StringBuffer("");
            byte[] bytes = s.getBytes("unicode");
            byte[] bt = new byte[bytes.length - 2];
            for (int i = 2, j = 0; i < bytes.length - 1; i += 2, j += 2) {
                bt[j] = (byte) (bytes[i + 1] & 0xff);
                bt[j + 1] = (byte) (bytes[i] & 0xff);
            }
            return bt;
        } catch (Exception e) {
            try {
                byte[] bt = s.getBytes("GBK");
                return bt;
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return null;
            }
        }
    }

    /*
     * @Chinese Text by GBK
     *
     */
    public void PrintCharacters(String str) {
        byte[] send;
        try {
            String ss = str;
            send = ss.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            send = str.getBytes();
        }
        write(send);
    }

    /* Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
    * @param src byte[] data
    * @return hex string
    */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public void SendOrder(byte[] send) {
        write(send);
    }

    public void PrintImage(Bitmap bitmapcode, int timeout) {
        this.timeout = timeout;
        PrintImageNew(bitmapcode);
        //PrintImageOld(bitmapcode);

    }

    public void PrintImageOld(Bitmap bitmapCode) {
        // TODO Auto-generated method stub
        int w = bitmapCode.getWidth();
        int h = bitmapCode.getHeight();
        byte[] sendbuf = StartBmpToPrintCode(bitmapCode);
        byte[] sendper;
        int num = 0;
        int total = 99;
        // 老版打印机只要用这个
        while (num != sendbuf.length) {
            if (1 == 1) {
                if ((sendbuf.length - num) > total) {
                    sendper = new byte[total];
                    System.arraycopy(sendbuf, num, sendper, 0, total);
                    num = num + total;
                } else {
                    sendper = new byte[sendbuf.length - num];
                    System.arraycopy(sendbuf, num, sendper, 0, sendbuf.length
                            - num);
                    num = sendbuf.length;
                }
                // try {
                // Thread.sleep(timeout);
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                write(sendper);
            }
        }
        // 新版打印机只要用这个
        // write(sendbuf);

    }

    public void PrintImageNew(Bitmap bitmapCode) {
        // TODO Auto-generated method stub
        int w = bitmapCode.getWidth();
        int h = bitmapCode.getHeight();
        byte[] sendbuf = StartBmpToPrintCode(bitmapCode);

        write(sendbuf);

    }

    private byte[] StartBmpToPrintCode(Bitmap bitmap) {
        byte temp = 0;
        int j = 7;
        int start = 0;
        if (bitmap != null) {
            int mWidth = bitmap.getWidth();
            int mHeight = bitmap.getHeight();

            int[] mIntArray = new int[mWidth * mHeight];
            byte[] data = new byte[mWidth * mHeight];
            bitmap.getPixels(mIntArray, 0, mWidth, 0, 0, mWidth, mHeight);
            encodeYUV420SP(data, mIntArray, mWidth, mHeight);
            byte[] result = new byte[mWidth * mHeight / 8];
            for (int i = 0; i < mWidth * mHeight; i++) {
                temp = (byte) ((byte) (data[i] << j) + temp);
                j--;
                if (j < 0) {
                    j = 7;
                }
                if (i % 8 == 7) {
                    result[start++] = temp;
                    temp = 0;
                }
            }
            if (j != 7) {
                result[start++] = temp;
            }

            int aHeight = 24 - mHeight % 24;
            int perline = mWidth / 8;
            byte[] add = new byte[aHeight * perline];
            byte[] nresult = new byte[mWidth * mHeight / 8 + aHeight * perline];
            System.arraycopy(result, 0, nresult, 0, result.length);
            System.arraycopy(add, 0, nresult, result.length, add.length);

            byte[] byteContent = new byte[(mWidth / 8 + 4)
                    * (mHeight + aHeight)];// 打印数组
            byte[] bytehead = new byte[4];// 每行打印头
            bytehead[0] = (byte) 0x1f;
            bytehead[1] = (byte) 0x10;
            bytehead[2] = (byte) (mWidth / 8);
            bytehead[3] = (byte) 0x00;
            for (int index = 0; index < mHeight + aHeight; index++) {
                System.arraycopy(bytehead, 0, byteContent, index
                        * (perline + 4), 4);
                System.arraycopy(nresult, index * perline, byteContent, index
                        * (perline + 4) + 4, perline);

            }
            return byteContent;
        }
        return null;

    }

    public void encodeYUV420SP(byte[] yuv420sp, int[] rgba, int width,
                               int height) {
        final int frameSize = width * height;
        int[] U, V;
        U = new int[frameSize];
        V = new int[frameSize];
        final int uvwidth = width / 2;
        int r, g, b, y, u, v;
        int bits = 8;
        int index = 0;
        int f = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                r = (rgba[index] & 0xff000000) >> 24;
                g = (rgba[index] & 0xff0000) >> 16;
                b = (rgba[index] & 0xff00) >> 8;
                // rgb to yuv
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // clip y
                // yuv420sp[index++] = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 :
                // y));
                byte temp = (byte) ((y < 0) ? 0 : ((y > 255) ? 255 : y));
                yuv420sp[index++] = temp > 0 ? (byte) 1 : (byte) 0;

                // {
                // if (f == 0) {
                // yuv420sp[index++] = 0;
                // f = 1;
                // } else {
                // yuv420sp[index++] = 1;
                // f = 0;
                // }

                // }

            }

        }
        f = 0;
    }
}
