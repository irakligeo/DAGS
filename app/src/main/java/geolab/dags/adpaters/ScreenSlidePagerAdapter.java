package geolab.dags.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    private final int NUM_PAGES = 2;
    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 2: return MapFragment.newInstance("viewPager");
            case 1: return ViewPagerFragment.newInstance("testFrag");
        }
        return new MapFragment();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position++;
        return container;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
