package geolab.dags.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import geolab.dags.R;
import geolab.dags.model.Coords;
import geolab.dags.DB.TableGraphite;

public class MapFragment extends android.support.v4.app.Fragment implements OnMarkerClickListener{
    MapView mMapView;
    private GoogleMap googleMap;

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


        ArrayList<Coords> coordsList;
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

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(coordsList.get(5).getLongitude(), coordsList.get(5).getLatitude())).zoom(9).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));
        

        // Perform any camera updates here
        return v;
    }


    //function gets coordinates and title from database
    private ArrayList<Coords> getCoordsFromDB(){
        ArrayList<Coords> tmpList = new ArrayList<>();
            Cursor cursor = ViewPagerFragment.db.rawQuery("SELECT * FROM " + TableGraphite.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                do{
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.longitude))));
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.latitude))));
                    String title = cursor.getString(cursor.getColumnIndex(TableGraphite.imgTitle));
                    String imgURL = cursor.getString(cursor.getColumnIndex(TableGraphite.imgURL));
                    Coords coords = new Coords(longitude,latitude,title,imgURL);

                    tmpList.add(coords);

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






    private static HashMap<String, Coords> mMarkersHashMap;


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

            final Coords myMarker = mMarkersHashMap.get(marker.getTitle());

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

            Picasso.with(getActivity())
                    .load(myMarker.getImgURL())
                    .resize(440,550)
                    .centerCrop()
                    .into(markerIcon);

            markerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"blah",Toast.LENGTH_LONG).show();
                    CustomDialogFragment detailFragment = new CustomDialogFragment();
                    Bundle arg = new Bundle();
                    arg.putParcelable("id",myMarker);
                    arg.putString("graphite", String.valueOf(myMarker));

                    detailFragment.setArguments(arg);
                    detailFragment.show(getActivity().getFragmentManager(), "detail-fragment");
                }
            });

//            markerIcon.setImageResource(R.drawable.spray);

            markerLabel.setText(myMarker.getTitle());

            return v;
        }
    }


    //dialog
    public static class CustomDialogFragment extends DialogFragment {
        String key = "";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

            View view = inflater.inflate(R.layout.custom_info_fragment,null);
            TextView imgTitleView = (TextView) view.findViewById(R.id.imgTitle);
            TextView imgDescriptionView = (TextView) view.findViewById(R.id.imgDescription);
            TextView imgUploadDateTimeView = (TextView) view.findViewById(R.id.uploadDateTime);
            ImageView imgView = (ImageView) view.findViewById(R.id.imgView);

            Bundle bundle = this.getArguments();
            key = bundle.getString("id");
            HashMap<String,Coords> tmpHashMap = MapFragment.mMarkersHashMap;

            if(tmpHashMap.containsKey(key)) {
                imgTitleView.setText(tmpHashMap.get(key).getTitle());

                Picasso.with(getActivity().getApplicationContext())
                        .load(tmpHashMap.get(key).getImgURL())
                        .resize(200, 200)
                        .centerCrop()
                        .into(imgView);
            }
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
}
