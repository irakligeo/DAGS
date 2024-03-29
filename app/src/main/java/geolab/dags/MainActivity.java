package geolab.dags;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import geolab.dags.animation.DepthPageTransformer;
import geolab.dags.dialogFragments.FilterDialogFragment;
import geolab.dags.dialogFragments.UploadStateFragment;
import geolab.dags.fragment.MapFragment;
import geolab.dags.fragment.ViewPagerFragment;
import geolab.dags.model.UserLikes;
import geolab.dags.parsers.MyLikesParser;

import static geolab.dags.fragment.MapFragment.newInstance;


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
    private TextView fbUserNameTextView;
    private ProfilePictureView profilePictureView;

    private View headerView;

    private DrawerLayout mDrawerLayout;
    private Activity activity;
    public TabLayout tabLayout;
    public static FilterDialogFragment filterDialogFragment;

    //user likes data
    public static HashMap<String,UserLikes> userLikes;

    private Activity mainActivity;
    public static String fbUserName;
    public static Toolbar toolbar;

    // for resource values
    public static int toolbarColorResId,tabLayoutResColorId,statusBarColorResId;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mainActivity = this;


        //get liked json data
        getUserLikedImages(URL);

        user_id = getUserFbID();


        context = this;
        activity = this;

        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        profilePictureView = (ProfilePictureView) view.findViewById(R.id.fb_image);
