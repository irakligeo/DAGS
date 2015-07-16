package geolab.graphitefinder;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import geolab.graphitefinder.adpaters.ScreenSlidePagerAdapter;
import geolab.graphitefinder.adpaters.TabsPagerAdapter;
import geolab.graphitefinder.animation.DepthPageTransformer;


public class ScreenSlideActivity extends FragmentActivity implements ActionBar.TabListener{

    private ViewPager mPager;
    private ViewPager mTPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
//        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setPageTransformer(true, new DepthPageTransformer());
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        actionBar = getActionBar();
        mTPager = (ViewPager)findViewById(R.id.pager);
        this.mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mTPager.setAdapter(mAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab().setText("გრაფიტები").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("რუკა").setTabListener(this));

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
