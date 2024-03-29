package geolab.dags.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;


public class TabsPagerAdapter extends FragmentStatePagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 2: return MapFragment.newInstance("map");
//            case 1: return TestFrag.newInstance("testFrag");
        }
        return new MapFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
