//package be.kerckhove.linefollowerremote;
//
//import android.bluetooth.BluetoothSocket;
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import static android.content.ContentValues.TAG;
//
///**
// * Created by Stijn on 12/12/2017.
// */
//
//public class ConnectedThread extends Thread {
//    private final BluetoothSocket mmSocket;
//    private final InputStream mmInStream;
//    private final OutputStream mmOutStream;
//    private byte[] mmBuffer; // mmBuffer store for the stream
//
//    public ConnectedThread(BluetoothSocket socket) {
//        mmSocket = socket;
//        InputStream tmpIn = null;
//        OutputStream tmpOut = null;
//
//        // Get the input and output streams; using temp objects because
//        // member streams are final.
//        try {
//            tmpIn = socket.getInputStream();
//        } catch (IOException e) {
//            Log.e(TAG, "Error occurred when creating input stream", e);
//        }
//        try {
//            tmpOut = socket.getOutputStream();
//        } catch (IOException e) {
//            Log.e(TAG, "Error occurred when creating output stream", e);
//        }
//
//        mmInStream = tmpIn;
//        mmOutStream = tmpOut;
//    }
//
//    public void run() {
//        mmBuffer = new byte[4096];
//        int numBytes; // bytes returned from read()
//
//        // Keep listening to the InputStream until an exception occurs.
//        while (true) {
//            try {
//                // Read from the InputStream.
//                numBytes = mmInStream.read(mmBuffer);
//
//                BluetoothService.getInstance().append(mmBuffer);
//            } catch (IOException e) {
//                Log.d(TAG, "Input stream was disconnected", e);
//                break;
//            }
//        }
//    }
//
//    // Call this from the main activity to send data to the remote device.
//    public void write(byte[] bytes) {
//        try {
//            mmOutStream.write(bytes);
//        } catch (IOException e) {
//            Log.e(TAG, "Error occurred when sending data", e);
//        }
//    }
//
//    // Call this method from the main activity to shut down the connection.
//    public void cancel() {
//        try {
//            mmSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not close the connect socket", e);
//        }
//    }
//}
//
//
//
