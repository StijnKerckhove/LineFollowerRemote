package be.kerckhove.linefollowerremote.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import be.kerckhove.linefollowerremote.BluetoothService;
import be.kerckhove.linefollowerremote.R;
import be.kerckhove.linefollowerremote.interfaces.Init;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfigureFragment extends Fragment implements Init {

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {

        }
    };

    @BindView(R.id.cycleTime)
    EditText cycleTime;
    @BindView(R.id.kp)
    TextView kp;
    @BindView(R.id.ki)
    TextView ki;
    @BindView(R.id.kd)
    TextView kd;
    @BindView(R.id.calibrate)
    Button calibrate;
    private OnFragmentInteractionListener mListener;

    public ConfigureFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configure, container, false);
        ButterKnife.bind(this, view);

        kp.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    BluetoothService.getInstance().send("set kp " + kp.getText(), null);
                    Toast.makeText(getContext(), "Kp was updated", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        ki.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    BluetoothService.getInstance().send("set ki " + ki.getText(), null);
                    Toast.makeText(getContext(), "Ki was updated", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        kd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    BluetoothService.getInstance().send("set kd " + kd.getText(), null);
                    Toast.makeText(getContext(), "Kd was updated", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        cycleTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    BluetoothService.getInstance().send("set cycle " + cycleTime.getText(), null);
                    Toast.makeText(getContext(), "Cycle time was updated", Toast.LENGTH_LONG).show();
                }

                return false;
            }
        });

        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothService.getInstance().send("calibrate", null);
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
        kp.setText(values.get("kp"));
        ki.setText(values.get("ki"));
        kd.setText(values.get("kd"));
        cycleTime.setText(values.get("cycle"));
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
