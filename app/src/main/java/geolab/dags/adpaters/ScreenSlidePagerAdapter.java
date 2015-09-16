package geolab.dags.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private final int NUM_PAGES = 3;
    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 1: return MapFragment.newInstance("viewPager");
//                case 1: return TestFrag.newInstance("testFrag");

        }
        return new ViewPagerFragment();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
