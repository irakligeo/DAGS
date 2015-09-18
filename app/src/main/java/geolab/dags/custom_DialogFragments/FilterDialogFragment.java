package geolab.dags.custom_DialogFragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import geolab.dags.MainActivity;
import geolab.dags.R;
import geolab.dags.fragment.MapFragment;
import geolab.dags.model.GraphiteItemModel;

public class FilterDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private View customView;
    private Spinner spinner;
    private SeekBar volumeControl = null;
    private TextView distanceNumberTextView;

    private ArrayList<GraphiteItemModel> data;
    private double longitude, latitude, distance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        customView = inflater.inflate(R.layout.filter_dialog_fragment,null);
        //location listener
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        // location listener

        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        spinner = (Spinner) customView.findViewById(R.id.spinnerCategory);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.graphite_categories, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);


        distanceNumberTextView = (TextView) customView.findViewById(R.id.distanceNumberTextView);
        //seekbar
        volumeControl = (SeekBar) customView.findViewById(R.id.seekBarDistance);

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                distanceNumberTextView.setText(String.valueOf( progressChanged ));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(), "seek bar progress:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
            }
        });


        Button bt_ok = (Button) customView.findViewById(R.id.button1);
        //on button click
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                data = new ArrayList<>();
                for (String key: MapFragment.mMarkersHashMap.keySet()) {
                    data.add(MapFragment.mMarkersHashMap.get(key));
                }
                if(longitude > 0 || latitude > 0 ) {
                    //cleare markers from map
                    MapFragment.googleMap.clear();
                    checkCoords(data);
                    Toast.makeText(getActivity(), "if shesrulda " + longitude + " " + latitude, Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(getActivity(), longitude + " " +latitude, Toast.LENGTH_SHORT).show();
                MainActivity.filterDialogFragment.dismiss();
            }

        });

        return  customView;
    }


    //filter markers
    private void checkCoords(ArrayList<GraphiteItemModel> data){

        double radius = Double.parseDouble(String.valueOf(distanceNumberTextView.getText()));
        for (int i = 0; i < data.size(); ++i) {
            distance = distanceFrom(longitude, latitude, data.get(i).getLongitude(), data.get(i).getLatitude());
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(data.get(i).getLatitude(), data.get(i).getLongitude())).title(data.get(i).getTitle());
            //get markers with this distance
            if (distance >= radius) {
                MapFragment.googleMap.addMarker(marker);
            } else {
                Toast.makeText(getActivity(), "გაქრა " +  data.get(i).getTitle(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Function for masure distance between two LatLeng
    public double distanceFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        int meterConversion = 1609;
        return new Double(dist * meterConversion).floatValue();    // this will return distance
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        adapterView.getItemAtPosition(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
