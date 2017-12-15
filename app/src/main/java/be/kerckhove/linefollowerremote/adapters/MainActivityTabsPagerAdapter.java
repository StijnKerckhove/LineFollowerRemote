package be.kerckhove.linefollowerremote.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import be.kerckhove.linefollowerremote.fragments.ConfigureFragment;
import be.kerckhove.linefollowerremote.fragments.ConnectFragment;
import be.kerckhove.linefollowerremote.fragments.ControllerFragment;

/**
 * Created by User on 8/12/2017.
 */
public class MainActivityTabsPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public MainActivityTabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ConnectFragment connectFragment = new ConnectFragment();
                fragments.add(connectFragment);
                return connectFragment;
            case 1:
                ControllerFragment controllerFragment = new ControllerFragment();
                fragments.add(controllerFragment);
                return controllerFragment;
            case 2:
                ConfigureFragment configureFragment = new ConfigureFragment();
                fragments.add(configureFragment);
                return configureFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0:
//                return "CONNECT";
//            case 1:
//                return "CONTROL";
//            case 2:
//                return "CONFIGURE";
//            default:
//                return null;
//        }
        return null;
    }

    public Fragment getFragment(int fragmentId) {
        return fragments.get(fragmentId);
    }
}
