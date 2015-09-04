package geolab.graphitefinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import geolab.graphitefinder.model.GraphiteItemModel;


public class GraphiteDetailActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphite_item_detail);


        //Set Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open,
                R.string.close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //get selected item detail
        GraphiteItemModel  graphiteItem = (GraphiteItemModel) getIntent().getSerializableExtra("GraphiteItem");

        TextView imgTitle = (TextView) findViewById(R.id.imgTitle);
        ImageView imgView = (ImageView) findViewById(R.id.peaceOfArtImg);
        TextView descriptionView = (TextView) findViewById(R.id.little_description);
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
                Toast.makeText(getApplicationContext(), "Item 1", Toast.LENGTH_LONG).show();
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                break;
            case R.id.navigation_item_2:
                Toast.makeText(getApplicationContext(), "Item 2", Toast.LENGTH_LONG).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
