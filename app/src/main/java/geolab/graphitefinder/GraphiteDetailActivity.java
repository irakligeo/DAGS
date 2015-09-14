package geolab.graphitefinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import geolab.graphitefinder.facebook.FacebookLoginActivity;
import geolab.graphitefinder.fileUpload.UploadFileActivity;
import geolab.graphitefinder.model.GraphiteItemModel;


public class GraphiteDetailActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{


    private TextView descriptionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_graphite_item_detail);

        //get selected item detail
        GraphiteItemModel  graphiteItem = (GraphiteItemModel) getIntent().getSerializableExtra("GraphiteItem");


        //share link content
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(graphiteItem.getImgURL()))
                .setContentTitle(graphiteItem.getTitle())
                .setContentDescription(graphiteItem.getDescription())
                .build();
        ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);


        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                if (graphResponse.getError() != null) {
                    System.out.println("ERROR");
                } else {
                    try {
                        String jsonresult = String.valueOf(jsonObject);
                        descriptionView.setText(jsonObject.toString());
                        String str_email = jsonObject.getString("email");
                        String str_id = jsonObject.getString("id");
                        String str_firstname = jsonObject.getString("first_name");
                        String str_lastname = jsonObject.getString("last_name");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).executeAsync();

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

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();



        TextView imgTitle = (TextView) findViewById(R.id.imgTitle);
        ImageView imgView = (ImageView) findViewById(R.id.peaceOfArtImg);
        descriptionView = (TextView) findViewById(R.id.little_description);
        TextView createDateView = (TextView) findViewById(R.id.createDate);
        TextView authorView = (TextView) findViewById(R.id.author);

        imgTitle.setText(graphiteItem.getTitle());
        createDateView.setText(graphiteItem.getCreateDate());
        authorView.setText(graphiteItem.getAuthor());
        descriptionView.setText(graphiteItem.getDescription());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;

        Picasso.with(this)
                .load(graphiteItem.getImgURL())
                .resize(width,height/2)
                .centerCrop()
                .into(imgView);

    }

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.navigation_item_1:
                Intent intent = new Intent(GraphiteDetailActivity.this, UploadFileActivity.class);
                startActivity(intent);
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                break;
            case R.id.navigation_item_2:
                Intent fbIntent = new Intent(GraphiteDetailActivity.this, FacebookLoginActivity.class);
                startActivity(fbIntent);
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
