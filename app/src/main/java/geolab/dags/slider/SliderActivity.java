package geolab.dags.slider;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import geolab.dags.R;

/**
 * Created by geolabedu on 9/21/15.
 */
public class SliderActivity extends AppCompatActivity {
    CustomPagerAdapter mCustomPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test);

        int[] mResources = {
                R.drawable.liked_icon,
                R.drawable.like_icon,
                R.drawable.graphite,
                R.drawable.palitra_icon,
                R.drawable.photoaparat,
                R.drawable.graphite
        };


        mCustomPagerAdapter = new CustomPagerAdapter(this, mResources);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

}
