package geolab.graphitefinder.fragment;

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

import java.util.ArrayList;

import geolab.graphitefinder.GraphiteDetailActivity;
import geolab.graphitefinder.R;
import geolab.graphitefinder.adpaters.ListViewAdapter;
import geolab.graphitefinder.model.GraphiteItemModel;


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

    ListView graphiteListView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graphiteitemslist, container, false);
        cardView = inflater.inflate(R.layout.graphite_cardview_layout,container,false);

        ArrayList<GraphiteItemModel> graphiteItems = getGraphiteItems();
        graphiteListView = (ListView)rootView.findViewById(R.id.graphiteList);

        graphiteListView.setAdapter(new ListViewAdapter(getActivity(), graphiteItems));

        return rootView;
    }





    public ArrayList<GraphiteItemModel> getGraphiteItems(){
        ArrayList<GraphiteItemModel> graphiteItems = new ArrayList<>();

        graphiteItems.add(new GraphiteItemModel("Android","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა ","http://www.wallfoz.com/wp-content/uploads/2015/05/3d_graffiti_wallpapers_49_design_photo_for_dekstop_wfz.jpg","giorgi","13-02-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("PHP-server","ჰიდროელექტროსადგური გაყიდროელექტროსადგურებულა საჰიდროელექტროსადგურში ჩაყიდროელექტროსადგურებულა ","http://theartmad.com/wp-content/uploads/2015/03/3d-Graffiti-Wallpapers-Free-Download-3.jpg","Lasha","14-07-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("web-design","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://www.homefurniture-ideas.com/wp-content/uploads/2014/06/inspiring-imaginative-blue-graffiti-wallpaper-mural-room-1.jpg","Gio","29-07-2015",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("PHP-Map","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://www.wallfoz.com/wp-content/uploads/2015/05/best_graffiti_wallpapers_cool_14_design_backgrunds_for_dekstop_wfz.jpg","Oto","8-07-2013",13.345,44.3345));
        graphiteItems.add(new GraphiteItemModel("design","გრაფიტი აგრაფიტებულა საგრაფიტეში ჩაგრაფიტებულა, საგრაფიტედან ამოგრაფიტებულა","http://hdwallpapersfit.com/wp-content/uploads/2015/02/cool-graffiti-bedroom-wallpapers-design.jpg","Lana","18-07-2014",13.345,44.3345));


        return graphiteItems;

    }

    public static ViewPagerFragment newInstance(String text) {

        ViewPagerFragment f = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
