package geolab.graphitefinder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import geolab.graphitefinder.GraphiteItemDetail;
import geolab.graphitefinder.R;
import geolab.graphitefinder.adpater.ListViewAdapter;
import geolab.graphitefinder.model.GraphiteItemModel;


public class ItemsListFragment extends Fragment {

    ListView graphiteListView;
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Toast.makeText(getActivity(),"Created",Toast.LENGTH_SHORT).show();


        graphiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),GraphiteItemDetail.class);

                GraphiteItemModel graphiteItem = (GraphiteItemModel) parent.getAdapter().getItem(position);
                intent.putExtra("GraphitItem", graphiteItem);
                startActivity(intent);

            }
        });
    }


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


    public ArrayList<GraphiteItemModel> getGraphiteItems(){
        ArrayList<GraphiteItemModel> graphiteItems = new ArrayList<>();

        graphiteItems.add(new GraphiteItemModel("jemo","blab","sdf","sdfsdF","sdfsdf",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("gio","TTT","sdf","JJJ","OOOO",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("lasha","AAA","sdf","sdfsdF",")))",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("oto","JJJ","sdf","HH234","QQQ",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("lana","LLL","sdf","sdfsdF","sdfsdf",13.345,44.3345));


        return graphiteItems;

    }

}