//



        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
                MapFragment.initMap();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Burger menu
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        fbUserNameTextView = (TextView) navigationView.findViewById(R.id.fb_user_name);
        if(user_id != ""){
            fbUserNameTextView.setText(fbUserName);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Instantiate toolbarColorResId ViewPager and toolbarColorResId PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);

        // animation styles
        // mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setPageTransformer(true, new DepthPageTransformer());


        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);


        //        status bar color
        Window window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.status_bar_color));
        }



        //filter dialog
        filterDialogFragment = new FilterDialogFragment();

        //load saved settings
        LoadSettings();
        //set settings
        changeStyle(toolbar, tabLayout, window, toolbarColorResId, tabLayoutResColorId, statusBarColorResId);

    }

    /**
     * returns facebook user id
     * @return
     */
    private String getUserFbID(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("user_id", "") ;
        return data;
    }


    //starting camera code
    public static CallbackManager callbackManager;


    private String URL = "http://geolab.club/streetart/json/likes/";
    //users liked data
    public static ArrayList<UserLikes> likesArrayList;
    //Request Queue
    private RequestQueue queue;

    //function gets user liked images (data)

    /**
     * method gets user liked posts json data from server
     * @param url
     */
    public void getUserLikedImages(String url){
        JsonArrayRequest request;
        if(queue == null) {
            queue = new Volley().newRequestQueue(mainActivity);
        }
        //cleare and create new ArrayList for likes
        likesArrayList = new ArrayList<>();

        request = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                //assign parsed data the likesArrayList
                likesArrayList = MyLikesParser.parseLikesData(jsonArray);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.getCause();
            }
        });
        queue.add(request);
    }


    /**
     * method onBackPressed
     */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }



    private String UserID = "";



    /**
     * method current activity state saves data
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * method restores saved data
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }






    /**
     * method changes style
     * @param toolbar
     * @param tabLayout
     * @param window
     * @param toolbarResID
     * @param tablayoutResId
     * @param statusbarResId
     */
    public void changeStyle(Toolbar toolbar,TabLayout tabLayout, Window window, int toolbarResID, int tablayoutResId, int statusbarResId){

        toolbar.setBackgroundColor(activity.getResources().getColor(toolbarResID));
        tabLayout.setBackgroundColor(activity.getResources().getColor(tablayoutResId));
        window = activity.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(statusbarResId));
        }

    }




    /**
     *  method loads saved settings
     */
    public void LoadSettings() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbarColorResId = sharedPreferences.getInt("toolbarColor", R.color.toolbar_color);
        tabLayoutResColorId = sharedPreferences.getInt("tabLayoutColor", R.color.tab_layout);
        statusBarColorResId = sharedPreferences.getInt("statusBarColor", R.color.status_bar_color);
    }


    /**
     * method for save user selected style. (args are for style parameters)
     * @param context
     * @param toolbarColorResId
     * @param tabLayoutColorResId
     * @param statusBarColorResId
     */
    public static void SaveUserSettings(Context context, int toolbarColorResId, int tabLayoutColorResId, int statusBarColorResId){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("toolbarColor", toolbarColorResId);
        editor.putInt("tabLayoutColor", tabLayoutColorResId);
        editor.putInt("statusBarColor", statusBarColorResId);
        editor.commit();
    }



    /**
     * function saves user_id ( user id )
     * @param key
     * @param value
     */
    private void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", value);
        editor.putString(key, value);
        editor.commit();
    }




    private ActionBarDrawerToggle mDrawerToggle;
    private String str_firstname = "";
    private AccessToken accessToken;
    private String user_id = "";

    /**
     * method onNavigationItemSelected
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.navigation_item_1: // fotos gadageba
                if(checkUserLoginStatus()) {
                    UploadStateFragment uploadStateFragment = new UploadStateFragment();
                    uploadStateFragment.show(getFragmentManager(), "uploadStateFragment");
                }
                break;

            case R.id.navigation_item_2:
                loginToFB();
                break;

            case R.id.navigation_item_3: // filtracia

                closeDrawerFromUiThread();
                try {
                    filterDialogFragment.show(getFragmentManager(), "filter_fragment");
                    GraphiteDetailActivity.filterDialogFragment.dismiss();
                }catch (NullPointerException e){

                }
                break;

            case R.id.navigation_item_favorites:
                closeDrawerFromUiThread();
                break;
            case R.id.navigation_item_style:
                settingsFragment = new SettingsFragment();
                settingsFragment.show(getFragmentManager(), "Pallete-Fragment");
                break;

            default:
                break;
        }
        return true;
    }



    /**
     * function checks if the user is logged or not
     */
    private boolean checkUserLoginStatus(){
        if (AccessToken.getCurrentAccessToken() != null) {
            UploadStateFragment uploadStateFragment = new UploadStateFragment();
            uploadStateFragment.show(getFragmentManager(),"uploadFileStateFragment");
            logged[0] = true;
            return true;
        }
        else {
            new AlertDialog.Builder(context)
                    .setTitle("ფოტოს ატვირთვა")
                    .setMessage("ფოტოს ასატვირთად საჭიროა ავტორიზაცია")
                    .setCancelable(false)
                    .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loginToFB();
                        }
            }).show();
        }
        return false;
    }


    /**
     * close drawerLayout on UI Thread
     */
    private void closeDrawerFromUiThread(){
        Thread thread = new Thread()
        {

            @Override
            public void run() {
                //yourOperation
                MainActivity.this.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }});
                super.run();
            }
        };
        thread.start();
    }

    final boolean[] logged = {false};

    /**
     * function for facebook authorization
     */
    public void loginToFB() {

        accessToken = AccessToken.getCurrentAccessToken();
        if (logged[0]) {
            new AlertDialog.Builder(context)
                    .setTitle("Success...")
                    .setMessage("გსურთ დარჩეთ ავტორიზებული")
                    .setCancelable(false)
                    .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            LoginManager.getInstance().logOut();
                            accessToken = null;
                            logged[0] = false;
                            user_id = "";
                            fbUserNameTextView.setText("Logged out");
                        }
                    })
                    .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            logged[0] = true;
                        }
                    }).show();
        } else {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            logged[0] = true;
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                    if (graphResponse.getError() != null) {
                                        System.out.println("ERROR");
                                    } else {
                                        try {
                                            user_id = jsonObject.getString("id");
                                            str_firstname = jsonObject.getString("name");
                                            fbUserName = jsonObject.getString("name");
                                            fbUserNameTextView.setText(fbUserName);
                                            SavePreferences("user_id", user_id);

                                        } catch (NullPointerException ex) {
                                            ex.getMessage();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } finally {
                                            UserID = user_id;
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
                            Log.d("FacebookException",exception.getMessage());
                            Toast.makeText(MainActivity.this, "onError", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    /**
     * onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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






    /**
     * method onResume()
     */
    @Override
    protected void onResume() {
//        getUserLikedImages(URL);
        super.onResume();
//        AppEventsLogger.activateApp(this);
    }


    /**
     * method onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
    }




    public static SettingsFragment settingsFragment;

    /**
     * method onCreateOptionsMenu for inflating menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        settingsFragment = new SettingsFragment();
        return true;
    }


    /**
     * method onOptionsItemSelected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_filter:
                filterDialogFragment = new FilterDialogFragment();
                filterDialogFragment.show(MainActivity.this.getFragmentManager(),"filter_fragment");
                return true;

            case R.id.action_pallete:
                settingsFragment = new SettingsFragment();
                settingsFragment.show(MainActivity.this.getFragmentManager(), "Pallete_fragment");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }


    /**
     * onDestroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindDrawables(findViewById(R.id.frameLayout));
        System.gc();
    }


    /**
     * class for theme style settings
     */
    @SuppressLint("ValidFragment")
    public class SettingsFragment extends DialogFragment implements View.OnClickListener {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View view = inflater.inflate(R.layout.settings,null);



            ImageView redStyle = (ImageView) view.findViewById(R.id.redStyleImageView);
                        redStyle.setOnClickListener(this);
            ImageView purpleStyle = (ImageView) view.findViewById(R.id.purpleStyleIamgeView);
                        purpleStyle.setOnClickListener(this);
            ImageView darkStyle = (ImageView) view.findViewById(R.id.darkStyleImageView);
                        darkStyle.setOnClickListener(this);
            ImageView grayStyle = (ImageView) view.findViewById(R.id.grayStyleImageView);
                        grayStyle.setOnClickListener(this);
            ImageView blueStyle = (ImageView) view.findViewById(R.id.blueStyleImageView);
                        blueStyle.setOnClickListener(this);

            return view;
        }


        int toolbarColor, tabLyoutColor,statusBarColor;
        public void initResColors(int a, int b, int c){
            toolbarColor = a; tabLyoutColor = b; statusBarColor = c;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.redStyleImageView:

                    changeStyle(toolbar, tabLayout, activity.getWindow(), R.color.red_toolbar_color, R.color.red_tab_layout, R.color.red_status_bar_color);
                    initResColors(R.color.red_toolbar_color, R.color.red_tab_layout, R.color.red_status_bar_color);
                    SaveUserSettings(getApplicationContext(), R.color.red_toolbar_color, R.color.red_tab_layout, R.color.red_status_bar_color);
                    settingsFragment.dismiss();
                    break;
                case R.id.purpleStyleIamgeView:
                    changeStyle(toolbar,tabLayout, activity.getWindow(),R.color.purple_toolbar_color, R.color.purple_tab_layout, R.color.purple_status_bar_color);
                    initResColors(R.color.purple_toolbar_color, R.color.purple_tab_layout, R.color.purple_status_bar_color);
                    SaveUserSettings(getApplicationContext(), R.color.purple_toolbar_color, R.color.purple_tab_layout, R.color.purple_status_bar_color);
                    settingsFragment.dismiss();
                    break;
                case R.id.blueStyleImageView:
                    changeStyle(toolbar,tabLayout, activity.getWindow(),R.color.blue_toolbar_color, R.color.blue_tab_layout, R.color.blue_status_bar_color);
                    initResColors(R.color.blue_toolbar_color, R.color.blue_tab_layout, R.color.blue_status_bar_color);
                    SaveUserSettings(getApplicationContext(), R.color.blue_toolbar_color, R.color.blue_tab_layout, R.color.blue_status_bar_color);
                    settingsFragment.dismiss();
                    break;
                case R.id.darkStyleImageView:
                    changeStyle(toolbar,tabLayout, activity.getWindow(),R.color.dark_toolbar_color, R.color.dark_tab_layout, R.color.dark_status_bar_color);
                    initResColors(R.color.dark_toolbar_color, R.color.dark_tab_layout, R.color.dark_status_bar_color);
                    SaveUserSettings(getApplicationContext(), R.color.dark_toolbar_color, R.color.dark_tab_layout, R.color.dark_status_bar_color);
                    settingsFragment.dismiss();
                    break;
                case R.id.grayStyleImageView:
                    changeStyle(toolbar,tabLayout, activity.getWindow(),R.color.toolbar_color, R.color.tab_layout, R.color.status_bar_color);
                    initResColors(R.color.toolbar_color, R.color.tab_layout, R.color.status_bar_color);
                    SaveUserSettings(getApplicationContext(), R.color.toolbar_color, R.color.tab_layout, R.color.status_bar_color);
                    settingsFragment.dismiss();
                    break;
                default:
                    changeStyle(toolbar,tabLayout, activity.getWindow(),R.color.toolbar_color, R.color.tab_layout, R.color.status_bar_color);
                    SaveUserSettings(getApplicationContext(), toolbarColor,tabLyoutColor,statusBarColor);
                    break;
            }
        }
    }

}