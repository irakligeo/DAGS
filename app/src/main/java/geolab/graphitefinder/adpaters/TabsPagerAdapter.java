package geolab.graphitefinder.adpaters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import geolab.graphitefinder.fragment.MapFragment;
import geolab.graphitefinder.fragment.TestFrag;
import geolab.graphitefinder.fragment.ViewPagerFragment;


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
        return new ViewPagerFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
