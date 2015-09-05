package geolab.graphitefinder.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

        ////
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas1 = new Canvas(bmp);

// paint defines the text color,
// stroke width, size
        Paint color = new Paint();
        color.setTextSize(35);
        color.setColor(Color.BLACK);

//modify canvas
        canvas1.drawBitmap(BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher1), 0,0, color);
        canvas1.drawText("User Name!", 30, 40, color);

        ////


        // create marker
        for( int i = 0; i < coordsList.size(); ++i ) {
            double longitude = coordsList.get(i).getLongitude();
            double latitude = coordsList.get(i).getLatitude();
            String title = coordsList.get(i).getTitle();

            //add marker to Map
//            LatLng latLng = new LatLng(longitude, latitude);
//            marker = new MarkerOptions().position(latLng)
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)).title(title)
//                            // Specifies the anchor to be at a particular point in the marker image.
//                    .anchor(0.5f, 1);

            marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(title);
//            // adding marker
            googleMap.addMarker(marker);
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(coordsList.get(3).getLongitude(), coordsList.get(3).getLatitude())).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));







        // Perform any camera updates here
        return v;
    }
    //end of onCreateView


    //function gets coordinates and title from database
    private ArrayList<Coords> getCoordsFromDB(){
        ArrayList<Coords> tmpList = new ArrayList<>();
            Cursor cursor = ViewPagerFragment.db.rawQuery("SELECT * FROM " + TableGraphite.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                do{
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.longitude))));
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.latitude))));
                    String title = cursor.getString(cursor.getColumnIndex(TableGraphite.imgTitle));
                    Coords coords = new Coords(longitude,latitude,title);

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
