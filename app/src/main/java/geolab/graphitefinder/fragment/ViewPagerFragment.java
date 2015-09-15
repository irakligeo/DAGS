package geolab.graphitefinder.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;

import geolab.graphitefinder.GraphiteDetailActivity;
import geolab.graphitefinder.R;
import geolab.graphitefinder.adpaters.ListViewAdapter;
import geolab.graphitefinder.model.DB.DBHelper;
import geolab.graphitefinder.model.DB.TableGraphite;
import geolab.graphitefinder.model.GraphiteItemModel;
import geolab.graphitefinder.parsers.MyResponseParser;


public class ViewPagerFragment extends android.support.v4.app.Fragment {


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    private ProgressDialog progressDialog;
    private ListView graphiteListView;
    public static View rootView;


    public static SQLiteDatabase db;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_graphiteitemslist, container, false);

        //swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                graphiteItems = null;
                getGraphiteDatas(URL);
            }
        });



        //Database instance
        DBHelper dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();



        // get data from server
        getGraphiteDatas(URL);

        graphiteListView = (ListView) rootView.findViewById(R.id.graphiteList);

        graphiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),GraphiteDetailActivity.class);

                GraphiteItemModel graphiteItem = (GraphiteItemModel) parent.getAdapter().getItem(position);
                intent.putExtra("GraphiteItem", graphiteItem);
                startActivity(intent);

            }
        });


        //scroll in list and select last item
        scrollBottom();

        return rootView;
    }

    FloatingActionButton fab;
    static int k = 0;
    private void scrollBottom(){
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_scrollBottom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollMyListViewToBottom();
            }
        });
    }


    // for specific item selecting in list
    private void scrollMyListViewToBottom() {
        graphiteListView.post(new Runnable() {
            @Override
            public void run() {
                if(k % 2 == 0) {
                    // Select the last row so it will scroll into view...
                    graphiteListView.setSelection(graphiteListView.getCount() - 1);
                }else{
                    graphiteListView.setSelection(0);
                    fab.destroyDrawingCache();
                }
                ++k;
            }

        });
    }




    private ContentValues contentValues;

    private String URL = "http://geolab.club/streetart/json/peaceofart/";
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private ArrayList<GraphiteItemModel> graphiteItems;

    // function gets data from server

    public void getGraphiteDatas(String url){
        // progressDialog for nice loading
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Graphites...");
        progressDialog.show();

        if(contentValues == null){
            contentValues = new ContentValues();
        }
        if(requestQueue == null){
            requestQueue = new Volley().newRequestQueue(getActivity());
        }
        if(graphiteItems == null){
            graphiteItems = new ArrayList<>();
        }

        if(jsonArrayRequest == null){
            jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {

                    graphiteItems = MyResponseParser.getData(jsonArray);

                    //insert graphiteItems arrayList into database
                    for(int i = 0; i < graphiteItems.size(); ++i){
                        contentValues.put(TableGraphite.id, graphiteItems.get(i).getId());
                        contentValues.put(TableGraphite.imgTitle, graphiteItems.get(i).getTitle());
                        contentValues.put(TableGraphite.imgAuthor, graphiteItems.get(i).getAuthor());
                        contentValues.put(TableGraphite.imgURL, graphiteItems.get(i).getImgURL());
                        contentValues.put(TableGraphite.latitude+"", graphiteItems.get(i).getLatitude());
                        contentValues.put(TableGraphite.longitude+"", graphiteItems.get(i).getLongitude());
                        contentValues.put(TableGraphite.uploadDateTime, graphiteItems.get(i).getCreateDate());
                        contentValues.put(TableGraphite.imgDescription, graphiteItems.get(i).getDescription());

                        db.insert(TableGraphite.TABLE_NAME,null,contentValues);
                    }

                    graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);
                    graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));

                    //dismiss progressDialog after loading data
                    progressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.dismiss();
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(),"Please check your internet connection",Toast.LENGTH_LONG).show();
                }
            });
        }
        requestQueue.add(jsonArrayRequest);
    }
}
