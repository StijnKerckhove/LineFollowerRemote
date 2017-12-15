package be.kerckhove.linefollowerremote.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import be.kerckhove.linefollowerremote.BluetoothService;
import be.kerckhove.linefollowerremote.DialogFactory;
import be.kerckhove.linefollowerremote.MainActivity;
import be.kerckhove.linefollowerremote.R;
import be.kerckhove.linefollowerremote.interfaces.Observer;
import be.kerckhove.linefollowerremote.interfaces.Subject;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;

public class ConnectFragment extends Fragment implements Observer {
    @BindView(R.id.pairedDevicesListView)
    ListView pairedDevicesListView;

    @BindView(R.id.connectedTo)
    TextView connectedTo;

    @BindView(R.id.connectedDevice)
    TextView connectedDevice;

    @BindView(R.id.disconnect)
    Button disconnect;


    private OnFragmentInteractionListener mListener;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothService bluetoothService;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private MainActivity activity;
    private int REQUEST_ENABLE_BT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        activity = (MainActivity) getActivity();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            bluetoothService = BluetoothService.getInstance();
            queryDevices();
        }
    }

    private void queryDevices() {
        pairedDevicesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        for (BluetoothDevice device : bluetoothService.getPairedDevices()) {
            pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        pairedDevicesListView.setAdapter(pairedDevicesAdapter);

        pairedDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothService.connect(i, getContext());
                registerObserver();
            }
        });
    }

    private void registerObserver() {
        BluetoothService.getInstance().registerObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        ButterKnife.bind(this, view);

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothService.getInstance().disconnect();
                connectedTo.setVisibility(View.GONE);
                getView().findViewById(R.id.connected).setVisibility(View.GONE);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            DialogFactory.createOkDialog(getActivity(), "Bluetooth must be enabled to use this app", null).show();
        }
        else {
            bluetoothService = BluetoothService.getInstance();
            queryDevices();
        }
    }

    public void changeDisconnectVisibility(boolean show) {
        if(show) {
            connectedTo.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.connected).setVisibility(View.VISIBLE);
        }
        else {
            connectedTo.setVisibility(View.GONE);
            getView().findViewById(R.id.connected).setVisibility(View.GONE);
        }
    }

    @Override
    public void update(Subject subject) {
        if (BluetoothService.getInstance().getState() == BluetoothService.State.CONNECTED) {
            BluetoothDevice device = BluetoothService.getInstance().getConnectedDevice();
            connectedDevice.setText(device.getName() + "\n" + device.getAddress());
            changeDisconnectVisibility(true);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
