package geolab.graphitefinder.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import geolab.graphitefinder.R;
import geolab.graphitefinder.model.Coords;
import geolab.graphitefinder.model.DB.TableGraphite;

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
        if(googleMap != null) {
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });
        }else{
            Toast.makeText(getActivity(),"nullPointer ex",Toast.LENGTH_SHORT).show();
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






    private HashMap<String, Coords> mMarkersHashMap;


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

            Coords myMarker = mMarkersHashMap.get(marker.getTitle());

            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);

            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);

            Picasso.with(getActivity())
                    .load(myMarker.getImgURL())
                    .resize(250,250)
                    .centerCrop()
                    .into(markerIcon);

//            markerIcon.setImageResource(R.drawable.spray);

            markerLabel.setText(myMarker.getTitle());

            markerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return v;
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
