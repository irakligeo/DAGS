package geolab.graphitefinder.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import geolab.graphitefinder.GraphiteItemDetail;
import geolab.graphitefinder.R;
import geolab.graphitefinder.adpater.ListViewAdapter;
import geolab.graphitefinder.model.GraphiteItemModel;


public class ViewPagerFragment extends android.support.v4.app.Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        graphiteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),GraphiteItemDetail.class);

                GraphiteItemModel graphiteItem = (GraphiteItemModel) parent.getAdapter().getItem(position);
                intent.putExtra("GraphiteItem", graphiteItem);
                startActivity(intent);

            }
        });
    }

    ListView graphiteListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graphiteitemslist, container, false);

        ArrayList<GraphiteItemModel> graphiteItems = getGraphiteItems();
        graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);

        graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));

        return rootView;
    }





    public ArrayList<GraphiteItemModel> getGraphiteItems(){
        ArrayList<GraphiteItemModel> graphiteItems = new ArrayList<>();

        graphiteItems.add(new GraphiteItemModel("Android","Description Description Description" +
                "Description Description Description Description Description Description Description Description Description" +
                "Description Description Description Description Description","URL","Jemo","13-02-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("PHP-server","Description Description Description" +
                "Description Description Description Description Description Description Description Description Description" +
                "Description Description Description Description Description","URL","Lasha","14-07-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("web-design","Description Description Description" +
                "Description Description Description Description Description Description Description Description Description" +
                "Description Description Description Description Description","URL","Gio","29-07-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("PHP-Map","Description Description Description" +
                "Description Description Description Description Description Description Description Description Description" +
                "Description Description Description Description Description","URL","Oto","8-07-2013",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("design","Description Description Description" +
                "Description Description Description Description Description Description Description Description Description" +
                "Description Description Description Description Description","URL","Lana","18-07-2014",13.345,44.3345));


        return graphiteItems;

    }


}
