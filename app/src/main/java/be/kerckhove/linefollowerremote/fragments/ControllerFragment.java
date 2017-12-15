package be.kerckhove.linefollowerremote.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Map;

import be.kerckhove.linefollowerremote.BluetoothService;
import be.kerckhove.linefollowerremote.R;
import be.kerckhove.linefollowerremote.interfaces.Init;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ControllerFragment extends Fragment implements Init {
    @BindView(R.id.start)
    ToggleButton start;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.currentSpeed)
    TextView currentSpeed;
    @BindView(R.id.debug)
    Button debug;
    @BindView(R.id.result)
    TextView result;
    private final Handler debugHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            result.append(new String((byte[]) message.obj));
        }
    };
    private OnFragmentInteractionListener mListener;

    public ControllerFragment() {
        // Required empty public constructor
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_controller, container, false);

        ButterKnife.bind(this, view);


        debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothService.getInstance().send("debug\n", debugHandler);
            }
        });

        start.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    BluetoothService.getInstance().send("start", null);
                } else {
                    BluetoothService.getInstance().send("stop", null);
                }
            }
        });

        currentSpeed.setText(Integer.toString(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentSpeed.setText(Integer.toString(i));
                BluetoothService.getInstance().send("set speed " + i, null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void initValues(Map<String, String> values) {
        seekBar.setProgress(Integer.parseInt(values.get("speed")));

        if (values.get("running").equals("1")) {
            start.setChecked(true);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
