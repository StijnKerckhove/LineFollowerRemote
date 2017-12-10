package be.kerckhove.linefollowerremote.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import be.kerckhove.linefollowerremote.fragments.ConfigureFragment;
import be.kerckhove.linefollowerremote.fragments.ControllerFragment;

/**
 * Created by User on 8/12/2017.
 */
public class MainActivityTabsPagerAdapter extends FragmentPagerAdapter {
    public MainActivityTabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ControllerFragment();
            case 1:
                return new ConfigureFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CONTROLLER";
            case 1:
                return "CONFIGURE";
            default:
                return null;
        }
    }
}
