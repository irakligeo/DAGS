package geolab.dags.fragment;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import geolab.dags.GraphiteDetailActivity;
import geolab.dags.DB.TableGraphite;
import geolab.dags.MainActivity;
import geolab.dags.model.GraphiteItemModel;
import geolab.dags.parsers.MyResponseParser;
import geolab.dags.R;
import geolab.dags.adpaters.ListViewAdapter;
import geolab.dags.DB.DBHelper;


public class ViewPagerFragment extends android.support.v4.app.Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
    }

    private ProgressDialog progressDialog;
    private ListView graphiteListView;
    public static View rootView;
    public ArrayList<GraphiteItemModel> favoriteItems;

    public static SQLiteDatabase db;
    public DBHelper dbHelper;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    //onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_graphite_items_list, container, false);



        //swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getGraphiteDatas(URL);

            }
        });



        //Database instance


        favoriteItems = new ArrayList<>();

        // get data from server
        getGraphiteDatas(URL);

        graphiteListView = (ListView) rootView.findViewById(R.id.graphiteList);

        graphiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GraphiteDetailActivity.class);

                GraphiteItemModel graphiteItem = (GraphiteItemModel) parent.getAdapter().getItem(position);
                intent.putExtra("GraphiteItem", (Serializable) graphiteItem);
                Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.slide_in_up, R.anim.slide_out_up).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        });


        return rootView;
    }



    private ContentValues contentValues;

    private String URL = "http://geolab.club/streetart/json/peaceofart/";
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    public static ArrayList<GraphiteItemModel> graphiteItems;

    String[] splitedHashtag;
    public static HashMap<String,ArrayList<String>> hashTagsMap;
    ArrayList<String> imgArrayList;
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
        if(hashTagsMap == null){
            hashTagsMap = new HashMap<>();
        }
        if(imgArrayList == null){
            imgArrayList = new ArrayList<>();
        }
        if(jsonArrayRequest == null){
            jsonArrayRequest = new JsonArrayRequest(url,new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray jsonArray) {


                    graphiteItems = MyResponseParser.getData(jsonArray);
                    //check if the server had some changes
//                    if(MyResponseParser.oldStatusCode != MyResponseParser.statusCode ) {

//                        db.execSQL("DELETE FROM " + TableGraphite.TABLE_NAME);
//                        db.execSQL("VACUUM");

                        //insert graphiteItems arrayList into database
                        for (int i = 0; i < graphiteItems.size(); ++i) {
                            contentValues.put(TableGraphite.id, graphiteItems.get(i).getMarkerID());
                            contentValues.put(TableGraphite.imgTitle, graphiteItems.get(i).getTitle());
                            contentValues.put(TableGraphite.imgAuthor, graphiteItems.get(i).getAuthor());
                            contentValues.put(TableGraphite.imgURL, graphiteItems.get(i).getImgURL());
                            contentValues.put(TableGraphite.latitude + "", graphiteItems.get(i).getLatitude());
                            contentValues.put(TableGraphite.longitude + "", graphiteItems.get(i).getLongitude());
                            contentValues.put(TableGraphite.uploadDateTime, graphiteItems.get(i).getCreateDate());
                            contentValues.put(TableGraphite.imgDescription, graphiteItems.get(i).getDescription());
                            contentValues.put(TableGraphite.likes,graphiteItems.get(i).getLikesCount());
                            contentValues.put(TableGraphite.hashtag,graphiteItems.get(i).getHashtag());


                            //retrieve hashTags
                            String hashtag = graphiteItems.get(i).getHashtag();


                            char[] charArray = hashtag.toCharArray();
                            //allocate images for same hashtags
                            if(hashtag != "") {

                                for(int j = 0; j < charArray.length; ++j){
                                    if(charArray[j] == ',')
                                        splitedHashtag = hashtag.split(",");
                                }

                                if(splitedHashtag != null && splitedHashtag.length != 0) {
                                    for (int k = 0; k < splitedHashtag.length; ++k) {
                                        if(hashTagsMap.containsKey(splitedHashtag[k])) {
                                            imgArrayList.add(graphiteItems.get(i).getImgURL());
                                            hashTagsMap.put(splitedHashtag[k], imgArrayList);
                                        }
                                        else {
                                            imgArrayList = new ArrayList<>();
                                            imgArrayList.add(graphiteItems.get(i).getImgURL());
                                            hashTagsMap.put(splitedHashtag[k], imgArrayList);
                                        }
                                    }
                                }else {
                                    if(hashTagsMap.containsKey(hashtag)) {
                                        imgArrayList.add(graphiteItems.get(i).getImgURL());
                                        hashTagsMap.put(hashtag, imgArrayList);
                                    }else{
                                        imgArrayList = new ArrayList<>();
                                        imgArrayList.add(graphiteItems.get(i).getImgURL());
                                        hashTagsMap.put(hashtag, imgArrayList);
                                    }
                                }

                            }
                            // insert into database
                            db.insert(TableGraphite.TABLE_NAME, null, contentValues);

                        }

                        graphiteListView = (ListView) rootView.findViewById(R.id.graphiteList);
                        ListViewAdapter mListViewAdapter = new ListViewAdapter(getActivity(),graphiteItems);
                        graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));
                        mListViewAdapter.notifyDataSetChanged();

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public static ViewPagerFragment newInstance(String text) {

        ViewPagerFragment f = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
