package geolab.dags;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import geolab.dags.custom_DialogFragments.FilterDialogFragment;
import geolab.dags.fileUpload.Config;
import geolab.dags.fileUpload.UploadActivity;
import geolab.dags.model.GraphiteItemModel;
import geolab.dags.slider.CustomPagerAdapter;


public class GraphiteDetailActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private ImageView likeImageView;
    private ImageView commentImageView;
    private ImageView shareImageView;



    private TextView likesCountTextView;
    private TextView likeTextView;
    private TextView commentsTextView;
    private TextView shareTextView;
    private Context context;
    private TextView descriptionView;

    private DrawerLayout mDrawerLayout;

    private Animation textAnimation, fadeIn, fadeOut;
    private CustomPagerAdapter mCustomPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_graphite_item_detail);

            context = this;
            //get selected item detail
            final GraphiteItemModel  graphiteItem = (GraphiteItemModel) getIntent().getSerializableExtra("GraphiteItem");

            fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
            fadeIn.setDuration(1200);
            fadeIn.setFillAfter(true);

            textAnimation = AnimationUtils.loadAnimation(context,R.anim.text_animation);


            //viewpager gallery
        int[] mResources = {
                R.drawable.liked_icon,
                R.drawable.like_icon,
                R.drawable.graphite,
                R.drawable.palitra_icon,
                R.drawable.photoaparat,
                R.drawable.graphite
        };

        LayoutInflater inflater  = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_test,null);
        mCustomPagerAdapter = new CustomPagerAdapter(this, mResources);
        final ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);

            //Set Toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //set back button icon
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
            navigationView.setNavigationItemSelectedListener(this);

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                    R.string.close);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();


            // init views
            likeImageView = (ImageView) findViewById(R.id.like_icon);
            commentImageView = (ImageView) findViewById(R.id.comments_icon);
            shareImageView = (ImageView) findViewById(R.id.share_icon);
            likesCountTextView = (TextView) findViewById(R.id.likes_countTextView);
            likeTextView = (TextView) findViewById(R.id.like_Text_View);
            commentsTextView = (TextView) findViewById(R.id.comments_text_view_id);
            shareTextView = (TextView) findViewById(R.id.share_text_view_id);
            TextView imgTitle = (TextView) findViewById(R.id.imgTitle);

            ImageView imgView = (ImageView) findViewById(R.id.peaceOfArtImg);

            descriptionView = (TextView) findViewById(R.id.little_description);
            TextView createDateView = (TextView) findViewById(R.id.createDate);
            TextView authorView = (TextView) findViewById(R.id.author);


            Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
            likeTextView.setTypeface(font);
            imgTitle.setTypeface(font);
            descriptionView.setTypeface(font);
            authorView.setTypeface(font);
            authorView.setTypeface(font);
            shareTextView.setTypeface(font);
            commentsTextView.setTypeface(font);

            imgTitle.setText(graphiteItem.getTitle());
            createDateView.setText(graphiteItem.getCreateDate());
            authorView.setText(graphiteItem.getAuthor());
            descriptionView.setText(graphiteItem.getDescription());
            likesCountTextView.setText(graphiteItem.getLikesCount()+" ");


            final boolean[] clicked = {false};
            //on like ImageView clikc
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!clicked[0]) {
                        likesCountTextView.setText(graphiteItem.getLikesCount() + 1 + " ");
                        likeImageView.setImageResource(R.drawable.liked_icon);
                        likesCountTextView.startAnimation(textAnimation);
                        likeTextView.setText("liked");
                        likeTextView.startAnimation(textAnimation);
                        likesCountTextView.startAnimation(fadeIn);
                        clicked[0] = true;
                    }else {
                        Toast.makeText(getApplicationContext(),"უკვე მოწონებულია", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            commentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"comming soon ;) ", Toast.LENGTH_SHORT).show();
                }
            });

            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"share",Toast.LENGTH_SHORT).show();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    //share link content
//                    ShareLinkContent content = new ShareLinkContent.Builder()
//                            .setContentUrl(Uri.parse(graphiteItem.getImgURL()))
//                            .setContentTitle(graphiteItem.getTitle())
//                            .setContentDescription(graphiteItem.getDescription())
//                            .build();
    //                ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
    //                shareButton.setShareContent(content);
                }
            });

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width=dm.widthPixels;
            int height=dm.heightPixels;


            Picasso.with(this)
                    .load(graphiteItem.getImgURL())
                    .fit()
                    .centerCrop()
                    .into(imgView);

    }

    private ActionBarDrawerToggle mDrawerToggle;

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
        Intent i = new Intent(GraphiteDetailActivity.this, UploadActivity.class);
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

    CallbackManager callbackManager;
    AccessToken accessToken;
    private static FilterDialogFragment filterDialogFragment;

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.navigation_item_1:
                captureImage();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
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
                                                    String str_lastname = jsonObject.getString("last_name");

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }
                                    }).executeAsync();

                                }

                                @Override
                                public void onCancel() {
                                    Toast.makeText(GraphiteDetailActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(FacebookException exception) {
                                    Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
                                }
                            });
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                }

                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;

            case R.id.navigation_item_3:
                filterDialogFragment = new FilterDialogFragment();
                filterDialogFragment.show(getFragmentManager(), "filter_fragment");
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;

            case R.id.navigation_item_favorites:
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graphite_item_detail, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.menu_item_share) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
