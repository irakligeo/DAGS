package geolab.dags.fragment;

import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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


        initMap();
        googleMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
        //OnMarkerClickListener
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




        // Perform any camera updates here
        return v;
    }


    //init map
    public static void initMap(){
        coordsList = new ArrayList<>();
        coordsList = ViewPagerFragment.graphiteItems;

        // Tbilisi Saburtalo's coords // zoomIn defoult location
        double lat = 41.7186058,lng = 44.7816541;
        // create marker
        for( int i = 0; i < coordsList.size(); ++i ) {

            double longitude = coordsList.get(i).getLongitude();
            double latitude = coordsList.get(i).getLatitude();
            String title = coordsList.get(i).getTitle();

            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

            // add marker
            googleMap.addMarker(marker);
            //insert into hashMap
            mMarkersHashMap.put(marker.getTitle(), coordsList.get(i));


            // camera target for given coordinates
            setCameraPostion(lat,lng,11);

        }

    }

    public static void setCameraPostion(double lat,double lng, int zoom){
        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(lat,lng)).zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }catch (IndexOutOfBoundsException e){
        }

    }

    //function gets coordinates and title from database
    public static ArrayList<GraphiteItemModel> getCoordsFromDB(){
        ArrayList<GraphiteItemModel> tmpList = new ArrayList<>();
            Cursor cursor = ViewPagerFragment.db.rawQuery("SELECT * FROM " + TableGraphite.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                do{
                    int markerId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.id))));
                    double longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.longitude))));
                    double latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.latitude))));
                    String title = cursor.getString(cursor.getColumnIndex(TableGraphite.imgTitle));
                    String imgURL = cursor.getString(cursor.getColumnIndex(TableGraphite.imgURL));
                    String imgDescription = cursor.getString(cursor.getColumnIndex(TableGraphite.imgDescription));
                    String uploadDateTime = cursor.getString(cursor.getColumnIndex(TableGraphite.uploadDateTime));
                    String author = cursor.getString(cursor.getColumnIndex(TableGraphite.imgAuthor));
                    int likesCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(String.valueOf(TableGraphite.likes))));
                    String hashtag = cursor.getString(cursor.getColumnIndex(TableGraphite.hashtag));

                    GraphiteItemModel model = new GraphiteItemModel(title,imgDescription,imgURL,author,uploadDateTime, longitude,latitude,likesCount,hashtag,markerId);

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
            TextView imgDescriptionView = (TextView) view.findViewById(R.id.little_description);
            TextView imgUploadDateTimeView = (TextView) view.findViewById(R.id.createDate);
            TextView authorTextView = (TextView) view.findViewById(R.id.author);
            final TextView likesCountTextView = (TextView) view.findViewById(R.id.likes_countTextView);
            ImageView imgView = (ImageView) view.findViewById(R.id.peaceOfArtImg);

            final ImageView likeImageView = (ImageView) view.findViewById(R.id.like_icon);



            Bundle bundle = this.getArguments();
            GraphiteItemModel graphiteItemModel = new GraphiteItemModel();
            graphiteItemModel = bundle.getParcelable("id");

            String[] createDate = graphiteItemModel.getCreateDate().split(" ");


            imgTitleView.setText(graphiteItemModel.getTitle());
            imgDescriptionView.setText(graphiteItemModel.getDescription());
            imgUploadDateTimeView.setText(createDate[0]);


            authorTextView.setText(graphiteItemModel.getAuthor());

            final Animation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
            fadeIn.setDuration(1200);
            fadeIn.setFillAfter(true);

            final Animation textAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.text_animation);

            final boolean[] clicked = {false};
            //on like ImageView click
            final GraphiteItemModel finalGraphiteItemModel = graphiteItemModel;
            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!clicked[0]) {
                        likesCountTextView.setText(finalGraphiteItemModel.getLikesCount() + 1 + " ");
                        likeImageView.setImageResource(R.drawable.liked_icon);
                        likesCountTextView.startAnimation(textAnimation);
                        likesCountTextView.startAnimation(fadeIn);
                        clicked[0] = true;
                    } else {
                        Toast.makeText(getActivity(), "უკვე მოწონებულია", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstance){
        super.onViewStateRestored(savedInstance);
        initMap();
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
