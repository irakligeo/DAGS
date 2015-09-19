package geolab.dags;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import geolab.dags.animation.DepthPageTransformer;
import geolab.dags.custom_DialogFragments.FilterDialogFragment;
import geolab.dags.fileUpload.Config;
import geolab.dags.fileUpload.UploadActivity;
import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;

import static geolab.dags.fragment.MapFragment.*;


public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int NUM_PAGES = 2;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private Context context;
    private LayoutInflater inflater;
    private View view;
    private TextView fbUserName;
    private Activity activity;
    public static FilterDialogFragment filterDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        context = this;
        activity = this;

        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.header_layout, null);
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




    //starting camera code

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }
    //launchUploadActivity
    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(MainActivity.this, UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * returning image
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    private static Uri fileUri; // file url to store image/video

    public void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //   end of camera code
    private ActionBarDrawerToggle mDrawerToggle;

    String str_firstname = "";
    CallbackManager callbackManager;
    AccessToken accessToken;

    //NavigationItemSelected
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_item_1:
                // capture picture
                captureImage();
//                mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
                filterDialogFragment = new FilterDialogFragment();
                filterDialogFragment.show(getFragmentManager(),"filter_fragment");
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
                case 1: return newInstance("viewPager");
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.action_filter:
                filterDialogFragment = new FilterDialogFragment();
                filterDialogFragment.show(MainActivity.this.getFragmentManager(),"filter_fragment");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}