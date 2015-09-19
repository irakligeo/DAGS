package geolab.dags.adpaters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import geolab.dags.R;
import geolab.dags.model.GraphiteItemModel;


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

    CardView cardView;

    private int lastPosition = -1;
    @Override
    public View getView(int index, View convertView, ViewGroup parentView) {

        View itemView;
        ViewHolder viewHolder;

        if(convertView == null) {
            itemView = inflater.inflate(R.layout.graphite_cardview_layout,null);
            viewHolder = new ViewHolder();

            cardView = (CardView) itemView.findViewById(R.id.cardview);
//            cardView.setCardBackgroundColor(Color.GRAY);


            //scrolling animation
            Animation animation = AnimationUtils.loadAnimation(context, (index > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            itemView.startAnimation(animation);
            lastPosition = index;


            TextView imgTitleView = (TextView) itemView.findViewById(R.id.imgTitle);
            TextView littleDescriptionView = (TextView) itemView.findViewById(R.id.little_description);
            TextView uploadDateTimeView = (TextView) itemView.findViewById(R.id.uploadDateTime);
            TextView authorTextView = (TextView) itemView.findViewById(R.id.author);
            ImageView imgView = (ImageView) itemView.findViewById(R.id.peaceOfArtImg);

            viewHolder.imgView = imgView;
            viewHolder.imgDescriptionView = littleDescriptionView;
            viewHolder.imgTitleView = imgTitleView;
            viewHolder.authorView = authorTextView;
            viewHolder.uploadDateTimeView = uploadDateTimeView;

            itemView.setTag(viewHolder);
        }
        else{
            itemView = convertView;

            //scrolling animation
            Animation animation = AnimationUtils.loadAnimation(context, (index > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            itemView.startAnimation(animation);
            lastPosition = index;

            viewHolder = (ViewHolder) itemView.getTag();
        }

        GraphiteItemModel graphiteItem = (GraphiteItemModel) getItem(index);

        viewHolder.imgTitleView.setText(graphiteItem.getTitle());
        viewHolder.imgDescriptionView.setText(graphiteItem.getDescription());
        viewHolder.uploadDateTimeView.setText(graphiteItem.getCreateDate());
        viewHolder.authorView.setText(graphiteItem.getAuthor());

        String url = graphiteItem.getImgURL();

        Picasso.with(context)
                .load(url)
                .resize(600, 400)
                .onlyScaleDown()
                .centerCrop()
                .into(viewHolder.imgView);

        return itemView;
    }


    private class ViewHolder {
        TextView imgTitleView, imgDescriptionView, uploadDateTimeView, authorView;
        ImageView imgView;
    }


}
