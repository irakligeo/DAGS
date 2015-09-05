package geolab.graphitefinder.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import geolab.graphitefinder.R;
import geolab.graphitefinder.model.Coords;
import geolab.graphitefinder.model.DB.TableGraphite;

public class MapFragment extends android.support.v4.app.Fragment {
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



        MarkerOptions marker = new MarkerOptions();

        ArrayList<Coords> coordsList;
        //ArrayList of longitude, latitude, title;
        coordsList = getCoordsFromDB();

        // create marker
        for( int i = 0; i < coordsList.size(); ++i ) {
            double longitude = coordsList.get(i).getLongitude();
            double latitude = coordsList.get(i).getLatitude();
            String title = coordsList.get(i).getTitle();

            marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(title);
            // adding marker
            googleMap.addMarker(marker);
        }
        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(coordsList.get(3).getLongitude(), coordsList.get(3).getLatitude())).zoom(5).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }


    private ArrayList<Coords> getCoordsFromDB(){
        ArrayList<Coords> tmpList = new ArrayList<>();
        for(int i = 0; i < 8; ++i){
            Cursor cursor = ViewPagerFragment.db.query(TableGraphite.TABLE_NAME,null,TableGraphite.id +" ="+ i,null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.longitude))));
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.latitude))));
                    String title = cursor.getString(cursor.getColumnIndex(TableGraphite.imgTitle));
                    Coords coords = new Coords(longitude,latitude,title);

                    tmpList.add(coords);

                }while (cursor.moveToNext());
            }
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

}
