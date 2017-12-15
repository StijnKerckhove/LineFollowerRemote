package be.kerckhove.linefollowerremote;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import be.kerckhove.linefollowerremote.interfaces.Observer;
import be.kerckhove.linefollowerremote.interfaces.Subject;

/**
 * Created by User on 17/12/2016.
 */

public class ConnectTask extends AsyncTask<Void, Void, Boolean> implements Subject {

    private final BluetoothSocket mmSocket;
    private Context context;
    private MainActivity mainActivity;
    private ProgressDialog dialog;
    private ArrayList<Observer> observers;

    private BluetoothAdapter mBluetoothAdapter;

    public ConnectTask(BluetoothDevice device, Context context) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        this.context = context;
        mainActivity = (MainActivity) context;
        observers = new ArrayList<>();

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            //TODO
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
        } catch (IOException e) {
            Log.w("Error", e.getMessage());
        }
        mmSocket = tmp;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "", "Trying to connect to device....", true);
    }

    @Override
    protected void onPostExecute(Boolean bool) {
        if (!bool) {
            DialogFactory.createOkDialog(context, "Unable to connect to device.", null).show();
        } else {
            mainActivity.switchToTab(MainActivity.TAB_CONTROLLER);
            notifyObservers();
        }
        dialog.dismiss();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out

            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.w("Error", closeException.getMessage());
            }

            return false;
        }

        return true;
    }

    public BluetoothSocket getBluetoothSocket() {
        return mmSocket;
    }

    public void cancelConnection() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.update(this);
        }
    }
}
