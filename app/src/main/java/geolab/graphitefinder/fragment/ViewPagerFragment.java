package geolab.graphitefinder.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import geolab.graphitefinder.model.GraphiteItemModel;
import geolab.graphitefinder.parsers.MyResponceParcer;


public class ViewPagerFragment extends android.support.v4.app.Fragment {


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        graphiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),GraphiteDetailActivity.class);

                GraphiteItemModel graphiteItem = (GraphiteItemModel) parent.getAdapter().getItem(position);
                intent.putExtra("GraphiteItem", graphiteItem);
                startActivity(intent);

            }
        });
        //onSprayClick();
    }

    View cardView;
    public void onSprayClick(){
        ImageView sprayBtn = (ImageView) cardView.findViewById(R.id.sprayImgViewId);
        sprayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"spray clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private ProgressDialog progressDialog;
    private ListView graphiteListView;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_graphiteitemslist, container, false);
        cardView = inflater.inflate(R.layout.graphite_cardview_layout,container,false);

        // progressDialog for nice loading
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading Graphites...");
        progressDialog.show();

        // get data from server
//        getGraphiteDatas(URL);

        // for dummy data
        ArrayList<GraphiteItemModel> graphiteItems = getGraphiteItems();

        graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);
        graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));


        return rootView;
    }


    private String URL = "";
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue requestQueue;
    private ArrayList<GraphiteItemModel> graphiteItems;

    public void getGraphiteDatas(String url){
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

                    graphiteItems = MyResponceParcer.getData(jsonArray);
                    graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);
                    graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));

                    //dismiss progressDialog after loading data
                    progressDialog.dismiss();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getActivity(),"Error Response",Toast.LENGTH_SHORT).show();
                }
            });
        }
        requestQueue.add(jsonArrayRequest);
    }

    public ArrayList<GraphiteItemModel> getGraphiteItems(){
        ArrayList<GraphiteItemModel> graphiteItems1 = new ArrayList<>();

        graphiteItems1.add(new GraphiteItemModel("Android","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა ","http://www.wallfoz.com/wp-content/uploads/2015/05/3d_graffiti_wallpapers_49_design_photo_for_dekstop_wfz.jpg","giorgi","13-02-2015",13.345,44.3345));
        graphiteItems1.add(new GraphiteItemModel("PHP-server","ჰიდროელექტროსადგური გაყიდროელექტროსადგურებულა საჰიდროელექტროსადგურში ჩაყიდროელექტროსადგურებულა ","http://theartmad.com/wp-content/uploads/2015/03/3d-Graffiti-Wallpapers-Free-Download-3.jpg","Lasha","14-07-2015",13.345,44.3345));
        graphiteItems1.add(new GraphiteItemModel("web-design","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://www.homefurniture-ideas.com/wp-content/uploads/2014/06/inspiring-imaginative-blue-graffiti-wallpaper-mural-room-1.jpg","Gio","29-07-2015",13.345,44.3345));
        graphiteItems1.add(new GraphiteItemModel("PHP-Map","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://www.wallfoz.com/wp-content/uploads/2015/05/best_graffiti_wallpapers_cool_14_design_backgrunds_for_dekstop_wfz.jpg","Oto","8-07-2013",13.345,44.3345));
        graphiteItems1.add(new GraphiteItemModel("design","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://hdwallpapersfit.com/wp-content/uploads/2015/02/cool-graffiti-bedroom-wallpapers-design.jpg","Lana","18-07-2014",13.345,44.3345));

        progressDialog.dismiss();
        return graphiteItems1;

    }

    public static ViewPagerFragment newInstance(String text) {

        ViewPagerFragment f = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
