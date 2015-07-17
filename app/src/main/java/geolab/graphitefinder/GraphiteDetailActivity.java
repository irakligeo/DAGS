package geolab.graphitefinder;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import geolab.graphitefinder.model.GraphiteItemModel;


public class GraphiteDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphite_item_detail);

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

        return super.onOptionsItemSelected(item);
    }
}
