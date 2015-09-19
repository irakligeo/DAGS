package geolab.dags.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import geolab.dags.DB.TableGraphite;
import geolab.dags.R;
import geolab.dags.model.GraphiteItemModel;

public class MapFragment extends android.support.v4.app.Fragment implements OnMarkerClickListener{
    MapView mMapView;
    public static GoogleMap googleMap;

    public static ArrayList<GraphiteItemModel> coordsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflat and return the layout
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();


        mMarkersHashMap = new HashMap<>();



        //ArrayList of longitude, latitude, title, imgURL;
        coordsList = getCoordsFromDB();

        // create marker
        for( int i = 0; i < coordsList.size(); ++i ) {

            double longitude = coordsList.get(i).getLongitude();
            double latitude = coordsList.get(i).getLatitude();
            String title = coordsList.get(i).getTitle();


            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(title);

            // add marker
            googleMap.addMarker(marker);
            //insert into hashMap
            mMarkersHashMap.put(marker.getTitle(), coordsList.get(i));
            googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        }

        //OnMarkerClickListener
        try {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }catch (NullPointerException ex){
            Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
        }



        //on imgClick (googleMap marker Bitmap)
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                final GraphiteItemModel myMarker = mMarkersHashMap.get(marker.getTitle());

                CustomDialogFragment detailFragment = new CustomDialogFragment();
                Bundle arg = new Bundle();
                arg.putParcelable("id", myMarker);

                detailFragment.setArguments(arg);
                detailFragment.show(getActivity().getFragmentManager(), "detail-fragment");
            }
        });

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(coordsList.get(0).getLatitude(), coordsList.get(0).getLongitude())).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }


    //function gets coordinates and title from database
    public static ArrayList<GraphiteItemModel> getCoordsFromDB(){
        ArrayList<GraphiteItemModel> tmpList = new ArrayList<>();
            Cursor cursor = ViewPagerFragment.db.rawQuery("SELECT * FROM " + TableGraphite.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                do{
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.longitude))));
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.latitude))));
                    String title = cursor.getString(cursor.getColumnIndex(TableGraphite.imgTitle));
                    String imgURL = cursor.getString(cursor.getColumnIndex(TableGraphite.imgURL));
                    String imgDescription = cursor.getString(cursor.getColumnIndex(TableGraphite.imgDescription));
                    String uploadDateTime = cursor.getString(cursor.getColumnIndex(TableGraphite.uploadDateTime));
                    String author = cursor.getString(cursor.getColumnIndex(TableGraphite.imgAuthor));

                    GraphiteItemModel model = new GraphiteItemModel(title,imgDescription,imgURL,author,uploadDateTime, longitude,latitude);

                    tmpList.add(model);

                }while (cursor.moveToNext());
            }
        return tmpList;
    }



    public static MapFragment newInstance(String text) {

        MapFragment f = new MapFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }




    public static HashMap<String,GraphiteItemModel> mMarkersHashMap;


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v  = getActivity().getLayoutInflater().inflate(R.layout.infowindow_layout, null);

            try {
                final GraphiteItemModel myMarker = mMarkersHashMap.get(marker.getTitle());

                ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

                TextView markerLabel = (TextView) v.findViewById(R.id.marker_label);

                Picasso.with(getActivity())
                        .load(myMarker.getImgURL())
                        .resize(200, 300)
                        .centerCrop()
                        .into(markerIcon);

                markerLabel.setText(myMarker.getTitle());
            }catch (NullPointerException ex){
                Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
            }
            return v;
        }
    }


    //dialog
    public static class CustomDialogFragment extends DialogFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View view = inflater.inflate(R.layout.custom_info_fragment,null);
            TextView imgTitleView = (TextView) view.findViewById(R.id.imgTitle);
            TextView imgDescriptionView = (TextView) view.findViewById(R.id.imgDescription);
            TextView imgUploadDateTimeView = (TextView) view.findViewById(R.id.uploadDateTime);
            TextView authorTextView = (TextView) view.findViewById(R.id.author);
            ImageView imgView = (ImageView) view.findViewById(R.id.imgView);

            Bundle bundle = this.getArguments();
            GraphiteItemModel graphiteItemModel = new GraphiteItemModel();
            graphiteItemModel = bundle.getParcelable("id");

            imgTitleView.setText(graphiteItemModel.getTitle());
            imgDescriptionView.setText(graphiteItemModel.getDescription());
            imgUploadDateTimeView.setText(graphiteItemModel.getCreateDate());
            authorTextView.setText(graphiteItemModel.getAuthor());

            Picasso.with(getActivity().getApplicationContext())
                    .load(graphiteItemModel.getImgURL())
                    .resize(600, 800)
                    .centerCrop()
                    .into(imgView);

            return view;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getActivity(),"sdf",Toast.LENGTH_LONG).show();
        return true;
    }



    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                Toast.makeText(getActivity(),"blah",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                // Not implemented here
                return true;
            default:
                break;
        }

        return false;
    }

}
