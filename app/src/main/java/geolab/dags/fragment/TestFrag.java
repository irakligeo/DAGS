package geolab.dags.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import geolab.dags.R;


public class TestFrag extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_test, container, false);

        TextView tv = (TextView) v.findViewById(R.id.tvFragTest);
        tv.setText(getArguments().getString("msg"));

        return v;
    }


    public static TestFrag newInstance(String text) {

        TestFrag f = new TestFrag();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
