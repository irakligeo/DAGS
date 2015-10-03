package geolab.dags.dialogFragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import geolab.dags.R;

/**
 * Created by geolabedu on 9/29/15.
 */
public class CommentsDialog extends DialogFragment {

    private View mCommentsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCommentsView = inflater.inflate(R.layout.filter_dialog_fragment, null);


        return mCommentsView;
    }
}
