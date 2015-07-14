package geolab.graphitefinder.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import geolab.graphitefinder.R;
import geolab.graphitefinder.model.GraphiteItemModel;


public class ListViewAdapter extends BaseAdapter {
    private ArrayList<GraphiteItemModel> graphiteItems;
    private Context context;
    private LayoutInflater inflater;


    public ListViewAdapter(Context context, ArrayList<GraphiteItemModel> graphiteItems){
        this.context = context;
        this.graphiteItems = graphiteItems;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return graphiteItems.size();
    }

    @Override
    public Object getItem(int i) {
        return graphiteItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int index, View convertView, ViewGroup parentView) {

        View itemView;
        ViewHolder viewHolder;

        if(convertView == null) {
            itemView = inflater.inflate(R.layout.graphite_cardview_layout,null);
            viewHolder = new ViewHolder();

            TextView imgTitleView = (TextView) itemView.findViewById(R.id.imgTitle);
            TextView littleDescriptionView = (TextView) itemView.findViewById(R.id.little_description);
            ImageView imgView = (ImageView) itemView.findViewById(R.id.peaceOfArtImg);

            viewHolder.imgView = imgView;
            viewHolder.imgDescriptionView = littleDescriptionView;
            viewHolder.imgTitleView = imgTitleView;

            itemView.setTag(viewHolder);
        }
        else{
            itemView = convertView;
            viewHolder = (ViewHolder) itemView.getTag();
        }

        GraphiteItemModel graphiteItem = (GraphiteItemModel) getItem(index);

        viewHolder.imgTitleView.setText(graphiteItem.getTitle());
        viewHolder.imgDescriptionView.setText(graphiteItem.getDescription());

        Picasso.with(context)
                .load(graphiteItem.getImgURL())
                .resize(200, 200)
                .centerCrop()
                .into(viewHolder.imgView);

        return itemView;
    }


    private class ViewHolder {
        TextView imgTitleView, imgDescriptionView;
        ImageView imgView;
    }


}
