package com.autometer.myapp.autometer;

/**
 * Created by sujaysudheendra on 10/9/14.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by sujaysudheendra on 10/9/14.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;
    String[] titles={"Search","About"};

    public MyPagerAdapter(android.support.v4.app.FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will sgothow FirstFragment
            {
                MyActivity.isAbout=false;
                return SearchFragment.newInstance(0, "Search");
            }
            //case 1: // Fragment # 0 - This will show FirstFragment different title
              //  return SettingFragment.newInstance(1, "Setting");

            case 1: // Fragment # 1 - This will show SecondFragment
            {
                MyActivity.isAbout=true;
                return AboutFragment.newInstance(2, "About Us");
            }

            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}

