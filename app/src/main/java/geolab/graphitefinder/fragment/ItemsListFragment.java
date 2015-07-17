package geolab.graphitefinder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;

import java.util.ArrayList;

import geolab.graphitefinder.GraphiteDetailActivity;
import geolab.graphitefinder.R;
import geolab.graphitefinder.adpaters.ListViewAdapter;
import geolab.graphitefinder.model.GraphiteItemModel;


public class ItemsListFragment extends Fragment {

    ListView graphiteListView;
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
    }

    Button btn;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graphiteitemslist, container, false);

        ArrayList<GraphiteItemModel> listContact = getGraphiteItems();
        graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);

        graphiteListView.setAdapter(new ListViewAdapter(getActivity(), listContact));

        return rootView;
    }

    private JsonArrayRequest jsonArrayRequest;
    private ContentValues contentValues;
    private RequestQueue requestQueue;
    public void getDataFromServer(String URL){
        if(requestQueue == null){
//            requestQueue = new Volley.newRequestQueue(getActivity());
        }
    }

    public ArrayList<GraphiteItemModel> getGraphiteItems(){
        ArrayList<GraphiteItemModel> graphiteItems = new ArrayList<>();

//        graphiteItems.add(new GraphiteItemModel("Android","Description Description Description" +
//                "Description Description Description Description Description Description Description Description Description" +
//                "Description Description Description Description Description","http://www.wallfoz.com/wp-content/uploads/2015/05/3d_graffiti_wallpapers_49_design_photo_for_dekstop_wfz.jpg","giorgi","13-02-2015",13.345,44.3345));
//        graphiteItems.add(new GraphiteItemModel("PHP-server","Description Description Description" +
//                "Description Description Description Description Description Description Description Description Description" +
//                "Description Description Description Description Description","http://theartmad.com/wp-content/uploads/2015/03/3d-Graffiti-Wallpapers-Free-Download-3.jpg","Lasha","14-07-2015",13.345,44.3345));
//        graphiteItems.add(new GraphiteItemModel("web-design","Description Description Description" +
//                "Description Description Description Description Description Description Description Description Description" +
//                "Description Description Description Description Description","http://www.homefurniture-ideas.com/wp-content/uploads/2014/06/inspiring-imaginative-blue-graffiti-wallpaper-mural-room-1.jpg","Gio","29-07-2015",13.345,44.3345));
//        graphiteItems.add(new GraphiteItemModel("PHP-Map","Description Description Description" +
//                "Description Description Description Description Description Description Description Description Description" +
//                "Description Description Description Description Description","http://www.wallfoz.com/wp-content/uploads/2015/05/best_graffiti_wallpapers_cool_14_design_backgrunds_for_dekstop_wfz.jpg","Oto","8-07-2013",13.345,44.3345));
//        graphiteItems.add(new GraphiteItemModel("design","Description Description Description" +
//                "Description Description Description Description Description Description Description Description Description" +
//                "Description Description Description Description Description","http://hdwallpapersfit.com/wp-content/uploads/2015/02/cool-graffiti-bedroom-wallpapers-design.jpg","Lana","18-07-2014",13.345,44.3345));
//

        return graphiteItems;

    }

}