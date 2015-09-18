package geolab.dags;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import geolab.dags.fileUpload.UploadFileActivity;
import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;
import geolab.dags.animation.DepthPageTransformer;
import geolab.dags.model.GraphiteItemModel;


public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView fbUserName;
    private View customView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        context = this;

        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.header_layout, null);
        //inflating custom view for filter
        customView = inflater.inflate(R.layout.custom_info_fragment,null);

        fbUserName = (TextView) view.findViewById(R.id.fb_user_name);


        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("მთავარი"));
        tabLayout.addTab(tabLayout.newTab().setText("რუკა"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        final PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return false;
            }
        };

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Burger menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);

        // animation styles
//        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setPageTransformer(true, new DepthPageTransformer());


        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }



    private ActionBarDrawerToggle mDrawerToggle;

    String str_firstname = "";
    CallbackManager callbackManager;
    AccessToken accessToken;

    //NavigationItemSelected
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_1:
                Intent intent = new Intent(MainActivity.this, UploadFileActivity.class);
                startActivity(intent);
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                menuItem.setChecked(true);

                break;

            case R.id.navigation_item_2:
                accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    new AlertDialog.Builder(context)
                            .setTitle("Success...")
                            .setMessage("გსურთ დარჩეთ ავტორიზებული")
                            .setCancelable(false)
                            .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LoginManager.getInstance().logOut();
                                }
                            })
                            .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                } else {
                    callbackManager = CallbackManager.Factory.create();
                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                            if (graphResponse.getError() != null) {
                                                System.out.println("ERROR");
                                            } else {
                                                try {
                                                    String jsonresult = String.valueOf(jsonObject);
                                                    System.out.println("JSON Result" + jsonresult);
                                                    String str_email = jsonObject.getString("email");
                                                    String str_id = jsonObject.getString("id");
                                                    str_firstname = jsonObject.getString("first_name");
                                                    String str_lastname = jsonObject.getString("last_name");

                                                    fbUserName.setText(str_firstname);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    }).executeAsync();

                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                                }
                            });
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                    break;
                }
            case R.id.navigation_item_3:
                Toast.makeText(MainActivity.this, "filter", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * A simple pager adapter that represents 2 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return new Double(dist * meterConversion).floatValue();    // this will return distance
    }

    private ArrayList<GraphiteItemModel> data;
    private double longitude, latitude;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_filter:

                data = new ArrayList<>();
                for (String key: MapFragment.mMarkersHashMap.keySet()) {
                    data.add(MapFragment.mMarkersHashMap.get(key));
                }

                // location listener
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                //spinner
                Spinner spinner = (Spinner) customView.findViewById(R.id.spinnerCategory);
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                        R.array.graphite_categories, android.R.layout.simple_spinner_item);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(adapter);


                //dialogFragment
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.filter_dialog_fragment);
                Button bt_ok = (Button) dialog.findViewById(R.id.button1);
                dialog.setTitle("Category");


                //on button click
                bt_ok.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        MapFragment.googleMap.clear();
                        //get markers with this distance
                        double distance;
                        for(int i = 0; i < data.size(); ++i) {
                            distance = distanceFrom(longitude, latitude, data.get(i).getLongitude(),data.get(i).getLatitude());
                            MarkerOptions marker = new MarkerOptions().position(
                                    new LatLng(data.get(i).getLongitude(), data.get(i).getLatitude())).title(data.get(i).getTitle());

                            if(distance > 1000){
                                MapFragment.googleMap.addMarker(marker);
                            }else{
                                Toast.makeText(MainActivity.this, "naklebia " + distance, Toast.LENGTH_SHORT).show();
                            }

                        }
                        dialog.dismiss();
                    }

                });
                //show dialog
                dialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}