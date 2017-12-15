package be.kerckhove.linefollowerremote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import be.kerckhove.linefollowerremote.interfaces.Observer;
import be.kerckhove.linefollowerremote.interfaces.Subject;

import static android.content.ContentValues.TAG;

/**
 * Created by Stijn on 11/12/2017.
 */

public class BluetoothService implements Observer {
    private static BluetoothService _instance;
    StringBuffer stringBuffer = new StringBuffer();
    private BluetoothAdapter bluetoothAdapter;
    private State currentState;
    private List<BluetoothDevice> pairedDevices;
    private BluetoothSocket bluetoothSocket;
    private ConnectedThread connectedThread;
    private BluetoothDevice connectedDevice;
    private String message;
    private Handler handler;
    private ConnectTask connectTask;
    private List<Observer> observers;


    public BluetoothService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        currentState = State.DISCONNECTED;
        pairedDevices = new ArrayList<>();
        observers = new ArrayList<>();
        observers.add(this);
    }

    public static BluetoothService getInstance() {
        if (_instance == null) {
            _instance = new BluetoothService();
        }

        return _instance;
    }

    public State getState() {
        return currentState;
    }

    public List<BluetoothDevice> getPairedDevices() {
        pairedDevices.clear();
        pairedDevices.addAll(bluetoothAdapter.getBondedDevices());
        return pairedDevices;
    }

    public void connect(int i, Context context) {
        if (currentState == State.DISCONNECTED) {
            BluetoothDevice device = pairedDevices.get(i);
            connectedDevice = device;
            connectTask = new ConnectTask(device, context);
            connectTask.execute();

            for(Observer observer : observers) {
                connectTask.register(observer);
            }

        }
    }

    public void registerObserver(Observer o) {
        observers.add(o);
    }

    public void disconnect() {
        if (currentState == State.CONNECTED) {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                    connectedThread.cancel();
                    currentState = State.DISCONNECTED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void send(String message, Handler handler) {
        if (currentState == State.CONNECTED) {

            if(handler != null)
                this.handler = handler;

            if(!message.endsWith("\n"))
                message += "\n";

            connectedThread.write(message.getBytes());
        }
    }

    @Override
    public void update(Subject subject) {
        if (subject instanceof ConnectTask) {
            ConnectTask connectTask = (ConnectTask) subject;
            bluetoothSocket = connectTask.getBluetoothSocket();

            if (bluetoothSocket.isConnected()) {
                currentState = State.CONNECTED;
                connectedThread = new ConnectedThread(bluetoothSocket);
                connectedThread.start();
            }
        }
    }

    public void append(byte[] buffer) {
        stringBuffer.append(new String(buffer));
    }

    public String getMessage() {
        return stringBuffer.toString();
    }

    public enum State {
        CONNECTED,
        DISCONNECTED
    }

    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

//            // Keep listening to the InputStream until an exception occurs.
//            while (true) {
//                try {
//                    // Read from the InputStream.
//                    numBytes = mmInStream.read(mmBuffer);
//
//                    //BluetoothService.getInstance().append(mmBuffer);
//                    handler.obtainMessage(0, mmBuffer).sendToTarget();
//                } catch (IOException e) {
//                    Log.d(TAG, "Input stream was disconnected", e);
//                    break;
//                }

            while (currentState == BluetoothService.State.CONNECTED) {
                int bytesAvailable = 0;

                try {
                    bytesAvailable = mmInStream.available();

                    if (bytesAvailable > 0) {
                        byte[] packetBytes = new byte[bytesAvailable];

                        mmInStream.read(packetBytes);

                        if(handler != null)
                            handler.obtainMessage(0, packetBytes).sendToTarget();
                    }
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }
}