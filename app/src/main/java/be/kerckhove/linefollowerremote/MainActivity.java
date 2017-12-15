package be.kerckhove.linefollowerremote;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kerckhove.linefollowerremote.adapters.MainActivityTabsPagerAdapter;
import be.kerckhove.linefollowerremote.interfaces.Init;
import be.kerckhove.linefollowerremote.interfaces.Observer;
import be.kerckhove.linefollowerremote.interfaces.Subject;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Observer {

    protected static final int TAB_CONNECT = 0;
    protected static final int TAB_CONTROLLER = 1;
    protected static final int TAB_CONFIGURE = 2;

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private String tmpData;
    private MainActivityTabsPagerAdapter mainActivityTabsPagerAdapter;
    private final Handler initHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            // Init
            String rawData = new String((byte[]) message.obj);

            if (!rawData.contains("-end-")) {
                tmpData += rawData;
            } else {
                tmpData += rawData;
                tmpData = tmpData.replace("-end-", "");
                tmpData = tmpData.replace("null", "");

                Init controller = (Init) mainActivityTabsPagerAdapter.getFragment(TAB_CONTROLLER);
                Init configure = (Init) mainActivityTabsPagerAdapter.getFragment(TAB_CONFIGURE);

                List<String> params = Arrays.asList(tmpData.split("\r\n"));
                Map<String, String>  dataMap = new HashMap<>();

                for(String s : params) {
                    String arr[] =  s.split(":");
                    dataMap.put(arr[0], arr[1]);
                }

                controller.initValues(dataMap);
                configure.initValues(dataMap);
            }
        }
    };
    private int[] tabIcons = {
            R.drawable.ic_bluetooth,
            R.drawable.ic_controller,
            R.drawable.ic_settings
    };
    private BluetoothService bluetoothService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        bluetoothService = BluetoothService.getInstance();
        bluetoothService.registerObserver(this);

        mainActivityTabsPagerAdapter = new MainActivityTabsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mainActivityTabsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    public BluetoothService getBluetoothService() {
        return this.bluetoothService;
    }

    public void switchToTab(int i) {
        tabLayout.getTabAt(i).select();
    }

    @Override
    public void update(Subject subject) {
        if (BluetoothService.getInstance().getState() == BluetoothService.State.CONNECTED) {
            BluetoothService.getInstance().send("getval", initHandler);
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

}
